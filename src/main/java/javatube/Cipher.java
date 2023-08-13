package javatube;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cipher {

    private static String[] transformPlan;
    private static HashMap<String, String> transformMap;
    private static String[] jsFuncPatterns;
    private static String throttlingFunctionName;
    private static String throttlingRawCode;
    private static String playerJs;

    public Cipher(String js, String ytPlayerJs) throws Exception {
        playerJs = ytPlayerJs;
        transformPlan = getTransformPlan(js);
        String varMatcher = Arrays.asList(transformPlan[0].split("\\.")).get(0);
        transformMap = getTransformMap(js, varMatcher);
        jsFuncPatterns = new String[]{"\\w+\\.(\\w+)\\(\\w,(\\d+)\\)", "\\w+\\[(\\\"\\w+\\\")\\]\\(\\w,(\\d+)\\)"};

        throttlingFunctionName = getThrottlingFunctionName(js);
        throttlingRawCode = getThrottlingFunctionCode(js);
    }

    private static String[] getTransformPlan(String js) throws Exception {
        String name = getInitialFunctionName(js);
        String[] pattern = {
                "=function\\(\\w\\)\\{[a-z]=[a-z]\\.[a-z]*\\(\\\"\\\"\\);([\\w*\\.\\w*\\(\\w,\\d\\);]*)(?:return)", //{"kD.EC(a,1)", "kD.UT(a,60)", "kD.gp(a,55)", "kD.UT(a,45)", "kD.EC(a,3)", "kD.UT(a,28)", "kD.gp(a,63)"};
                "=function\\(\\w\\)\\{[a-z]=[a-z]\\.[a-z]*\\(\\\"\\\"\\);([\\$*\\.\\w*\\(\\w,\\d\\);]*)(?:return)" //{"$F.yG(a,62)", "$F.F5(a,2)", "$F.lt(a,55)", "$F.yG(a,48)"}
        };
        for (String s : pattern) {
            Pattern regex = Pattern.compile(name + s);
            Matcher matcher = regex.matcher(js);
            if (matcher.find()) {
                return matcher.group(1).split(";");
            }
        }
        throw new Exception("RegexMatcherError: \"" + name + "\" function not found in playerJs: " + playerJs);
    }

    private static String mapFunction(String jsFunc) throws Exception {
        String[][] mapper = {{"\\{\\w\\.reverse\\(\\)\\}", "reverse"},
                {"\\{\\w\\.splice\\(0,\\w\\)\\}", "splice"},
                {"\\{var\\s\\w=\\w\\[0\\];\\w\\[0\\]=\\w\\[\\w\\%\\w.length\\];\\w\\[\\w\\]=\\w\\}", "swap"},
                {"\\{var\\s\\w=\\w\\[0\\];\\w\\[0\\]=\\w\\[\\w\\%\\w.length\\];\\w\\[\\w\\%\\w.length\\]=\\w\\}", "swap"}};
        for(int i = 0; i <= 3; i++){
            Pattern regex = Pattern.compile(mapper[i][0]);
            Matcher matcher = regex.matcher(jsFunc);
            if (matcher.find()) {
                return mapper[i][1];
            }
        }
        throw new Exception("RegexMatcherError. Unable to map function: " + jsFunc);
    }
    private static HashMap<String, String> getTransformMap(String js, String var) throws Exception {
        String[] transformObject = getTransformObject(js, var.replace("$", "\\$"));
        //{"UT:function(a,b){var c=a[0];a[0]=a[b%a.length];a[b%a.length]=c}", "gp:function(a){a.reverse()}", "EC:function(a,b){a.splice(0,b)}"}
        HashMap<String, String> mapper = new HashMap<>();
        for(String obj: transformObject){
            String name = Arrays.asList(obj.split(":")).get(0);
            String function = Arrays.asList(obj.split(":")).get(1);
            mapper.put(name, mapFunction(function));
        }
        return mapper;
    }

    private static String[] getTransformObject(String js, String var) throws Exception {
        String pattern = "var " + var + "=\\{(.*?)\\};";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(js);
        if(matcher.find()){
            return matcher.group(1).replaceAll("(\\}\\,)", "}, ").split(", ");
        }else {
            throw new Exception("RegexMatcherError. Could not find transform function: " + pattern + " in playerJs: " + playerJs);
        }
    }

    private static String getInitialFunctionName(String js) throws Exception {
        String[] functionPattern = {"\\b[cs]\\s*&&\\s*[adf]\\.set\\([^,]+\\s*,\\s*encodeURIComponent\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
        "\\b[a-zA-Z0-9]+\\s*&&\\s*[a-zA-Z0-9]+\\.set\\([^,]+\\s*,\\s*encodeURIComponent\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
        "(?:\\b|[^a-zA-Z0-9$])([a-zA-Z0-9$]{2})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)",
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

    private static String[] parseFunction(String jsFunc){
        String fnName = null;
        String fnArg = null;
        for(String pattern : jsFuncPatterns){
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(jsFunc);
            if(matcher.find()){
                fnName = matcher.group(1);
                fnArg = matcher.group(2);
            }
        }
        return new String[]{fnName, fnArg};
    }

    private static ArrayList<String> transform(String name, String[] signature, int argument){

        ArrayList<String> t = null;
        if(Objects.equals(transformMap.get(name), "reverse")){
            t = reverse(new ArrayList<>(Arrays.asList(signature)));

        }else if (Objects.equals(transformMap.get(name), "splice")) {
            t = splice(new ArrayList<>(Arrays.asList(signature)), argument);

        }else if (Objects.equals(transformMap.get(name), "swap")){
            t = swap(signature, argument);
        }
        return t;
    }

    public String getSignature(String[] cipherSignature){
        for(String jsFunc : transformPlan){
            String name;
            int argument;
            String[] returnParse = parseFunction(jsFunc);
            name = returnParse[0];
            argument = Integer.parseInt(returnParse[1]);
            cipherSignature = (transform(name, cipherSignature, argument)).toArray(new String[0]);
        }
        return String.join(", ", cipherSignature).replace(", ", "");
    }

    private static ArrayList<String> splice(ArrayList<String> arr, int b){
        while (b > 0){
            arr.remove(0);
            b --;
        }
        return arr;
    }
    private static ArrayList<String> swap(String[] arr, int b){
        String temp = arr[b];
        arr[b] = arr[0];
        arr[0] = temp;

        return new ArrayList<>(Arrays.asList(arr));
    }

    private static ArrayList<String> reverse(ArrayList<String> arr){
        ArrayList<String> revArrayList = new ArrayList<>();
        for (int i = arr.size() - 1; i >= 0; i--) {
            revArrayList.add(arr.get(i));
        }
        return revArrayList;
    }

    private String getThrottlingFunctionCode(String js) throws Exception {
        String pattern = throttlingFunctionName + "=function\\(\\w\\)(\\{.*?return b.join\\(\\\"\\\"\\)\\}\\;)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(js);
        if (matcher.find()){
            return matcher.group(1);
        }
        throw new Exception("RegexMatcherError. Could not find function code: " + pattern + " in playerJs: " + playerJs);
    }

    private String getThrottlingFunctionName(String js) throws Exception {
        // a.D && (b = a.get("n")) && (b = Usa[0](b), a.set("n", b), Usa.length || mma(""))
        String functionPatterns = "a\\.[a-zA-Z]\\s*&&\\s*\\([a-z]\\s*=\\s*a\\.get\\(\\\"n\\\"\\)\\)\\s*&&\\s*\\([a-z]=([a-zA-Z]*\\[\\d\\]).*?\\)";
        Pattern regex = Pattern.compile(functionPatterns);
        Matcher matcher = regex.matcher(js);
        if (matcher.find()){
            String idx = matcher.group(1); // Usa[0]
            String funName = matcher.group(1).replaceAll("(\\[\\d\\])", ""); // Usa
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

    public String calculateN(String n) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        engine.eval(throttlingFunctionName + "=function(a)" + throttlingRawCode + "var b = " + throttlingFunctionName + "('" + n + "')");
        return (String) engine.get("b");
    }
}