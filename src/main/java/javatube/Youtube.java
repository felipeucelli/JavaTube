package javatube;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;
import org.json.*;


public class Youtube {
    private final String urlVideo;
    private final String watchUrl;

    public Youtube(String url) throws Exception {
        urlVideo = url;
        watchUrl = "https://www.youtube.com/watch?v=" + videoId();
    }

    private String videoId() throws Exception {
        Pattern pattern = Pattern.compile("(?:v=|/)([0-9A-Za-z_-]{11}).*");
        Matcher matcher = pattern.matcher(urlVideo);
        if (matcher.find()) {
            return matcher.group(1);
        }else {
            throw new Exception("RegexMatcherError");
        }
    }
    private String getHtml() throws Exception {
        return InnerTube.downloadWebPage(watchUrl);
    }

    private static JSONArray applyDescrambler(JSONObject streamData){
        JSONArray formats = new JSONArray();
        for(int i = 0; streamData.getJSONArray("formats").length() > i; i++){
            formats.put(streamData.getJSONArray("formats").get(i));
        }
        for(int i = 0; streamData.getJSONArray("adaptiveFormats").length() > i; i++){
            formats.put(streamData.getJSONArray("adaptiveFormats").get(i));
        }
        for(int i = 0; i < formats.length(); i++){
            if(formats.getJSONObject(i).has("signatureCipher")){
                String rawSig = formats.getJSONObject(i).getString("signatureCipher").replace("sp=sig", "");
                for(int j = 0; j < rawSig.split("&").length; j++){
                    if(Arrays.asList(rawSig.split("&")).get(j).startsWith("url")){
                        formats.getJSONObject(i).put("url", Arrays.asList(rawSig.split("&")).get(j).replace("url=", ""));
                    }else if(Arrays.asList(rawSig.split("&")).get(j).startsWith("s")){
                        formats.getJSONObject(i).put("s", Arrays.asList(rawSig.split("&")).get(j).replace("s=", ""));
                    }
                }
            }
        }
        return formats;
    }

    private JSONObject vidInfo() throws Exception {
        String pattern = "ytInitialPlayerResponse\\s=\\s(\\{\\\"responseContext\\\":.*?\\});</script>";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(getHtml());
        String s = "";
        if(matcher.find()){
            s = matcher.group(1).replaceAll(";</script>.*", "");
        }else {
            throw new Exception("RegexMatcherError");
        }
        return new JSONObject(s);
    }

    private JSONObject streamData() throws Exception {
        return vidInfo().getJSONObject("streamingData");
    }

    private String decodeURL(String s){
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
    private ArrayList<Stream> fmtStreams() throws Exception {

        JSONArray streamManifest = applyDescrambler(streamData());

        ArrayList<Stream> fmtStream = new ArrayList<>();
        String title = getTitle();
        Stream video;
        Cipher cipher = new Cipher(getJs());
        for (int i = 0; streamManifest.length() > i; i++) {
            if(streamManifest.getJSONObject(i).has("signatureCipher")){
                String oldUrl = decodeURL(streamManifest.getJSONObject(i).getString("url"));
                streamManifest.getJSONObject(i).remove("url");
                streamManifest.getJSONObject(i).put("url", oldUrl + "&sig=" + cipher.getSignature(decodeURL(streamManifest.getJSONObject(i).getString("s")).split("")));
            }
            video = new Stream(streamManifest.getJSONObject(i), title);
            fmtStream.add(video);
        }
        return fmtStream;
    }

    private String getYtPlayerJs(String html) throws Exception {
        Pattern pattern = Pattern.compile("(/s/player/[\\w\\d]+/[\\w\\d_/.]+/base\\.js)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return "https://youtube.com" + matcher.group(1);
        }else {
            throw new Exception("RegexMatcherError");
        }
    }
    private String getJsUrl() throws Exception {
        return InnerTube.downloadWebPage(getYtPlayerJs(getHtml()));
    }

    public String getJs() throws Exception {
        return getJsUrl();
    }

    public String getTitle() throws Exception {
        return vidInfo().getJSONObject("videoDetails").getString("title");
    }

    public String getDescription() throws Exception {
        return vidInfo().getJSONObject("videoDetails").getString("shortDescription");
    }

    public String getPublishDate() throws Exception {
        Pattern pattern = Pattern.compile("(?<=itemprop=\"datePublished\" content=\")\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(getHtml());
        if (matcher.find()) {
            return matcher.group(0);
        }else {
            throw new Exception("RegexMatcherError");
        }
    }

    public Integer length() throws Exception {
        return vidInfo().getJSONObject("videoDetails").getInt("lengthSeconds");
    }

    public String getThumbnailUrl() throws Exception {
        JSONArray thumbnails = vidInfo().getJSONObject("videoDetails").getJSONObject("thumbnail").getJSONArray("thumbnails");
        return new JSONObject(thumbnails.get(thumbnails.length() - 1).toString()).getString("url");
    }

    public Integer getViews() throws Exception {
        return Integer.parseInt(vidInfo().getJSONObject("videoDetails").getString("viewCount"));
    }

    public String getAuthor() throws Exception {
        return vidInfo().getJSONObject("videoDetails").getString("author");
    }

    public ArrayList<Captions> getCaptionTracks() throws Exception {
        JSONArray rawTracks = vidInfo().getJSONObject("captions").getJSONObject("playerCaptionsTracklistRenderer").getJSONArray("captionTracks");
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
        try {
            return vidInfo().getJSONObject("videoDetails").getJSONArray("keywords");
        }catch (Exception e){
            return null;
        }
    }

    public StreamQuery streams() throws Exception {
        return new StreamQuery(fmtStreams());
    }
}