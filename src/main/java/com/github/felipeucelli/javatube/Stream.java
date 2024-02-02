package com.github.felipeucelli.javatube;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.Math.min;

public class Stream{

    private final String title;
    private final String url;
    private final Integer itag;
    private final String mimeType;
    private final String codecs;
    private final String type;
    private final String subType;
    private final String videoCodec;
    private final String audioCodec;
    private final Integer bitrate;
    private final Boolean isOtf;
    private final long fileSize;
    private final Map<String, String> itagProfile;
    private final String abr;
    private Integer fps = null;
    private final String resolution;
    private final JSONObject multipleAudioTracks;
    private final boolean defaultAudioTrack;
    private String audioTrackName = null;
    private String audioTrackId = null;


    public Stream(JSONObject stream, String videoTitle) throws Exception {
        title = videoTitle;
        url = stream.getString("url");
        itag = stream.getInt("itag");
        mimeType = mimeTypeCodec(stream.getString("mimeType")).group(1);
        codecs = mimeTypeCodec(stream.getString("mimeType")).group(2);
        type = Arrays.asList(mimeType.split("/")).get(0);
        subType = Arrays.asList(mimeType.split("/")).get(1);
        videoCodec = parseCodecs().get(0);
        audioCodec = parseCodecs().get(1);
        bitrate = stream.getInt("bitrate");
        isOtf = setIsOtf(stream);
        fileSize = setFileSize(stream.has("contentLength") ? stream.getString("contentLength") : null);
        itagProfile = getFormatProfile();
        abr = itagProfile.get("abr");
        if(stream.has("fps")){
            fps = stream.getInt("fps");
        }
        resolution = itagProfile.get("resolution");
        multipleAudioTracks = stream.has("audioTrack") ? stream.getJSONObject("audioTrack") : null;
        if(includesMultipleAudioTracks()){
            defaultAudioTrack = multipleAudioTracks.getBoolean("audioIsDefault");
            audioTrackName = Arrays.asList(multipleAudioTracks.getString("displayName").split(" ")).get(0);
            audioTrackId = multipleAudioTracks.getString("id");
        }else {
            defaultAudioTrack = includeAudioTrack() && !includeVideoTrack();
        }
    }

