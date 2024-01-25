package com.github.felipeucelli.javatube;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

class Request{
    private static ByteArrayOutputStream executeRequest(String url, String method, String data, Map<String, String> headers) throws IOException {
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        int attempts = 0;
        int maxAttempts = 3;

        while (attempts <= maxAttempts) {
            try {
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("accept-language", "en-US,en");

                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        connection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                if (data != null && method.equalsIgnoreCase("POST")) {
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length()));
                    connection.getOutputStream().write(data.getBytes());
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    byte[] buf = new byte[1024];
                    int n;
                    while ((n = in.read(buf)) != -1) {
                        responseStream.write(buf, 0, n);
                    }
                    responseStream.close();
                    in.close();
                } else {
                    throw new IOException("HTTP request failed with response code: " + responseCode);
                }
                connection.disconnect();
                break;
            } catch (SocketTimeoutException e) {
                if (attempts == maxAttempts) {
                    throw new IOException("Timeout occurred during the request.", e);
                }
                attempts += 1;
            } catch (IOException e) {
                if (attempts == maxAttempts) {
                    throw e;
                }
                attempts += 1;
            }
        }
        return responseStream;
    }

    public static ByteArrayOutputStream get(String url, String data, Map<String, String> headers) throws Exception {
        return executeRequest(url, "GET", data, headers);
    }
    public static ByteArrayOutputStream get(String url) throws Exception {
        return executeRequest(url, "GET", null, null);
    }
    public static ByteArrayOutputStream get(String url, String data) throws Exception {
        return executeRequest(url, "GET", data, null);
    }
    public static ByteArrayOutputStream get(String url, Map<String, String> headers) throws Exception {
        return executeRequest(url, "GET", null, headers);
    }

    public static ByteArrayOutputStream post(String url, String data, Map<String, String> headers) throws Exception {
        return executeRequest(url, "POST", data, headers);
    }
    public static ByteArrayOutputStream post(String url) throws Exception {
        return executeRequest(url, "POST", null, null);
    }
    public static ByteArrayOutputStream post(String url, String data) throws Exception {
        return executeRequest(url, "POST", data, null);
    }
    public static ByteArrayOutputStream post(String url, Map<String, String> headers) throws Exception {
        return executeRequest(url, "POST", null, headers);
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