package javatube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search {

    private final String query;

    private JSONObject jsonResult = null;

    public Search(String query){
        this.query = query;
    }

    private String safeQuery(){
        return URLEncoder.encode(this.query, StandardCharsets.UTF_8);
    }

    private String baseData(){
        return "{\"context\": {\"client\": {\"clientName\": \"WEB\", \"clientVersion\": \"2.20200720.00.02\"}}}";
    }

    private String baseParam(){
        return "https://www.youtube.com/youtubei/v1/search?query=" + safeQuery() + "&key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8&contentCheckOk=True&racyCheckOk=True";
    }

    private JSONObject getJsonResult() throws Exception {
        if(jsonResult == null){
            jsonResult =  new JSONObject(fetchQuery());
        }
        return jsonResult;
    }

    private String fetchQuery() throws IOException {
        return InnerTube.post(baseParam(), baseData());
    }

    private ArrayList<String> extractShelfRenderer(JSONArray items) {
        ArrayList<String> ids = new ArrayList<>();
        for(int i = 0; items.length() > i; i++){
            String vidId = items.getJSONObject(i).getJSONObject("videoRenderer").getString("videoId");
            ids.add("https://www.youtube.com/watch?v=" + vidId);
        }
        return ids;
    }

    private ArrayList<String> extractReelShelfRenderer(JSONArray items) {
        ArrayList<String> ids = new ArrayList<>();
        for(int i = 0; items.length() > i; i++){
            String vidId = items.getJSONObject(i).getJSONObject("reelItemRenderer").getString("videoId");
            ids.add("https://www.youtube.com/shorts/" + vidId);
        }
        return ids;
    }

    private Map<String, ArrayList<String>> fetchAndParse() throws Exception {
        JSONObject rawResults = getJsonResult();
        JSONObject sections = rawResults.getJSONObject("contents")
                .getJSONObject("twoColumnSearchResultsRenderer")
                .getJSONObject("primaryContents")
                .getJSONObject("sectionListRenderer")
                .getJSONArray("contents")
                .getJSONObject(0);

        JSONArray rawVideoList = new JSONArray(sections.getJSONObject("itemSectionRenderer")
                .getJSONArray("contents"));

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
                videos.addAll(extractShelfRenderer(rawVideoList.getJSONObject(i).getJSONObject("shelfRenderer")
                        .getJSONObject("content")
                        .getJSONObject("verticalListRenderer")
                        .getJSONArray("items")));

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

    public List<Object> getCompletionSuggestions() throws Exception {
        try {
            return getJsonResult().getJSONArray("refinements").toList();
        }catch (JSONException e){
            return null;
        }
    }

    public ArrayList<String> getResults() throws Exception {
        ArrayList<String> result = new ArrayList<>();
        Map<String, ArrayList<String>> parsed =  fetchAndParse();
        result.addAll(parsed.get("videos"));
        result.addAll(parsed.get("shorts"));
        result.addAll(parsed.get("channel"));
        result.addAll(parsed.get("playlist"));
        return result;
    }

    public ArrayList<Youtube> getVideosResults() throws Exception {
        ArrayList<Youtube> result = new ArrayList<>();
        ArrayList<String> channel =  fetchAndParse().get("videos");
        for(String ids : channel){
            result.add(new Youtube(ids));
        }
        return result;
    }

    public ArrayList<Youtube> getShortsResults() throws Exception {
        ArrayList<Youtube> result = new ArrayList<>();
        ArrayList<String> channel =  fetchAndParse().get("shorts");
        for(String ids : channel){
            result.add(new Youtube(ids));
        }
        return result;
    }

    public ArrayList<Channel> getChannelsResults() throws Exception {
        ArrayList<Channel> result = new ArrayList<>();
        ArrayList<String> channel =  fetchAndParse().get("channel");
        for(String ids : channel){
            result.add(new Channel(ids));
        }
        return result;
    }

    public ArrayList<Playlist> getPlaylistsResults() throws Exception {
        ArrayList<Playlist> result = new ArrayList<>();
        ArrayList<String> playlist =  fetchAndParse().get("playlist");
        for(String ids : playlist){
            result.add(new Playlist(ids));
        }
        return result;
    }

}