package com.github.felipeucelli.javatube.nodejsrunner;

import java.io.*;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;

import org.json.JSONObject;
import com.github.felipeucelli.nodejswrapper.NodeWrapper;

public class NodeRunner {
    private final Process process;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private String functionName;
    private final String code;

    public NodeRunner(String code) throws IOException {
        this.code = code;
        ProcessBuilder pb = new ProcessBuilder(NodeWrapper.ensureNode().toString(), extractResource().toString());
        pb.redirectErrorStream(true);
        this.process = pb.start();

        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
    }

    public static Path extractResource() throws IOException {
        String resourcePath = "/vm/runner.js";
        try (InputStream in = Objects.requireNonNull(
                NodeRunner.class.getResourceAsStream(resourcePath),
                "Resource not found: " + resourcePath
        )) {
            Path tempFile = Files.createTempFile(
                    Paths.get(System.getProperty("java.io.tmpdir")),
                    "vm-", "-" + Paths.get(resourcePath).getFileName().toString()
            );
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit();
            return tempFile;
        }
    }

    private String exposed(String code, String funName) {
        String exposed = "_exposed['" + funName + "']=" + funName + ";})(_yt_player);";
        return code.replace("})(_yt_player);", exposed);
    }

    private String send(JSONObject data) throws IOException {
        writer.write(data.toString());
        writer.newLine();
        writer.flush();

        String line = reader.readLine();
        if (line == null) throw new IOException("Node process closed unexpectedly");
        return line;
    }

    public String loadFunction(String functionName) throws IOException {
        this.functionName = functionName;
        JSONObject payload = new JSONObject();
        payload.put("type", "load");
        String code = exposed(this.code, functionName);
        payload.put("code", code);
        return send(payload);
    }

    public String call(List<Object> args) throws IOException {
        JSONObject payload = new JSONObject();
        payload.put("type", "call");
        payload.put("fun", this.functionName.trim());
        payload.put("args", args != null ? args : Collections.emptyList());
        return send(payload).replace("\"", "");
    }

    public void close() throws IOException, InterruptedException {
        writer.close();
        process.destroy();
        process.waitFor();
    }

}
