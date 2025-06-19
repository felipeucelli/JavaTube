package com.github.felipeucelli.javatube.sabr.common;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class InitRange {
    public int start;
    public int end;

    public InitRange() {
        this.start = 0;
        this.end = 0;
    }

    public InitRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public static BinaryWriter encode(InitRange message, BinaryWriter writer) {
        if (writer == null) {
            writer = new BinaryWriter();
        }

        if (message.start != 0) {
            writer.uint32(8);
            writer.int32(message.start);
        }

        if (message.end != 0) {
            writer.uint32(16);
            writer.int32(message.end);
        }

        return writer;
    }

    public static InitRange decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        if (length != null) {
            end = reader.getPos() + length;
        }
        InitRange message = new InitRange();

        while (reader.getPos() < end) {
            long tag = reader.uint32();
            int fieldNo = (int) (tag >> 3);

            if (fieldNo == 1 && tag == 8) {
                message.start = reader.int32();
                continue;
            } else if (fieldNo == 2 && tag == 16) {
                message.end = reader.int32();
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


