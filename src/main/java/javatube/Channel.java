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

    private final String channelUrl;
    private final String videosUrl;
    private final String shortsUrl;
    private final String streamsUrl;
    private final String playlistUrl;
    private final String communityUrl;
    private final String featuredChannelUrl;
    private final String aboutUrl;
    private final String url;
    private String htmlPage;
    private String visitorData;

    public Channel(String inputUrl) throws Exception {
        super(inputUrl);
        url = inputUrl;
        channelUrl = "https://www.youtube.com" + extractUrl();

        videosUrl = channelUrl + "/videos";
        shortsUrl = channelUrl + "/shorts";
        streamsUrl = channelUrl + "/streams";
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
        throw new Exception("RegexMatcherError");
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
    protected String baseData(String continuation){
        return "{\"continuation\": \"" + continuation + "\", \"context\": {\"client\": {\"clientName\": \"WEB\",  \"visitorData\": \"" + visitorData + "\", \"clientVersion\": \"2.20221107.06.00\"}}}";
    }

    @Override
    protected String setHtml() throws IOException {
        return InnerTube.downloadWebPage(getHtmlUrl());
    }

    @Override
    protected JSONArray extractVideos(JSONObject rawJson){
        JSONArray swap = new JSONArray();
        try {
            JSONArray importantContent;
            try {
                JSONObject activeTab = new JSONObject();
                for(Object tab : rawJson.getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs")) {
                    String tabUrl = new JSONObject(tab.toString()).getJSONObject("tabRenderer")
                            .getJSONObject("endpoint")
                            .getJSONObject("commandMetadata")
                            .getJSONObject("webCommandMetadata")
                            .getString("url");
                    if (tabUrl.substring(tabUrl.lastIndexOf("/") + 1).equals(getHtmlUrl().substring(getHtmlUrl().lastIndexOf("/") + 1))) {
                        activeTab = new JSONObject(tab.toString());
                        break;
                    }
                }

                visitorData = rawJson.getJSONObject("responseContext")
                        .getJSONObject("webResponseContextExtensionData")
                        .getJSONObject("ytConfigData")
                        .getString("visitorData");

                importantContent = activeTab.getJSONObject("tabRenderer")
                        .getJSONObject("content")
                        .getJSONObject("richGridRenderer")
                        .getJSONArray("contents");

            }catch (JSONException e){
                importantContent = rawJson.getJSONArray("onResponseReceivedActions")
                        .getJSONObject(0)
                        .getJSONObject("appendContinuationItemsAction")
                        .getJSONArray("continuationItems");
            }

            try{
                String continuation = importantContent.getJSONObject(importantContent.length() - 1)
                        .getJSONObject("continuationItemRenderer")
                        .getJSONObject("continuationEndpoint")
                        .getJSONObject("continuationCommand")
                        .getString("token");
                JSONArray continuationEnd = new JSONArray(buildContinuationUrl(continuation));

                for(int i = 0; i < importantContent.length(); i++){
                    swap.put(importantContent.get(i));
                }

                if (!continuationEnd.isEmpty()){
                    for(int i = 0; i < continuationEnd.length(); i++){
                        swap.put(continuationEnd.get(i));
                    }
                }

            } catch (JSONException e) {
                for(int i = 0; i < importantContent.length(); i++){
                    swap.put(importantContent.get(i));
                }
            }

        } catch (Exception ignored) {
        }
        return swap;
    }

    public ArrayList<String> getVideos() throws Exception {
        setHtmlUrl(getVideosUrl());
        return extractVideosId();
    }

    public ArrayList<String> getShorts() throws Exception {
        setHtmlUrl(getShortsUrl());
        return extractVideosId();
    }

    public ArrayList<String> getLives() throws Exception {
        setHtmlUrl(getStreamsUrl());
        return extractVideosId();
    }

    private ArrayList<String> extractVideosId() throws Exception {
        JSONArray video = extractVideos(getJson());
        ArrayList<String> videosId = new ArrayList<>();
        try {
            for(int i = 0; i < video.length(); i++){
                try{
                    try {
                        videosId.add("https://www.youtube.com/watch?v=" + video.getJSONObject(i)
                                .getJSONObject("richItemRenderer")
                                .getJSONObject("content")
                                .getJSONObject("videoRenderer")
                                .getString("videoId"));
                    }catch (JSONException s){
                        videosId.add("https://www.youtube.com/watch?v=" + video.getJSONObject(i)
                                .getJSONObject("richItemRenderer")
                                .getJSONObject("content")
                                .getJSONObject("reelItemRenderer")
                                .getString("videoId"));
                    }
                }catch (Exception ignored){
                }
            }
            return videosId;
        } catch (JSONException e) {
            throw new Error(e);
        }
    }

    @Override
    public String getTitle() throws Exception {
        return getChannelName();
    }

    @Override
    public String getLastUpdated() throws Exception {
        setHtmlUrl(getVideosUrl());
        try {
            return getJson().getJSONObject("contents")
                    .getJSONObject("twoColumnBrowseResultsRenderer")
                    .getJSONArray("tabs")
                    .getJSONObject(1)
                    .getJSONObject("tabRenderer")
                    .getJSONObject("content")
                    .getJSONObject("richGridRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("richItemRenderer")
                    .getJSONObject("content")
                    .getJSONObject("videoRenderer")
                    .getJSONObject("publishedTimeText")
                    .getString("simpleText");
        }catch (JSONException j){
            return null;
        }

    }

    @Override
    public String length() throws Exception {
        setHtmlUrl(getChannelUrl());
        return getJson().getJSONObject("header")
                .getJSONObject("c4TabbedHeaderRenderer")
                .getJSONObject("videosCountText")
                .getJSONArray("runs")
                .getJSONObject(0)
                .getString("text");
    }


    public String getChannelName() throws Exception {
        setHtmlUrl(getChannelUrl());
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("title");
    }

    public String getChannelId() throws Exception {
        setHtmlUrl(getChannelUrl());
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("externalId");
    }

    public String getVanityUrl() throws Exception {
        setHtmlUrl(getChannelUrl());
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("vanityChannelUrl");
    }

    @Override
    public String getDescription() throws Exception {
        setHtmlUrl(getChannelUrl());
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("description");
    }

    public String getBiography() throws Exception {
        setHtmlUrl(getAboutUrl());
        int pos = getJson().getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs").length() - 2;
        try {
            return getJson().getJSONObject("contents")
                    .getJSONObject("twoColumnBrowseResultsRenderer")
                    .getJSONArray("tabs")
                    .getJSONObject(pos)
                    .getJSONObject("tabRenderer")
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
        setHtmlUrl(getAboutUrl());
        int pos = getJson().getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs").length() - 2;
        try {
            return getJson().getJSONObject("contents")
                    .getJSONObject("twoColumnBrowseResultsRenderer")
                    .getJSONArray("tabs")
                    .getJSONObject(pos)
                    .getJSONObject("tabRenderer")
                    .getJSONObject("content")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("itemSectionRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("channelAboutFullMetadataRenderer")
                    .getJSONObject("viewCountText")
                    .getString("simpleText");
        }catch (JSONException e){
            return null;
        }
    }

    public String getKeywords() throws Exception {
        setHtmlUrl(getChannelUrl());
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getString("keywords");
    }

    public JSONArray getAvailableCountryCodes() throws Exception {
        setHtmlUrl(getChannelUrl());
        return getJson().getJSONObject("metadata")
                .getJSONObject("channelMetadataRenderer")
                .getJSONArray("availableCountryCodes");
    }

    public String getThumbnailUrl() throws Exception {
        setHtmlUrl(getChannelUrl());
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