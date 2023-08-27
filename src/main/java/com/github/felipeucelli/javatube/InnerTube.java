package com.github.felipeucelli.javatube;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

class InnerTube{
    private static JSONObject context;
    private static JSONObject header;
    private static String apiKey;

    public InnerTube(String client) throws JSONException {
        JSONObject defaultClient = new JSONObject("{" +
            "WEB: {" +
                "context: {" +
                    "client: {" +
                        "clientName: WEB," +
                        "clientVersion: 2.20200720.00.02" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"Mozilla/5.0\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
             "}," +
            "ANDROID: {" +
                "context: {" +
                    "client: {" +
                        "clientName: ANDROID," +
                        "clientVersion: 17.31.35," +
                        "androidSdkVersion: 30" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"com.google.android.youtube/\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +
            "IOS: {" +
                "context: {" +
                    "client: {" +
                        "clientName: IOS," +
                        "clientVersion: 17.33.2," +
                        "deviceModel: \"iPhone14,3\"" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"com.google.ios.youtube/\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +

            "WEB_EMBED: {" +
                "context: {" +
                    "client: {" +
                        "clientName: WEB_EMBEDDED_PLAYER," +
                        "clientVersion: 2.20210721.00.00," +
                        "clientScreen: EMBED" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"Mozilla/5.0\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +
            "ANDROID_EMBED: {" +
                "context: {" +
                    "client: {" +
                        "clientName: ANDROID_EMBEDDED_PLAYER," +
                        "clientVersion: 17.31.35," +
                        "clientScreen: EMBED," +
                        "androidSdkVersion: 30" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"com.google.android.youtube/\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +
            "IOS_EMBED: {" +
                "context: {" +
                    "client: {" +
                        "clientName: IOS_MESSAGES_EXTENSION," +
                        "clientVersion: 17.33.2," +
                        "deviceModel: \"iPhone14,3\"" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"com.google.ios.youtube/\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +

            "WEB_MUSIC: {" +
                "context: {" +
                    "client: {" +
                        "clientName: WEB_REMIX," +
                        "clientVersion: 1.20220727.01.00" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"Mozilla/5.0\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +
            "ANDROID_MUSIC: {" +
                "context: {" +
                    "client: {" +
                        "clientName: ANDROID_MUSIC," +
                        "clientVersion: 5.16.51," +
                        "androidSdkVersion: 30" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"com.google.android.apps.youtube.music/\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +
            "IOS_MUSIC: {" +
                "context: {" +
                    "client: {" +
                        "clientName: IOS_MUSIC," +
                        "clientVersion: 5.21," +
                        "deviceModel: \"iPhone14,3\"" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"com.google.ios.youtubemusic/\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +

            "WEB_CREATOR: {" +
                "context: {" +
                    "client: {" +
                        "clientName: WEB_CREATOR," +
                        "clientVersion: 1.20220726.00.00" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"Mozilla/5.0\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +
            "ANDROID_CREATOR: {" +
                "context: {" +
                    "client: {" +
                        "clientName: ANDROID_CREATOR," +
                        "clientVersion: 22.30.100," +
                        "androidSdkVersion: 30" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"com.google.android.apps.youtube.creator/\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +
            "IOS_CREATOR: {" +
                "context: {" +
                    "client: {" +
                        "clientName: IOS_CREATOR," +
                        "clientVersion: 22.33.101," +
                        "deviceModel: \"iPhone14,3\"" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"com.google.ios.ytcreator/\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +

            "MWEB: {" +
                "context: {" +
                    "client: {" +
                        "clientName: MWEB," +
                        "clientVersion: 2.20220801.00.00" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"Mozilla/5.0\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}," +

            "TV_EMBED: {" +
                "context: {" +
                    "client: {" +
                        "clientName: TVHTML5_SIMPLY_EMBEDDED_PLAYER," +
                        "clientVersion: 2.0" +
                    "}" +
                "}," +
                "header: {" +
                    "User-Agent: \"Mozilla/5.0\"" +
                "}," +
                "apiKey: AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8" +
            "}" +
        "}");

        context = defaultClient.getJSONObject(client).getJSONObject("context");
        header = defaultClient.getJSONObject(client).getJSONObject("header");
        apiKey = defaultClient.getJSONObject(client).getString("apiKey");
    }

    private String getBaseUrl(){
        return "https://www.youtube.com/youtubei/v1";
    }

    public JSONObject getBaseData() throws JSONException {
        return  new JSONObject("{context: " + context + "}");
    }

    private String getBaseParam(){
        return "key: " + apiKey + "," +
                "contentCheckOk: \"true\"," +
                "racyCheckOk: \"true\"";
    }

    private String urlEncode(JSONObject json){
        StringBuilder query = new StringBuilder();
        try {
            for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                String key = it.next();
                String value = json.getString(key);
                query.append(URLEncoder.encode(key, "UTF-8"));
                query.append("=");
                query.append(URLEncoder.encode(value, "UTF-8"));
                query.append("&");
            }
            if (query.length() > 0) {
                query.setLength(query.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return query.toString();
    }

    private JSONObject callApi(String endpoint, JSONObject query, JSONObject data) throws Exception {

        String endpointUrl = endpoint + "?" + urlEncode(query);

        HashMap<String, String> headers = new HashMap<>();
        Iterator<String> keys = header.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = header.getString(key);
            headers.put(key, value);
        }

        ByteArrayOutputStream response = Request.post(endpointUrl, data.toString(), headers);
        return new JSONObject(response.toString());
    }

    public JSONObject player(String videoId) throws Exception {
        String endpoint = getBaseUrl() + "/player";
        JSONObject query = new JSONObject("{videoId: " + videoId + ", " + getBaseParam() + "}");
        return callApi(endpoint, query, getBaseData());
    }

    public JSONObject search(String searchQuery) throws Exception {
        String endpoint = getBaseUrl() + "/search";
        JSONObject query = new JSONObject("{query: " + searchQuery + ", " + getBaseParam() + "}");
        return callApi(endpoint, query, getBaseData());
    }
}