    @Override
    public String toString(){
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(
                "itag=\"" + itag + "\"", "mime_type=\"" + mimeType + "\""));
        if(includeVideoTrack()){
            parts.addAll(Arrays.asList("res=\"" + resolution + "\"", "fps=\"" + fps + "\""));
            if(!isAdaptive()){
                parts.addAll(Arrays.asList("vcodec=\"" + videoCodec + "\"", "acodec=\"" + audioCodec + "\""));
            }else {
                parts.add("vcodec=\"" + videoCodec + "\"");
            }
        }
        else {
            parts.addAll(Arrays.asList("abr=\"" + abr + "\"", "acodec=\"" + audioCodec + "\""));
        }
        parts.addAll(Arrays.asList("progressive=\"" + isProgressive() + "\"", "type=\"" + type + "\""));
        return "<Stream: " + String.join(" ", parts) + ">";
    }

    private long setFileSize(String size) throws IOException {
        if (Objects.equals(size, null)) {
            if(!isOtf){
                URL url = new URL(this.url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("HEAD");

                try {
                    size = http.getHeaderFields().get("Content-Length").get(0);
                } catch (NullPointerException e) {
                    size = "0";
                }
                http.disconnect();
            }else {
                size = "0";
            }
            return Long.parseLong(size);
        }
        return Long.parseLong(size);
    }

    private boolean setIsOtf(JSONObject stream) throws JSONException {
        if(stream.has("type")){
            return Objects.equals(stream.getString("type"), "FORMAT_STREAM_TYPE_OTF");
        }else{
            return false;
        }
    }

    public Boolean isAdaptive(){
        return (Arrays.asList(codecs.split(",")).size() % 2) == 1;
    }

    public Boolean isProgressive(){
        return !isAdaptive();
    }

    public Boolean includeAudioTrack(){
        return isProgressive() || Objects.equals(type, "audio");
    }

    public Boolean includeVideoTrack() { return isProgressive() || Objects.equals(type, "video"); }

    public Boolean includesMultipleAudioTracks(){
        return multipleAudioTracks != null;
    }

    private ArrayList<String> parseCodecs(){
        ArrayList<String> array = new ArrayList<>();
        String video = null, audio = null;
        if(!isAdaptive()){
            video = Arrays.asList(codecs.split(",")).get(0);
            audio = Arrays.asList(codecs.split(",")).get(1);
        }else if(includeVideoTrack()){
            video = Arrays.asList(codecs.split(",")).get(0);
        } else if (includeAudioTrack()) {
            audio = Arrays.asList(codecs.split(",")).get(0);
        }
        array.add(video);
        array.add(audio);

        return array;
    }

    private Matcher mimeTypeCodec(String mimeTypeCodec) throws Exception {
        Pattern pattern = Pattern.compile("(\\w+/\\w+);\\scodecs=\"([a-zA-Z-0-9.,\\s]*)\"");
        Matcher matcher = pattern.matcher(mimeTypeCodec);
        if (matcher.find()) {
            return matcher;
        }else {
            throw new Exception("RegexMatcherError: " + pattern);
        }
    }

    private String safeFileName(String s){
        return s.replaceAll("[\"'#$%*,.:;<>?\\\\^|~/]", " ");
    }

    private void checkFile(String filePath) throws IOException {
        File file = new File(filePath);
        if(file.exists()){
            if(!file.delete()){
                throw new IOException("Failed to delete existing output file: " + file.getName());
            }
        }
    }

    public static void onProgress(long value){
        System.out.println(value + "%");
    }
    public void download(String path) throws Exception {
        startDownload(path, title, Stream::onProgress);
    }
    public void download(String path, Consumer<Long> progress) throws Exception {
        startDownload(path, title, progress);
    }
    public void download(String path, String fileName) throws Exception {
        startDownload(path, fileName, Stream::onProgress);
    }
    public void download(String path, String fileName, Consumer<Long> progress) throws Exception {
        startDownload(path, fileName, progress);
    }
    private void startDownload(String path, String fileName, Consumer<Long> progress) throws Exception {
        String savePath = path + safeFileName(fileName) + "." + subType;
        if(!isOtf){
            long startSize = 0;
            long stopPos;
            int defaultRange = 1048576;
            long progressPercentage;
            long lastPrintedProgress = 0;
            byte[] chunkReceived;

            checkFile(savePath);
            do {
                stopPos = min(startSize + defaultRange, fileSize);
                if (stopPos >= fileSize) {
                    stopPos = fileSize;
                }
                String chunk = url + "&range=" + startSize + "-" + stopPos;
                chunkReceived = Request.get(chunk).toByteArray();

                progressPercentage = (stopPos * 100L) / (fileSize);

                if (progressPercentage != lastPrintedProgress) {
                    lastPrintedProgress = progressPercentage;
                    progress.accept(progressPercentage);
                }
                startSize = startSize + chunkReceived.length;
                try (FileOutputStream fos = new FileOutputStream(savePath, true)) {
                    fos.write(chunkReceived);
                }
            } while (stopPos != fileSize);
        }else {
            downloadOtf(savePath, progress);
        }
    }

    private void downloadOtf(String savePath, Consumer<Long> progress) throws Exception {
        int countChunk = 0;
        byte[] chunkReceived;
        int lastChunk = 0;

        checkFile(savePath);
        do {
            String chunk = url + "&sq=" + countChunk;

            chunkReceived = Request.postChunk(chunk).toByteArray();

            if(countChunk == 0){
                Pattern pattern = Pattern.compile("Segment-Count: (\\d*)");
                Matcher matcher = pattern.matcher(new String(chunkReceived));
                if (matcher.find()){
                    lastChunk = Integer.parseInt(matcher.group(1));
                }else{
                    throw new Exception("RegexMatcherError: " + pattern);
                }
            }
            progress.accept((countChunk * 100L) / (lastChunk));
            countChunk = countChunk + 1;
            try (FileOutputStream fos = new FileOutputStream(savePath, true)) {
                fos.write(chunkReceived);
            }
        }while (countChunk <= lastChunk);
    }

    private Map<String, String> getFormatProfile(){
        Map<Integer, ArrayList<String>> itags = new HashMap<>();

        // progressive video
        itags.put(5, new ArrayList<>(Arrays.asList("240p", "64kbps")));
        itags.put(6, new ArrayList<>(Arrays.asList("270p", "64kbps")));
        itags.put(13, new ArrayList<>(Arrays.asList("144p", null)));
        itags.put(17, new ArrayList<>(Arrays.asList("144p", "24kbps")));
        itags.put(18, new ArrayList<>(Arrays.asList("360p", "96kbps")));
        itags.put(22, new ArrayList<>(Arrays.asList("720p", "192kbps")));
        itags.put(34, new ArrayList<>(Arrays.asList("360p", "128kbps")));
        itags.put(35, new ArrayList<>(Arrays.asList("480p", "128kbps")));
        itags.put(36, new ArrayList<>(Arrays.asList("240p", null)));
        itags.put(37, new ArrayList<>(Arrays.asList("1080p", "192kbps")));
        itags.put(38, new ArrayList<>(Arrays.asList("3072p", "192kbps")));
        itags.put(43, new ArrayList<>(Arrays.asList("360p", "128kbps")));
        itags.put(44, new ArrayList<>(Arrays.asList("480p", "128kbps")));
        itags.put(45, new ArrayList<>(Arrays.asList("720p", "192kbps")));
        itags.put(46, new ArrayList<>(Arrays.asList("1080p", "192kbps")));
        itags.put(59, new ArrayList<>(Arrays.asList("480p", "128kbps")));
        itags.put(78, new ArrayList<>(Arrays.asList("480p", "128kbps")));
        itags.put(82, new ArrayList<>(Arrays.asList("360p", "128kbps")));
        itags.put(83, new ArrayList<>(Arrays.asList("480p", "128kbps")));
        itags.put(84, new ArrayList<>(Arrays.asList("720p", "192kbps")));
        itags.put(85, new ArrayList<>(Arrays.asList("1080p", "192kbps")));
        itags.put(91, new ArrayList<>(Arrays.asList("144p", "48kbps")));
        itags.put(92, new ArrayList<>(Arrays.asList("240p", "48kbps")));
        itags.put(93, new ArrayList<>(Arrays.asList("360p", "128kbps")));
        itags.put(94, new ArrayList<>(Arrays.asList("480p", "128kbps")));
        itags.put(95, new ArrayList<>(Arrays.asList("720p", "256kbps")));
        itags.put(96, new ArrayList<>(Arrays.asList("1080p", "256kbps")));
        itags.put(100, new ArrayList<>(Arrays.asList("360p", "128kbps")));
        itags.put(101, new ArrayList<>(Arrays.asList("480p", "192kbps")));
        itags.put(102, new ArrayList<>(Arrays.asList("720p", "192kbps")));
        itags.put(132, new ArrayList<>(Arrays.asList("240p", "48kbps")));
        itags.put(151, new ArrayList<>(Arrays.asList("720p", "24kbps")));
        itags.put(300, new ArrayList<>(Arrays.asList("720p", "128kbps")));
        itags.put(301, new ArrayList<>(Arrays.asList("1080p", "128kbps")));

        // dash video
        itags.put(133, new ArrayList<>(Arrays.asList("240p", null))); // MP4
        itags.put(134, new ArrayList<>(Arrays.asList("360p", null))); // MP4
        itags.put(135, new ArrayList<>(Arrays.asList("480p", null))); // MP4
        itags.put(136, new ArrayList<>(Arrays.asList("720p", null))); // MP4
        itags.put(137, new ArrayList<>(Arrays.asList("1080p", null))); // MP4
        itags.put(138, new ArrayList<>(Arrays.asList("2160p", null))); // MP4
        itags.put(160, new ArrayList<>(Arrays.asList("144p", null))); // MP4
        itags.put(167, new ArrayList<>(Arrays.asList("360p", null))); // WEBM
        itags.put(168, new ArrayList<>(Arrays.asList("480p", null))); // WEBM
        itags.put(169, new ArrayList<>(Arrays.asList("720p", null))); // WEBM
        itags.put(170, new ArrayList<>(Arrays.asList("1080p", null))); // WEBM
        itags.put(212, new ArrayList<>(Arrays.asList("480p", null))); // MP4
        itags.put(218, new ArrayList<>(Arrays.asList("480p", null))); // WEBM
        itags.put(219, new ArrayList<>(Arrays.asList("480p", null))); // WEBM
        itags.put(242, new ArrayList<>(Arrays.asList("240p", null))); // WEBM
        itags.put(243, new ArrayList<>(Arrays.asList("360p", null))); // WEBM
        itags.put(244, new ArrayList<>(Arrays.asList("480p", null))); // WEBM
        itags.put(245, new ArrayList<>(Arrays.asList("480p", null))); // WEBM
        itags.put(246, new ArrayList<>(Arrays.asList("480p", null))); // WEBM
        itags.put(247, new ArrayList<>(Arrays.asList("720p", null))); // WEBM
        itags.put(248, new ArrayList<>(Arrays.asList("1080p", null))); // WEBM
        itags.put(264, new ArrayList<>(Arrays.asList("1440p", null))); // MP4
        itags.put(266, new ArrayList<>(Arrays.asList("2160p", null))); // MP4
        itags.put(271, new ArrayList<>(Arrays.asList("1440p", null))); // WEBM
        itags.put(272, new ArrayList<>(Arrays.asList("4320p", null))); // WEBM
        itags.put(278, new ArrayList<>(Arrays.asList("144p", null))); // WEBM
        itags.put(298, new ArrayList<>(Arrays.asList("720p", null))); // MP4
        itags.put(299, new ArrayList<>(Arrays.asList("1080p", null))); // MP4
        itags.put(302, new ArrayList<>(Arrays.asList("720p", null)));  // WEBM
        itags.put(303, new ArrayList<>(Arrays.asList("1080p", null))); // WEBM
        itags.put(308, new ArrayList<>(Arrays.asList("1440p", null))); // WEBM
        itags.put(313, new ArrayList<>(Arrays.asList("2160p", null))); // WEBM
        itags.put(315, new ArrayList<>(Arrays.asList("2160p", null))); // WEBM
        itags.put(330, new ArrayList<>(Arrays.asList("144p", null))); // WEBM
        itags.put(331, new ArrayList<>(Arrays.asList("240p", null))); // WEBM
        itags.put(332, new ArrayList<>(Arrays.asList("360p", null))); // WEBM
        itags.put(333, new ArrayList<>(Arrays.asList("480p", null))); // WEBM
        itags.put(334, new ArrayList<>(Arrays.asList("720p", null))); // WEBM
        itags.put(335, new ArrayList<>(Arrays.asList("1080p", null))); // WEBM
        itags.put(336, new ArrayList<>(Arrays.asList("1440p", null))); // WEBM
        itags.put(337, new ArrayList<>(Arrays.asList("2160p", null))); // WEBM
        itags.put(394, new ArrayList<>(Arrays.asList("144p", null)));  // MP4
        itags.put(395, new ArrayList<>(Arrays.asList("240p", null))); // MP4
        itags.put(396, new ArrayList<>(Arrays.asList("360p", null))); // MP4
        itags.put(397, new ArrayList<>(Arrays.asList("480p", null))); // MP4
        itags.put(398, new ArrayList<>(Arrays.asList("720p", null))); // MP4
        itags.put(399, new ArrayList<>(Arrays.asList("1080p", null))); // MP4
        itags.put(400, new ArrayList<>(Arrays.asList("1440p", null))); // MP4
        itags.put(401, new ArrayList<>(Arrays.asList("2160p", null))); // MP4
        itags.put(402, new ArrayList<>(Arrays.asList("4320p", null))); // MP4
        itags.put(571, new ArrayList<>(Arrays.asList("4320p", null))); // MP4
        itags.put(597, new ArrayList<>(Arrays.asList(null, null))); // MP4
        itags.put(598, new ArrayList<>(Arrays.asList(null, null))); // WEBM
        itags.put(694, new ArrayList<>(Arrays.asList("144p", null))); // MP4
        itags.put(695, new ArrayList<>(Arrays.asList("240p", null))); // MP4
        itags.put(696, new ArrayList<>(Arrays.asList("360p", null))); // MP4
        itags.put(697, new ArrayList<>(Arrays.asList("480p", null))); // MP4
        itags.put(698, new ArrayList<>(Arrays.asList("720p", null))); // MP4
        itags.put(699, new ArrayList<>(Arrays.asList("1080p", null))); // MP4
        itags.put(700, new ArrayList<>(Arrays.asList("1440p", null))); // MP4
        itags.put(701, new ArrayList<>(Arrays.asList("2160p", null))); // MP4
        itags.put(702, new ArrayList<>(Arrays.asList("4320p", null))); // MP4

        // dash audio
        itags.put(139, new ArrayList<>(Arrays.asList(null, "48kbps"))); // MP4
        itags.put(140, new ArrayList<>(Arrays.asList(null, "128kbps"))); // MP4
        itags.put(141, new ArrayList<>(Arrays.asList(null, "256kbps"))); // MP4
        itags.put(171, new ArrayList<>(Arrays.asList(null, "128kbps"))); // WEBM
        itags.put(172, new ArrayList<>(Arrays.asList(null, "256kbps"))); // WEBM
        itags.put(249, new ArrayList<>(Arrays.asList(null, "50kbps"))); // WEBM
        itags.put(250, new ArrayList<>(Arrays.asList(null, "70kbps"))); // WEBM
        itags.put(251, new ArrayList<>(Arrays.asList(null, "160kbps"))); // WEBM
        itags.put(256, new ArrayList<>(Arrays.asList(null, "192kbps"))); // MP4
        itags.put(258, new ArrayList<>(Arrays.asList(null, "384kbps"))); // MP4
        itags.put(325, new ArrayList<>(Arrays.asList(null, null))); // MP4
        itags.put(328, new ArrayList<>(Arrays.asList(null, null))); // MP4
        itags.put(599, new ArrayList<>(Arrays.asList(null, null))); // MP4
        itags.put(600, new ArrayList<>(Arrays.asList(null, null))); // webm


        String res, bitrate;
        if(itags.containsKey(itag)){
            res = itags.get(itag).get(0);
            bitrate = itags.get(itag).get(1);
        }else{
            res = null;
            bitrate = null;
        }

        Map<String, String> returnItags = new HashMap<>();

        returnItags.put("resolution", res);
        returnItags.put("abr", bitrate);

        return returnItags;
    }
    public String getTitle(){
        return title;
    }
    public String getUrl(){
        return url;
    }
    public Integer getItag(){
        return itag;
    }
    public String getMimeType(){
        return mimeType;
    }
    public String getCodecs(){
        return codecs;
    }
    public String getType(){
        return type;
    }
    public String getSubType(){
        return subType;
    }
    public String getVideoCodec(){
        return videoCodec;
    }
    public String getAudioCodec(){
        return audioCodec;
    }
    public Integer getBitrate(){
        return bitrate;
    }
    public Boolean getIsOtf(){
        return isOtf;
    }
    public long getFileSize(){
        return fileSize;
    }
    public Map<String, String> getItagProfile(){
        return itagProfile;
    }
    public String getAbr(){
        return abr;
    }
    public Integer getFps(){
        return fps;
    }
    public String getResolution(){
        return resolution;
    }
    public boolean isDefaultAudioTrack(){
        return defaultAudioTrack;
    }
    public String getAudioTrackName(){
        return audioTrackName;
    }
    public String getAudioTrackId(){
        return audioTrackId;
    }

}
