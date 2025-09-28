package com.github.felipeucelli.javatube;

import com.github.felipeucelli.javatube.exceptions.RegexMatchError;
import com.github.felipeucelli.javatube.nodejsrunner.NodeRunner;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Cipher {

    private static String playerJs;
    private static String signatureFunctionName;
    private static int signatureParam;
    private static String nsigFunctionName;

    private final NodeRunner runnerSig;
    private final NodeRunner runnerNsig;

    public Cipher(String js, String ytPlayerJs) throws Exception {
        playerJs = ytPlayerJs;
        signatureFunctionName = getSigFunctionName(js);
        nsigFunctionName = getNsigFunctionName(js);

        runnerSig = new NodeRunner(js);
        runnerSig.loadFunction(signatureFunctionName);

        runnerNsig = new NodeRunner(js);
        runnerNsig.loadFunction(nsigFunctionName);

    }
    private static String getSigFunctionName(String js) throws Exception {
        String[] functionPattern = {
                "(?<sig>[a-zA-Z0-9_$]+)\\s*=\\s*function\\(\\s*(?<arg>[a-zA-Z0-9_$]+)\\s*\\)\\s*\\{\\s*(\\k<arg>)\\s*=\\s*(\\k<arg>)\\.split\\(\\s*[a-zA-Z0-9_\\$\\\"\\[\\]]+\\s*\\)\\s*;\\s*[^}]+;\\s*return\\s+(\\k<arg>)\\.join\\(\\s*[a-zA-Z0-9_\\$\\\"\\[\\]]+\\s*\\)",
                "\\b(?<var>[a-zA-Z0-9_$]+)&&\\((\\k<var>)=(?<sig>[a-zA-Z0-9_$]{2,})\\((?:(?<param>\\d+),decodeURIComponent)\\((\\k<var>)\\)\\)",
                "(?:\\b|[^a-zA-Z0-9_$])(?<sig>[a-zA-Z0-9_$]{2,})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\\\"\\\"\\s*\\)(?:;[a-zA-Z0-9_$]{2}\\.[a-zA-Z0-9_$]{2}\\(a,\\d+\\))?"
        };
        for(String pattern : functionPattern){
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(js);
            if (matcher.find()) {
                signatureParam = Integer.parseInt(matcher.group("param"));
                return matcher.group("sig");
            }
        }
        throw new RegexMatchError("getSigFunctionName: Could not find function name in playerJs:" + playerJs);
    }
    private String getNsigFunctionName(String js) throws Exception {
        String pattern = "var\\s*[a-zA-Z0-9$_]{3}\\s*=\\s*\\[(?<funcname>[a-zA-Z0-9$_]{3})\\]";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(js);
        if (matcher.find()) {
            return matcher.group("funcname");
        }

        throw new RegexMatchError("getNsigFunctionName: Could not find function name in playerJs:" + playerJs);

    }

    public String getSignatureFunctionName(){
        return signatureFunctionName;
    }

    public String getNisgFunctionName(){
        return nsigFunctionName;
    }

    public String getSignature(String cipherSignature) throws Exception {
        return runnerSig.call(List.of(signatureParam, cipherSignature));
    }

    public String getNSig(String n) throws Exception {
        return runnerNsig.call(List.of(n));
    }
}