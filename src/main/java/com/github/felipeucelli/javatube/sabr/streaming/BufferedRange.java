package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.common.FormatId;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;


public class BufferedRange {

    public FormatId formatId;
    public long startTimeMs;
    public long durationMs;
    public Long startSegmentIndex;
    public Long endSegmentIndex;
    public TimeRange timeRange;
    public Kob field9;

    public BufferedRange() {
        this.formatId = null;
        this.startTimeMs = 0;
        this.durationMs = 0;
        this.startSegmentIndex = 0L;
        this.endSegmentIndex = 0L;
        this.timeRange = null;
        this.field9 = null;
    }

    public static BinaryWriter encode(BufferedRange message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.formatId != null) {
            FormatId.encode(message.formatId, writer.uint32(10).fork()).join();
        }
        if (message.startTimeMs != 0) {
            writer.uint32(16).int64(message.startTimeMs);
        }
        if (message.durationMs != 0) {
            writer.uint32(24).int64(message.durationMs);
        }
        if (message.startSegmentIndex != 0) {
            writer.uint32(32).int32(Math.toIntExact(message.startSegmentIndex));
        }
        if (message.endSegmentIndex != 0) {
            writer.uint32(40).int32(Math.toIntExact(message.endSegmentIndex));
        }
        if (message.timeRange != null) {
            TimeRange.encode(message.timeRange, writer.uint32(50).fork()).join();
        }
        if (message.field9 != null) {
            Kob.encode(message.field9, writer.uint32(74).fork()).join();
        }

        return writer;
    }

    public static BufferedRange decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        BufferedRange message = new BufferedRange();
        while (reader.getPos() < end) {
            int tag = (int) reader.uint32();
            switch (tag >>> 3) {
                case 1:
                    if (tag == 10) {
                        message.formatId = FormatId.decode(reader, (int) reader.uint32());
                        continue;
                    }
                    break;
                case 2:
                    if (tag == 16) {
                        message.startTimeMs = longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 3:
                    if (tag == 24) {
                        message.durationMs = longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 4:
                    if (tag == 32) {
                        message.startSegmentIndex = (long) reader.int32();
                        continue;
                    }
                    break;
                case 5:
                    if (tag == 40) {
                        message.endSegmentIndex = (long) reader.int32();
                        continue;
                    }
                    break;
                case 6:
                    if (tag == 50) {
                        message.timeRange = TimeRange.decode(reader, (int) reader.uint32());
                        continue;
                    }
                    break;
                case 9:
                    if (tag == 74) {
                        message.field9 = Kob.decode(reader, (int) reader.uint32());
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


    public static class Kob {
        public String videoId;
        public long lmt;

        public Kob() {
            this.videoId = null;
            this.lmt = 0;
        }

        public static BinaryWriter encode(Kob message, BinaryWriter writer) {
            writer = writer != null ? writer : new BinaryWriter();
            if (message.videoId != null && !message.videoId.isEmpty()) {
                writer.uint32(10).string(message.videoId);
            }
            if (message.lmt != 0) {
                writer.uint32(16).uint64(message.lmt);
            }
            return writer;
        }

        public static Kob decode(Object inputData, Integer length) {
            BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
            int end = length == null ? reader.len() : reader.getPos() + length;
            Kob message = new Kob();
            while (reader.getPos() < end) {
                int tag = (int) reader.uint32();
                switch (tag >>> 3) {
                    case 1:
                        if (tag == 10) {
                            message.videoId = reader.string();
                            continue;
                        }
                        break;
                    case 2:
                        if (tag == 16) {
                            message.lmt = reader.uint64();
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


}
