package com.github.felipeucelli.javatube;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

class InnerTube{
    private static JSONObject innerTubeContext;
    private static boolean requireJsPlayer;
    private static JSONObject header;
    private static String apiKey;

    /**
     * @Clients:
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
     *          ANDROID_TESTSUITE,
     *          MWEB,
     *          TV_EMBED
     * */
    public InnerTube(String client) throws JSONException {
        JSONObject defaultClient = new JSONObject("""
            {
               "WEB": {
                 "innerTubeContext": {
                   "context": {
                     "client": {
                       "clientName": "WEB",
                       "clientVersion": "2.20240430.01.00"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "Mozilla/5.0",
                   "X-Youtube-Client-Name": "1"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "true"
               },
               "ANDROID": {
                 "innerTubeContext": {
                   "context": {
                     "client": {
                       "clientName": "ANDROID",
                       "clientVersion": "19.08.35",
                       "androidSdkVersion": "30"
                     }
                   },
                   "params": "CgIIAdgDAQ%3D%3D"
                 },
                 "header": {
                   "User-Agent": "com.google.android.youtube/",
                   "X-Youtube-Client-Name": "3"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "false"
               },
               "IOS": {
                 "innerTubeContext": {
                   "context": {
                     "client": {
                       "clientName": "IOS",
                       "clientVersion": "19.08.35",
                       "deviceModel": "iPhone14,3"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.ios.youtube/",
                   "X-Youtube-Client-Name": "5"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "false"
               },
               
               "WEB_EMBED": {
                 "innerTubeContext": {
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
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "true"
               },
               "ANDROID_EMBED": {
                 "innerTubeContext": {
                   "context": {
                     "client": {
                       "clientName": "ANDROID_EMBEDDED_PLAYER",
                       "clientVersion": "19.08.35",
                       "clientScreen": "EMBED",
                       "androidSdkVersion": "30"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.android.youtube/",
                   "X-Youtube-Client-Name": "55"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "false"
               },
               "IOS_EMBED": {
                 "innerTubeContext": {
                   "context": {
                     "client": {
                       "clientName": "IOS_MESSAGES_EXTENSION",
                       "clientVersion": "19.08.35",
                       "deviceModel": "iPhone14,3"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.ios.youtube/",
                   "X-Youtube-Client-Name": "66"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "false"
               },
               
               "WEB_MUSIC": {
                 "innerTubeContext": {
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
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "true"
               },
               "ANDROID_MUSIC": {
                 "innerTubeContext": {
                   "context": {
                     "client": {
                       "clientName": "ANDROID_MUSIC",
                       "clientVersion": "6.40.52",
                       "androidSdkVersion": "30"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.android.apps.youtube.music/",
                   "X-Youtube-Client-Name": "21"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "false"
               },
               "IOS_MUSIC": {
                 "innerTubeContext": {
                   "context": {
                     "client": {
                       "clientName": "IOS_MUSIC",
                       "clientVersion": "6.41",
                       "deviceModel": "iPhone14,3"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.ios.youtubemusic/",
                   "X-Youtube-Client-Name": "26"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "false"
               },
               
               "WEB_CREATOR": {
                 "innerTubeContext": {
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
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "true"
               },
               "ANDROID_CREATOR": {
                 "innerTubeContext": {
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
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "false"
               },
               "IOS_CREATOR": {
                 "innerTubeContext": {
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
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "false"
               },
               "ANDROID_TESTSUITE": {
                 "innerTubeContext": {
                   "context": {
                     "client": {
                       "clientName": "ANDROID_TESTSUITE",
                       "clientVersion": "1.9",
                       "androidSdkVersion": "30"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "com.google.android.youtube/",
                   "X-Youtube-Client-Name": "30"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "false"
               },
               "MWEB": {
                 "innerTubeContext": {
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
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "true"
               },
               
               "TV_EMBED": {
                 "innerTubeContext": {
                   "context": {
                     "client": {
                       "clientName": "TVHTML5_SIMPLY_EMBEDDED_PLAYER",
                       "clientVersion": "2.0"
                     }
                   }
                 },
                 "header": {
                   "User-Agent": "Mozilla/5.0",
                   "X-Youtube-Client-Name": "85"
                 },
                 "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                 "requireJsPlayer": "true"
               }
             }
            """);

        innerTubeContext = defaultClient.getJSONObject(client).getJSONObject("innerTubeContext");
        requireJsPlayer = defaultClient.getJSONObject(client).getBoolean("requireJsPlayer");
        header = defaultClient.getJSONObject(client).getJSONObject("header");
        apiKey = defaultClient.getJSONObject(client).getString("apiKey");
    }
    public JSONObject getInnerTubeContext() throws JSONException {
        return innerTubeContext;
    }
    public void updateInnerTubeContext(JSONObject innerTubeContext, JSONObject extraInfo) throws JSONException {
        for (Iterator<String> it = extraInfo.keys(); it.hasNext(); ) {
            String key = it.next();
            if (innerTubeContext.has(key) && innerTubeContext.get(key) instanceof JSONObject) {
                updateInnerTubeContext(innerTubeContext.getJSONObject(key), extraInfo.getJSONObject(key));
            } else {
                innerTubeContext.put(key, extraInfo.get(key));
            }
        }
    }
    public Map<String, String> getClientHeaders() throws JSONException {
        return getHeaderMap();
    }
    public String getClientApiKey() throws JSONException {
        return apiKey;
    }
    public boolean getRequireJsPlayer(){
        return requireJsPlayer;
    }

    private String getBaseUrl(){
        return "https://www.youtube.com/youtubei/v1";
    }

    private String getBaseParam(){
        return "key: " + apiKey + "," +
                "contentCheckOk: \"true\"," +
                "racyCheckOk: \"true\"";
    }
    private String urlEncode(JSONObject json) throws JSONException, UnsupportedEncodingException {
        StringBuilder query = new StringBuilder();
        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String key = it.next();
            String value = json.getString(key);
            query.append(URLEncoder.encode(key, StandardCharsets.UTF_8.name()));
            query.append("=");
            query.append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
            query.append("&");
        }
        if (query.length() != 0) {
            query.setLength(query.length() - 1);
        }
        return query.toString();
    }

    private Map<String, String> getHeaderMap() throws JSONException {
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
        return callApi(endpoint, query, getInnerTubeContext());
    }

    public JSONObject browse(JSONObject data) throws Exception {
        String endpoint = getBaseUrl() + "/browse";
        JSONObject query = new JSONObject("{" + getBaseParam() + "}");
        updateInnerTubeContext(getInnerTubeContext(), data);
        return callApi(endpoint, query, getInnerTubeContext());
    }

    public JSONObject search(String searchQuery, String continuationToken) throws Exception {
        String endpoint = getBaseUrl() + "/search";
        JSONObject query = new JSONObject("{query: " + searchQuery + ", " + getBaseParam() + "}");
        if(!Objects.equals(continuationToken, "")){
            updateInnerTubeContext(getInnerTubeContext(), new JSONObject("{continuation:" + continuationToken + "}"));
        }
        return callApi(endpoint, query, getInnerTubeContext());
    }
}