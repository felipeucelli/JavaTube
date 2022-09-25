package javatube;

import java.util.*;
import java.util.regex.*;
import org.json.*;


public class Youtube {
    public String urlVideo;
    public String watchUrl;
    public String title;

    public Youtube(String url) throws Exception {

        urlVideo = url;
        watchUrl = "https://www.youtube.com/watch?v=" + videoId();
        title = getTitle();
    }

    public String videoId() throws Exception {
        Pattern pattern = Pattern.compile("(?:v=|/)([0-9A-Za-z_-]{11}).*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlVideo);
        if (matcher.find()) {
            return matcher.group(1);
        }else {
            throw new Exception("RegexMatcherError");
        }

    }

    private String baseData(){
        return "{\"context\": {\"client\": {\"clientName\": \"ANDROID\", \"clientVersion\": \"16.20\"}}}";
    }

    private String baseParam() throws Exception {
        return "https://www.youtube.com/youtubei/v1/player?videoId=" + videoId() + "&key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8&contentCheckOk=True&racyCheckOk=True";
    }

    private static JSONArray applyDescrambler(JSONObject streamData){
        JSONArray formats = new JSONArray();
        for(int i = 0; streamData.getJSONArray("formats").length() > i; i++){
            formats.put(streamData.getJSONArray("formats").get(i));
        }
        for(int i = 0; streamData.getJSONArray("adaptiveFormats").length() > i; i++){
            formats.put(streamData.getJSONArray("adaptiveFormats").get(i));
        }
        return formats;
    }

    private JSONObject vidInfo() throws Exception {
        return new JSONObject(InnerTube.post(baseParam(), baseData()));
    }

    private JSONObject streamData() throws Exception {
        return vidInfo().getJSONObject("streamingData");
    }

    private ArrayList<Stream> fmtStreams() throws Exception {

        JSONArray streamManifest = applyDescrambler(streamData());

        ArrayList<Stream> fmtStream = new ArrayList<>();

        Stream video;
        for (int i = 0; streamManifest.length() > i; i++) {
            video = new Stream(streamManifest.getJSONObject(i), getTitle());
            fmtStream.add(video);
        }

        return fmtStream;
    }

    public String getTitle() throws Exception {
        return String.valueOf(vidInfo().getJSONObject("videoDetails").getString("title"));
    }

    public String getDescription() throws Exception {
        return vidInfo().getJSONObject("videoDetails").getString("shortDescription");
    }

    public Integer length() throws Exception {
        return vidInfo().getJSONObject("videoDetails").getInt("lengthSeconds");
    }

    public String getThumbnailUrl() throws Exception {
        JSONArray thumbnails = new JSONArray(vidInfo().getJSONObject("videoDetails").getJSONObject("thumbnail").getJSONArray("thumbnails"));
        return new JSONObject(thumbnails.get(thumbnails.length() - 1).toString()).getString("url");
    }

    public Integer getViews() throws Exception {
        return Integer.parseInt(vidInfo().getJSONObject("videoDetails").getString("viewCount"));
    }

    public String getAuthor() throws Exception {
        return vidInfo().getJSONObject("videoDetails").getString("author");
    }

    public ArrayList<Captions> getCaptionTracks() throws Exception {
        JSONArray rawTracks = new JSONArray(vidInfo().getJSONObject("captions").getJSONObject("playerCaptionsTracklistRenderer").getJSONArray("captionTracks"));
        ArrayList<Captions> captions = new ArrayList<>();
        for(int i = 0; i < rawTracks.length() - 1; i++){
           captions.add(new Captions(new JSONObject(rawTracks.get(i).toString())));
        }
        return captions;
    }

    public CaptionQuery getCaptions() throws Exception {
        return new CaptionQuery(getCaptionTracks());
    }

    public JSONArray getKeywords() throws Exception {
        return vidInfo().getJSONObject("videoDetails").getJSONArray("keywords");
    }

    public StreamQuery streams() throws Exception {
        return new StreamQuery(fmtStreams());
    }
}