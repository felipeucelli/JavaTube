package com.github.felipeucelli.javatube;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CipherTest {
    static java.util.stream.Stream<String> fileNames() {
        return Stream.of(
                "f980f2a9-player_ias.vflset-en_US.txt",
                "71547d26-player_ias.vflset-en_US.txt",
                "23604418-player_ias.vflset-en_US.txt",
                "f980f2a9-player-plasma-ias-tablet-en_US.vflset.txt",
                "3cd2d050-player-plasma-ias-tablet-en_US.vflset.txt",
                "da7c2a60-player_ias.vflset-en_US.txt",
                "21812a9c-player_ias.vflset-en_US.txt",
                "c153b631-player-plasma-ias-tablet-en_US.vflset.txt",
                "5bdfe6d5-player_ias.vflset-en_US.txt",
                "31e0b6d9-player_ias.vflset-en_US.txt"
        );
    }
    private String readFileContent(String fileName) throws IOException {
        Path filePath = Paths.get("src/test/resources/com/github/felipeucelli/javatube/base", fileName);
        assertTrue(Files.exists(filePath), "File " + fileName + " not found.");

        return new String(Files.readAllBytes(filePath));
    }
    @Test
    public void testExtractCipher() throws Exception {
        Youtube yt = new Youtube("https://www.youtube.com/watch?v=O3ElohqHEzQ");
        Cipher cipher = new Cipher(yt.getJs(), yt.getYtPlayerJs());
        Assertions.assertNotNull(cipher.getSignatureFunctionName());
        Assertions.assertNotNull(cipher.getThrottlingFunctionName());
    }
    @ParameterizedTest
    @MethodSource("fileNames")
    public void testGetNSigName(String fileName) throws Exception {
        String fileContent = readFileContent(fileName);
        String funName = getNSigFunName(fileName);

        assertEquals(funName, new Cipher(fileContent, fileName).getThrottlingFunctionName());
    }
    @ParameterizedTest
    @MethodSource("fileNames")
    public void testGetSigName(String fileName) throws Exception {
        String fileContent = readFileContent(fileName);
        String funName = getSigFunName(fileName);

        assertEquals(funName, new Cipher(fileContent, fileName).getSignatureFunctionName());
    }

    private String getNSigFunName(String fileName) {
        return switch (fileName) {
            case "f980f2a9-player_ias.vflset-en_US.txt", "da7c2a60-player_ias.vflset-en_US.txt" -> "Ula";
            case "71547d26-player_ias.vflset-en_US.txt" -> "ema";
            case "23604418-player_ias.vflset-en_US.txt", "5bdfe6d5-player_ias.vflset-en_US.txt" -> "fma";
            case "f980f2a9-player-plasma-ias-tablet-en_US.vflset.txt" -> "bq";
            case "3cd2d050-player-plasma-ias-tablet-en_US.vflset.txt" -> "$p";
            case "21812a9c-player_ias.vflset-en_US.txt" -> "Tla";
            case "c153b631-player-plasma-ias-tablet-en_US.vflset.txt" -> "dq";
            case "31e0b6d9-player_ias.vflset-en_US.txt" -> "kma";
            default -> "";
        };
    }
    private String getSigFunName(String fileName) {
        return switch (fileName) {
            case "f980f2a9-player_ias.vflset-en_US.txt" -> "bua";
            case "71547d26-player_ias.vflset-en_US.txt" -> "Hsa";
            case "23604418-player_ias.vflset-en_US.txt" -> "Isa";
            case "f980f2a9-player-plasma-ias-tablet-en_US.vflset.txt" -> "Nka";
            case "3cd2d050-player-plasma-ias-tablet-en_US.vflset.txt" -> "jka";
            case "da7c2a60-player_ias.vflset-en_US.txt" -> "Zta";
            case "21812a9c-player_ias.vflset-en_US.txt" -> "$ya";
            case "c153b631-player-plasma-ias-tablet-en_US.vflset.txt" -> "Qja";
            case "5bdfe6d5-player_ias.vflset-en_US.txt" -> "ZKa";
            case "31e0b6d9-player_ias.vflset-en_US.txt" -> "zLa";
            default -> "";
        };
    }

}