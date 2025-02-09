package com.github.felipeucelli.javatube;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class InnerTube{
    private static JSONObject innerTubeContext;
    private static boolean requireJsPlayer;
    private static boolean requirePoToken;
    private static JSONObject header;
    private static String apiKey;

    private final boolean usePoToken;
    private String accessPoToken;
    private String accessVisitorData;

    JSONObject defaultClient = new JSONObject("""
                {
                  "WEB": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "WEB",
                          "osName": "Windows",
                          "osVersion": "10.0",
                          "clientVersion": "2.20250122.01.00",
                          "platform": "DESKTOP"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "Mozilla/5.0",
                      "X-Youtube-Client-Name": "1",
                      "X-Youtube-Client-Version": "2.20250122.01.00"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "true",
                    "requirePoToken": "true"
                  },
                
                  "WEB_EMBED": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "WEB_EMBEDDED_PLAYER",
                          "osName": "Windows",
                          "osVersion": "10.0",
                          "clientVersion": "2.20240530.02.00",
                          "clientScreen": "EMBED"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "Mozilla/5.0",
                      "X-Youtube-Client-Name": "56"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "true",
                    "requirePoToken": "true"
                  },
                
                  "WEB_MUSIC": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "WEB_REMIX",
                          "clientVersion": "1.20240403.01.00"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "Mozilla/5.0",
                      "X-Youtube-Client-Name": "67"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "true",
                    "requirePoToken": "false"
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
                    "requireJsPlayer": "true",
                    "requirePoToken": "false"
                  },
                
                  "WEB_SAFARI": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "WEB",
                          "clientVersion": "2.20240726.00.00"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.5 Safari/605.1.15,gzip(gfe)",
                      "X-Youtube-Client-Name": "1"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "true",
                    "requirePoToken": "true"
                  },
                
                  "MWEB": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "MWEB",
                          "clientVersion": "2.20241202.07.00"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "Mozilla/5.0 (iPad; CPU OS 16_7_10 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1,gzip(gfe)",
                      "X-Youtube-Client-Name": "2"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "true",
                    "requirePoToken": "true"
                  },
                
                  "WEB_KIDS": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "WEB_KIDS",
                          "osName": "Windows",
                          "osVersion": "10.0",
                          "clientVersion": "2.20241125.00.00",
                          "platform": "DESKTOP"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "Mozilla/5.0"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "true",
                    "requirePoToken": "false"
                  },
                
                
                  "ANDROID": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "ANDROID",
                          "clientVersion": "19.44.38",
                          "platform": "MOBILE",
                          "osName": "Android",
                          "osVersion": "14",
                          "androidSdkVersion": "34"
                        }
                      },
                      "params": "2AMB"
                    },
                    "header": {
                      "User-Agent": "com.google.android.youtube/",
                      "X-Youtube-Client-Name": "3"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "true"
                  },
                
                  "ANDROID_VR": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "ANDROID_VR",
                          "clientVersion": "1.60.19",
                          "deviceMake": "Oculus",
                          "deviceModel": "Quest 3",
                          "osName": "Android",
                          "osVersion": "12L",
                          "androidSdkVersion": "32"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.android.apps.youtube.vr.oculus/1.60.19 (Linux; U; Android 12L; eureka-user Build/SQ3A.220605.009.A1) gzip",
                      "X-Youtube-Client-Name": "28"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                  "ANDROID_MUSIC": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "ANDROID_MUSIC",
                          "clientVersion": "7.27.52",
                          "androidSdkVersion": "30",
                          "osName": "Android",
                          "osVersion": "11"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.android.apps.youtube.music/7.27.52 (Linux; U; Android 11) gzip",
                      "X-Youtube-Client-Name": "21"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                  "ANDROID_CREATOR": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "ANDROID_CREATOR",
                          "clientVersion": "24.45.100",
                          "androidSdkVersion": "30",
                          "osName": "Android",
                          "osVersion": "11"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.android.apps.youtube.creator/24.45.100 (Linux; U; Android 11) gzip",
                      "X-Youtube-Client-Name": "14"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                  "ANDROID_TESTSUITE": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "ANDROID_TESTSUITE",
                          "clientVersion": "1.9",
                          "platform": "MOBILE",
                          "osName": "Android",
                          "osVersion": "14",
                          "androidSdkVersion": "34"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.android.youtube/",
                      "X-Youtube-Client-Name": "30",
                      "X-Youtube-Client-Version": "1.9"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                  "ANDROID_PRODUCER": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "ANDROID_PRODUCER",
                          "clientVersion": "0.111.1",
                          "androidSdkVersion": "30",
                          "osName": "Android",
                          "osVersion": "11"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.android.apps.youtube.producer/0.111.1 (Linux; U; Android 11) gzip",
                      "X-Youtube-Client-Name": "91"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                  "ANDROID_KIDS": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "ANDROID_KIDS",
                          "clientVersion": "7.36.1",
                          "osName": "Android",
                          "osVersion": "11",
                          "androidSdkVersion": "30"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.android.youtube/"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                  "IOS": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "IOS",
                          "clientVersion": "19.45.4",
                          "deviceMake": "Apple",
                          "platform": "MOBILE",
                          "osName": "iPhone",
                          "osVersion": "18.1.0.22B83",
                          "deviceModel": "iPhone16,2"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.ios.youtube/19.45.4 (iPhone16,2; U; CPU iOS 18_1_0 like Mac OS X;)",
                      "X-Youtube-Client-Name": "5"
                    },
                    "apiKey": "AIzaSyB-63vPrdThhKuerbB2N_l7Kwwcxj6yUAc",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                  "IOS_MUSIC": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "IOS_MUSIC",
                          "clientVersion": "7.27.0",
                          "deviceMake": "Apple",
                          "platform": "MOBILE",
                          "osName": "iPhone",
                          "osVersion": "18.1.0.22B83",
                          "deviceModel": "iPhone16,2"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.ios.youtubemusic/7.27.0 (iPhone16,2; U; CPU iOS 18_1_0 like Mac OS X;)",
                      "X-Youtube-Client-Name": "26"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                  "IOS_CREATOR": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "IOS_CREATOR",
                          "clientVersion": "24.45.100",
                          "deviceMake": "Apple",
                          "deviceModel": "iPhone16,2",
                          "osName": "iPhone",
                          "osVersion": "18.1.0.22B83"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.ios.ytcreator/24.45.100 (iPhone16,2; U; CPU iOS 18_1_0 like Mac OS X;)",
                      "X-Youtube-Client-Name": "15"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                  "IOS_KIDS": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "IOS_KIDS",
                          "clientVersion": "7.36.1",
                          "deviceMake": "Apple",
                          "platform": "MOBILE",
                          "osName": "iPhone",
                          "osVersion": "18.1.0.22B83",
                          "deviceModel": "iPhone16,2"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "com.google.ios.youtube/19.45.4 (iPhone16,2; U; CPU iOS 18_1_0 like Mac OS X;)"
                    },
                    "apiKey": "AIzaSyB-63vPrdThhKuerbB2N_l7Kwwcxj6yUAc",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  },
                
                   "TV": {
                      "innerTubeContext": {
                          "context": {
                              "client": {
                                  "clientName": "TVHTML5",
                                  "clientVersion": "7.20240813.07.00",
                                  "platform": "TV"
                              }
                          }
                      },
                      "header": {
                          "User-Agent": "Mozilla/5.0",
                          "X-Youtube-Client-Name": "7"
                      },
                      "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                      "requireJsPlayer": "true",
                    "requirePoToken": "false"
                  },
                
                  "TV_EMBED": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "TVHTML5_SIMPLY_EMBEDDED_PLAYER",
                          "clientVersion": "2.0",
                          "clientScreen": "EMBED",
                          "platform": "TV"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "Mozilla/5.0",
                      "X-Youtube-Client-Name": "85"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "true",
                    "requirePoToken": "false"
                  },
                
                  "MEDIA_CONNECT": {
                    "innerTubeContext": {
                      "context": {
                        "client": {
                          "clientName": "MEDIA_CONNECT_FRONTEND",
                          "clientVersion": "0.1"
                        }
                      }
                    },
                    "header": {
                      "User-Agent": "Mozilla/5.0",
                      "X-Youtube-Client-Name": "95"
                    },
                    "apiKey": "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
                    "requireJsPlayer": "false",
                    "requirePoToken": "false"
                  }
                }
                """);


    /**
     * @Clients:
     *          WEB,
     *          WEB_EMBED,
     *          WEB_MUSIC,
     *          WEB_CREATOR,
     *          WEB_SAFARI,
     *          MWEB,
     *          ANDROID,
     *          ANDROID_VR,
     *          ANDROID_MUSIC,
     *          ANDROID_CREATOR,
     *          ANDROID_TESTSUITE,
     *          ANDROID_PRODUCER,
     *          IOS,
     *          IOS_MUSIC,
     *          IOS_CREATOR,
     *          TV_EMBED,
     *          MEDIA_CONNECT
     * */
    public InnerTube(String client, boolean usePoToken, boolean allowCache) throws JSONException {

        innerTubeContext = defaultClient.getJSONObject(client).getJSONObject("innerTubeContext");
        requireJsPlayer = defaultClient.getJSONObject(client).getBoolean("requireJsPlayer");
        requirePoToken = defaultClient.getJSONObject(client).getBoolean("requirePoToken");
        header = defaultClient.getJSONObject(client).getJSONObject("header");

        // API keys are not required, see: https://github.com/TeamNewPipe/NewPipeExtractor/pull/1168
        apiKey = defaultClient.getJSONObject(client).getString("apiKey");

        this.usePoToken = usePoToken;

        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            Path path = Paths.get(tempDir, "tokens.json");

            if (usePoToken && allowCache && Files.exists(path)) {
                String content = new String(Files.readAllBytes(path));
                JSONObject jsonObject = new JSONObject(content);
                accessVisitorData = jsonObject.getString("visitorData");
                accessPoToken = jsonObject.getString("poToken");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Clients:
     *          WEB,
     *          WEB_EMBED,
     *          WEB_MUSIC,
     *          WEB_CREATOR,
     *          WEB_SAFARI,
     *          MWEB,
     *          ANDROID,
     *          ANDROID_VR,
     *          ANDROID_MUSIC,
     *          ANDROID_CREATOR,
     *          ANDROID_TESTSUITE,
     *          ANDROID_PRODUCER,
     *          IOS,
     *          IOS_MUSIC,
     *          IOS_CREATOR,
     *          TV_EMBED,
     *          MEDIA_CONNECT
     * */
    public InnerTube(String client, boolean usePoToken) throws JSONException {
        this(client, usePoToken, false);
    }

    /**
     * @Clients:
     *          WEB,
     *          WEB_EMBED,
     *          WEB_MUSIC,
     *          WEB_CREATOR,
     *          WEB_SAFARI,
     *          MWEB,
     *          ANDROID,
     *          ANDROID_VR,
     *          ANDROID_MUSIC,
     *          ANDROID_CREATOR,
     *          ANDROID_TESTSUITE,
     *          ANDROID_PRODUCER,
     *          IOS,
     *          IOS_MUSIC,
     *          IOS_CREATOR,
     *          TV_EMBED,
     *          MEDIA_CONNECT
     * */
    public InnerTube(String client) throws JSONException {
        this(client, false, false);
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
    @Deprecated
    public String getClientApiKey() throws JSONException {
        return apiKey;
    }
    public boolean getRequireJsPlayer(){
        return requireJsPlayer;
    }

    public boolean getRequirePoToken(){
        return requirePoToken;
    }

    public String getVisitorData(){
        return accessVisitorData;
    }

    public String getPoToken(){
        return accessPoToken;
    }

    private String getBaseUrl(){
        return "https://www.youtube.com/youtubei/v1";
    }

    private String getBaseParam(){
        return "{prettyPrint: \"false\"}";
    }

    private String[] defaultPoTokenVerifier(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("You can use the tool: https://github.com/YunzheZJU/youtube-po-token-generator, to get the token");
        System.out.print("Enter with your visitorData: ");
        String visitorData = scanner.nextLine();
        System.out.print("Enter with your PoToken: ");
        String poToken = scanner.nextLine();
        return new String[]{visitorData, poToken};
    }

    public void cacheTokens() throws JSONException {
        if (usePoToken){
            JSONObject data = new JSONObject(
                    "{" +
                                "\"visitorData\": \"" + accessVisitorData + "\"," +
                                "\"poToken\": \"" + accessPoToken + "\"" +
                            "}"
            );

            try {
                String tempDir = System.getProperty("java.io.tmpdir");
                Path path = Paths.get(tempDir, "tokens.json");
                Files.write(path, data.toString(4).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertVisitorData(String visitorData) throws JSONException {
        JSONObject context = new JSONObject(
                "{" +
                            "\"context\": {" +
                                "\"client\": {" +
                                    "\"visitorData\": \"" + visitorData + "\"" +
                                "}"+
                            "}" +
                        "}"
        );
        updateInnerTubeContext(innerTubeContext, context);
    }

    public void insetPoToken() throws JSONException {
        JSONObject context = new JSONObject(
                "{" +
                            "\"context\": {" +
                                "\"client\": {" +
                                    "\"visitorData\": \"" + accessVisitorData + "\"" +
                                "}"+
                            "}," +
                            "\"serviceIntegrityDimensions\": {" +
                                "\"poToken\": \"" + accessPoToken + "\"" +
                            "}" +
                        "}"
        );
        updateInnerTubeContext(innerTubeContext, context);
    }

    public void insetPoToken(String poToken, String visitorData) throws JSONException {
        JSONObject context = new JSONObject(
                "{" +
                            "\"context\": {" +
                                "\"client\": {" +
                                    "\"visitorData\": \"" + visitorData + "\"" +
                                "}"+
                            "}," +
                            "\"serviceIntegrityDimensions\": {" +
                                "\"poToken\": \"" + poToken + "\"" +
                            "}" +
                        "}"
        );
        updateInnerTubeContext(innerTubeContext, context);
    }

    public void fetchPoToken() throws JSONException {
        String[] token = defaultPoTokenVerifier();
        accessVisitorData = token[0];
        accessPoToken = token[1];
        cacheTokens();
        insetPoToken();
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

    private JSONObject callApi(String endpoint, JSONObject query) throws Exception {

        String endpointUrl = endpoint + "?" + urlEncode(query);

        if(usePoToken){
            if(accessPoToken != null){
                insetPoToken();
            }else {
                fetchPoToken();
            }
        }

        ByteArrayOutputStream response = Request.post(endpointUrl, getInnerTubeContext().toString(), getHeaderMap());
        return new JSONObject(response.toString());
    }

    public JSONObject player(String videoId) throws Exception {
        String endpoint = getBaseUrl() + "/player";
        JSONObject query = new JSONObject(getBaseParam());
        JSONObject context = new JSONObject("{videoId: " + videoId + ", " + "contentCheckOk: \"true\"" + "}");
        updateInnerTubeContext(getInnerTubeContext(), context);
        return callApi(endpoint, query);
    }

    public JSONObject browse(JSONObject data) throws Exception {
        String endpoint = getBaseUrl() + "/browse";
        JSONObject query = new JSONObject(getBaseParam());
        updateInnerTubeContext(getInnerTubeContext(), data);
        return callApi(endpoint, query);
    }

    public JSONObject search(String searchQuery, String continuationToken) throws Exception {
        String endpoint = getBaseUrl() + "/search";
        JSONObject query = new JSONObject(getBaseParam());
        JSONObject contextQuery = new JSONObject("{query: " + searchQuery + "}");
        updateInnerTubeContext(getInnerTubeContext(), contextQuery);
        if(!Objects.equals(continuationToken, "")){
            updateInnerTubeContext(getInnerTubeContext(), new JSONObject("{continuation:" + continuationToken + "}"));
        }
        return callApi(endpoint, query);
    }
}