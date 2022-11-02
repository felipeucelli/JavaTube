package javatube;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Captions {

    public String url;
    public String code;

    public Captions(JSONObject captionTrack) throws IOException {
        url = captionTrack.getString("baseUrl");
        code = captionTrack.getString("vssId").replace(".", "");

    }

    private String getXmlCaptions() throws IOException {
        return InnerTube.downloadWebPage(url).replace("&#39;", "'");
    }

    private String generateSrtCaptions() throws IOException {
        return getXmlCaptions();
    }

    private String srtTimeFormat(Float d){

        Float ms = (d % 1);
        int round = Integer.parseInt(String.valueOf((d - ms)).replace(".", "") + "00");
        Integer seconds = ((round / 1000 ) % 60);
        Integer minutes = ( round / 60000 ) % 60;
        Integer hours = round / 3600000;
        return String.format("%02d:%02d:%02d,", hours, minutes, seconds) + String.format("%.3f", ms).replace("0,", "");
    }

    public String xmlCaptionToSrt() throws IOException {
        String root = generateSrtCaptions();
        String initialBody = new ArrayList<>(List.of((root.split("<body>")))).get(1);
        String fullBody = new ArrayList<>(List.of((initialBody.split("</body>")))).get(0);
        ArrayList<String> obj = new ArrayList<>(List.of((fullBody.replace("</p>", "\n").replace("<p", "")).split("\n")));

        String segments = "";
        int i = 0;
        for(String child : obj){
            String caption = Arrays.asList(child.split(">")).get(1);
            Float start = Float.parseFloat((new ArrayList<>(List.of(child.split("d=\"[0-9]*\""))).get(0).replaceAll("[t=\"]", "").replace(" ", ""))) / 1000.0F;
            Float duration = Float.parseFloat((new ArrayList<>(List.of(child.split("t=\"[0-9]*\""))).get(1).replaceAll("[d=\"]", "").replaceAll(">.*", "").replace(" ", ""))) / 1000.0F;
            Float end = start + duration;
            int sequenceNumber = i + 1;

            String line = sequenceNumber + "\n" + srtTimeFormat(start) + " --> " + srtTimeFormat(end) + "\n" + caption + "\n";

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

        Path path = Paths.get(fullPath);

        if(filename.endsWith(".srt")){
            try {
                Files.writeString(path, xmlCaptionToSrt(), StandardCharsets.UTF_8);
            }
            catch (IOException ex) {
                System.out.print("Invalid Path");
            }
        }else{
            try {
                Files.writeString(path, getXmlCaptions(), StandardCharsets.UTF_8);
            }
            catch (IOException ex) {
                System.out.print("Invalid Path");
            }
        }

    }

}