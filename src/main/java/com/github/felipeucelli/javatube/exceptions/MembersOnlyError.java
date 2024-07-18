package com.github.felipeucelli.javatube.exceptions;

public class MembersOnlyError extends Exception{
    public MembersOnlyError(String videoId) {
        super(videoId + " is a members-only video");
    }
}
