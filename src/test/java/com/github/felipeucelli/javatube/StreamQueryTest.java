package com.github.felipeucelli.javatube;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;

public class StreamQueryTest {

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=g_VxOIlg7q8",
            "https://www.youtube.com/watch?v=WdXQabecaEs"
    })
    public void testGetDefaultAudioTracks(String url) throws Exception {
        for(Stream s : new Youtube(url).streams().getDefaultAudioTracks().getAll()){
            Assertions.assertTrue(s.isDefaultAudioTrack());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=g_VxOIlg7q8",
            "https://www.youtube.com/watch?v=WdXQabecaEs"
    })
    public void testGetExtraAudioTracks(String url) throws Exception {
        for(Stream s : new Youtube(url).streams().getExtraAudioTracks().getAll()){
            Assertions.assertFalse(s.isDefaultAudioTrack());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=g_VxOIlg7q8",
            "https://www.youtube.com/watch?v=WdXQabecaEs"
    })
    public void TestGetExtraAudioTracksByName(String url) throws Exception {
        ArrayList<Stream> yt = new Youtube(url).streams().getExtraAudioTracksByName("English").getAll();
        Assertions.assertFalse(yt.isEmpty());

    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=60ItHLz5WEA",
            "https://www.youtube.com/watch?v=2lAe1cqCOXo",
            "https://www.youtube.com/watch?v=J9Q1J5I7ac0"
    })
    public void TestGetProgressive(String url) throws Exception {
        for(Stream s : new Youtube(url).streams().getProgressive().getAll()){
            Assertions.assertTrue(s.isProgressive());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=60ItHLz5WEA",
            "https://www.youtube.com/watch?v=2lAe1cqCOXo",
            "https://www.youtube.com/watch?v=J9Q1J5I7ac0"
    })
    public void TestGetAdaptive(String url) throws Exception {
        for(Stream s : new Youtube(url).streams().getAdaptive().getAll()){
            Assertions.assertTrue(s.isAdaptive());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=60ItHLz5WEA",
            "https://www.youtube.com/watch?v=2lAe1cqCOXo",
            "https://www.youtube.com/watch?v=J9Q1J5I7ac0"
    })
    public void TestGetOnlyAudio(String url) throws Exception {
        Stream yt = new Youtube(url).streams().getOnlyAudio();
        Assertions.assertFalse(yt.includeVideoTrack());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=60ItHLz5WEA",
            "https://www.youtube.com/watch?v=2lAe1cqCOXo",
            "https://www.youtube.com/watch?v=J9Q1J5I7ac0"
    })
    public void TestGetLowestResolution(String url) throws Exception {
        Stream yt = new Youtube(url).streams().getLowestResolution();
        Assertions.assertTrue(yt.includeVideoTrack() && yt.includeAudioTrack());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=60ItHLz5WEA",
            "https://www.youtube.com/watch?v=2lAe1cqCOXo",
            "https://www.youtube.com/watch?v=J9Q1J5I7ac0"
    })
    public void TestGetHighestResolution(String url) throws Exception {
        Stream yt = new Youtube(url).streams().getHighestResolution();
        Assertions.assertTrue(yt.includeVideoTrack() && yt.includeAudioTrack());
    }

}
