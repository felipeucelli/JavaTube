package com.github.felipeucelli.javatube.exceptions;

public class VideoRegionBlockedError extends Exception {
    public VideoRegionBlockedError(String videoId) {
        super(videoId + " is not available in your region");
    }
}
