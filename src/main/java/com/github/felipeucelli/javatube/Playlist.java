package com.github.felipeucelli.javatube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playlist {
    private final String url;
    protected String html = null;
    protected JSONObject json = null;
    protected String continuationToken = null;
    InnerTube innerTube;

    public Playlist(String InputUrl) throws JSONException {
        url = InputUrl;
        innerTube = new InnerTube("WEB");
    }

    @Override
    public String toString(){
        try {
            return "<com.github.felipeucelli.javatube.Playlist object: playlistId=" + getPlaylistId() + ">";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getPlaylistId() throws Exception {
        Pattern pattern = Pattern.compile("list=([a-zA-Z0-9_\\-]*)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()){
            return matcher.group(1);
        }else {
            throw new Exception("RegexMatcherError. Unable to find match on: " + url);
        }
    }

    private String getPlaylistUrl() throws Exception {
        return "https://www.youtube.com/playlist?list=" + getPlaylistId();
    }

    protected String setHtml() throws Exception {
        return Request.get(getPlaylistUrl(), null, innerTube.getClientHeaders()).toString();
    }
    protected String getHtml() throws Exception {
        if(html == null){
            html = setHtml();
        }
        return html;
    }

    protected JSONObject setJson() throws Exception {
        Pattern pattern = Pattern.compile("ytInitialData\\s=\\s(\\{\\\"responseContext\\\":.*\\});</script>");
        Matcher matcher = pattern.matcher(getHtml());
        if(matcher.find()){
            return new JSONObject(matcher.group(1));
        }else {
            throw new Exception("RegexMatcherError: " + pattern);
        }
    }

    protected JSONObject getJson() throws Exception {
        if(json == null){
            json = setJson();
        }
        return json;
    }

    protected void setContinuationToken(JSONArray importantContent) throws JSONException {
        continuationToken = importantContent.getJSONObject(importantContent.length() - 1)
                .getJSONObject("continuationItemRenderer")
                .getJSONObject("continuationEndpoint")
                .getJSONObject("continuationCommand")
                .getString("token");
    }

    protected JSONArray extractContinuationItems(JSONArray importantContent) throws Exception {
        JSONArray swap = new JSONArray();

        JSONArray continuationEnd = buildContinuationUrl(continuationToken);

        for(int i = 0; i < importantContent.length(); i++){
            swap.put(importantContent.get(i));
        }

        for(int i = 0; i < continuationEnd.length(); i++){
            swap.put(continuationEnd.get(i));
        }
        return swap;
    }

    protected JSONArray buildContinuationUrl(String continuation) throws Exception {
        String data = "{" +
                        "\"continuation\": \"" + continuation + "\"" +
                    "}";
        return extractVideos(innerTube.browse(new JSONObject(data)));
    }

    protected JSONArray extractVideos(JSONObject rawJson) {
        JSONArray swap = new JSONArray();
        try {
            JSONArray importantContent;
            try {
                JSONObject tabs = rawJson.getJSONObject("contents")
                        .getJSONObject("twoColumnBrowseResultsRenderer")
                        .getJSONArray("tabs")
                        .getJSONObject(0)
                        .getJSONObject("tabRenderer")
                        .getJSONObject("content")
                        .getJSONObject("sectionListRenderer")
                        .getJSONArray("contents")
                        .getJSONObject(0);

                importantContent = tabs.getJSONObject("itemSectionRenderer")
                        .getJSONArray("contents")
                        .getJSONObject(0)
                        .getJSONObject("playlistVideoListRenderer")
                        .getJSONArray("contents");

            }catch (JSONException e){
                importantContent = rawJson.getJSONArray("onResponseReceivedActions")
                        .getJSONObject(0)
                        .getJSONObject("appendContinuationItemsAction")
                        .getJSONArray("continuationItems");
            }
            if(importantContent.getJSONObject(importantContent.length() - 1).has("continuationItemRenderer")){
                setContinuationToken(importantContent);
                swap = extractContinuationItems(importantContent);
            } else {
                for(int i = 0; i < importantContent.length(); i++){
                    swap.put(importantContent.get(i));
                }
            }

        }catch (Exception ignored){
        }
        return swap;
    }

    protected ArrayList<String> unify(ArrayList<String> list){
        LinkedHashSet<String> unifiedList = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(unifiedList);
        return list;
    }

    public ArrayList<String> getVideos() throws Exception {
        JSONArray video = extractVideos(getJson());
        ArrayList<String> videosId = new ArrayList<>();
        try {
            for(int i = 0; i < video.length(); i++){
                try{
                    videosId.add("https://www.youtube.com/watch?v=" + video.getJSONObject(i)
                            .getJSONObject("playlistVideoRenderer")
                            .getString("videoId"));
                }catch (Exception ignored){
                }
            }
            return unify(videosId);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private JSONObject getSidebarInfo(Integer i) throws Exception {
        return getJson().getJSONObject("sidebar")
                .getJSONObject("playlistSidebarRenderer")
                .getJSONArray("items")
                .getJSONObject(i);
    }

    public String getUrl() throws Exception {
        return getPlaylistUrl();
    }

    public String getTitle() throws Exception {
        return getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer")
                .getJSONObject("title")
                .getJSONArray("runs")
                .getJSONObject(0)
                .getString("text");
    }

    public String getDescription() throws Exception {
        try {
            try {
                return getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer")
                        .getJSONObject("description")
                        .getString("simpleText");
            }catch (JSONException e) {
                return getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer")
                        .getJSONObject("description")
                        .getJSONArray("runs")
                        .getJSONObject(0)
                        .getString("text");
            }
        }catch (Exception e){
            return null;
        }
    }

    public String getViews() throws Exception {
        return getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer")
                .getJSONArray("stats")
                .getJSONObject(1)
                .getString("simpleText");
    }

    public String getLastUpdated() throws Exception {
        try {
            return getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer")
                    .getJSONArray("stats").getJSONObject(2)
                    .getJSONArray("runs").getJSONObject(1)
                    .getString("text");
        }catch (JSONException e){
            return getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer")
                    .getJSONArray("stats")
                    .getJSONObject(2)
                    .getJSONArray("runs")
                    .getJSONObject(0)
                    .getString("text");
        }
    }

    public String getOwner() throws Exception {
        return getSidebarInfo(1).getJSONObject("playlistSidebarSecondaryInfoRenderer")
                .getJSONObject("videoOwner")
                .getJSONObject("videoOwnerRenderer")
                .getJSONObject("title")
                .getJSONArray("runs")
                .getJSONObject(0)
                .getString("text");
    }

    public String getOwnerId() throws Exception {
        return getSidebarInfo(1).getJSONObject("playlistSidebarSecondaryInfoRenderer")
                .getJSONObject("videoOwner")
                .getJSONObject("videoOwnerRenderer")
                .getJSONObject("title")
                .getJSONArray("runs")
                .getJSONObject(0)
                .getJSONObject("navigationEndpoint")
                .getJSONObject("browseEndpoint")
                .getString("browseId");
    }

    public String getOwnerUrl() throws Exception {
        return "https://www.youtube.com/channel/" + getOwnerId();
    }

    public String length() throws Exception {
        return getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer")
                .getJSONArray("stats")
                .getJSONObject(0)
                .getJSONArray("runs")
                .getJSONObject(0)
                .getString("text");
    }

}