package javatube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Channel extends Playlist{

    private final String videosUrl;
    private final String shortsUrl;
    private final String playlistUrl;
    private final String communityUrl;
    private final String featuredChannelUrl;
    private final String aboutUrl;
    private final String url;
    private String getUrlPage;
    private String visitorData;

    public Channel(String inputUrl) throws Exception {
        super(inputUrl);
        url = inputUrl;
        String channelUrl = "https://www.youtube.com" + extractUrl();

        videosUrl = channelUrl + "/videos";
        shortsUrl = channelUrl + "/shorts";
        playlistUrl = channelUrl + "/playlists";
        communityUrl = channelUrl + "/community";
        featuredChannelUrl = channelUrl + "/channels";
        aboutUrl = channelUrl + "/about";

    }
    private String extractUrl() throws Exception {
        ArrayList<String> re = new ArrayList<>();
        re.add("(?:\\/(c)\\/([%\\d\\w_\\-]+)(\\/.*)?)");
        re.add("(?:\\/(channel)\\/([%\\w\\d_\\-]+)(\\/.*)?)");
        re.add("(?:\\/(u)\\/([%\\d\\w_\\-]+)(\\/.*)?)");
        re.add("(?:\\/(user)\\/([%\\w\\d_\\-]+)(\\/.*)?)");

        for (String s : re) {
            Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()){
                return "/" + matcher.group(1) + "/" + matcher.group(2);
            }
        }
        throw new Exception("RegexMatcherError");
    }

    private void setUrl(String url){
        getUrlPage = url;
    }

    private String getUrl(){
        return getUrlPage;
    }

    @Override
    public String baseData(String continuation){
        if(Objects.equals(getUrl(), getShortsUrl())){
            return "{\"continuation\": \"" + continuation + "\", \"context\": {\"client\": {\"clientName\": \"WEB\",  \"visitorData\": \"" + visitorData + "\", \"clientVersion\": \"2.20221107.06.00\"}}}";
        }else {
            return "{\"continuation\": \"" + continuation + "\", \"context\": {\"client\": {\"clientName\": \"WEB\", \"clientVersion\": \"2.20221107.06.00\"}}}";
        }
    }

    @Override
    public String getHtml() throws IOException {
        return InnerTube.downloadWebPage(getUrl());
    }
    @Override
    public JSONArray extractVideos(JSONObject rawJson){
        JSONArray swap = new JSONArray();
        try {
            JSONArray importantContent;
            JSONObject tabs;

            try {
                try {
                    tabs = new JSONObject(rawJson.getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs").get(1).toString()).getJSONObject("tabRenderer").getJSONObject("content");
                }catch (Exception e){
                    tabs = new JSONObject(rawJson.getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs").get(2).toString()).getJSONObject("tabRenderer").getJSONObject("content");
                    visitorData = rawJson.getJSONObject("responseContext").getJSONObject("webResponseContextExtensionData").getJSONObject("ytConfigData").getString("visitorData");
                }
                try{
                    importantContent = new JSONArray(new JSONObject(new JSONObject(tabs.getJSONObject("sectionListRenderer").getJSONArray("contents").get(0).toString()).getJSONObject("itemSectionRenderer").getJSONArray("contents").get(0).toString()).getJSONObject("gridRenderer").getJSONArray("items"));
                }catch (Exception e){
                    importantContent = new JSONArray(tabs.getJSONObject("richGridRenderer").getJSONArray("contents"));
                }
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

        } catch (Exception ignored) {
        }
        return swap;
    }

    public ArrayList<String>  getShorts() throws Exception {
        setUrl(shortsUrl);
        return extractVideosId();
    }

    public ArrayList<String> getVideos() throws Exception {
        setUrl(videosUrl);
        return extractVideosId();
    }

    private ArrayList<String> extractVideosId() throws Exception {
        JSONArray video = extractVideos(getJson());
        ArrayList<String> videosId = new ArrayList<>();
        try {
            for(int i = 0; i < video.length(); i++){
                try{
                    try {
                        videosId.add("https://www.youtube.com/watch?v=" + new JSONObject(video.get(i).toString()).getJSONObject("gridVideoRenderer").get("videoId").toString());
                    }catch (JSONException j){
                        try {
                            videosId.add("https://www.youtube.com/watch?v=" + new JSONObject(video.get(i).toString()).getJSONObject("richItemRenderer").getJSONObject("content").getJSONObject("videoRenderer").get("videoId").toString());
                        }catch (JSONException s){
                            videosId.add("https://www.youtube.com/watch?v=" + new JSONObject(video.get(i).toString()).getJSONObject("richItemRenderer").getJSONObject("content").getJSONObject("reelItemRenderer").get("videoId").toString());
                        }
                    }
                }catch (Exception ignored){
                }
            }
            return videosId;
        } catch (JSONException e) {
            throw new Error(e);
        }
    }

    public String getChannelName() throws Exception {
        setUrl(url);
        return new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getString("title");
    }

    public String getChannelId() throws Exception {
        setUrl(url);
        return new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getString("externalId");
    }

    public String getVanityUrl() throws Exception {
        setUrl(url);
        return new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getString("vanityChannelUrl");
    }

    @Override
    public String getDescription() throws Exception {
        setUrl(url);
        return new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getString("description");
    }

    @Override
    public String getViews() throws Exception {
        setUrl(aboutUrl);
        int pos = getJson().getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs").length() - 2;
        try {
            return new JSONObject(new JSONObject(new JSONObject(getJson().getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs").get(pos).toString()).getJSONObject("tabRenderer").getJSONObject("content").getJSONObject("sectionListRenderer").getJSONArray("contents").get(0).toString()).getJSONObject("itemSectionRenderer").getJSONArray("contents").get(0).toString()).getJSONObject("channelAboutFullMetadataRenderer").getJSONObject("viewCountText").getString("simpleText");
        }catch (JSONException e){
            return null;
        }
    }

    public String getKeywords() throws Exception {
        setUrl(url);
        return new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getString("keywords");
    }

    public JSONArray getAvailableCountryCodes() throws Exception {
        setUrl(url);
        return new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getJSONArray("availableCountryCodes");
    }

    public String getThumbnailUrl() throws Exception {
        setUrl(url);
        return new JSONObject(new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getJSONObject("avatar").getJSONArray("thumbnails").get(0).toString()).getString("url");
    }

    public String getVideosUrl(){
        return videosUrl;
    }

    public String getShortsUrl(){
        return shortsUrl;
    }

    public String getPlaylistUrl(){
        return playlistUrl;
    }

    public String getCommunityUrl(){
        return communityUrl;
    }

    public String getFeaturedChannelUrl(){
        return featuredChannelUrl;
    }

    public String getAboutUrl(){
        return aboutUrl;
    }

    @Deprecated
    @Override
    public String getTitle(){
        return null;
    }

    @Deprecated
    @Override
    public String getLastUpdated(){
        return null;
    }

    @Deprecated
    @Override
    public String getOwner(){
        return null;
    }

    @Deprecated
    @Override
    public String getOwnerId(){
        return null;
    }

    @Deprecated
    @Override
    public String getOwnerUrl(){
        return null;
    }

    @Deprecated
    @Override
    public Integer length(){
        return null;
    }

}