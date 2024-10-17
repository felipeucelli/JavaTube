package com.github.felipeucelli.javatube;

import com.github.felipeucelli.javatube.exceptions.RegexMatchError;

import java.util.Objects;
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
        throw new RegexMatchError("getInitialFunctionName: Could not find function name in playerJs:" + playerJs);
    }
    private String getThrottlingFunctionName(String js) throws Exception {
        // a.D && (b = a.get("n")) && (b = Usa[0](b), a.set("n", b), Usa.length || mma(""))
        // a\\.[a-zA-Z]\\s*&&\\s*\\([a-z]\\s*=\\s*a\\.get\\(\\\"n\\\"\\)\\)\\s*&&\\s*\\([a-z]=([$a-zA-Z]*\\[\\d\\]).*?\\)"

        // New pattern added on July 9, 2024
        // In this example we can find the name of the function at index "0" of "IRa"
        // a.D && (b = String.fromCharCode(110), c = a.get(b)) && (c = IRa[0](c), a.set(b,c), IRa.length || Ima(""))


        // New pattern added on July 23, 2024
        // a.D&&(b="nn"[+a.D],c=a.get(b))&&(c=rDa[0](c),a.set(b,c),rDa.length||rma(""))
        // (?:\\.get\\(\"n\"\\)\\)&&\\(b=|b=String\\.fromCharCode\\(\\d+\\),c=a\\.get\\(b\\)\\)&&\\(c=)([a-zA-Z0-9$]+)(?:\\[(\\d+)])?\\([a-zA-Z0-9]\\)

        // New pattern used in player "20dfca59" on July 29, 2024
        // a.D && (b = a.get("n")) && (b = Msa[0](b), a.set("n",b), Msa.length || ema(""))


        String[] functionPatterns = {"""
                            (?x)
                                            [abc]=(?<nfunc>[a-zA-Z0-9$]+)
                                            \\[(?<idx>\\d+)]\\([abc]\\),
                                            a\\.set\\([a-zA-Z0-9$",]+\\),
                                            [a-zA-Z0-9$]+\\.length
                                            \\|\\|
                                            (?<func>[a-zA-Z0-9$]+)
                                            \\(""\\)""",

                // New pattern used in player "2f238d39" on October 10, 2024
                // a.D && (b = "nn" [+a.D], zM(a), c = a.j[b] || null) && (c = XDa[0](c), a.set(b, c))
                ";a\\.D&&\\(b=\"nn\".*?&&\\([abc]=(?<nfunc>[a-zA-Z$]{3})\\[(?<idx>\\d{1})\\].*?a.set\\(b.[abc]\\)\\)}};"
        };

        for(String pattern : functionPatterns){
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(js);
            if (matcher.find()){
                String funName = Pattern.quote(matcher.group("nfunc"));
                String idx = matcher.group("idx");
                if(!idx.isEmpty()){
                    String pattern2 = "var " + funName + "\\s*=\\s*\\[(.+?)]";
                    Pattern regex2 = Pattern.compile(pattern2);
                    Matcher nFuncFound = regex2.matcher(js);
                    if (nFuncFound.find()){
                        String throttlingFunction = nFuncFound.group(1);
                        return throttlingFunction;
                    }else {
                        throw new RegexMatchError("getThrottlingFunctionName: Could not find function name " + pattern2 + " in playerJs: " + playerJs);
                    }
                }
            }
        }

        throw new RegexMatchError("getThrottlingFunctionName: Could not find function name in playerJs: " + playerJs);
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