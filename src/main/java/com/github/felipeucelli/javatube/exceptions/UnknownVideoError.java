package com.github.felipeucelli.javatube.exceptions;

public class UnknownVideoError extends Exception{
    public UnknownVideoError(String videoId, String status, String reason){
        super("Unknown Video Error, VideoId: " + videoId + " Status: " + status + " Reason: " + reason);
    }
}
