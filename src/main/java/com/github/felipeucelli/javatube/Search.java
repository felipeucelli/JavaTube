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
    private Filter filter = null;
    private JSONObject jsonResult = null;
    private final InnerTube innerTubeClient = new InnerTube("WEB");
    private Map<String, ArrayList<String>> results = new HashMap<>();
    private String continuationToken = "";

    public Search(String query) throws JSONException {
        this.query = query;
    }

    public Search(String query, Filter filter) throws JSONException {
        this.query = query;
        this.filter = filter;
    }

    public static class Filter {

        public static Filter create(){
            return new Filter();
        }

        private Map<Integer, Map<Integer, Integer>> TYPE;
        private Map<Integer, Map<Integer, Integer>> UPLOAD_DATA;
        private Map<Integer, Map<Integer, Integer>> DURATION;
        private List<Map<Integer, Map<Integer, Integer>>> FEATURE = new ArrayList<>();
        private Map<Integer, Integer> SORTBY;

        public enum Type{
            VIDEO(Map.of(2, Map.of(2, 1))),
            CHANNEL(Map.of(2, Map.of(2, 2))),
            PLAYLIST(Map.of(2, Map.of(2, 3))),
            MOVIE(Map.of(2, Map.of(2, 4)));

            public final Map<Integer, Map<Integer, Integer>> label;
            Type(Map<Integer, Map<Integer, Integer>> label) { this.label = label; }
        }

        public enum UploadData{
            LAST_HOUR(Map.of(2, Map.of(1, 1))),
            TODAY(Map.of(2, Map.of(1, 2))),
            THIS_WEEK(Map.of(2, Map.of(1, 3))),
            THIS_MONTH(Map.of(2, Map.of(1, 4))),
            THIS_YEAR(Map.of(2, Map.of(1, 5)));

            public final Map<Integer, Map<Integer, Integer>> label;
            UploadData(Map<Integer, Map<Integer, Integer>> label) { this.label = label; }
        }

        public enum Duration{
            UNDER_4_MIN(Map.of(2, Map.of(3, 1))),
            OVER_20_MIN(Map.of(2, Map.of(3, 2))),
            BETWEEN_4_20(Map.of(2, Map.of(3, 3)));

            public final Map<Integer, Map<Integer, Integer>> label;
            Duration(Map<Integer, Map<Integer, Integer>> label) { this.label = label; }
        }

        public enum Feature{
            LIVE(Map.of(2, Map.of(8, 1))),
            _4K(Map.of(2, Map.of(14, 1))),
            HD(Map.of(2, Map.of(4, 1))),
            SUBTITLES(Map.of(2, Map.of(5, 1))),
            CREATIVE_COMMONS(Map.of(2, Map.of(6, 1))),
            _360(Map.of(2, Map.of(15, 1))),
            VR180(Map.of(2, Map.of(26, 1))),
            _3D(Map.of(2, Map.of(7, 1))),
            HDR(Map.of(2, Map.of(25, 1))),
            LOCATION(Map.of(2, Map.of(23, 1))),
            PURCHASED(Map.of(2, Map.of(9, 1)));

            public final Map<Integer, Map<Integer, Integer>> label;
            Feature(Map<Integer, Map<Integer, Integer>> label) { this.label = label; }
        }

        public enum SortBy{
            RELEVANCE(Map.of(1, 0)),
            UPLOAD_DATE(Map.of(1, 2)),
            VIEW_COUNT(Map.of(1, 3)),
            RATING(Map.of(1, 1));

            public final Map<Integer, Integer> label;
            SortBy(Map<Integer, Integer> label) { this.label = label; }
        }

        public Filter type(Filter.Type type) { this.TYPE = type.label; return this; }
        public Filter uploadData(Filter.UploadData uploadData) { this.UPLOAD_DATA = uploadData.label; return this; }
        public Filter duration(Filter.Duration duration) { this.DURATION = duration.label; return this; }
        public Filter feature(Filter.Feature feature) { this.FEATURE.add(feature.label); return this; }
        public Filter feature(List<Filter.Feature> feature) {
            for (Filter.Feature f : feature){
                this.FEATURE.add(f.label);
            }
            return this;
        }
        public Filter sortBy(Filter.SortBy sortBy) { this.SORTBY = sortBy.label; return this; }

        String buildFilter() {
            Map<Integer, Object> result = new LinkedHashMap<>();

            if (SORTBY != null) {
                result.putAll(SORTBY);
            }

            Map<Integer, Integer> mergedInner = new LinkedHashMap<>();

            if (TYPE != null) TYPE.forEach((k, v) -> mergedInner.putAll(v));
            if (UPLOAD_DATA != null) UPLOAD_DATA.forEach((k, v) -> mergedInner.putAll(v));
            if (DURATION != null) DURATION.forEach((k, v) -> mergedInner.putAll(v));
            for (Map<Integer, Map<Integer, Integer>> f : FEATURE) {
                f.forEach((k, v) -> mergedInner.putAll(v));
            }

            if (!mergedInner.isEmpty()) {
                result.put(2, mergedInner);
            }

            return new JSONObject(result).toString().replace("\"", "");
        }
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
        JSONObject data = new JSONObject();
        if (filter != null && Objects.equals(continuationToken, "")){
            data.put("params", Protobuf.encodeProtobuf(filter.buildFilter()));
        }
        if (!Objects.equals(continuationToken, "")){
            data.put("continuation", continuationToken);
        }
        return innerTubeClient.search(safeQuery(), data);
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
            String vidId;
            if (items.getJSONObject(i).has("reelItemRenderer")){
                vidId = items.getJSONObject(i)
                        .getJSONObject("reelItemRenderer")
                        .getString("videoId");
            }else {
                vidId = items.getJSONObject(i)
                        .getJSONObject("shortsLockupViewModel")
                        .getJSONObject("onTap")
                        .getJSONObject("innertubeCommand")
                        .getJSONObject("reelWatchEndpoint")
                        .getString("videoId");
            }

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

            // Get videos results
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

                // Get shorts results
            } else if (rawVideoList.getJSONObject(i).has("reelShelfRenderer")) {
                shorts.addAll(extractReelShelfRenderer(rawVideoList.getJSONObject(i)
                        .getJSONObject("reelShelfRenderer")
                        .getJSONArray("items")));

                // Get channel results
            } else if (rawVideoList.getJSONObject(i).has("channelRenderer")) {
                String channelId = rawVideoList.getJSONObject(i).getJSONObject("channelRenderer")
                        .getString("channelId");
                channel.add("https://www.youtube.com/channel/" + channelId);

                // Get playlist results
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