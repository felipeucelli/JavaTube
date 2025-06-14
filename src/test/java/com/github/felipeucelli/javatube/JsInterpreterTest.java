package com.github.felipeucelli.javatube;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class JsInterpreterTest {
    static Stream<String> fileNames() {
        return Stream.of(
                "f980f2a9-player_ias.vflset-en_US.txt",
                "71547d26-player_ias.vflset-en_US.txt",
                "23604418-player_ias.vflset-en_US.txt",
                "f980f2a9-player-plasma-ias-tablet-en_US.vflset.txt",
                "3cd2d050-player-plasma-ias-tablet-en_US.vflset.txt",
                "da7c2a60-player_ias.vflset-en_US.txt",
                "21812a9c-player_ias.vflset-en_US.txt",
                "c153b631-player-plasma-ias-tablet-en_US.vflset.txt",
                "019a2dc2-player-ias-vflset_en_US.txt",
                "5bdfe6d5-player_ias.vflset-en_US.txt",
                "31e0b6d9-player_ias.vflset-en_US.txt",
                "42a553e1-player_ias.vflset-en_US.txt",
                "3ffefd71-player_ias.vflset-en_US.txt",
                "bc657243-player_ias.vflset-en_US.txt",
                "b22ef6e7-player_ias.vflset-en_US.txt",
                "1f8742dc-player_ias.vflset-en_US.txt",
                "20dfca59-player_ias.vflset-en_US.txt",
                "b12cc44b-player_ias.vflset-en_US.txt",
                "3bb1f723-player_ias.vflset-en_US.txt",
                "2f1832d2-player_ias.vflset-en_US.txt",
                "e7567ecf-player_ias_tce.vflset-en_US.txt",
                "74e4bb46-player_ias_tce.vflset-en_US.txt",
                "4fcd6e4a-player_ias.vflset-en_US.txt",
                "20830619-player_ias.vflset-en_US.txt",
                "6450230e-player_ias.vflset-en_US.txt",
                "59b252b9-player_ias.vflset-en_US.txt"
                );
    }
    private String readFileContent(String fileName) throws IOException {
        Path filePath = Paths.get("src/test/resources/com/github/felipeucelli/javatube/base", fileName);
        assertTrue(Files.exists(filePath), "File " + fileName + " not found.");

        return new String(Files.readAllBytes(filePath));
    }

    @Test
    public void testInterpreterCurrentSig() throws Exception {
        Youtube yt = new Youtube("https://www.youtube.com/watch?v=O3ElohqHEzQ");
        Cipher c = new Cipher(yt.getJs(), yt.getYtPlayerJs());

        String sig = "AOq0QJ8wRQIhANGlXiqWj4dne3ftJz6RMy5hK5Xe3QP3oC7MzEWXWYhWAiBOlqAWYj6ZOU-jlBaNLTTUGFOuR%3Dm397tElCFtpaC8jw%3Db";

        new JsInterpreter(yt.getJs()).callFunction(c.getSignatureFunctionName(), sig);
    }

    @Test
    public void testInterpreterCurrentNSig() throws Exception {
        Youtube yt = new Youtube("https://www.youtube.com/watch?v=O3ElohqHEzQ");
        Cipher c = new Cipher(yt.getJs(), yt.getYtPlayerJs());

        String Nsig = "70QzMb0nhneLLS6BN";

        String result = (String) new JsInterpreter(yt.getJs()).callFunction(c.getThrottlingFunctionName(), Nsig);

        assertFalse(result.startsWith("enhanced_except") || result.endsWith(Nsig));
    }

    @ParameterizedTest
    @MethodSource("fileNames")
    public void testNSigInterpreter(String fileName) throws Exception {
        String fileContent = readFileContent(fileName);
        List<String> params = getNSigParams(fileName);

        assertEquals(params.get(0), new JsInterpreter(fileContent).callFunction(params.get(1), params.get(2)));
    }
    @ParameterizedTest
    @MethodSource("fileNames")
    public void testSigInterpreter(String fileName) throws Exception {
        String fileContent = readFileContent(fileName);
        List<String> params = getSigParams(fileName);

        assertEquals(params.get(0), new JsInterpreter(fileContent).callFunction(params.get(1), params.get(2)));
    }

    private List<String> getNSigParams(String fileName) {
        return switch (fileName) {
            case "f980f2a9-player_ias.vflset-en_US.txt" -> List.of("4Dvzk8E8Iz-9xQ", "Ula", "t0yYMGhLCCndDH7_oH");
            case "71547d26-player_ias.vflset-en_US.txt" -> List.of("mCqsuKWEbNMz4A", "ema", "w5Ur1wQ_oojYWTNH4z");
            case "23604418-player_ias.vflset-en_US.txt" -> List.of("BTSM6hSdKLn-rw", "fma", "8UGuO2Tv_wq_lCb");
            case "f980f2a9-player-plasma-ias-tablet-en_US.vflset.txt" ->
                    List.of("a2K_xA_CfqvTUg", "bq", "tMZ1SVtCu1G7wBJrH-");
            case "3cd2d050-player-plasma-ias-tablet-en_US.vflset.txt" ->
                    List.of("rvxf4lqA9UC6Y", "$p", "tMZ1SVtCu1G7wBJrH-");
            case "da7c2a60-player_ias.vflset-en_US.txt" -> List.of("kV6feunzajGyGaPo", "Ula", "tMZ1SVtCu1G7wBJrH-");
            case "21812a9c-player_ias.vflset-en_US.txt" -> List.of("M5pXBNVBZf01MA", "Tla", "70QzMb0nhneLLS6BN");
            case "c153b631-player-plasma-ias-tablet-en_US.vflset.txt" ->
                    List.of("AAnFkOJ694_Uew", "dq", "NL7YwUhStcFhEdqJ");
            case "019a2dc2-player-ias-vflset_en_US.txt" -> List.of("6giJCNZ6QlHatA", "Ula", "mSyHU9iB6viPu5-");
            case "5bdfe6d5-player_ias.vflset-en_US.txt" -> List.of("6WVNa9oCSHok", "fma", "6giJCNZ6QlHatA");
            case "31e0b6d9-player_ias.vflset-en_US.txt" -> List.of("BVP_Rb2aDs", "kma", "6giJCNZ6QlHatA");
            case "42a553e1-player_ias.vflset-en_US.txt" -> List.of("t5Jp2AyA1Jog2", "ema", "70QzMb0nhneLLS6BN");
            case "3ffefd71-player_ias.vflset-en_US.txt" -> List.of("Qp4YR89PTr-vmb", "ima", "70QzMb0nhneLLS6BN");
            case "d8a5aa5e-player_ias.vflset-en_US.txt" -> List.of("KvdthMya5dxi87dY", "ema", "70QzMb0nhneLLS6BN");
            case "bc657243-player_ias.vflset-en_US.txt" -> List.of("l_slyNHt1evTOm", "Ula", "70QzMb0nhneLLS6BN");
            case "b22ef6e7-player_ias.vflset-en_US.txt"  -> List.of("3imMGazkXNJnWK", "Ima", "70QzMb0nhneLLS6BN");
            case "1f8742dc-player_ias.vflset-en_US.txt"  -> List.of("wx4GPH8bp1v7A9", "rma", "70QzMb0nhneLLS6BN");
            case "20dfca59-player_ias.vflset-en_US.txt"  -> List.of("zmLIydxjzkWeBA9", "rma", "70QzMb0nhneLLS6BN");
            case "b12cc44b-player_ias.vflset-en_US.txt"  -> List.of("FnDBX5UEM1NHNyr", "Ema", "70QzMb0nhneLLS6BN");
            case "3bb1f723-player_ias.vflset-en_US.txt"  -> List.of("-zeqduSAj2ON5", "fyn", "70QzMb0nhneLLS6BN");
            case "2f1832d2-player_ias.vflset-en_US.txt"  -> List.of("e7hn0bMSQ", "B_H", "70QzMb0nhneLLS6BN");
            case "e7567ecf-player_ias_tce.vflset-en_US.txt"  -> List.of("eGJT0Dt7IGpKgk", "X_S", "70QzMb0nhneLLS6BN");
            case "74e4bb46-player_ias_tce.vflset-en_US.txt"  -> List.of("YRvnhiNcKsUj", "Bzs", "70QzMb0nhneLLS6BN");
            case "4fcd6e4a-player_ias.vflset-en_US.txt"  -> List.of("DtBH24Jm4Vu4ga", "gGu", "70QzMb0nhneLLS6BN");
            case "20830619-player_ias.vflset-en_US.txt"  -> List.of("R5zfbyrpEoUHl", "e2E", "70QzMb0nhneLLS6BN");
            case "6450230e-player_ias.vflset-en_US.txt"  -> List.of("yaMQah-K-J8VJ0-", "J5S", "70QzMb0nhneLLS6BN");
            case "59b252b9-player_ias.vflset-en_US.txt"  -> List.of("CnHxxVCaFYf", "m85", "70QzMb0nhneLLS6BN");
            default -> List.of("", "", "");
        };
    }

    private List<String> getSigParams(String fileName) {
        return switch (fileName) {
            case "f980f2a9-player_ias.vflset-en_US.txt" ->
                    List.of("AOq0QJ8wRQIhANGlXiqWj4dne3ftJz6RMy5hK5Xe3QP3oC7MzEWXWYhWAiBOlqAWYj6ZOU-jlBaNLTTUGFOuR%3Dm397tElCFtpaC8jw%3Db", "bua", "AAOq0QJ8wRQIhANGlXiqWj4dne3ftJz6RMy5hK5Xe3QP3oC7MzEWXWYhWAiBOlqAWYj6ZOUbjlBaNLTTUGFOuR%3Dm397tElCFtpaC8jw%3D-");
            case "71547d26-player_ias.vflset-en_US.txt" ->
                    List.of("AOq0QJ8wRQIhAInqO2_vdMNMCbbHOW_X71EL1Mj-ayWA0MagUI10cAI0AiAIYB_QAseLg8dk54Sz0bnappC3213iiq24WJRLJF5hew==", "Hsa", "====weh5FJLRJW42qii3123Cppanb0zS45kd8gLesAQ_BYIAiA0IAb01IUgaM0AWya-jM1LE17X_WcOAbCMNMdv_2OqnIAhIQRw8JQ0qOcqOH");
            case "23604418-player_ias.vflset-en_US.txt" ->
                    List.of("WQJ8wRQIhANGlXiqWj4dne3ftJz6RMyAhK5Xe3QP3oC7MzEWXWYh0qiBOlqAWYj6ZOUbjlBaNLTTUGDOuR%3Dm397tElCFtpaC8jw%3F", "Isa", "AAOq0QJ8wRQIhANGlXiqWj4dne3ftJz6RMy5hK5Xe3QP3oC7MzEWXWYhWAiBOlqAWYj6ZOUbjlBaNLTTUGFOuR%3Dm397tElCFtpaC8jw%3D-");
            case "f980f2a9-player-plasma-ias-tablet-en_US.vflset.txt" ->
                    List.of("AOq0QJ8wRgIhAMulJffBQeJA-jLY-DOAkazwLV1KrW6AsWPC8ne1iceUAiEA5T2QA0vOX6FGwONJg4szsLoT6u4b6I1iVkMDO9HwSJw=", "Nka", "AAOq0QJ8wRgIhAMulJffBQeJA-jLY-DOAkazwLV1KrW6AsWPC8ne1iceUAiEA5T2QA0=OX6FGwONJg4szsLoT6u4b6I1iVkMDO9HwSJwv");
            case "3cd2d050-player-plasma-ias-tablet-en_US.vflset.txt" ->
                    List.of("AOq0QJ8wRgIhAMulJffBQeJA-jLY-DOAkazwLV1KrW6AsWPC8ne1iceUAiEA5T2QA0=OX6FGwONJg4szsLoT6v4b6I1iVkMDO9HwSJwu", "jka", "AAOq0QJ8wRgIhAMulJffBQeJA-jLY-DOAkazwLV1KrW6AsWPC8ne1iceUAiEA5T2QA0=OX6FGwONJg4szsLoT6u4b6I1iVkMDO9HwSJwv");
            case "da7c2a60-player_ias.vflset-en_US.txt" ->
                    List.of("Oq0QJ8wAgIhAMulJffBQeJA-jLY-DOAkazwLV1KrW6AsWPC8ne1RceUAiEA5T2QA0=OX6FGwONJg4szsLoT6u4b6I1iVkMDO9HwSJwv", "Zta", "AAOq0QJ8wRgIhAMulJffBQeJA-jLY-DOAkazwLV1KrW6AsWPC8ne1iceUAiEA5T2QA0=OX6FGwONJg4szsLoT6u4b6I1iVkMDO9HwSJwv");
            case "21812a9c-player_ias.vflset-en_US.txt" ->
                    List.of("TSwH9ODMkVi1I6b4u6JoLszs4gJNOwGF6XO=0AQ2T5AEiAUeci1en8COWsA6WrK1VLwzakAOD-YLj-AJeQBffJluMAhIgRw8JQ0qP", "$ya", "AAOq0QJ8wRgIhAMulJffBQeJA-jLY-DOAkazwLV1KrW6AsWPC8ne1iceUAiEA5T2QA0=OX6FGwONJg4szsLoT6u4b6I1iVkMDO9HwSJwv");
            case "c153b631-player-plasma-ias-tablet-en_US.vflset.txt" ->
                    List.of("4wH9ODMkVi1I6bSu6ToLszs4gJNOwGF6XO=0AQ2T5AEiAUeci1en8CPWsA6WrK1VLwzakAOD-YLj-AJeQBffJluMAhIgRw8JQ0qOAA", "Qja", "AAOq0QJ8wRgIhAMulJffBQeJA-jLY-DOAkazwLV1KrW6AsWPC8ne1iceUAiEA5T2QA0=OX6FGwONJg4szsLoT6u4b6I1iVkMDO9HwSJwv");
            case "019a2dc2-player-ias-vflset_en_US.txt" ->
                    List.of("AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO", "AKa", "AOqAOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo9Oyib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO3");
            case "5bdfe6d5-player_ias.vflset-en_US.txt" -> List.of("tTxUgoDDREeR36Kj5wrO_NjqCHEmy6PGNIGWF9GKnFEIC4P07pOGh54LI6biy39oUrq5mTnRI3mE0gJchXqWRWNIgIARw8JQ0q", "ZKa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "31e0b6d9-player_ias.vflset-en_US.txt" -> List.of("OTxUgoDDREeR36Kj5wrO_NjqCHEmy6PGNIGTF9GKnFEIC4P07ptGh54LI6biy39oUrq5m0nRI3mE0gJchXqWRWNIgIARw8JQA", "zLa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "42a553e1-player_ias.vflset-en_US.txt" -> List.of("xUgoDDREeR36Kj5wrObNjqCHEmy6PGNIGWF9GKnFEIC4P07ptGh54AI6Oiy39oUrq5mTnRI3mE0gJchXqWRWNIgIARw8JQ0qO", "YLa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "3ffefd71-player_ias.vflset-en_US.txt" -> List.of("UgoDDREeR36Kj5wrO_NjqCHEmy6PGNIGWO9GKnFEIC4P0JptGh54LI6biy39oUrq5mTnRI3mE0gJchXqWRWNIgIARw8", "CMa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "d8a5aa5e-player_ias.vflset-en_US.txt" -> List.of("gxUToDDREeR36Kj5wrO_NjqCHEqy6PGNIGWF9GKnFEIC4P07ptGh54LI6biy39oUrq5mTnRImmE0gJchXqWRWNIgIARw8JQ03", "MNa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "bc657243-player_ias.vflset-en_US.txt" -> List.of("pOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGt970P4CIEFnKGOFWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTA", "GOa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "b22ef6e7-player_ias.vflset-en_US.txt" -> List.of("goDDREeR36Kj5wrO_NjqCHEmy6PANTGWF9GKnFEIC4P07ptGh54LI6biy39oUrq5ITnRI3mE0gJchXqWRWNIgIARw8JQ0qOG", "LPa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "1f8742dc-player_ias.vflset-en_US.txt" -> List.of("mQJ8wRAIgINW9WqXhcJg0E03IRnTm5qrUoA3yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO", "HBa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "20dfca59-player_ias.vflset-en_US.txt" -> List.of("U0QJ8wRAIgINWRWqXhcJg0qm3IRnTm5qrEo93yib6IL45hGtp70P4CTEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxI", "EBa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "b12cc44b-player_ias.vflset-en_US.txt" -> List.of("EJ8wRAIgINWRWqXhcJg0Am3IRnTm5qrUo93yib6IL45hGtp70P4CQEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDoOU", "PBa", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "3bb1f723-player_ias.vflset-en_US.txt" -> List.of("iwR8IgIN5RWqXhcJg0Em3IRATm5qrUo93yAb6IL40hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO", "pen", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "2f1832d2-player_ias.vflset-en_US.txt" -> List.of("KpC5ut2EHrCarJWeXtsVBjE-IM3YQwOpfz8kcrr8emyEL62UySqmFPy6MPf8wVOXd41yKQhDHvEx_8f-Gu75ltKNohkk-", "dCH", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "e7567ecf-player_ias_tce.vflset-en_US.txt" -> List.of("UgoDDREOR36Kj5wrO_NjqCHEmy6PGNIGWF9GKnFEIC4P0eptGh54LI6biy39oUrq5mTnRI3mE0gJchXqWRWNIgIARw8JQ0qO", "$oW", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "74e4bb46-player_ias_tce.vflset-en_US.txt" -> List.of("TxUgoDDREeR36Kj5wrO_NjqCHEmy6PGNIGWF9GKnFEIC4P07ptGh54LI6biy39oUrqAmTnRIOmE0gJchXqWRWNIgIARw8JQ0q3", "bol", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "4fcd6e4a-player_ias.vflset-en_US.txt" -> List.of("eTxUgoDDREOR36Kj5wrO_NjqCHEmy6PGNIGWq9GKnFEIC4P07ptGh54LI6biy39oUrq5mTnRI3mE0gJchXqWRWNIgIARw8JQ0", "YqR", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "20830619-player_ias.vflset-en_US.txt" -> List.of("IUgoDDREeR36Kj5wrO_NjqCOEmy6PGNIGWF9GKnFExCAP07ptGh54LI6biy39oUrq5mTnRI3mE0gJchXqWRWNIgIARw8JQ0qO4", "X8$", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "6450230e-player_ias.vflset-en_US.txt" -> List.of("qOG0QJ8wRAIgINWRWqXhcJg0Em3IRnAm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWTINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO", "L1S", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            case "59b252b9-player_ias.vflset-en_US.txt" -> List.of("pOq0QJ8wRAIgINWRWEXhcJg0Em3IRnTm5qrUo93yib6ILA5hGtP70q4CI4FnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxT", "Had", "AOq0QJ8wRAIgINWRWqXhcJg0Em3IRnTm5qrUo93yib6IL45hGtp70P4CIEFnKG9FWGINGP6ymEHCqjN_Orw5jK63ReERDDogUxTO");
            default -> List.of("", "", "");
        };

    }
}
