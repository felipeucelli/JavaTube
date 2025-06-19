package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.common.FormatId;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class PlaybackCookie {
    public int field1;
    public int field2;
    public FormatId videoFmt;
    public FormatId audioFmt;

    public PlaybackCookie() {
        this.field1 = 0;
        this.field2 = 0;
        this.videoFmt = null;
        this.audioFmt = null;
    }

    public static BinaryWriter encode(PlaybackCookie message) {
        return encode(message, null);
    }

    public static BinaryWriter encode(PlaybackCookie message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.field1 != 0) {
            writer.uint32(8).int32(message.field1);
        }
        if (message.field2 != 0) {
            writer.uint32(16).int32(message.field2);
        }
        if (message.videoFmt != null) {
            FormatId.encode(message.videoFmt, writer.uint32(58).fork()).join();
        }
        if (message.audioFmt != null) {
            FormatId.encode(message.audioFmt, writer.uint32(66).fork()).join();
        }
        return writer;
    }

    public static PlaybackCookie decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        PlaybackCookie message = new PlaybackCookie();
        while (reader.getPos() < end) {
            int tag = Math.toIntExact(reader.uint32());
            switch (tag >>> 3) {
                case 1:
                    if (tag == 8) {
                        message.field1 = reader.int32();
                        continue;
                    }
                    break;
                case 2:
                    if (tag == 16) {
                        message.field2 = reader.int32();
                        continue;
                    }
                    break;
                case 7:
                    if (tag == 58) {
                        message.videoFmt = FormatId.decode(reader, (int) reader.uint32());
                        continue;
                    }
                    break;
                case 8:
                    if (tag == 66) {
                        message.audioFmt = FormatId.decode(reader, (int) reader.uint32());
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


