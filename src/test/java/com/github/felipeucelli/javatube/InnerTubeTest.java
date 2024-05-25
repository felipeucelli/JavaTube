package com.github.felipeucelli.javatube;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class InnerTubeTest {

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=60ItHLz5WEA, WEB",
            "https://www.youtube.com/watch?v=60ItHLz5WEA, WEB_EMBED",
            "https://www.youtube.com/watch?v=60ItHLz5WEA, WEB_MUSIC",
            "https://www.youtube.com/watch?v=60ItHLz5WEA, WEB_CREATOR",

          //  "https://www.youtube.com/watch?v=60ItHLz5WEA, ANDROID",
            "https://www.youtube.com/watch?v=60ItHLz5WEA, ANDROID_EMBED",
            "https://www.youtube.com/watch?v=60ItHLz5WEA, ANDROID_MUSIC",
            "https://www.youtube.com/watch?v=60ItHLz5WEA, ANDROID_CREATOR",

            "https://www.youtube.com/watch?v=60ItHLz5WEA, IOS",
        //    "https://www.youtube.com/watch?v=60ItHLz5WEA, IOS_EMBED",
            "https://www.youtube.com/watch?v=60ItHLz5WEA, IOS_MUSIC",
            "https://www.youtube.com/watch?v=60ItHLz5WEA, IOS_CREATOR",

            "https://www.youtube.com/watch?v=60ItHLz5WEA, MWEB",

            "https://www.youtube.com/watch?v=60ItHLz5WEA, TV_EMBED"
    })
    public void testGetStreamData(String linkToTest, String client) {
        assertDoesNotThrow(() ->
                Assertions.assertNotNull(new Youtube(linkToTest, client).streamData())
        );
    }
}