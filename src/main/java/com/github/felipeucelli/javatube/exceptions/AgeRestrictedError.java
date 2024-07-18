package com.github.felipeucelli.javatube.exceptions;

public class AgeRestrictedError extends Exception {
    public AgeRestrictedError(String videoId) {
        super("Video ID = " + videoId + " is age restricted, and can't be accessed without logging in");
    }
}
