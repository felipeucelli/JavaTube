package com.github.felipeucelli.javatube.exceptions;

public class LiveStreamError extends Exception{
    public LiveStreamError(String videoId) {
        super(videoId + " is streaming live and cannot be loaded");
    }
}
