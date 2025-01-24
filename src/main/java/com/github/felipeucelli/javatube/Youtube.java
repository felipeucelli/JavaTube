package com.github.felipeucelli.javatube;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

import com.github.felipeucelli.javatube.exceptions.*;
import org.json.*;


public class Youtube {

    private final String urlVideo;
    private final String watchUrl;
    private InnerTube innerTube = null;
    private String client = null;
    private JSONObject vidInfo = null;
    private String html = null;
    private JSONObject initialData = null;
    private String js = null;
    private JSONObject ytCfg = null;
    private JSONObject signatureTimestamp = null;
    private String visitorData = null;
    private String playerJs = null;
    private final boolean usePoToken;
    private final boolean allowCache;

    /**
     * Default client: ANDROID_VR
     * */
    public Youtube(String url) throws Exception {
        this(url, "ANDROID_VR", false, false);
    }
    /**
     * @Clients:
     *          WEB,
     *          WEB_EMBED,
     *          WEB_MUSIC,
     *          WEB_CREATOR,
     *          WEB_SAFARI,
     *          MWEB,
     *          ANDROID,
     *          ANDROID_VR,
     *          ANDROID_MUSIC,
     *          ANDROID_CREATOR,
     *          ANDROID_TESTSUITE,
     *          ANDROID_PRODUCER,
     *          IOS,
     *          IOS_MUSIC,
     *          IOS_CREATOR,
     *          TV_EMBED,
     *          MEDIA_CONNECT
     * */
    public Youtube(String url, String clientName) throws Exception {
        this(url, clientName, false, false);
    }
    /**
     * Default client: WEB
     * */
    public Youtube(String url, boolean usePoToken) throws Exception {
        this(url, "ANDROID_VR", usePoToken, false);
    }
    /**
     * Default client: WEB
     * */
    public Youtube(String url, boolean usePoToken, boolean allowCache) throws Exception {
        this(url, "ANDROID_VR", usePoToken, allowCache);
    }
    /**
     * @Clients:
     *          WEB,
     *          WEB_EMBED,
     *          WEB_MUSIC,
     *          WEB_CREATOR,
     *          WEB_SAFARI,
     *          MWEB,
     *          ANDROID,
     *          ANDROID_VR,
     *          ANDROID_MUSIC,
     *          ANDROID_CREATOR,
     *          ANDROID_TESTSUITE,
     *          ANDROID_PRODUCER,
     *          IOS,
     *          IOS_MUSIC,
     *          IOS_CREATOR,
     *          TV_EMBED,
     *          MEDIA_CONNECT
     * */
    public Youtube(String url, String clientName, boolean usePoToken, boolean allowCache) throws Exception {
        client = usePoToken ? "WEB" : clientName;
        this.usePoToken = usePoToken;
        this.allowCache = allowCache;
        innerTube = new InnerTube(client, usePoToken, allowCache);
        urlVideo = url;
        watchUrl = "https://www.youtube.com/watch?v=" + videoId();
    }

    private String videoId() throws Exception {
        Pattern pattern = Pattern.compile("(?:v=|/)([0-9A-Za-z_-]{11}).*");
        Matcher matcher = pattern.matcher(urlVideo);
        if (matcher.find()) {
            return matcher.group(1);
        }else {
            throw new RegexMatchError("videoId: could not find match for " + pattern);
        }
    }

