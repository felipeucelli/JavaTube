package com.github.felipeucelli.javatube;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


public class PlaylistTest {

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj, https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj",
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u, https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u"
    })
    public void testGetUrl(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Playlist(linkToTest).getUrl());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj, Pop Music Playlist - Timeless Pop Songs (Updated Weekly 2024)",
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u, Best Pop Music Videos - Top Pop Hits Playlist"
    })
    public void testGetTitle(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Playlist(linkToTest).getTitle());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj",
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u"
    })
    public void testLength(String linkToTest) throws Exception {
        Assertions.assertTrue(100 < Integer.parseInt(new Playlist(linkToTest).length().replace(",", "")));
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj",
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u"
    })
    public void testGetVideos(String linkToTest) throws Exception {
        Assertions.assertTrue(100 < new Playlist(linkToTest).getVideos().size());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj",
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u"
    })
    public void testGetViews(String linkToTest) throws Exception {
        Assertions.assertNotNull(new Playlist(linkToTest).getViews());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj, by Redlist - Just Hits",
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u, Redlist - Just Hits"
    })
    public void testGetOwner(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Playlist(linkToTest).getOwner());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj, https://www.youtube.com/channel/UCs72iRpTEuwV3y6pdWYLgiw",
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u, https://www.youtube.com/channel/UCs72iRpTEuwV3y6pdWYLgiw"
    })
    public void testGetOwnerUrl(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Playlist(linkToTest).getOwnerUrl());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj, UCs72iRpTEuwV3y6pdWYLgiw",
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u, UCs72iRpTEuwV3y6pdWYLgiw"
    })
    public void testGetOwnerId(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Playlist(linkToTest).getOwnerId());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj",
            "https://www.youtube.com/playlist?list=PLMC9KNkIncKtGvr2kFRuXBVmBev6cAJ2u"
    })
    public void testGetLastUpdated(String linkToTest) throws Exception {
        Assertions.assertNotNull(new Playlist(linkToTest).getLastUpdated());
    }

}
