package com.github.felipeucelli.javatube;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class InnerTube{
    private static JSONObject baseData;
    private static JSONObject header;
    private static String apiKey;

    /**
     * Clients:
     *          WEB,
     *          WEB_EMBED,
     *          WEB_MUSIC,
     *          WEB_CREATOR,
     *          ANDROID,
     *          ANDROID_EMBED,
     *          ANDROID_MUSIC,
     *          ANDROID_CREATOR ,
     *          IOS,
     *          IOS_EMBED,
     *          IOS_MUSIC,
     *          IOS_CREATOR,
     *          MWEB,
     *          TV_EMBED
     * */
    public InnerTube(String client) throws JSONException {
        JSONObject defaultClient = new JSONObject("""
            {
               "WEB": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "WEB",
                       "clientVersion": "2.20200720.00.02"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "Mozilla/5.0",
                   "X-Youtube-Client-Name": "1"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               "ANDROID": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "ANDROID",
                       "clientVersion": "17.31.35",
                       "androidSdkVersion": "30"
                     }
                   },
                   "params": "CgIQBg"
                 },
                 "header": {
                   "User-Agent": "com.google.android.youtube/",
                   "X-Youtube-Client-Name": "3"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               "IOS": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "IOS",
                       "clientVersion": "17.33.2",
                       "deviceModel": "iPhone14,3"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.ios.youtube/",
                   "X-Youtube-Client-Name": "5"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               
               "WEB_EMBED": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "WEB_EMBEDDED_PLAYER",
                       "clientVersion": "2.20210721.00.00",
                       "clientScreen": "EMBED"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "Mozilla/5.0",
                   "X-Youtube-Client-Name": "56"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               "ANDROID_EMBED": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "ANDROID_EMBEDDED_PLAYER",
                       "clientVersion": "17.31.35",
                       "clientScreen": "EMBED",
                       "androidSdkVersion": "30"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.android.youtube/",
                   "X-Youtube-Client-Name": "55"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               "IOS_EMBED": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "IOS_MESSAGES_EXTENSION",
                       "clientVersion": "17.33.2",
                       "deviceModel": "iPhone14,3"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.ios.youtube/",
                   "X-Youtube-Client-Name": "66"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               
               "WEB_MUSIC": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "WEB_REMIX",
                       "clientVersion": "1.20220727.01.00"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "Mozilla/5.0",
                   "X-Youtube-Client-Name": "67"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               "ANDROID_MUSIC": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "ANDROID_MUSIC",
                       "clientVersion": "5.16.51",
                       "androidSdkVersion": "30"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.android.apps.youtube.music/",
                   "X-Youtube-Client-Name": "21"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               "IOS_MUSIC": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "IOS_MUSIC",
                       "clientVersion": "5.21",
                       "deviceModel": "iPhone14,3"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.ios.youtubemusic/"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                   "X-Youtube-Client-Name": "26"
               },
               
               "WEB_CREATOR": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "WEB_CREATOR",
                       "clientVersion": "1.20220726.00.00"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "Mozilla/5.0",
                   "X-Youtube-Client-Name": "62"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               "ANDROID_CREATOR": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "ANDROID_CREATOR",
                       "clientVersion": "22.30.100",
                       "androidSdkVersion": "30"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.android.apps.youtube.creator/",
                   "X-Youtube-Client-Name": "14"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               "IOS_CREATOR": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "IOS_CREATOR",
                       "clientVersion": "22.33.101",
                       "deviceModel": "iPhone14,3"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.ios.ytcreator/",
                   "X-Youtube-Client-Name": "15"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               
               "MWEB": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "MWEB",
                       "clientVersion": "2.20220801.00.00"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36",
                   "X-Youtube-Client-Name": "2"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               },
               
               "TV_EMBED": {
                 "baseData": {
                   "context": {
                     "client": {
                       "clientName": "TVHTML5_SIMPLY_EMBEDDED_PLAYER",
                       "clientVersion": "2.0"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36",
                   "X-Youtube-Client-Name": "85"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
               }
             }
            """);

        baseData = defaultClient.getJSONObject(client).getJSONObject("baseData");
        header = defaultClient.getJSONObject(client).getJSONObject("header");
        apiKey = defaultClient.getJSONObject(client).getString("apiKey");
    }

    private String getBaseUrl(){
        return "https://www.youtube.com/youtubei/v1";
    }

    public JSONObject getClientBaseData() throws JSONException {
        return baseData;
    }
    public Map<String, String> getClientHeaders() throws JSONException {
        return getHeaderMap();
    }
    public String getClientApiKey() throws JSONException {
        return apiKey;
    }

    private String getBaseParam(){
        return "key: " + apiKey + "," +
                "contentCheckOk: \"true\"," +
                "racyCheckOk: \"true\"";
    }

    private String urlEncode(JSONObject json){
        StringBuilder query = new StringBuilder();
        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String key = it.next();
            String value = json.getString(key);
            query.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
            query.append("=");
            query.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            query.append("&");
        }
        if (!query.isEmpty()) {
            query.setLength(query.length() - 1);
        }
        return query.toString();
    }

    private Map<String, String> getHeaderMap(){
        HashMap<String, String> headers = new HashMap<>();
        Iterator<String> keys = header.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = header.getString(key);
            headers.put(key, value);
        }
        return headers;
    }

    private JSONObject callApi(String endpoint, JSONObject query, JSONObject data) throws Exception {

        String endpointUrl = endpoint + "?" + urlEncode(query);

        ByteArrayOutputStream response = Request.post(endpointUrl, data.toString(), getHeaderMap());
        return new JSONObject(response.toString());
    }

    public JSONObject player(String videoId) throws Exception {
        String endpoint = getBaseUrl() + "/player";
        JSONObject query = new JSONObject("{videoId: " + videoId + ", " + getBaseParam() + "}");
        return callApi(endpoint, query, getClientBaseData());
    }

    public JSONObject search(String searchQuery) throws Exception {
        String endpoint = getBaseUrl() + "/search";
        JSONObject query = new JSONObject("{query: " + searchQuery + ", " + getBaseParam() + "}");
        return callApi(endpoint, query, getClientBaseData());
    }
}