    @Override
    public String toString(){
        try {
            return "<com.github.felipeucelli.javatube.Youtube object: videoId=" + videoId() + ">";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String setHtml() throws Exception {
        Map<String, String> headers =  !client.contains("WEB") ? null : innerTube.getClientHeaders();
        return Request.get(watchUrl, headers).toString(StandardCharsets.UTF_8.name()).replace("\n", "");
    }

    public String getHtml() throws Exception {
        if(html == null){
            html = setHtml();
        }
        return html;
    }

    private String setSignatureTimestamp() throws Exception {
        String pattern = "signatureTimestamp:(\\d*)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(getJs());
        if(matcher.find()){
            return matcher.group(1);
        }else {
            throw new RegexMatchError("setSignatureTimestamp: Unable to find signatureTimestamp in playerJs: " + getYtPlayerJs());
        }
    }
    public JSONObject getSignatureTimestamp() throws Exception {
        if(signatureTimestamp == null){
            signatureTimestamp =  new JSONObject(
                    "{" +
                                "\"playbackContext\": {" +
                                    "\"contentPlaybackContext\": {" +
                                        "\"signatureTimestamp\": " + setSignatureTimestamp() +
                                    "}" +
                                "}" +
                            "}"
            );
        }
        return signatureTimestamp;
    }

    private JSONObject setYtCfg() throws Exception {
        Pattern pattern = Pattern.compile("window\\.ytplayer=\\{};ytcfg\\.set\\((\\{.*?\\})\\);");
        Matcher matcher = pattern.matcher(getHtml());
        if(matcher.find()){
            return new JSONObject(matcher.group(1));
        }else {
            throw new RegexMatchError("setYtCfg: Could not find ytCfg: " + pattern);
        }
    }

    public JSONObject getYtCfg() throws Exception {
        if(ytCfg == null){
            ytCfg = setYtCfg();
        }
        return ytCfg;
    }

    private String setVisitorData() throws Exception {
        JSONObject innerTubeResponse = new InnerTube(client).player(videoId());

        try {
            return innerTubeResponse.getJSONObject("responseContext").getString("visitorData");
        }catch (JSONException e){
            return innerTubeResponse.getJSONObject("responseContext").getJSONArray("serviceTrackingParams").getJSONObject(0).getJSONArray("params").getJSONObject(6).getString("value");
        }
    }

    public String getVisitorData() throws Exception {
        if(visitorData == null){
            visitorData = setVisitorData();
        }

        return visitorData;
    }

    private JSONObject setInitialData() throws Exception {
        String pattern = "ytInitialPlayerResponse\\s=\\s(\\{\"responseContext\":.*?\\});(?:var|</script>)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(getHtml());
        if(matcher.find()){
            return new JSONObject(matcher.group(1));
        }else {
            throw new RegexMatchError("setInitialData: Unable to find InitialData: " + pattern);
        }
    }

    public JSONObject getInitialData() throws Exception {
        if(initialData == null){
            initialData = setInitialData();
        }
        return initialData;
    }

    private String setYtPlayerJs() throws Exception {
        Pattern pattern = Pattern.compile("(/s/player/[\\w\\d]+/[\\w\\d_/.\\-]+/base\\.js)");
        Matcher matcher = pattern.matcher(getHtml());
        if (matcher.find()) {
            return "https://youtube.com" + matcher.group(1);
        }else {
            throw new RegexMatchError("setYtPlayerJs: Could not find playerJs: " + pattern);
        }
    }
    public String getYtPlayerJs() throws Exception {
        if(playerJs == null){
            playerJs = setYtPlayerJs();
        }
        return playerJs;
    }

    private String setJs() throws Exception {
        return Request.get(getYtPlayerJs()).toString().replace("\n", "");
    }
    public String getJs() throws Exception {
        if(js == null){
            js = setJs();
        }
        return js;
    }

    public String getUrl(){
        return watchUrl;
    }

    private JSONObject callInnerTube() throws Exception {
        if (innerTube.getRequireJsPlayer()) {
            innerTube.updateInnerTubeContext(innerTube.getInnerTubeContext(), getSignatureTimestamp());
        }
        if(!usePoToken){
            innerTube.insertVisitorData(getVisitorData());
        }

        return innerTube.player(videoId());
    }

    private JSONObject getVidInfo() throws Exception {
        List<String> fallbackClients = Arrays.asList("IOS", "WEB");

        if (vidInfo != null) {
            return vidInfo;
        }
        JSONObject innerTubeResponse = callInnerTube();
        for(String client : fallbackClients) {

            JSONObject playabilityStatus = innerTubeResponse.getJSONObject("playabilityStatus");

            if (Objects.equals(playabilityStatus.getString("status"), "UNPLAYABLE")) {
                if (playabilityStatus.has("reason") && Objects.equals(playabilityStatus.getString("reason"), "This video is not available")) {
                    innerTube = new InnerTube(client, usePoToken, allowCache);
                    innerTubeResponse = callInnerTube();
                }
            }else{
                break;
            }
        }
        vidInfo = innerTubeResponse;

        return vidInfo;
    }


    private List<String> extractAvailability(JSONObject playabilityStatus){
        String status = "";
        String reason = "";

        if (playabilityStatus.has("status")){
            status = playabilityStatus.getString("status");

            if (playabilityStatus.has("reason")){
                reason = playabilityStatus.getString("reason");

            } else if (playabilityStatus.has("messages")){
                reason = playabilityStatus.getJSONArray("messages").getString(0);
            }
        }

        return Arrays.asList(status, reason);
    }

     void checkAvailability() throws Exception {
        JSONObject playabilityStatus = getVidInfo().getJSONObject("playabilityStatus");

        List<String> availability = extractAvailability(playabilityStatus);

        String status = availability.get(0);
        String reason = availability.get(1);

        if (playabilityStatus.has("status")){
            status = playabilityStatus.getString("status");

            if (playabilityStatus.has("reason")){
                reason = playabilityStatus.getString("reason");

            } else if (playabilityStatus.has("messages")){
                reason = playabilityStatus.getJSONArray("messages").getString(0);
            }
        }

        switch (status) {
            case "UNPLAYABLE" -> {
                if (reason.equals("Join this channel to get access to members-only content like this video, and other exclusive perks.")) {
                    throw new MembersOnlyError(videoId());

                } else if (reason.equals("This live stream recording is not available.")){
                    throw new RecordingUnavailableError(videoId());

                } else if(reason.equals("The uploader has not made this video available in your country")){
                    throw new VideoRegionBlockedError(videoId());

                } else {
                    throw new VideoUnavailableError(videoId());
                }
            }
            case "LOGIN_REQUIRED" -> {
                if (reason.equals("Sign in to confirm your age") || reason.equals("This video may be inappropriate for some users.")) {
                    throw new AgeRestrictedError(videoId());

                } else if (reason.equals("Sign in to confirm you’re not a bot")){
                    throw new BotDetectionError(videoId());

                }else {
                    throw new VideoPrivateError(videoId());
                }
            }

            case "LIVE_STREAM_OFFLINE" -> throw new LiveStreamOffline(videoId(), reason);

            case "ERROR" -> {
                if (reason.equals("Video unavailable")) {
                    throw new VideoUnavailableError(videoId());

                }else if(reason.equals("This video is private")){
                    throw new VideoPrivateError(videoId());

                }else if (reason.equals("This video is unavailable")){
                    throw new VideoUnavailableError(videoId());

                }else if (reason.equals("This video has been removed by the uploader")){
                    throw new VideoUnavailableError(videoId());

                }else if (reason.equals("This video is no longer available because the YouTube account associated with this video has been terminated.")){
                    throw new VideoUnavailableError(videoId());

                }else {
                    throw new UnknownVideoError(videoId(), status, reason);
                }
            }
        }
        if (getVidInfo().getJSONObject("videoDetails").has("isLive")){
            throw new LiveStreamError(videoId());
        }
    }

     JSONObject streamData() throws Exception {
        checkAvailability();
        return getVidInfo().getJSONObject("streamingData");
    }

    private String decodeURL(String s) throws UnsupportedEncodingException {
        return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
    }

    private static JSONArray applyDescrambler(JSONObject streamData) throws JSONException{
        JSONArray formats = new JSONArray();
        if(streamData.has("formats")){
            for(int i = 0; i < streamData.getJSONArray("formats").length(); i ++){
                formats.put(streamData.getJSONArray("formats").get(i));
            }
        }
        if(streamData.has("adaptiveFormats")){
            for(int i = 0; i < streamData.getJSONArray("adaptiveFormats").length(); i ++){
                formats.put(streamData.getJSONArray("adaptiveFormats").get(i));
            }
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

    private ArrayList<Stream> fmtStreams() throws Exception {

        JSONArray streamManifest = applyDescrambler(streamData());

        ArrayList<Stream> fmtStream = new ArrayList<>();
        String title = getTitle();
        Stream video;

        if(innerTube == null || innerTube.getRequireJsPlayer()){
            applySignature(streamManifest);
        }
        for (int i = 0; streamManifest.length() > i; i++) {
            video = new Stream(streamManifest.getJSONObject(i), title);
            fmtStream.add(video);
        }
        return fmtStream;
    }

    private void applySignature(JSONArray streamManifest) throws Exception {
        Cipher cipher = new Cipher(getJs(), getYtPlayerJs());
        Pattern nSigPattern = Pattern.compile("&n=(.*?)&");
        Map<String, String> discoveredNSig = new HashMap<>();
        for (int i = 0; streamManifest.length() > i; i++) {
            if (streamManifest.getJSONObject(i).has("signatureCipher")) {
                String oldUrl = decodeURL(streamManifest.getJSONObject(i).getString("url"));
                streamManifest.getJSONObject(i).remove("url");
                String sig = streamManifest.getJSONObject(i).getString("s");
                streamManifest.getJSONObject(i).put("url", oldUrl + "&sig=" + cipher.getSignature(decodeURL(sig)));
            }

            String oldUrl = streamManifest.getJSONObject(i).getString("url");
            Matcher matcher = nSigPattern.matcher(oldUrl);
            if (matcher.find()) {
                String nSig = matcher.group(1);
                if(!discoveredNSig.containsKey(nSig)){
                    discoveredNSig.put(nSig, cipher.getNSig(nSig));
                }
                String newUrl = oldUrl.replaceFirst("&n=(.*?)&", "&n=" + discoveredNSig.get(nSig) + "&");
                streamManifest.getJSONObject(i).put("url", newUrl);
            }
            if(usePoToken){
                oldUrl = streamManifest.getJSONObject(i).getString("url");
                String newUrl = oldUrl + "&pot=" + innerTube.getPoToken();
                streamManifest.getJSONObject(i).put("url", newUrl);
            }
        }
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
            throw new RegexMatchError("getPublishDate: Unable to find publication date: " + pattern);
        }
    }

    public Integer length() throws Exception {
        return getVidInfo().getJSONObject("videoDetails")
                .getInt("lengthSeconds");
    }

    public String getThumbnailUrl() throws Exception {
        JSONArray thumbnails = new InnerTube("WEB").player(videoId()).getJSONObject("videoDetails")
                .getJSONObject("thumbnail")
                .getJSONArray("thumbnails");
        return thumbnails.getJSONObject(thumbnails.length() - 1).getString("url");
    }

    public Long getViews() throws Exception {
        return Long.parseLong(getVidInfo().getJSONObject("videoDetails")
                .getString("viewCount"));
    }

    public String getAuthor() throws Exception {
        return getVidInfo().getJSONObject("videoDetails")
                .getString("author");
    }

    public ArrayList<Captions> getCaptionTracks() throws Exception {
        try{
            JSONArray rawTracks = new InnerTube("WEB").player(videoId()).getJSONObject("captions")
                    .getJSONObject("playerCaptionsTracklistRenderer")
                    .getJSONArray("captionTracks");
            ArrayList<Captions> captions = new ArrayList<>();
            for(int i = 0; i < rawTracks.length(); i++){
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