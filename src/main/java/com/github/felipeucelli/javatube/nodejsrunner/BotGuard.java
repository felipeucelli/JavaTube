package com.github.felipeucelli.javatube.nodejsrunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import com.github.felipeucelli.nodejswrapper.NodeWrapper;

public class BotGuard {

    public static String generatePoToken(String visitorData) throws IOException {


        String nodePath = NodeWrapper.ensureNode().toString();

        String vmPath = extractResource().toString();

        ProcessBuilder pb = new ProcessBuilder(nodePath, vmPath, visitorData);
        pb.redirectErrorStream(false);

        try {
            Process process = pb.start();

            String output;
            try (InputStream is = process.getInputStream()) {
                output = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            String errorMsg;
            try (InputStream es = process.getErrorStream()) {
                errorMsg = new String(es.readAllBytes(), StandardCharsets.UTF_8);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Failed to execute botGuard.js: " + errorMsg.trim());
            }

            return output.replace("\n", "").trim();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    "Node.js not found", e
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error executing botGuard.js", e);
        }
    }

    public static Path extractResource() throws IOException {
        String resourcePath = "/vm/botGuard.js";
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
}

