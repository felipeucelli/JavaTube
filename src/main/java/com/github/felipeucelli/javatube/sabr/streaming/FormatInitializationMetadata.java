package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.common.FormatId;
import com.github.felipeucelli.javatube.sabr.common.InitRange;
import com.github.felipeucelli.javatube.sabr.common.IndexRange;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

import java.util.Map;

public class FormatInitializationMetadata {
    public String videoId;
    public FormatId formatId;
    public int endTimeMs;
    public long endSegmentNumber;
    public String mimeType;
    public InitRange initRange;
    public Map<String, Object> indexRange;
    public int field8;
    public int durationMs;
    public int field10;

    public FormatInitializationMetadata() {
        this.videoId = "";
        this.formatId = null;
        this.endTimeMs = 0;
        this.endSegmentNumber = 0;
        this.mimeType = "";
        this.initRange = null;
        this.indexRange = null;
        this.field8 = 0;
        this.durationMs = 0;
        this.field10 = 0;
    }

    public static BinaryWriter encode(FormatInitializationMetadata message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.videoId != null && !message.videoId.isEmpty()) {
            writer.uint32(10).string(message.videoId);
        }
        if (message.formatId != null) {
            writer.uint32(18);
            FormatId.encode(message.formatId, writer.fork()).join();
        }
        if (message.endTimeMs != 0) {
            writer.uint32(24).int32(message.endTimeMs);
        }
        if (message.endSegmentNumber != 0) {
            writer.uint32(32).int64(message.endSegmentNumber);
        }
        if (message.mimeType != null && !message.mimeType.isEmpty()) {
            writer.uint32(42).string(message.mimeType);
        }
        if (message.initRange != null) {
            writer.uint32(50);
            InitRange.encode(message.initRange, writer.fork()).join();
        }
        if (message.indexRange != null) {
            writer.uint32(58);
            IndexRange.encode(message.indexRange, writer.fork()).join();
        }
        if (message.field8 != 0) {
            writer.uint32(64).int32(message.field8);
        }
        if (message.durationMs != 0) {
            writer.uint32(72).int32(message.durationMs);
        }
        if (message.field10 != 0) {
            writer.uint32(80).int32(message.field10);
        }
        return writer;
    }

    public static FormatInitializationMetadata decode(Object inputData) {
        return decode(inputData, null);
    }
    public static FormatInitializationMetadata decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        FormatInitializationMetadata message = new FormatInitializationMetadata();
        while (reader.getPos() < end) {
            int tag = Math.toIntExact(reader.uint32());
            switch (tag >>> 3) {
                case 1:
                    if (tag == 10) {
                        message.videoId = reader.string();
                        continue;
                    }
                    break;
                case 2:
                    if (tag == 18) {
                        message.formatId = FormatId.decode(reader, (int) reader.uint32());
                        continue;
                    }
                    break;
                case 3:
                    if (tag == 24) {
                        message.endTimeMs = reader.int32();
                        continue;
                    }
                    break;
                case 4:
                    if (tag == 32) {
                        message.endSegmentNumber = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 5:
                    if (tag == 42) {
                        message.mimeType = reader.string();
                        continue;
                    }
                    break;
                case 6:
                    if (tag == 50) {
                        message.initRange = InitRange.decode(reader, (int) reader.uint32());
                        continue;
                    }
                    break;
                case 7:
                    if (tag == 58) {
                        message.indexRange = IndexRange.decode(reader, (int) reader.uint32());
                        continue;
                    }
                    break;
                case 8:
                    if (tag == 64) {
                        message.field8 = reader.int32();
                        continue;
                    }
                    break;
                case 9:
                    if (tag == 72) {
                        message.durationMs = reader.int32();
                        continue;
                    }
                    break;
                case 10:
                    if (tag == 80) {
                        message.field10 = reader.int32();
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


