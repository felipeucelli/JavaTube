package com.github.felipeucelli.javatube.sabr.common;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class FormatId {
    public int itag;
    public long lastModified;
    public String xtags;

    public FormatId() {
        this.itag = 0;
        this.lastModified = 0;
        this.xtags = null;
    }

    public static BinaryWriter encode(FormatId message, BinaryWriter writer) {
        if (writer == null) {
            writer = new BinaryWriter();
        }

        if (message.itag != 0) {
            writer.uint32(8).int32(message.itag);
        }

        if (message.lastModified != 0) {
            writer.uint32(16).uint64(message.lastModified);
        }

        if (message.xtags != null) {
            writer.uint32(26).string(message.xtags);
        }

        return writer;
    }

    public static FormatId decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        if (length != null) {
            end = reader.getPos() + length;
        }
        FormatId message = new FormatId();

        while (reader.getPos() < end) {
            long tag = reader.uint32();
            int fieldNo = (int) (tag >> 3);

            if (fieldNo == 1 && tag == 8) {
                message.itag = reader.int32();
                continue;
            } else if (fieldNo == 2 && tag == 16) {
                message.lastModified = reader.uint64();
                continue;
            } else if (fieldNo == 3 && tag == 26) {
                message.xtags = reader.string();
                continue;
            }

            if ((tag & 7) == 4 || tag == 0) {
                break;
            }

            reader.skip((int) (tag & 7), null);
        }

        return message;
    }
}


