package javatube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playlist {
    String url;
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

    private String baseData(String continuation){
        return "{\"continuation\": \"" + continuation + "\", \"context\": {\"client\": {\"clientName\": \"WEB\", \"clientVersion\": \"2.20200720.00.02\"}}}";
    }

    private String baseParam(){
        return "https://www.youtube.com/youtubei/v1/browse?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";
    }

    private String getPlaylistUrl() throws Exception {
        return "https://www.youtube.com/playlist?" + getPlaylistId();
    }

    private String getHtml() throws Exception {
        return InnerTube.downloadWebPage(getPlaylistUrl());
    }

    private JSONObject getJson() throws Exception {
        Pattern pattern = Pattern.compile("ytInitialData\\s=\\s\\{\\\"responseContext\\\":.*};</script>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(getHtml());
        if(matcher.find()){
            return new JSONObject(matcher.group(0).replace("ytInitialData = ", "").replace(";</script>", ""));
        }else {
            throw new Exception("RegexMatcherError");
        }
    }

    private JSONArray buildContinuationUrl(String continuation) throws Exception {
        return extractVideos(new JSONObject(InnerTube.post(baseParam(), baseData(continuation))));
    }

    private JSONArray extractVideos(JSONObject rawJson) {
        try {

            JSONObject tabs = new JSONObject(rawJson.getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs").get(0).toString());
            JSONObject contents = new JSONObject(tabs.getJSONObject("tabRenderer").getJSONObject("content").getJSONObject("sectionListRenderer").getJSONArray("contents").get(0).toString());
            JSONArray importantContent = new JSONArray(new JSONObject(contents.getJSONObject("itemSectionRenderer").getJSONArray("contents").get(0).toString()).getJSONObject("playlistVideoListRenderer").getJSONArray("contents"));
            JSONArray swap = new JSONArray();
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

            return swap;


        } catch (JSONException e) {
            JSONArray importantContent = new JSONArray(new JSONObject(rawJson.getJSONArray("onResponseReceivedActions").get(0).toString()).getJSONObject("appendContinuationItemsAction").getJSONArray("continuationItems"));
            JSONArray swap = new JSONArray();
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

            } catch (Exception d) {
                for(int i = 0; i < importantContent.length(); i++){
                    swap.put(importantContent.get(i));
                }
            }

            return swap;

        }catch (Exception e){
            throw new Error(e);
        }

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
            throw new RuntimeException(e);
        }

    }

    private JSONObject getSidebarInfo() throws Exception {
        return new JSONObject(getJson().getJSONObject("sidebar").getJSONObject("playlistSidebarRenderer").getJSONArray("items").get(0).toString());
    }

    public String getTitle() throws Exception {
        return new JSONObject(getSidebarInfo().getJSONObject("playlistSidebarPrimaryInfoRenderer").getJSONObject("title").getJSONArray("runs").get(0).toString()).getString("text");
    }

    public Integer length() throws Exception {
        return Integer.parseInt(new JSONObject(new JSONObject(getSidebarInfo().getJSONObject("playlistSidebarPrimaryInfoRenderer").getJSONArray("stats").get(0).toString()).getJSONArray("runs").get(0).toString()).getString("text").replace(",", ""));
    }

}