package javatube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Channel extends Playlist{

    private final String videosUrl;
    private final String playlistUrl;
    private final String communityUrl;
    private final String featuredChannelUrl;
    private final String aboutUrl;
    private final String url;

    public Channel(String inputUrl) throws Exception {
        super(inputUrl);
        url = inputUrl;
        String channelUrl = "https://www.youtube.com" + extractUrl();

        videosUrl = channelUrl + "/videos";
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

    @Override
    public String getHtml() throws IOException {
        return InnerTube.downloadWebPage(getVideosUrl());
    }

    @Override
    public JSONArray extractVideos(JSONObject rawJson){
        try {
            JSONArray importantContent;
            try {
                JSONObject tabs = new JSONObject(rawJson.getJSONObject("contents").getJSONObject("twoColumnBrowseResultsRenderer").getJSONArray("tabs").get(1).toString());
                try{
                    JSONObject contents = new JSONObject(tabs.getJSONObject("tabRenderer").getJSONObject("content").getJSONObject("sectionListRenderer").getJSONArray("contents").get(0).toString());
                    importantContent = new JSONArray(new JSONObject(contents.getJSONObject("itemSectionRenderer").getJSONArray("contents").get(0).toString()).getJSONObject("gridRenderer").getJSONArray("items"));
                }catch (Exception e){
                    importantContent = new JSONArray(tabs.getJSONObject("tabRenderer").getJSONObject("content").getJSONObject("richGridRenderer").getJSONArray("contents"));
                }
            }catch (JSONException e){
                importantContent = new JSONArray(new JSONObject(rawJson.getJSONArray("onResponseReceivedActions").get(0).toString()).getJSONObject("appendContinuationItemsAction").getJSONArray("continuationItems"));
            }
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

        }catch (Exception e){
            throw new Error(e);
        }
    }

    public String getChannelName() throws Exception {
        return new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getString("title");
    }

    public String getChannelId() throws Exception {
        return new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getString("externalId");
    }

    public String getVanityUrl() throws Exception {
        return new JSONObject(getJson().toString()).getJSONObject("metadata").getJSONObject("channelMetadataRenderer").getString("vanityChannelUrl");
    }

    public String getVideosUrl(){
        return videosUrl;
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
}