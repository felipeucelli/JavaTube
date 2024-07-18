package com.github.felipeucelli.javatube.exceptions;

public class VideoUnavailableError extends Exception{
    public VideoUnavailableError(String videoId) {
        super(videoId + " is unavailable");
    }
}
