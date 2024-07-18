package com.github.felipeucelli.javatube.exceptions;

public class RecordingUnavailableError extends Exception{
    public RecordingUnavailableError(String videoId) {
        super(videoId + " does not have a live stream recording available");
    }
}