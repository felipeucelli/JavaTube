package javatube;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Search {

    private final String query;

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

    private String fetchQuery() throws IOException {
        return InnerTube.post(baseParam(), baseData());
    }

    private ArrayList<Youtube> fetchAndParse() throws Exception {
        JSONObject rawResults = new JSONObject(fetchQuery());
        JSONObject sections = rawResults.getJSONObject("contents")
                .getJSONObject("twoColumnSearchResultsRenderer")
                .getJSONObject("primaryContents")
                .getJSONObject("sectionListRenderer")
                .getJSONArray("contents")
                .getJSONObject(0);

        JSONArray rawVideoList = new JSONArray(sections.getJSONObject("itemSectionRenderer")
                .getJSONArray("contents"));
        ArrayList<Youtube> videos = new ArrayList<>();
        for(int i = 0; i < rawVideoList.length() - 1; i++) {
            if (!rawVideoList.getJSONObject(i).has("videoRenderer")) {
                continue;
            }
            JSONObject vidRenderer = rawVideoList.getJSONObject(i).getJSONObject("videoRenderer");
            String vidId = vidRenderer.getString("videoId");
            String vidUrl = "https://www.youtube.com/watch?v=" + vidId;
            videos.add(new Youtube(vidUrl));
        }
        return videos;
    }

    public ArrayList<Youtube> results() throws Exception {
        return fetchAndParse();
    }

}