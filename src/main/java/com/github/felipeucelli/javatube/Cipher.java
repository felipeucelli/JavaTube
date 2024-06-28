package com.github.felipeucelli.javatube;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cipher {

    private static String playerJs;
    private static JsInterpreter jsInterpreter;
    private static String signatureFunctionName;
    private static String throttlingFunctionName;

    public Cipher(String js, String ytPlayerJs) throws Exception {
        playerJs = ytPlayerJs;
        jsInterpreter = new JsInterpreter(js);
        signatureFunctionName = getInitialFunctionName(js);
        throttlingFunctionName = getThrottlingFunctionName(js);
    }
    private static String getInitialFunctionName(String js) throws Exception {
        String[] functionPattern = {"\\b[cs]\\s*&&\\s*[adf]\\.set\\([^,]+\\s*,\\s*encodeURIComponent\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
                "\\b[a-zA-Z0-9]+\\s*&&\\s*[a-zA-Z0-9]+\\.set\\([^,]+\\s*,\\s*encodeURIComponent\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
                "(?:\\b|[^a-zA-Z0-9$])([a-zA-Z0-9$]{2,3})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)",
                "([a-zA-Z0-9$]+)\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)",
                "([\"\\'])signature\\1\\s*,\\s*([a-zA-Z0-9$]+)\\(",
                "\\.sig\\|\\|([a-zA-Z0-9$]+)\\(",
                "yt\\.akamaized\\.net/\\)\\s*\\|\\|\\s*.*?\\s*[cs]\\s*&&\\s*[adf]\\.set\\([^,]+\\s*,\\s*(?:encodeURIComponent\\s*\\()?\\s*([a-zA-Z0-9$]+)\\(",
                "\\b[cs]\\s*&&\\s*[adf]\\.set\\([^,]+\\s*,\\s*([a-zA-Z0-9$]+)\\(",
                "\\b[a-zA-Z0-9]+\\s*&&\\s*[a-zA-Z0-9]+\\.set\\([^,]+\\s*,\\s*([a-zA-Z0-9$]+)\\(",
                "\\bc\\s*&&\\s*a\\.set\\([^,]+\\s*,\\s*\\([^)]*\\)\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
                "\\bc\\s*&&\\s*[a-zA-Z0-9]+\\.set\\([^,]+\\s*,\\s*\\([^)]*\\)\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
                "\\bc\\s*&&\\s*[a-zA-Z0-9]+\\.set\\([^,]+\\s*,\\s*\\([^)]*\\)\\s*\\(\\s*([a-zA-Z0-9$]+)\\("};
        for(String pattern : functionPattern){
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(js);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        throw new Exception("RegexMatcherError. Could not find function name in playerJs:" + playerJs);
    }
    private String getThrottlingFunctionName(String js) throws Exception {
        // a.D && (b = a.get("n")) && (b = Usa[0](b), a.set("n", b), Usa.length || mma(""))
        String functionPatterns = "a\\.[a-zA-Z]\\s*&&\\s*\\([a-z]\\s*=\\s*a\\.get\\(\\\"n\\\"\\)\\)\\s*&&\\s*\\([a-z]=([$a-zA-Z]*\\[\\d\\]).*?\\)";
        Pattern regex = Pattern.compile(functionPatterns);
        Matcher matcher = regex.matcher(js);
        if (matcher.find()){
            String idx = matcher.group(1); // Usa[0]
            String funName = Pattern.quote(matcher.group(1).replaceAll("(\\[\\d\\])", "")); // Usa
            if(!idx.isEmpty()){
                String pattern2 = "var " + funName + "\\s*=\\s*(\\[.+?\\])"; // var Usa = [mma], Rsa = !1;
                Pattern regex2 = Pattern.compile(pattern2);
                Matcher matcher2 = regex2.matcher(js);
                if (matcher2.find()){
                    String match = matcher2.group(1); // [mma]
                    return  match.replace("[", "").replace("]", ""); // mma
                }else {
                    throw new Exception("RegexMatcherError. Could not find function name: " + pattern2 + " in playerJs: " + playerJs);
                }
            }
        }
        throw new Exception("RegexMatcherError. Could not find function name: " + functionPatterns + " in playerJs: " + playerJs);
    }

    public String getSignatureFunctionName(){
        return signatureFunctionName;
    }

    public String getThrottlingFunctionName(){
        return throttlingFunctionName;
    }

    public String getSignature(String cipherSignature) throws Exception {
        return (String) jsInterpreter.callFunction(signatureFunctionName, cipherSignature);
    }

    public String getNSig(String n) throws Exception {
        return (String) jsInterpreter.callFunction(throttlingFunctionName, n);
    }
}