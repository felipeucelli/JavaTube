package com.github.felipeucelli.javatube.exceptions;

public class BotDetectionError extends Exception {
    public BotDetectionError(String videoId) {
        super(videoId + " This request was detected as a bot. Use `usePoToken=True` to view");
    }
}
