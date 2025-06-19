package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class SabrRedirect {
    public String url;

    public SabrRedirect() {
        this.url = "";
    }

    public static BinaryWriter encode(SabrRedirect message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.url != null && !message.url.isEmpty()) {
            writer.uint32(10).string(message.url);
        }
        return writer;
    }

    public static SabrRedirect decode(Object inputData) {
        return decode(inputData, null);
    }
    public static SabrRedirect decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        SabrRedirect message = new SabrRedirect();
        while (reader.getPos() < end) {
            int tag = Math.toIntExact(reader.uint32());
            if (tag >>> 3 == 1) {
                if (tag == 10) {
                    message.url = reader.string();
                    continue;
                }
            }
            if ((tag & 7) == 4 || tag == 0) {
                break;
            } else {
                reader.skip(tag & 7);
            }
        }
        return message;
    }
}


