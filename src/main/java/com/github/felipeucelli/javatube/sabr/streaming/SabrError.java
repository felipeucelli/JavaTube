package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class SabrError {
    public String type;
    public int code;

    public SabrError() {
        this.type = "";
        this.code = 0;
    }

    public static BinaryWriter encode(SabrError message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.type != null && !message.type.isEmpty()) {
            writer.uint32(10).string(message.type);
        }
        if (message.code != 0) {
            writer.uint32(16).int32(message.code);
        }
        return writer;
    }

    public static SabrError decode(Object inputData) {
        return decode(inputData, null);
    }

    public static SabrError decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        SabrError message = new SabrError();
        while (reader.getPos() < end) {
            int tag = Math.toIntExact(reader.uint32());
            switch (tag >>> 3) {
                case 1:
                    if (tag == 10) {
                        message.type = reader.string();
                        continue;
                    }
                    break;
                case 2:
                    if (tag == 16) {
                        message.code = reader.int32();
                        continue;
                    }
                    break;
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


