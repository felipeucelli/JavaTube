package com.github.felipeucelli.javatube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Search {

    private final String query;
    private JSONObject jsonResult = null;
    private final InnerTube innerTubeClient = new InnerTube("WEB");
    private Map<String, ArrayList<String>> results = new HashMap<>();
    private String continuationToken = "";

    public Search(String query) throws JSONException {
        this.query = query;
    }

    private String safeQuery() throws UnsupportedEncodingException {
        return URLEncoder.encode(this.query, StandardCharsets.UTF_8.name());
    }

    private JSONObject getJsonResult() throws Exception {
        if(jsonResult == null){
            jsonResult =  setJson();
        }
        return jsonResult;
    }

    private JSONObject setJson() throws Exception {
        return innerTubeClient.search(safeQuery(), continuationToken);
    }

    private ArrayList<String> extractShelfRenderer(JSONArray items) throws JSONException {
        ArrayList<String> ids = new ArrayList<>();
        for(int i = 0; items.length() > i; i++){
            String vidId = items.getJSONObject(i).getJSONObject("videoRenderer").getString("videoId");
            ids.add("https://www.youtube.com/watch?v=" + vidId);
        }
        return ids;
    }

    private ArrayList<String> extractReelShelfRenderer(JSONArray items) throws JSONException {
        ArrayList<String> ids = new ArrayList<>();
        for(int i = 0; items.length() > i; i++){
            String vidId = items.getJSONObject(i).getJSONObject("reelItemRenderer").getString("videoId");
            ids.add("https://www.youtube.com/shorts/" + vidId);
        }
        return ids;
    }

    private Map<String, ArrayList<String>> fetchAndParse() throws Exception {
        JSONObject rawResults = getJsonResult();
        JSONArray sections;
        try {
            sections = rawResults.getJSONObject("contents")
                    .getJSONObject("twoColumnSearchResultsRenderer")
                    .getJSONObject("primaryContents")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents");
        }catch (JSONException e){
            sections = rawResults.getJSONArray("onResponseReceivedCommands")
                    .getJSONObject(0)
                    .getJSONObject("appendContinuationItemsAction")
                    .getJSONArray("continuationItems");
        }

        JSONArray rawVideoList = new JSONArray();
        for(int i = 0; i < sections.length(); i++){
            if(sections.getJSONObject(i).has("itemSectionRenderer")){
                rawVideoList = sections.getJSONObject(i).getJSONObject("itemSectionRenderer")
                        .getJSONArray("contents");
            }
            if(sections.getJSONObject(i).has("continuationItemRenderer")){
                continuationToken = sections.getJSONObject(i).getJSONObject("continuationItemRenderer")
                        .getJSONObject("continuationEndpoint")
                        .getJSONObject("continuationCommand")
                        .getString("token");
            }
        }

        ArrayList<String> videos = new ArrayList<>();
        ArrayList<String> shorts = new ArrayList<>();
        ArrayList<String> channel = new ArrayList<>();
        ArrayList<String> playlist = new ArrayList<>();

        for(int i = 0; i < rawVideoList.length() - 1; i++) {
            if (rawVideoList.getJSONObject(i).has("videoRenderer")) {
                JSONObject vidRenderer = rawVideoList.getJSONObject(i).getJSONObject("videoRenderer");
                String vidId = vidRenderer.getString("videoId");
                videos.add("https://www.youtube.com/watch?v=" + vidId);

            } else if (rawVideoList.getJSONObject(i).has("shelfRenderer")) {
                JSONObject contents = rawVideoList.getJSONObject(i).getJSONObject("shelfRenderer")
                        .getJSONObject("content");
                if(contents.has("verticalListRenderer")){
                    videos.addAll(extractShelfRenderer(contents.getJSONObject("verticalListRenderer")
                            .getJSONArray("items")));
                }

            } else if (rawVideoList.getJSONObject(i).has("reelShelfRenderer")) {
                shorts.addAll(extractReelShelfRenderer(rawVideoList.getJSONObject(i)
                        .getJSONObject("reelShelfRenderer")
                        .getJSONArray("items")));

            } else if (rawVideoList.getJSONObject(i).has("channelRenderer")) {
                String channelId = rawVideoList.getJSONObject(i).getJSONObject("channelRenderer")
                        .getString("channelId");
                channel.add("https://www.youtube.com/channel/" + channelId);

            } else if (rawVideoList.getJSONObject(i).has("playlistRenderer")) {
                String playlistId = rawVideoList.getJSONObject(i).getJSONObject("playlistRenderer")
                        .getString("playlistId");
                playlist.add("https://www.youtube.com/playlist?list=" + playlistId);
            }
        }

        Map<String, ArrayList<String>> results = new HashMap<>();
        results.put("videos", videos);
        results.put("shorts", shorts);
        results.put("channel", channel);
        results.put("playlist", playlist);

        return results;
    }

    public void generateContinuation() throws Exception {
        if(!Objects.equals(continuationToken, "")){
            jsonResult = null;
            Map<String, ArrayList<String>> result = fetchAndParse();
            results.get("videos").addAll(result.get("videos"));
            results.get("shorts").addAll(result.get("shorts"));
            results.get("channel").addAll(result.get("channel"));
            results.get("playlist").addAll(result.get("playlist"));
        }
    }

    public List<String> getCompletionSuggestions() throws Exception {
        List<String> result = new ArrayList<>();
        try {
            JSONArray refinements = getJsonResult().getJSONArray("refinements");
            for(int i = 0; refinements.length() > i; i ++){
                result.add(refinements.getString(i));
            }
            return result;
        }catch (JSONException e){
            return null;
        }
    }

    public ArrayList<String> getResults() throws Exception {
        ArrayList<String> result = new ArrayList<>();
        if(results.isEmpty()){
            results = fetchAndParse();
        }
        result.addAll(results.get("videos"));
        result.addAll(results.get("shorts"));
        result.addAll(results.get("channel"));
        result.addAll(results.get("playlist"));
        return result;
    }

    public ArrayList<Youtube> getVideosResults() throws Exception {
        ArrayList<Youtube> result = new ArrayList<>();
        if(results.isEmpty()){
            results = fetchAndParse();
        }
        for(String ids : results.get("videos")){
            result.add(new Youtube(ids));
        }
        return result;
    }

    public ArrayList<Youtube> getShortsResults() throws Exception {
        ArrayList<Youtube> result = new ArrayList<>();
        if(results.isEmpty()){
            results = fetchAndParse();
        }
        for(String ids : results.get("shorts")){
            result.add(new Youtube(ids));
        }
        return result;
    }

    public ArrayList<Channel> getChannelsResults() throws Exception {
        ArrayList<Channel> result = new ArrayList<>();
        if(results.isEmpty()){
            results = fetchAndParse();
        }
        for(String ids : results.get("channel")){
            result.add(new Channel(ids));
        }
        return result;
    }

    public ArrayList<Playlist> getPlaylistsResults() throws Exception {
        ArrayList<Playlist> result = new ArrayList<>();
        if(results.isEmpty()){
            results = fetchAndParse();
        }
        for(String ids : results.get("playlist")){
            result.add(new Playlist(ids));
        }
        return result;
    }

}