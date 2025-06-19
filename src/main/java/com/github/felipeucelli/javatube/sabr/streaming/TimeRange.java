package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class TimeRange {
    private Integer start;
    private Integer duration;
    private Integer timescale;

    public TimeRange() {
        this.start = 0;
        this.duration = 0;
        this.timescale = 0;

    }

    public static BinaryWriter encode(TimeRange message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.start != 0) {
            writer.uint32(8).int64(message.start);
        }
        if (message.duration != 0) {
            writer.uint32(16).int64(message.duration);
        }
        if (message.timescale != 0) {
            writer.uint32(24).int64(message.timescale);
        }

        return writer;
    }

    public static TimeRange decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        TimeRange message = new TimeRange();

        while (reader.getPos() < end) {
            int tag = (int) reader.uint32();
            int field = tag >>> 3;

            switch (field) {
                case 1:
                    if (tag == 8) {
                        message.start = (int) longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 2:
                    if (tag == 16) {
                        message.duration = (int) longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 3:
                    if (tag == 24) {
                        message.timescale = reader.int32();
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

    public static long longToNumber(long int64Value) {
        if (int64Value > (Math.pow(2, 53) - 1)) {
            throw new ArithmeticException("Value is larger than 9007199254740991");
        }
        if (int64Value < -(Math.pow(2, 53) - 1)) {
            throw new ArithmeticException("Value is smaller than -9007199254740991");
        }
        return int64Value;
    }
}
