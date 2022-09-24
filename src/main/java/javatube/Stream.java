package javatube;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.Math.min;

public class Stream{
    public String title;
    public String url;
    public Integer itag;
    public String mimeType;
    public String codecs;
    public String type;
    public String subType;
    public String videoCodec;
    public String audioCodec;
    public Integer bitrate;
    public Integer fileSize;
    public Map<String, String> itagProfile;
    public String abr;
    public Integer fps = null;

    public String resolution;

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
        fileSize = setFileSize(stream.has("contentLength") ? stream.getString("contentLength") : null);
        itagProfile = getFormatProfile();
        abr = itagProfile.get("abr");
        if(stream.has("fps")){
            fps = stream.getInt("fps");
        }
        resolution = itagProfile.get("resolution");

    }

    private Integer setFileSize(String size) throws IOException {
        if (Objects.equals(size, null)) {
            URL url = new URL(this.url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("HEAD");

            size = http.getHeaderFields().get("Content-Length").get(0);

            http.disconnect();

            return Integer.parseInt(size);
        }
        return Integer.parseInt(size);
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
        Pattern pattern = Pattern.compile("(\\w+/\\w+);\\scodecs=\"([a-zA-Z-0-9.,\\s]*)\"", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(mimeTypeCodec);
        if (matcher.find()) {
            return matcher;
        }else {
            throw new Exception("RegexMatcherError");
        }
    }

    private String safeFileName(String s){
        return s.replaceAll("[\"'#$%*,.:;<>?\\\\^|~/]", " ");
    }

    public static void onProgress(long value){
        System.out.println(value + "%");
    }
    public void download(String path) throws IOException{
        startDownload(path, Stream::onProgress);
    }
    public void download(String path, Consumer<Long> progress) throws IOException{
        startDownload(path, progress);
    }
    private void startDownload(String path, Consumer<Long> progress) throws IOException {
        String savePath = path + safeFileName(title) + ".mp4";
        int startSize = 0;
        int stopPos;
        int defaultRange = 1048576;
        File f = new File(savePath);
        if(f.exists()){
            f.delete();
        }
        do {
            stopPos = min(startSize + defaultRange, fileSize);
            if (stopPos >= fileSize) {
                stopPos = fileSize;
            }
            InnerTube.get(url, savePath, Integer.toString(startSize), Integer.toString(stopPos));
            progress.accept((stopPos * 100L) / (fileSize));
            if(startSize < defaultRange){
                startSize = stopPos;
            }else{
                startSize = stopPos + 1;
            }
        } while (stopPos != fileSize);
    }


    private Map<String, String> getFormatProfile(){
        Map<Integer, ArrayList<String>> itags = new HashMap<>();

        // progressive video
        itags.put(5, new ArrayList<>(){{add("240p");add("64kps");}});
        itags.put(6, new ArrayList<>(){{add("270p");add("64kps");}});
        itags.put(13, new ArrayList<>(){{add("144p");add(null);}});
        itags.put(17, new ArrayList<>(){{add("144p");add("24kps");}});
        itags.put(18, new ArrayList<>(){{add("360p");add("96kps");}});
        itags.put(22, new ArrayList<>(){{add("720p");add("192kps");}});
        itags.put(34, new ArrayList<>(){{add("360p");add("128kps");}});
        itags.put(35, new ArrayList<>(){{add("480p");add("128kps");}});
        itags.put(36, new ArrayList<>(){{add("240p");add(null);}});
        itags.put(37, new ArrayList<>(){{add("1080p");add("192kps");}});
        itags.put(38, new ArrayList<>(){{add("3072p");add("192kps");}});
        itags.put(43, new ArrayList<>(){{add("360p");add("128kps");}});
        itags.put(44, new ArrayList<>(){{add("480p");add("128kps");}});
        itags.put(45, new ArrayList<>(){{add("720p");add("192kps");}});
        itags.put(46, new ArrayList<>(){{add("1080p");add("192kps");}});
        itags.put(59, new ArrayList<>(){{add("480p");add("128kps");}});
        itags.put(78, new ArrayList<>(){{add("480p");add("128kps");}});
        itags.put(82, new ArrayList<>(){{add("360p");add("128kps");}});
        itags.put(83, new ArrayList<>(){{add("480p");add("128kps");}});
        itags.put(84, new ArrayList<>(){{add("720p");add("192kps");}});
        itags.put(85, new ArrayList<>(){{add("1080p");add("192kps");}});
        itags.put(91, new ArrayList<>(){{add("144p");add("48kps");}});
        itags.put(92, new ArrayList<>(){{add("240p");add("48kps");}});
        itags.put(93, new ArrayList<>(){{add("360p");add("128kps");}});
        itags.put(94, new ArrayList<>(){{add("480p");add("128kps");}});
        itags.put(95, new ArrayList<>(){{add("720p");add("256kps");}});
        itags.put(96, new ArrayList<>(){{add("1080p");add("256kps");}});
        itags.put(100, new ArrayList<>(){{add("360p");add("128kps");}});
        itags.put(101, new ArrayList<>(){{add("480p");add("192kps");}});
        itags.put(102, new ArrayList<>(){{add("720p");add("192kps");}});
        itags.put(132, new ArrayList<>(){{add("240p");add("48kps");}});
        itags.put(151, new ArrayList<>(){{add("720p");add("24kps");}});
        itags.put(300, new ArrayList<>(){{add("720p");add("128kps");}});
        itags.put(301, new ArrayList<>(){{add("1080p");add("128kps");}});

        // dash video
        itags.put(133, new ArrayList<>(){{add("240p");add(null);}}); // MP4
        itags.put(134, new ArrayList<>(){{add("360p");add(null);}}); // MP4
        itags.put(135, new ArrayList<>(){{add("480p");add(null);}}); // MP4
        itags.put(136, new ArrayList<>(){{add("720p");add(null);}}); // MP4
        itags.put(137, new ArrayList<>(){{add("1080p");add(null);}}); // MP4
        itags.put(138, new ArrayList<>(){{add("2160p");add(null);}}); // MP4
        itags.put(160, new ArrayList<>(){{add("144p");add(null);}}); // WEBM
        itags.put(167, new ArrayList<>(){{add("360p");add(null);}}); // WEBM
        itags.put(168, new ArrayList<>(){{add("480p");add(null);}}); // WEBM
        itags.put(169, new ArrayList<>(){{add("720p");add(null);}}); // WEBM
        itags.put(170, new ArrayList<>(){{add("1080p");add(null);}}); // WEBM
        itags.put(212, new ArrayList<>(){{add("480p");add(null);}}); // MP4
        itags.put(218, new ArrayList<>(){{add("480p");add(null);}}); // WEBM
        itags.put(219, new ArrayList<>(){{add("480p");add(null);}}); // WEBM
        itags.put(242, new ArrayList<>(){{add("240p");add(null);}}); // WEBM
        itags.put(243, new ArrayList<>(){{add("360p");add(null);}}); // WEBM
        itags.put(244, new ArrayList<>(){{add("480p");add(null);}}); // WEBM
        itags.put(245, new ArrayList<>(){{add("480p");add(null);}}); // WEBM
        itags.put(246, new ArrayList<>(){{add("480p");add(null);}}); // WEBM
        itags.put(247, new ArrayList<>(){{add("720p");add(null);}}); // WEBM
        itags.put(248, new ArrayList<>(){{add("1080p");add(null);}}); // WEBM
        itags.put(264, new ArrayList<>(){{add("1440p");add(null);}}); // MP4
        itags.put(266, new ArrayList<>(){{add("2160p");add(null);}}); // MP4
        itags.put(271, new ArrayList<>(){{add("1440p");add(null);}}); // WEBM
        itags.put(272, new ArrayList<>(){{add("4320p");add(null);}}); // WEBM
        itags.put(278, new ArrayList<>(){{add("144p");add(null);}}); // WEBM
        itags.put(298, new ArrayList<>(){{add("720p");add(null);}}); // MP4
        itags.put(299, new ArrayList<>(){{add("1080p");add(null);}}); // MP4
        itags.put(302, new ArrayList<>(){{add("720p");add(null);}});  // WEBM
        itags.put(303, new ArrayList<>(){{add("1080p");add(null);}}); // WEBM
        itags.put(308, new ArrayList<>(){{add("1440p");add(null);}}); // WEBM
        itags.put(313, new ArrayList<>(){{add("2160p");add(null);}}); // WEBM
        itags.put(315, new ArrayList<>(){{add("2160p");add(null);}}); // WEBM
        itags.put(330, new ArrayList<>(){{add("144p");add(null);}}); // WEBM
        itags.put(331, new ArrayList<>(){{add("240p");add(null);}}); // WEBM
        itags.put(332, new ArrayList<>(){{add("360p");add(null);}}); // WEBM
        itags.put(333, new ArrayList<>(){{add("480p");add(null);}}); // WEBM
        itags.put(334, new ArrayList<>(){{add("720p");add(null);}}); // WEBM
        itags.put(335, new ArrayList<>(){{add("1080p");add(null);}}); // WEBM
        itags.put(336, new ArrayList<>(){{add("1440p");add(null);}}); // WEBM
        itags.put(337, new ArrayList<>(){{add("2160p");add(null);}}); // WEBM
        itags.put(394, new ArrayList<>(){{add("144p");add(null);}});  // MP4
        itags.put(395, new ArrayList<>(){{add("240p");add(null);}}); // MP4
        itags.put(396, new ArrayList<>(){{add("360p");add(null);}}); // MP4
        itags.put(397, new ArrayList<>(){{add("480p");add(null);}}); // MP4
        itags.put(398, new ArrayList<>(){{add("720p");add(null);}}); // MP4
        itags.put(399, new ArrayList<>(){{add("1080p");add(null);}}); // MP4
        itags.put(400, new ArrayList<>(){{add("1440p");add(null);}}); // MP4
        itags.put(401, new ArrayList<>(){{add("2160p");add(null);}}); // MP4
        itags.put(402, new ArrayList<>(){{add("4320p");add(null);}}); // MP4
        itags.put(571, new ArrayList<>(){{add("4320p");add(null);}}); // MP4

        // dash audio
        itags.put(139, new ArrayList<>(){{add(null);add("48kps");}}); // MP4
        itags.put(140, new ArrayList<>(){{add(null);add("128kps");}}); // MP4
        itags.put(141, new ArrayList<>(){{add(null);add("256kps");}}); // MP4
        itags.put(171, new ArrayList<>(){{add(null);add("128kps");}}); // WEBM
        itags.put(172, new ArrayList<>(){{add(null);add("256kps");}}); // WEBM
        itags.put(249, new ArrayList<>(){{add(null);add("50kps");}}); // WEBM
        itags.put(250, new ArrayList<>(){{add(null);add("70kps");}}); // WEBM
        itags.put(251, new ArrayList<>(){{add(null);add("160kps");}}); // WEBM
        itags.put(256, new ArrayList<>(){{add(null);add("192kps");}}); // MP4
        itags.put(258, new ArrayList<>(){{add(null);add("384kps");}}); // MP4
        itags.put(325, new ArrayList<>(){{add(null);add(null);}}); // MP4
        itags.put(328, new ArrayList<>(){{add(null);add(null);}}); // MP4


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

}
