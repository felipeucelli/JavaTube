package com.github.felipeucelli.javatube;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.ValueSource;


public class YoutubeTest {


    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=2lAe1cqCOXo, https://www.youtube.com/watch?v=2lAe1cqCOXo",
            "https://www.youtube.com/watch?v=WNeLUngb-Xg, https://www.youtube.com/watch?v=WNeLUngb-Xg"
    })
    public void testGetUrl(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Youtube(linkToTest).getUrl());

    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=2lAe1cqCOXo, YouTube Rewind 2019: For the Record | #YouTubeRewind",
            "https://www.youtube.com/watch?v=WNeLUngb-Xg, Linkin Park - In The End (Mellen Gi & Tommee Profitt Remix)"
    })
    public void testGetTitle(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Youtube(linkToTest).getTitle());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=2lAe1cqCOXo, YouTube",
            "https://www.youtube.com/watch?v=WNeLUngb-Xg, TrapMusicHDTV"
    })
    public void testGetAuthor(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Youtube(linkToTest).getAuthor());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=2lAe1cqCOXo, https://i.ytimg.com/vi/2lAe1cqCOXo/maxresdefault.jpg",
            "https://www.youtube.com/watch?v=WNeLUngb-Xg, https://i.ytimg.com/vi/WNeLUngb-Xg/maxresdefault.jpg"
    })
    public void testGetThumbnailUrl(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Youtube(linkToTest).getThumbnailUrl());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=2lAe1cqCOXo, 120854317",
            "https://www.youtube.com/watch?v=WNeLUngb-Xg, 120854317"
    })
    public void testGetViews(String linkToTest, long ref) throws Exception {
        Assertions.assertTrue(new Youtube(linkToTest).getViews() >=  ref);
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=2lAe1cqCOXo, 2019-12-05",
            "https://www.youtube.com/watch?v=WNeLUngb-Xg, 2018-10-09"
    })
    public void testGetPublishDate(String linkToTest, String expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Youtube(linkToTest).getPublishDate());
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=2lAe1cqCOXo, 337",
            "https://www.youtube.com/watch?v=WNeLUngb-Xg, 219"
    })
    public void testLength(String linkToTest, int expectedOutput) throws Exception {
        Assertions.assertEquals(expectedOutput, new Youtube(linkToTest).length());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://www.youtube.com/watch?v=2lAe1cqCOXo",
            "https://www.youtube.com/watch?v=WNeLUngb-Xg"
    })
    public void testGetDescription(String linkToTest) throws Exception {
        Assertions.assertNotNull(new Youtube(linkToTest).getDescription());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://www.youtube.com/watch?v=2lAe1cqCOXo",
            "https://www.youtube.com/watch?v=WNeLUngb-Xg"
    })
    public void testGetKeyword(String linkToTest) throws Exception {
        Assertions.assertNotNull(new Youtube(linkToTest).getKeywords());
    }
}
