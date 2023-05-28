package javatube;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

class InnerTube{

    private static final int ConnectTimeout = 5000;
    private static final int ReadTimeout = 10000;

    public static String post(String param, String data) throws IOException {
        StringBuilder output = new StringBuilder();
        URL url = new URL(param);

        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(ConnectTimeout);
        connection.setReadTimeout(ReadTimeout);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", Integer.toString(data.length()));

        try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
            dos.writeBytes(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(
                connection.getInputStream())))
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

    public static ByteArrayOutputStream get(String videoUrl) throws IOException {

        URL urlObj = new URL(videoUrl);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(ConnectTimeout);
        connection.setReadTimeout(ReadTimeout);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream in = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            connection.disconnect();
            return out;
        } else {
            throw new IOException("Connection fail. Response code: " + responseCode);
        }
    }

    public static String downloadWebPage(String webpage) throws IOException {
        URL url = new URL(webpage);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty ( "accept-language", "en-US,en" );
        connection.setRequestProperty ( "User-Agent", "Mozilla/5.0" );
        connection.setConnectTimeout(ConnectTimeout);
        connection.setReadTimeout(ReadTimeout);
        InputStream ins = connection.getInputStream();
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