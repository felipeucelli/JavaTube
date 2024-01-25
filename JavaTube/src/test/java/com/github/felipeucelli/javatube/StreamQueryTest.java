package com.github.felipeucelli.javatube;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;

public class StreamQueryTest {

    static ArrayList<StreamQuery> streamTest = new ArrayList<>();
    static ArrayList<StreamQuery> streamDubbedTest = new ArrayList<>();

    public static void setDubbedStreams() throws Exception {
        String[] url = {
                "https://www.youtube.com/watch?v=g_VxOIlg7q8",
                "https://www.youtube.com/watch?v=WdXQabecaEs"
        };
        for(String s : url){
            streamDubbedTest.add(new Youtube(s).streams());
        }
    }
    public static void setStreams() throws Exception {
        String[] url = {
                "https://www.youtube.com/watch?v=60ItHLz5WEA",
                "https://www.youtube.com/watch?v=2lAe1cqCOXo",
                "https://www.youtube.com/watch?v=J9Q1J5I7ac0"
        };
        for(String s : url){
            streamTest.add(new Youtube(s).streams());
        }
    }

    static ArrayList<StreamQuery> getStreams() throws Exception {
        if(streamTest.isEmpty()){
            setStreams();
        }
        return streamTest;
    }

    static ArrayList<StreamQuery> getDubbedStreams() throws Exception {
        if(streamDubbedTest.isEmpty()){
            setDubbedStreams();
        }
        return streamDubbedTest;
    }

    @ParameterizedTest
    @MethodSource("getDubbedStreams")
    public void testGetDefaultAudioTracks(StreamQuery streams) {
        for(Stream s : streams.getDefaultAudioTracks().getAll()){
            Assertions.assertTrue(s.isDefaultAudioTrack());
        }
    }

    @ParameterizedTest
    @MethodSource("getDubbedStreams")
    public void testGetExtraAudioTracks(StreamQuery streams) {
        for(Stream s : streams.getExtraAudioTracks().getAll()){
            Assertions.assertFalse(s.isDefaultAudioTrack());
        }
    }

    @ParameterizedTest
    @MethodSource("getDubbedStreams")
    public void TestGetExtraAudioTracksByName(StreamQuery streams)  {
        ArrayList<Stream> yt = streams.getExtraAudioTracksByName("English").getAll();
        Assertions.assertFalse(yt.isEmpty());

    }

    @ParameterizedTest
    @MethodSource("getStreams")
    public void TestGetProgressive(StreamQuery streams) {
        for(Stream s : streams.getProgressive().getAll()){
            Assertions.assertTrue(s.isProgressive());
        }
    }

    @ParameterizedTest
    @MethodSource("getStreams")
    public void TestGetAdaptive(StreamQuery streams) {
        for(Stream s : streams.getAdaptive().getAll()){
            Assertions.assertTrue(s.isAdaptive());
        }
    }

    @ParameterizedTest
    @MethodSource("getStreams")
    public void TestGetOnlyAudio(StreamQuery streams) {
        Stream yt = streams.getOnlyAudio();
        Assertions.assertFalse(yt.includeVideoTrack());
    }

    @ParameterizedTest
    @MethodSource("getStreams")
    public void TestGetLowestResolution(StreamQuery streams) {
        Stream yt = streams.getLowestResolution();
        Assertions.assertTrue(yt.includeVideoTrack() && yt.includeAudioTrack());
    }

    @ParameterizedTest
    @MethodSource("getStreams")
    public void TestGetHighestResolution(StreamQuery streams) {
        Stream yt = streams.getHighestResolution();
        Assertions.assertTrue(yt.includeVideoTrack() && yt.includeAudioTrack());
    }

}
