package com.github.felipeucelli.javatube;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;

public class SearchTest {

    @ParameterizedTest
    @CsvSource({
            "java",
            "java tutorial",
            "dev java"
    })
    public void testResults(String query) throws Exception {
        Assertions.assertFalse(new Search(query).getResults().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "java",
            "how to program in java",
            "java in android",
            "java tutorial",
            "learning java"
    })
    public void testGetVideosResults(String query) throws Exception {
        Assertions.assertFalse(new Search(query).getVideosResults().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "java",
            "dev java"
    })
    public void testGetShortsResults(String query) throws Exception {
        Assertions.assertFalse(new Search(query).getShortsResults().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "java videos",
            "java tutorial"
    })
    public void testGetPlaylistsResults(String query) throws Exception {
        Assertions.assertFalse(new Search(query).getPlaylistsResults().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "java channels"
    })
    public void testGetChannelsResults(String query) throws Exception {
        Assertions.assertFalse(new Search(query).getChannelsResults().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "java",
    })
    public void testGetCompletionSuggestions(String query) throws Exception {
        Assertions.assertNotNull(new Search(query).getCompletionSuggestions());
    }

    @Test
    public void testGenerateContinuation() throws Exception {
        Search s = new Search("java");
        ArrayList<String> result = s.getResults();
        s.generateContinuation();
        Assertions.assertTrue(s.getResults().size() > result.size());
    }

}
