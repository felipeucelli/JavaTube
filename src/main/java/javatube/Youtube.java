package javatube;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;
import org.json.*;


public class Youtube {

    private final String urlVideo;
    private final String watchUrl;
    private JSONObject vidInfo = null;
    private String html = null;
    private String js = null;

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
            throw new Exception("RegexMatcherError: " + pattern);
        }
    }

    private String setHtml() throws IOException {
        return InnerTube.downloadWebPage(watchUrl);
    }

    private String getHtml() throws Exception {
        if(html == null){
            html = setHtml();
        }
        return html;
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

    private JSONObject setVidInfo() throws Exception {
        String pattern = "ytInitialPlayerResponse\\s=\\s(\\{\\\"responseContext\\\":.*?\\});</script>";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(getHtml());
        if(matcher.find()){
           return new JSONObject(matcher.group(1));
        }else {
            throw new Exception("RegexMatcherError: " + pattern);
        }
    }

    private JSONObject getVidInfo() throws Exception {
        if(vidInfo == null){
            vidInfo = setVidInfo();
        }
        return vidInfo;
    }

    private JSONObject streamData() throws Exception {
        return getVidInfo().getJSONObject("streamingData");
    }

    private String decodeURL(String s) throws UnsupportedEncodingException {
        return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
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
                streamManifest.getJSONObject(i).put("url", oldUrl + "&sig=" + cipher.getSignature(decodeURL(streamManifest.getJSONObject(i).getString("s")).split("(?!^)")));
            }

            String oldUrl = streamManifest.getJSONObject(i).getString("url");
            Matcher matcher = Pattern.compile("&n=(.*?)&").matcher(oldUrl);
            if (matcher.find()) {
                String newUrl = oldUrl.replaceFirst("&n=(.*?)&", "&n=" + cipher.calculateN(matcher.group(1)) + "&");
                streamManifest.getJSONObject(i).put("url", newUrl);
            }

            video = new Stream(streamManifest.getJSONObject(i), title);
            fmtStream.add(video);
        }
        return fmtStream;
    }

    private String getYtPlayerJs() throws Exception {
        Pattern pattern = Pattern.compile("(/s/player/[\\w\\d]+/[\\w\\d_/.]+/base\\.js)");
        Matcher matcher = pattern.matcher(getHtml());
        if (matcher.find()) {
            return "https://youtube.com" + matcher.group(1);
        }else {
            throw new Exception("RegexMatcherError: " + pattern);
        }
    }

    private String setJs() throws Exception {
        return InnerTube.downloadWebPage(getYtPlayerJs());
    }
    private String getJs() throws Exception {
        if(js == null){
            js = setJs();
        }
        return js;
    }

    public String getTitle() throws Exception {
        return getVidInfo().getJSONObject("videoDetails")
                .getString("title");
    }

    public String getDescription() throws Exception {
        return getVidInfo().getJSONObject("videoDetails")
                .getString("shortDescription");
    }

    public String getPublishDate() throws Exception {
        Pattern pattern = Pattern.compile("(?<=itemprop=\"datePublished\" content=\")\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(getHtml());
        if (matcher.find()) {
            return matcher.group(0);
        }else {
            throw new Exception("RegexMatcherError: " + pattern);
        }
    }

    public Integer length() throws Exception {
        return getVidInfo().getJSONObject("videoDetails")
                .getInt("lengthSeconds");
    }

    public String getThumbnailUrl() throws Exception {
        JSONArray thumbnails = getVidInfo().getJSONObject("videoDetails")
                .getJSONObject("thumbnail")
                .getJSONArray("thumbnails");
        return thumbnails.getJSONObject(thumbnails.length() - 1).getString("url");
    }

    public Integer getViews() throws Exception {
        return Integer.parseInt(getVidInfo().getJSONObject("videoDetails")
                .getString("viewCount"));
    }

    public String getAuthor() throws Exception {
        return getVidInfo().getJSONObject("videoDetails")
                .getString("author");
    }

    public ArrayList<Captions> getCaptionTracks() throws Exception {
        try{
            JSONArray rawTracks = getVidInfo().getJSONObject("captions")
                    .getJSONObject("playerCaptionsTracklistRenderer")
                    .getJSONArray("captionTracks");
            ArrayList<Captions> captions = new ArrayList<>();
            for(int i = 0; i < rawTracks.length() - 1; i++){
                captions.add(new Captions(rawTracks.getJSONObject(i)));
            }
            return captions;
        } catch (JSONException e) {
            return null;
        }
    }

    public CaptionQuery getCaptions() throws Exception {
        return new CaptionQuery(getCaptionTracks());
    }

    public JSONArray getKeywords() throws Exception {
        try {
            return getVidInfo().getJSONObject("videoDetails")
                    .getJSONArray("keywords");
        }catch (JSONException e){
            return null;
        }
    }

    public StreamQuery streams() throws Exception {
        return new StreamQuery(fmtStreams());
    }
}