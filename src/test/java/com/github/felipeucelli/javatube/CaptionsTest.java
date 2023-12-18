package com.github.felipeucelli.javatube;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CaptionsTest {

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=4LdhxXL-RZ8",
            "https://www.youtube.com/watch?v=jgCVkQhlScc",
            "https://www.youtube.com/watch?v=wJnBTPUQS5A"
    })
    public void testGetCaptionTracks(String linkToTest) throws Exception {
        Assertions.assertFalse(new Youtube(linkToTest).getCaptionTracks().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=4LdhxXL-RZ8",
            "https://www.youtube.com/watch?v=jgCVkQhlScc",
            "https://www.youtube.com/watch?v=wJnBTPUQS5A"
    })
    public void testXmlCaptionToSrt(String linkToTest) throws Exception {
        String srtCaptions = new Youtube(linkToTest).getCaptions().getByCode("en").xmlCaptionToSrt();
        Pattern pattern = Pattern.compile("\\d+\\n\\d{2}:\\d{2}:\\d{2},\\d{3}\\s-->\\s\\d{2}:\\d{2}:\\d{2},\\d{3}\\n.*\\n");
        Matcher matcher = pattern.matcher(srtCaptions);
        Assertions.assertTrue(matcher.find());

    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=4LdhxXL-RZ8",
            "https://www.youtube.com/watch?v=jgCVkQhlScc",
            "https://www.youtube.com/watch?v=wJnBTPUQS5A"
    })
    public void testGetUrl(String linkToTest) throws Exception {
        ArrayList<Captions> c = new Youtube(linkToTest).getCaptionTracks();
        for(Captions captions : c){
            Assertions.assertDoesNotThrow(captions::getUrl);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=4LdhxXL-RZ8",
            "https://www.youtube.com/watch?v=jgCVkQhlScc",
            "https://www.youtube.com/watch?v=wJnBTPUQS5A"
    })
    public void testGetCode(String linkToTest) throws Exception {
        ArrayList<Captions> c = new Youtube(linkToTest).getCaptionTracks();
        for(Captions captions : c){
            Assertions.assertDoesNotThrow(captions::getCode);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=4LdhxXL-RZ8",
            "https://www.youtube.com/watch?v=jgCVkQhlScc",
            "https://www.youtube.com/watch?v=wJnBTPUQS5A"
    })
    public void testGetName(String linkToTest) throws Exception {
        ArrayList<Captions> c = new Youtube(linkToTest).getCaptionTracks();
        for(Captions captions : c){
            Assertions.assertDoesNotThrow(captions::getName);
        }
    }
}
