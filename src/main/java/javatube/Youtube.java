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
        title = title();

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

    public String title() throws Exception {
        return String.valueOf(vidInfo().getJSONObject("videoDetails").getString("title"));
    }

    public JSONObject vidInfo() throws Exception {
        return new JSONObject(InnerTube.post(videoId()));
    }

    public JSONObject streamData() throws Exception {
        return vidInfo().getJSONObject("streamingData");
    }

    public ArrayList<Stream> fmtStreams() throws Exception {

        JSONArray streamManifest = applyDescrambler(streamData());

        ArrayList<Stream> fmtStream = new ArrayList<>();

        Stream video;
        for (int i = 0; streamManifest.length() > i; i++) {
            video = new Stream(streamManifest.getJSONObject(i), title());
            fmtStream.add(video);
        }

        return fmtStream;
    }

    public StreamQuery streams() throws Exception {
        return new StreamQuery(fmtStreams());
    }
}