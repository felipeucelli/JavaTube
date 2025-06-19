package com.github.felipeucelli.javatube.sabr.common;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;
import java.util.HashMap;
import java.util.Map;

public class IndexRange {
    public int start;
    public int end;

    public IndexRange() {
        this.start = 0;
        this.end = 0;
    }

    public static BinaryWriter encode(Map<String, Object> message, BinaryWriter writer) {
        if (writer == null) {
            writer = new BinaryWriter();
        }

        if (message.containsKey("start") && (int) message.get("start") != 0) {
            writer.uint32(8).int32((int) message.get("start"));
        }

        if (message.containsKey("end") && (int) message.get("end") != 0) {
            writer.uint32(16).int32((int) message.get("end"));
        }

        return writer;
    }

    public static Map<String, Object> decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        if (length != null) {
            end = reader.getPos() + length;
        }
        Map<String, Object> message = new HashMap<>();
        message.put("start", 0);
        message.put("end", 0);

        while (reader.getPos() < end) {
            long tag = reader.uint32();
            int fieldNo = (int) (tag >> 3);

            if (fieldNo == 1 && tag == 8) {
                message.put("start", reader.int32());
                continue;
            } else if (fieldNo == 2 && tag == 16) {
                message.put("end", reader.int32());
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


