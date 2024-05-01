package com.github.felipeucelli.javatube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Channel extends Playlist{

    private final String channelUrl;
    private final String featuredUrl;
    private final String videosUrl;
    private final String shortsUrl;
    private final String streamsUrl;
    private final String releasesUrl;
    private final String playlistUrl;
    private final String communityUrl;
    private final String featuredChannelUrl;
    private final String aboutUrl;
    private final String url;
    private String htmlPage;
    private String visitorData;
    private int attempts = 0;

    public Channel(String inputUrl) throws Exception {
        super(inputUrl);
        url = inputUrl;
        channelUrl = "https://www.youtube.com" + extractUrl();

        featuredUrl = channelUrl + "/featured";
        videosUrl = channelUrl + "/videos";
        shortsUrl = channelUrl + "/shorts";
        streamsUrl = channelUrl + "/streams";
        releasesUrl = channelUrl + "/releases";
        playlistUrl = channelUrl + "/playlists";
        communityUrl = channelUrl + "/community";
        featuredChannelUrl = channelUrl + "/channels";
        aboutUrl = channelUrl + "/about";
    }

    @Override
    public String toString(){
        try {
            return "<com.github.felipeucelli.javatube.Channel object: channelUrl=" + extractUrl() + ">";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String extractUrl() throws Exception {
        ArrayList<String> re = new ArrayList<>();
        re.add("(?:\\/(c)\\/([%\\d\\w_\\-]+)(\\/.*)?)");
        re.add("(?:\\/(channel)\\/([%\\w\\d_\\-]+)(\\/.*)?)");
        re.add("(?:\\/(u)\\/([%\\d\\w_\\-]+)(\\/.*)?)");
        re.add("(?:\\/(user)\\/([%\\w\\d_\\-]+)(\\/.*)?)");
        re.add("(?:\\/(\\@)([%\\d\\w_\\-\\.]+)(\\/.*)?)");

        for (String regex : re) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()){
                if (Objects.equals(matcher.group(1), "@")){
                    return "/@" + matcher.group(2);
                }else {
                    return "/" + matcher.group(1) + "/" + matcher.group(2);
                }
            }
        }
        throw new Exception("RegexMatcherError. Unable to find match on: " + url);
    }

    private void setHtmlUrl(String url){
        if(!Objects.equals(htmlPage, url)){
            htmlPage = url;
            html = null;
            json = null;
        }
    }

    private String getHtmlUrl(){
        return htmlPage;
    }

    @Override
    protected String setHtml() throws Exception {
        return Request.get(getHtmlUrl(), null, innerTube.getClientHeaders()).toString();
    }

    private JSONObject getActiveTab(JSONObject rawJson) throws JSONException{
        JSONObject activeTab = new JSONObject();
        JSONArray tabs = rawJson.getJSONObject("contents")
                .getJSONObject("twoColumnBrowseResultsRenderer")
                .getJSONArray("tabs");
        for(int i = 0; tabs.length() > i; i ++ ) {
            if(tabs.getJSONObject(i).has("tabRenderer")){
                String tabUrl = tabs.getJSONObject(i).getJSONObject("tabRenderer")
                        .getJSONObject("endpoint")
                        .getJSONObject("commandMetadata")
                        .getJSONObject("webCommandMetadata")
                        .getString("url");
                if (tabUrl.substring(tabUrl.lastIndexOf("/") + 1).equals(getHtmlUrl().substring(getHtmlUrl().lastIndexOf("/") + 1))) {
                    activeTab = tabs.getJSONObject(i);
                    break;
                }
            }
        }
        return activeTab;
    }

    private JSONArray getImportantContent(JSONObject activeTab) throws JSONException {
        try {
            return activeTab.getJSONObject("tabRenderer")
                    .getJSONObject("content")
                    .getJSONObject("richGridRenderer")
                    .getJSONArray("contents");
        } catch (JSONException e) {
            return getImportantContentFromSectionList(activeTab);
        }
    }

    private JSONArray getImportantContentFromSectionList(JSONObject activeTab) throws JSONException {
        try {
            JSONObject firstItem = activeTab.getJSONObject("tabRenderer")
                    .getJSONObject("content")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("itemSectionRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("gridRenderer");
            return firstItem.getJSONArray("items");
        } catch (JSONException e) {
            return getImportantContentFromShelfRenderers(activeTab);
        }
    }

    private JSONArray getImportantContentFromShelfRenderers(JSONObject activeTab) throws JSONException {
        JSONArray contents = activeTab.getJSONObject("tabRenderer")
                .getJSONObject("content")
                .getJSONObject("sectionListRenderer")
                .getJSONArray("contents");
        JSONArray items = new JSONArray();
        for (int i = 0; i < contents.length(); i++) {
            JSONObject shelfRenderer = contents.getJSONObject(i)
                    .getJSONObject("itemSectionRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("shelfRenderer")
                    .getJSONObject("content")
                    .getJSONObject("horizontalListRenderer");

            for(int j = 0; shelfRenderer.getJSONArray("items").length() > j; j++){
                items.put(shelfRenderer.getJSONArray("items").getJSONObject(j));
            }
        }
        return items;
    }

    @Override
    protected JSONArray buildContinuationUrl(String continuation) throws Exception {
        String data = "{" +
                        "\"continuation\": \"" + continuation + "\"," +
                        "\"context\": {" +
                            "\"client\": {" +
                                "\"visitorData\": \"" + visitorData + "\"" +
                            "}" +
                        "}" +
                    "}";
        return extractVideos(innerTube.browse(new JSONObject(data)));
    }

    private JSONArray extractHomePage(JSONObject rawJson){
        JSONArray items = new JSONArray();
        try{
            JSONArray contents = getActiveTab(rawJson)
                    .getJSONObject("tabRenderer")
                    .getJSONObject("content")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents");

            for(int i = 0; i < contents.length(); i ++){
                JSONObject itemSectionRenderer = contents.getJSONObject(i)
                        .getJSONObject("itemSectionRenderer")
                        .getJSONArray("contents")
                        .getJSONObject(0);

                // Skip the presentation videos for non-subscribers
                if(itemSectionRenderer.has("channelVideoPlayerRenderer")){
                    continue;
                }

                // Skip presentation videos for subscribers
                if(itemSectionRenderer.has("channelFeaturedContentRenderer")){
                    continue;
                }

                // Skip the list with channel members
                if(itemSectionRenderer.has("recognitionShelfRenderer")){
                    continue;
                }

                // Get the horizontal shorts
                if(itemSectionRenderer.has("reelShelfRenderer")){
                    for(int k = 0; k < itemSectionRenderer.length(); k ++){
                        items.put(itemSectionRenderer.getJSONObject("reelShelfRenderer")
                                .getJSONArray("items"));
                    }
                }

                // Get videos, playlist and horizontal channels
                if(itemSectionRenderer.has("shelfRenderer")){
                    if(itemSectionRenderer
                            .getJSONObject("shelfRenderer")
                            .getJSONObject("content")
                            .has("horizontalListRenderer")){

                        JSONArray obj = itemSectionRenderer.getJSONObject("shelfRenderer")
                                .getJSONObject("content")
                                .getJSONObject("horizontalListRenderer")
                                .getJSONArray("items");

                        for(int j = 0; obj.length() > j; j++){
                            items.put(obj.getJSONObject(j));
                        }
                    }

                }
            }
        }catch (Exception e){
            return new JSONArray();
        }
        return items;
    }

    @Override
    protected JSONArray extractVideos(JSONObject rawJson){
        JSONArray swap = new JSONArray();
        try {
            JSONArray importantContent = new JSONArray();
            if(rawJson.has("contents")) {
                JSONObject activeTab = getActiveTab(rawJson);

                visitorData = rawJson.getJSONObject("responseContext")
                        .getJSONObject("webResponseContextExtensionData")
                        .getJSONObject("ytConfigData")
                        .getString("visitorData");

                importantContent = getImportantContent(activeTab);

            }else if (rawJson.has("onResponseReceivedActions")){
                importantContent = rawJson.getJSONArray("onResponseReceivedActions")
                        .getJSONObject(0)
                        .getJSONObject("appendContinuationItemsAction")
                        .getJSONArray("continuationItems");

            }else if (attempts < 3){
                    // YouTube is blocking very quick requests for shorts, so we wait and repeat the request
                    Thread.sleep(1000);
                    swap = extractContinuationItems(importantContent);
                    attempts += 1;
            }

            if(importantContent.getJSONObject(importantContent.length() - 1).has("continuationItemRenderer")){
                setContinuationToken(importantContent);
                swap = extractContinuationItems(importantContent);
            } else {
                for(int i = 0; i < importantContent.length(); i++){
                    swap.put(importantContent.get(i));
                }
            }

        } catch (Exception ignored) {
        }
        return swap;
    }

    public ArrayList<String> getHome() throws Exception {
        setHtmlUrl(featuredUrl);
        return extractId(extractHomePage(getJson()));
    }

    @Override
    public ArrayList<String> getVideos() throws Exception {
        setHtmlUrl(videosUrl);
        return extractId(extractVideos(getJson()));
    }

    public ArrayList<String> getShorts() throws Exception {
        setHtmlUrl(shortsUrl);
        return extractId(extractVideos(getJson()));
    }

    public ArrayList<String> getLives() throws Exception {
        setHtmlUrl(streamsUrl);
        return extractId(extractVideos(getJson()));
    }

    public ArrayList<String> getReleases() throws Exception {
        setHtmlUrl(releasesUrl);
        return extractId(extractVideos(getJson()));
    }

    public ArrayList<String> getPlaylists() throws Exception {
        setHtmlUrl(playlistUrl);
        return extractId(extractVideos(getJson()));
    }

    private ArrayList<String> extractId(JSONArray obj) throws Exception {
        ArrayList<String> objId = new ArrayList<>();

        for(int i = 0; i < obj.length(); i++){
            try {
                if(!obj.getJSONObject(i).has("continuationItemRenderer")) {
                    objId.add(getVideoId(obj.getJSONObject(i)));
                }
            }catch (JSONException ignored){
            }
        }
        return unify(objId);
    }

    private String getVideoId(JSONObject ids) throws JSONException{
        try{
            return "https://www.youtube.com/watch?v=" +  ids.getJSONObject("richItemRenderer")
                    .getJSONObject("content")
                    .getJSONObject("videoRenderer")
                    .getString("videoId");
        }catch (JSONException e){
            return getShortId(ids);
        }
    }

    private String getShortId(JSONObject ids) throws JSONException{
        try{
            return "https://www.youtube.com/watch?v=" +  ids.getJSONObject("richItemRenderer")
                    .getJSONObject("content")
                    .getJSONObject("reelItemRenderer")
                    .getString("videoId");
        }catch (JSONException e){
            return getReleasesId(ids);
        }
    }

    private String getReleasesId(JSONObject ids) throws JSONException{
        try{
            return "https://www.youtube.com/playlist?list=" + ids.getJSONObject("richItemRenderer")
                    .getJSONObject("content")
                    .getJSONObject("playlistRenderer")
                    .getString("playlistId");
        }catch (JSONException e){
            return getVideoIdFromHome(ids);
        }
    }

    private String getVideoIdFromHome(JSONObject ids) throws JSONException {
        try{
            return "https://www.youtube.com/watch?v=" + ids.getJSONObject("gridVideoRenderer")
                    .getString("videoId");
        }catch (JSONException e){
            return getPlaylistId(ids);
        }
    }

    private String getPlaylistId(JSONObject ids) throws JSONException{
        try{
            return "https://www.youtube.com/playlist?list=" + ids.getJSONObject("gridPlaylistRenderer")
                    .getString("playlistId");
        }catch (JSONException e){
            return getChannelIdFromHome(ids);
        }
    }

    private String getChannelIdFromHome(JSONObject ids) throws JSONException{
        try{
            return "https://www.youtube.com/playlist?list=" + ids.getJSONObject("gridChannelRenderer")
                    .getString("channelId");
        }catch (JSONException e){
            return getCompactStationRenderer(ids);
        }
    }

    private String getCompactStationRenderer(JSONObject ids) throws JSONException{
        try{
            return "https://www.youtube.com/playlist?list=" + ids.getJSONObject("compactStationRenderer")
                    .getJSONObject("navigationEndpoint")
                    .getJSONObject("watchPlaylistEndpoint")
                    .getString("playlistId");
        }catch (JSONException e){
            throw new JSONException(e);
        }
    }

    @Override
    public String getUrl() {
        return channelUrl;
    }

    @Override
    public String getTitle() throws Exception {
        return getChannelName();
    }

    @Override
    public String getLastUpdated() throws Exception {
        setHtmlUrl(videosUrl);
        JSONObject lastVideoContent;
        try{
             lastVideoContent = getActiveTab(getJson()).getJSONObject("tabRenderer")
                    .getJSONObject("content")
                    .getJSONObject("richGridRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("richItemRenderer")
                    .getJSONObject("content")
                    .getJSONObject("videoRenderer");
        }catch (JSONException e){
            return null;
        }
        String videoId = lastVideoContent.getString("videoId");
        JSONObject response = new InnerTube("WEB").player(videoId);
        try {
            return response.getJSONObject("microformat")
                    .getJSONObject("playerMicroformatRenderer")
                    .getString("publishDate");

        }catch (JSONException j){
            return lastVideoContent.getJSONObject("publishedTimeText")
                    .getString("simpleText");
        }
    }

    @Override
    public String length() throws Exception {
        setHtmlUrl(getChannelUrl());
        try {
            JSONObject header = getJson().getJSONObject("header");

            if(header.has("c4TabbedHeaderRenderer")){
                return header.getJSONObject("c4TabbedHeaderRenderer")
                        .getJSONObject("videosCountText")
                        .getJSONArray("runs")
                        .getJSONObject(0)
                        .getString("text");
            }else {
                return header.getJSONObject("pageHeaderRenderer")
                        .getJSONObject("content")
                        .getJSONObject("pageHeaderViewModel")
                        .getJSONObject("metadata")
                        .getJSONObject("contentMetadataViewModel")
                        .getJSONArray("metadataRows")
                        .getJSONObject(1)
                        .getJSONArray("metadataParts")
                        .getJSONObject(1)
                        .getJSONObject("text")
                        .getString("content");
            }
        }catch (JSONException j){
            return null;
        }

    }

    public String getChannelName() throws Exception {
        setHtmlUrl(channelUrl);
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("title");
    }

    public String getChannelId() throws Exception {
        setHtmlUrl(channelUrl);
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("externalId");
    }

    public String getVanityUrl() throws Exception {
        setHtmlUrl(channelUrl);
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("vanityChannelUrl");
    }

    @Override
    public String getDescription() throws Exception {
        setHtmlUrl(channelUrl);
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("description");
    }

    public String getSubscribers() throws Exception {
        setHtmlUrl(getChannelUrl());
        try{
            JSONObject header = getJson().getJSONObject("header");

            if(header.has("c4TabbedHeaderRenderer")){
                return getJson().getJSONObject("header")
                        .getJSONObject("c4TabbedHeaderRenderer")
                        .getJSONObject("subscriberCountText")
                        .getString("simpleText");
            }else {
                return header.getJSONObject("pageHeaderRenderer")
                        .getJSONObject("content")
                        .getJSONObject("pageHeaderViewModel")
                        .getJSONObject("metadata")
                        .getJSONObject("contentMetadataViewModel")
                        .getJSONArray("metadataRows")
                        .getJSONObject(1)
                        .getJSONArray("metadataParts")
                        .getJSONObject(0)
                        .getJSONObject("text")
                        .getString("content");
            }

        }catch (JSONException e){
            return null;
        }
    }

    @Deprecated
    public String getBiography() throws Exception {
        setHtmlUrl(aboutUrl);
        try {
            return getActiveTab(getJson()).getJSONObject("tabRenderer")
                    .getJSONObject("content")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("itemSectionRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("channelAboutFullMetadataRenderer")
                    .getJSONObject("artistBio")
                    .getString("simpleText");
        }catch (JSONException e){
            return null;
        }
    }

    @Override
    public String getViews() throws Exception {
        setHtmlUrl(aboutUrl);
        try {
            return getJson().getJSONArray("onResponseReceivedEndpoints")
                    .getJSONObject(0)
                    .getJSONObject("showEngagementPanelEndpoint")
                    .getJSONObject("engagementPanel")
                    .getJSONObject("engagementPanelSectionListRenderer")
                    .getJSONObject("content")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("itemSectionRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("aboutChannelRenderer")
                    .getJSONObject("metadata")
                    .getJSONObject("aboutChannelViewModel")
                    .getString("viewCountText");
        }catch (JSONException e){
            return null;
        }
    }

    public String getKeywords() throws Exception {
        setHtmlUrl(channelUrl);
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("keywords");
    }

    public JSONArray getAvailableCountryCodes() throws Exception {
        setHtmlUrl(channelUrl);
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getJSONArray("availableCountryCodes");
    }

    public String getThumbnailUrl() throws Exception {
        setHtmlUrl(channelUrl);
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getJSONObject("avatar")
                .getJSONArray("thumbnails")
                .getJSONObject(0)
                .getString("url");
    }

    public String getChannelUrl(){
        return channelUrl;
    }
    public String getVideosUrl(){
        return videosUrl;
    }

    public String getShortsUrl(){
        return shortsUrl;
    }

    public String getStreamsUrl(){
        return streamsUrl;
    }

    public String getReleasesUrl(){
        return releasesUrl;
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

}