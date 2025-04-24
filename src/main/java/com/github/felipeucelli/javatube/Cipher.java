package com.github.felipeucelli.javatube;

import com.github.felipeucelli.javatube.exceptions.RegexMatchError;

import java.util.ArrayList;
import java.util.HashMap;
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
        String[] functionPattern = {
                "(?<sig>[a-zA-Z0-9_$]+)\\s*=\\s*function\\(\\s*(?<arg>[a-zA-Z0-9_$]+)\\s*\\)\\s*\\{\\s*(\\k<arg>)\\s*=\\s*(\\k<arg>)\\.split\\(\\s*[a-zA-Z0-9_\\$\\\"\\[\\]]+\\s*\\)\\s*;\\s*[^}]+;\\s*return\\s+(\\k<arg>)\\.join\\(\\s*[a-zA-Z0-9_\\$\\\"\\[\\]]+\\s*\\)",
                "\\b(?<var>[a-zA-Z0-9_$]+)&&\\((\\k<var>)=(?<sig>[a-zA-Z0-9_$]{2,})\\(decodeURIComponent\\((\\k<var>)\\)\\)",
                "(?:\\b|[^a-zA-Z0-9_$])(?<sig>[a-zA-Z0-9_$]{2,})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\\\"\\\"\\s*\\)(?:;[a-zA-Z0-9_$]{2}\\.[a-zA-Z0-9_$]{2}\\(a,\\d+\\))?"
        };
        for(String pattern : functionPattern){
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(js);
            if (matcher.find()) {
                return matcher.group("sig");
            }
        }
        throw new RegexMatchError("getInitialFunctionName: Could not find function name in playerJs:" + playerJs);
    }
    private String getThrottlingFunctionName(String js) throws Exception {

        try {
            String[] globalVar = jsInterpreter.extractPlayerJsGlobalVar(js);
            String name = globalVar[0];
            String code = globalVar[1];
            String value = globalVar[2];
            if(code != null) {
                Object array = jsInterpreter.interpretExpression(value, new LocalNameSpace(new HashMap<>()), 100);
                if(array instanceof ArrayList<?>){
                    @SuppressWarnings("unchecked")
                    ArrayList<String> globalArray = (ArrayList<String>) array;
                    for(int i = 0; i < globalArray.size(); i ++){
                        if (globalArray.get(i).endsWith("_w8_")){
                            String pattern =
                                    "(?xs)"
                                            + "[;\\n](?:"
                                            + "(?<f>function\\s+)|"
                                            + "(?:var\\s+)?"
                                            + ")(?<funcname>[a-zA-Z0-9_$]+)\\s*((f)|=\\s*function\\s*)"
                                            + "\\((?<argname>[a-zA-Z0-9_$]+)\\)\\s*\\{"
                                            + "(?:(?!\\}[;\\n]).)+"
                                            + "\\}\\s*catch\\(\\s*[a-zA-Z0-9_$]+\\s*\\)\\s*"
                                            + "\\{\\s*return\\s+" + name + "\\[" + i + "\\]\\s*\\+\\s*(\\k<argname>)\\s*\\}\\s*return\\s+[^}]+\\}[;\\n]"
                                    ;
                            Pattern regex = Pattern.compile(pattern);
                            Matcher matcher = regex.matcher(js);
                            if(matcher.find()){
                                return matcher.group("funcname");
                            }
                        }
                    }
                }
            }
        }catch (Exception e){}

        String functionPattern = """
                            (?x)
                                (?:
                                    \\.get\\(\\"n\\"\\)\\)&&\\(b=|
                                    (?:
                                        b=String\\.fromCharCode\\(110\\)|
                                        (?<stridx>[a-zA-Z0-9_$.]+)&&\\(b=\\"nn\\"\\[\\+(\\k<stridx>)\\]
                                    )
                                    (?:
                                        ,[a-zA-Z0-9_$]+\\(a\\))?,c=a\\.
                                        (?:
                                            get\\(b\\)|
                                            [a-zA-Z0-9_$]+\\[b\\]\\|\\|null
                                        )\\)&&\\(c=|
                                    \\b(?<var>[a-zA-Z0-9_$]+)=
                                )(?<nfunc>[a-zA-Z0-9_$]+)(?:\\[(?<idx>\\d+)\\])?\\([a-zA-Z]\\)
                                ((var)|,[a-zA-Z0-9_$]+\\.set\\((?:\\"n+\\"|[a-zA-Z0-9_$]+)\\,(\\k<var>)\\))""";

        Pattern regex = Pattern.compile(functionPattern);
        Matcher matcher = regex.matcher(js);
        if (matcher.find()){
            String funName = Pattern.quote(matcher.group("nfunc"));
            String idx = matcher.group("idx");
            if(!idx.isEmpty()){
                String pattern2 = "var " + funName + "\\s*=\\s*\\[(.+?)]";
                Pattern regex2 = Pattern.compile(pattern2);
                Matcher nFuncFound = regex2.matcher(js);
                if (nFuncFound.find()){
                    return nFuncFound.group(1);
                }else {
                    throw new RegexMatchError("getThrottlingFunctionName: Could not find function name " + pattern2 + " in playerJs: " + playerJs);
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