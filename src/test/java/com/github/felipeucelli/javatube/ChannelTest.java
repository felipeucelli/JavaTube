package com.github.felipeucelli.javatube;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ChannelTest {

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic, https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds, https://www.youtube.com/@NoCopyrightSounds"
    })
    public void testGetUrl(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Channel(linkToTest).getUrl());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic, Alan Walker",
            "https://www.youtube.com/@NoCopyrightSounds, NoCopyrightSounds"
    })
    public void testGetChannelName(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Channel(linkToTest).getChannelName());

    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic, UCJrOtniJ0-NWz37R30urifQ",
            "https://www.youtube.com/@NoCopyrightSounds, UC_aEa8K-EOJ3D6gOs7HcyNg"
    })
    public void testGetChannelId(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Channel(linkToTest).getChannelId());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic, http://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds, http://www.youtube.com/@NoCopyrightSounds"
    })
    public void testGetVanityUrl(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Channel(linkToTest).getVanityUrl());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds"
    })
    public void testGetViews(String linkToTest) throws Exception {
        Assertions.assertNotNull(new Channel(linkToTest).getViews());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds"
    })
    public void testGetSubscribers(String linkToTest) throws Exception {
        Assertions.assertNotNull(new Channel(linkToTest).getSubscribers());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds"
    })
    public void testGetKeywords(String linkToTest) throws Exception {
        Assertions.assertFalse(new Channel(linkToTest).getKeywords().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds"
    })
    public void testGetAvailableCountryCodes(String linkToTest) throws Exception {
        Assertions.assertFalse(new Channel(linkToTest).getAvailableCountryCodes().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds"
    })
    public void testGetLastUpdated(String linkToTest) throws Exception {
        Assertions.assertNotNull(new Channel(linkToTest).getLastUpdated());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds"
    })
    public void testLength(String linkToTest) throws Exception {
        Assertions.assertNotNull(new Channel(linkToTest).length());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds"
    })
    public void testGetDescription(String linkToTest) throws Exception {
        Assertions.assertFalse(new Channel(linkToTest).getDescription().isEmpty());
    }



    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@NoCopyrightSounds"
    })
    public void testGeThumbnailUrl(String linkToTest) throws Exception {
        Assertions.assertNotNull(new Channel(linkToTest).getThumbnailUrl());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic"
    })
    public void testGetVideos(String linkToTest) throws Exception {
        Assertions.assertTrue(30 < new Channel(linkToTest).getVideos().size());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic",
            "https://www.youtube.com/@spinninrecords",
            "https://www.youtube.com/@NoCopyrightSounds",
            "https://www.youtube.com/@Monstercat"
    })
    public void testGetShorts(String linkToTest) throws Exception {
        Assertions.assertTrue(48 < new Channel(linkToTest).getShorts().size());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@spinninrecords",
    })
    public void testGetLives(String linkToTest) throws Exception {
        Assertions.assertTrue(30 < new Channel(linkToTest).getLives().size());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic"
    })
    public void testGetReleases(String linkToTest) throws Exception {
        Assertions.assertTrue(28 < new Channel(linkToTest).getReleases().size());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/@Alanwalkermusic, 30",
            "https://www.youtube.com/@Monstercat, 30"
    })
    public void testGetPlaylists(String linkToTest, int expectedOutput) throws Exception {
        Assertions.assertTrue(expectedOutput < new Channel(linkToTest).getPlaylists().size());
    }

}
