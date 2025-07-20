package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class StreamProtectionStatus {
    public int status;
    public int field2;

    public StreamProtectionStatus() {
        this.status = 0;
        this.field2 = 0;
    }

    public static BinaryWriter encode(StreamProtectionStatus message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.status != 0) {
            writer.uint32(8).int32(message.status);
        }
        if (message.field2 != 0) {
            writer.uint32(16).int32(message.field2);
        }
        return writer;
    }
    public static StreamProtectionStatus decode(Object inputData) {
        return decode(inputData, null);
    }
    public static StreamProtectionStatus decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        StreamProtectionStatus message = new StreamProtectionStatus();
        while (reader.getPos() < end) {
            int tag = Math.toIntExact(reader.uint32());
            switch (tag >>> 3) {
                case 1:
                    if (tag == 8) {
                        message.status = reader.int32();
                        continue;
                    }
                    break;
                case 2:
                    if (tag == 16) {
                        message.field2 = reader.int32();
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

    public enum Status {
        OK(1),
        ATTESTATION_PENDING(2),
        ATTESTATION_REQUIRED(3);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}


