package javatube;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Captions {
    private final String url;
    private final String code;

    public Captions(JSONObject captionTrack) throws JSONException {
        url = captionTrack.getString("baseUrl");
        code = captionTrack.getString("vssId").replace(".", "");
    }
    public String getUrl(){
        return url;
    }
    public String getCode(){
        return code;
    }

    private String getXmlCaptions() throws IOException {
        return InnerTube.downloadWebPage(url).replaceAll("(&#39;)|(&amp;#39;)", "'");
    }

    private String generateSrtCaptions() throws IOException {
        return getXmlCaptions();
    }

    private String decodeString(String s) throws UnsupportedEncodingException {
        return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
    }

    private String srtTimeFormat(Float d){

        Float ms = (d % 1);
        int round = Integer.parseInt(String.valueOf((d - ms)).replace(".", "") + "00");
        Integer seconds = ((round / 1000 ) % 60);
        Integer minutes = ( round / 60000 ) % 60;
        Integer hours = round / 3600000;
        return String.format("%02d:%02d:%02d,", hours, minutes, seconds) + String.format("%.3f", ms).replace("0,", "");
    }

    public String xmlCaptionToSrt() throws Exception {
        String root = generateSrtCaptions();

        int i = 0;
        String segments = "";

        String pattern = "start=\\\"(.*?)\\\".*?dur=\\\"(.*?)\\\">(.*?)<";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(root);
        while (matcher.find()) {
            Float start = Float.parseFloat(matcher.group(1));
            Float duration = Float.parseFloat(matcher.group(2));
            String caption = decodeString(matcher.group(3));

            Float end = start + duration;
            int sequenceNumber = i + 1;

            String line = sequenceNumber + "\n" + srtTimeFormat(start) + " --> " + srtTimeFormat(end) + "\n" + caption + "\n\n";

            segments += line;

            i++;
        }

        return segments;
    }

    public void download(String filename, String savePath) {
        String fullPath;

        if(savePath.endsWith("/")){
            fullPath = savePath + filename;
        }else{
            fullPath = savePath + "/" + filename;
        }


        if(filename.endsWith(".srt")){
            try {
                File file = new File(fullPath);
                FileWriter write = new FileWriter(file);
                write.write(xmlCaptionToSrt());
                write.close();
            }
            catch (IOException ex) {
                System.out.print("Invalid Path");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            try {
                File file = new File(fullPath);
                FileWriter write = new FileWriter(file);
                write.write(getXmlCaptions());
                write.close();
            }
            catch (IOException ex) {
                System.out.print("Invalid Path");
            }
        }

    }

}