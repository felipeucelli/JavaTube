package javatube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playlist {
    private final String url;
    public Playlist(String InputUrl){
        url = InputUrl;
    }

    private String getPlaylistId() throws Exception {
        Pattern pattern = Pattern.compile("list=[a-zA-Z0-9_\\-]*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()){
            return matcher.group(0);
        }else {
            throw new Exception("RegexMatcherError");
        }
    }

    public String baseData(String continuation){
        return "{\"continuation\": \"" + continuation + "\", \"context\": {\"client\": {\"clientName\": \"WEB\", \"clientVersion\": \"2.20200720.00.02\"}}}";
    }

    private String baseParam(){
        return "https://www.youtube.com/youtubei/v1/browse?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";
    }

    private String getPlaylistUrl() throws Exception {
        return "https://www.youtube.com/playlist?" + getPlaylistId();
    }

    public String getHtml() throws Exception {
        return InnerTube.downloadWebPage(getPlaylistUrl());
    }

    public JSONObject getJson() throws Exception {
        Pattern pattern = Pattern.compile("ytInitialData\\s=\\s\\{\\\"responseContext\\\":.*};</script>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(getHtml());
        if(matcher.find()){
            return new JSONObject(matcher.group(0).replace("ytInitialData = ", "").replace(";</script>", ""));
        }else {
            throw new Exception("RegexMatcherError");
        }
    }

    public JSONArray buildContinuationUrl(String continuation) throws Exception {
        return extractVideos(new JSONObject(InnerTube.post(baseParam(), baseData(continuation))));
    }

    public JSONArray extractVideos(JSONObject rawJson) {
        JSONArray swap = new JSONArray();
        try {
            JSONArray importantContent;
            try {
                JSONObject tabs = new JSONObject(new JSONObject(rawJson.getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs").get(0).toString()).getJSONObject("tabRenderer").getJSONObject("content").getJSONObject("sectionListRenderer").getJSONArray("contents").get(0).toString());
                importantContent = new JSONArray(new JSONObject(tabs.getJSONObject("itemSectionRenderer").getJSONArray("contents").get(0).toString()).getJSONObject("playlistVideoListRenderer").getJSONArray("contents"));
            }catch (JSONException e){
                importantContent = new JSONArray(new JSONObject(rawJson.getJSONArray("onResponseReceivedActions").get(0).toString()).getJSONObject("appendContinuationItemsAction").getJSONArray("continuationItems"));
            }
            try{
                String continuation = new JSONObject(importantContent.get(importantContent.length() - 1).toString()).getJSONObject("continuationItemRenderer").getJSONObject("continuationEndpoint").getJSONObject("continuationCommand").getString("token");
                JSONArray continuationEnd = new JSONArray(buildContinuationUrl(continuation));

                for(int i = 0; i < importantContent.length(); i++){
                    swap.put(importantContent.get(i));
                }

                if (!continuationEnd.isEmpty()){
                    for(int i = 0; i < continuationEnd.length(); i++){
                        swap.put(continuationEnd.get(i));
                    }
                }

            } catch (Exception e) {
                for(int i = 0; i < importantContent.length(); i++){
                    swap.put(importantContent.get(i));
                }
            }

        }catch (Exception ignored){
        }
        return swap;
    }

    public ArrayList<String>  getVideos() throws Exception {
        JSONArray video = extractVideos(getJson());
        ArrayList<String> videosId = new ArrayList<>();
        try {
            for(int i = 0; i < video.length(); i++){
                try{
                    videosId.add("https://www.youtube.com/watch?v=" + new JSONObject(video.get(i).toString()).getJSONObject("playlistVideoRenderer").get("videoId").toString());
                }catch (Exception ignored){
                }
            }
            return videosId;
        } catch (JSONException e) {
            throw new Error(e);
        }

    }

    private JSONObject getSidebarInfo(Integer i) throws Exception {
        return new JSONObject(getJson().getJSONObject("sidebar").getJSONObject("playlistSidebarRenderer").getJSONArray("items").get(i).toString());
    }

    public String getTitle() throws Exception {
        return new JSONObject(getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer").getJSONObject("title").getJSONArray("runs").get(0).toString()).getString("text");
    }

    public String getDescription() throws Exception {
        try {
            try {
                return getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer").getJSONObject("description").getString("simpleText");
            }catch (JSONException e) {
                return new JSONObject(getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer").getJSONObject("description").getJSONArray("runs").get(0).toString()).getString("text");
            }
        }catch (Exception e){
            return null;
        }
    }

    public String getViews() throws Exception {
        return new JSONObject(getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer").getJSONArray("stats").get(1).toString()).getString("simpleText");
    }

    public String getLastUpdated() throws Exception {
        try {
            return new JSONObject(new JSONObject(getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer").getJSONArray("stats").get(2).toString()).getJSONArray("runs").get(1).toString()).getString("text");
        }catch (JSONException e){
            return new JSONObject(new JSONObject(getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer").getJSONArray("stats").get(2).toString()).getJSONArray("runs").get(0).toString()).getString("text");
        }
    }

    public String getOwner() throws Exception {
        return new JSONObject(getSidebarInfo(1).getJSONObject("playlistSidebarSecondaryInfoRenderer").getJSONObject("videoOwner").getJSONObject("videoOwnerRenderer").getJSONObject("title").getJSONArray("runs").get(0).toString()).getString("text");
    }

    public String getOwnerId() throws Exception {
        return new JSONObject(getSidebarInfo(1).getJSONObject("playlistSidebarSecondaryInfoRenderer").getJSONObject("videoOwner").getJSONObject("videoOwnerRenderer").getJSONObject("title").getJSONArray("runs").get(0).toString()).getJSONObject("navigationEndpoint").getJSONObject("browseEndpoint").getString("browseId");
    }

    public String getOwnerUrl() throws Exception {
        return "https://www.youtube.com/channel/" + getOwnerId();
    }

    public Integer length() throws Exception {
        return Integer.parseInt(new JSONObject(new JSONObject(getSidebarInfo(0).getJSONObject("playlistSidebarPrimaryInfoRenderer").getJSONArray("stats").get(0).toString()).getJSONArray("runs").get(0).toString()).getString("text").replace(",", ""));
    }

}