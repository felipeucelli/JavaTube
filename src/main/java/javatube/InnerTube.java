package javatube;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

class InnerTube{
    public static String post(String param, String data) throws IOException {
        StringBuilder output = new StringBuilder();
        URL url = new URL(param);

        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", Integer.toString(data.length()));

        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
            dos.writeBytes(data);
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
        StringBuilder html = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            html.append(inputLine);
        }
        in.close();
        return html.toString();
    }

    public static ByteArrayOutputStream postChunk(String chunk) throws IOException {
        URL url = new URL(chunk);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while (-1!=(n=in.read(buf))) {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();

        return out;
    }

}