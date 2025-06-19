package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class NextRequestPolicy {
    public int targetAudioReadaheadMs;
    public int targetVideoReadaheadMs;
    public int backoffTimeMs;
    public PlaybackCookie playbackCookie;
    public String videoId;

    public NextRequestPolicy() {
        this.targetAudioReadaheadMs = 0;
        this.targetVideoReadaheadMs = 0;
        this.backoffTimeMs = 0;
        this.playbackCookie = null;
        this.videoId = "";
    }

    public static BinaryWriter encode(NextRequestPolicy message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.targetAudioReadaheadMs != 0) {
            writer.uint32(8).int32(message.targetAudioReadaheadMs);
        }
        if (message.targetVideoReadaheadMs != 0) {
            writer.uint32(16).int32(message.targetVideoReadaheadMs);
        }
        if (message.backoffTimeMs != 0) {
            writer.uint32(32).int32(message.backoffTimeMs);
        }
        if (message.playbackCookie != null) {
            PlaybackCookie.encode(message.playbackCookie, writer.uint32(58).fork()).join();
        }
        if (message.videoId != null && !message.videoId.isEmpty()) {
            writer.uint32(66).string(message.videoId);
        }
        return writer;
    }

    public static NextRequestPolicy decode(Object inputData) {
        return decode(inputData, null);
    }
    public static NextRequestPolicy decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        NextRequestPolicy message = new NextRequestPolicy();
        while (reader.getPos() < end) {
            int tag = Math.toIntExact(reader.uint32());
            switch (tag >>> 3) {
                case 1:
                    if (tag == 8) {
                        message.targetAudioReadaheadMs = reader.int32();
                        continue;
                    }
                    break;
                case 2:
                    if (tag == 16) {
                        message.targetVideoReadaheadMs = reader.int32();
                        continue;
                    }
                    break;
                case 4:
                    if (tag == 32) {
                        message.backoffTimeMs = reader.int32();
                        continue;
                    }
                    break;
                case 7:
                    if (tag == 58) {
                        message.playbackCookie = PlaybackCookie.decode(reader, (int) reader.uint32());
                        continue;
                    }
                    break;
                case 8:
                    if (tag == 66) {
                        message.videoId = reader.string();
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


