package javatube;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

class InnerTube{
    public static String post(String videoId) throws IOException {
        StringBuilder output = new StringBuilder();
        URL url = new URL("https://www.youtube.com/youtubei/v1/player?videoId=" + videoId + "&key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8&contentCheckOk=True&racyCheckOk=True");
        String postData = "{\"context\": {\"client\": {\"clientName\": \"ANDROID\", \"clientVersion\": \"16.20\"}}}";

        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length()));

        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
            dos.writeBytes(postData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try (BufferedReader bf = new BufferedReader(new InputStreamReader(
                conn.getInputStream())))
        {

            boolean keepGoing = true;
            while (keepGoing) {
                String currentLine = bf.readLine();
                if (currentLine == null) {
                    keepGoing = false;
                } else {
                    output.append(currentLine);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return output.toString();
    }
    public static void get(String videoUrl, String outputFileName, String start, String end) throws IOException {

        URL url = new URL(videoUrl);
        URLConnection com = url.openConnection();
        com.setRequestProperty("Range", "bytes="+start+"-"+end);
        com.setRequestProperty("Method", "GET");
        try(
                ReadableByteChannel rbc = Channels.newChannel(com.getInputStream());
                FileOutputStream fos = new FileOutputStream(outputFileName, true)) {
            fos.getChannel().transferFrom(rbc, Integer.parseInt(start), Integer.parseInt(end));
        }
    }
    public static String downloadWebPage(String webpage) throws IOException {
        URL url = new URL(webpage);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestProperty ( "accept-language", "en-US,en" );
        con.setRequestProperty ( "User-Agent", "Mozilla/5.0" );
        InputStream ins = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader in = new BufferedReader(isr);
        String inputLine;
        String html = null;

        while ((inputLine = in.readLine()) != null) {
            html += inputLine;

        }
        in.close();
        return html;
    }

    public static String postContinuation(String continuation) throws IOException {
        StringBuilder output = new StringBuilder();
        URL url = new URL("https://www.youtube.com/youtubei/v1/browse?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8");
        String postData = "{\"continuation\": \"" + continuation + "\", \"context\": {\"client\": {\"clientName\": \"WEB\", \"clientVersion\": \"2.20200720.00.02\"}}}";

        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length()));

        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
            dos.writeBytes(postData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try (BufferedReader bf = new BufferedReader(new InputStreamReader(
                conn.getInputStream())))
        {

            boolean keepGoing = true;
            while (keepGoing) {
                String currentLine = bf.readLine();
                if (currentLine == null) {
                    keepGoing = false;
                } else {
                    output.append(currentLine);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return output.toString();
    }

}