package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.common.FormatId;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class MediaHeader {
    public Integer headerId;
    public String videoId;
    public Integer itag;
    public Long lmt;
    public String xtags;
    public Long startRange;
    public Integer compressionAlgorithm;
    public Boolean isInitSeg;
    public Integer sequenceNumber;
    public Long field10;
    public Long startMs;
    public Long durationMs;
    public FormatId formatId;
    public Long contentLength;
    public TimeRange timeRange;


    public MediaHeader() {
        this.headerId = 0;
        this.videoId = "";
        this.itag = 0;
        this.lmt = 0L;
        this.xtags = "";
        this.startRange = 0L;
        this.compressionAlgorithm = 0;
        this.isInitSeg = false;
        this.sequenceNumber = 0;
        this.field10 = 0L;
        this.startMs = 0L;
        this.durationMs = 0L;
        this.formatId = null;
        this.contentLength = 0L;
        this.timeRange = null;

    }

    public static MediaHeader decode(Object inputData) {
        return decode(inputData, null);
    }
    public static MediaHeader decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        MediaHeader message = new MediaHeader();

        while (reader.getPos() < end) {
            int tag = (int) reader.uint32();
            int field = tag >>> 3;

            if (field == 1 && tag == 8) {
                message.headerId = (int) reader.uint32();
            }

             else if (field == 2 && tag == 18){
                 message.videoId = reader.string();
             }

             else if (field == 3 && tag ==24){
                message.itag = reader.int32();
             }

             else if (field == 4 && tag == 32){
                message.lmt = BufferedRange.longToNumber(reader.uint64());
             }

             else if (field == 5 && tag == 42){
                message.xtags = reader.string();
             }

             else if (field == 6 && tag == 48) {
                message.startRange = BufferedRange.longToNumber(reader.int64());
             }

             else if (field == 7 && tag == 56){
                message.compressionAlgorithm = reader.int32();
             }

             else if (field == 8 && tag ==64){
                message.isInitSeg = reader.bool();
             }

             else if (field == 9 && tag == 72){
                message.sequenceNumber = (int) BufferedRange.longToNumber(reader.int64());
             }

             else if (field == 10 && tag == 80){
                message.field10 = BufferedRange.longToNumber(reader.int64());
             }

             else if (field == 11 && tag == 80){
                message.startMs = BufferedRange.longToNumber(reader.int64());
             }

             else if (field == 12 && tag == 96){
                message.durationMs = BufferedRange.longToNumber(reader.int64());
             }

             else if (field == 13 && tag == 106){
                message.formatId = FormatId.decode(reader, (int) reader.uint32());
             }

             else if (field == 14 && tag == 112){
                message.contentLength = BufferedRange.longToNumber(reader.int64());
             }

             else if (field == 15 && tag == 122){
                message.timeRange = TimeRange.decode(reader, (int) reader.uint32());
             }

             else if ((tag & 7) == 4 || tag == 0){
                 break;
            }
             else {
                 reader.skip(tag & 7);
            }

        }
        return message;
    }

    public static BinaryWriter encode(MediaHeader message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.headerId != 0) {
            writer.uint32(8).uint32(message.headerId);
        }
        if (message.videoId != null && !message.videoId.isEmpty()) {
            writer.uint32(18).string(message.videoId);
        }
        if (message.itag != 0) {
            writer.uint32(24).int32(message.itag);
        }
        if (message.lmt != 0) {
            writer.uint32(32).uint64(message.lmt);
        }
        if (message.xtags != null && !message.xtags.isEmpty()) {
            writer.uint32(42).string(message.xtags);
        }
        if (message.startRange != 0) {
            writer.uint32(48).int64(message.startRange);
        }
        if (message.compressionAlgorithm != 0) {
            writer.uint32(56).int32(message.compressionAlgorithm);
        }
        if (message.isInitSeg) {
            writer.uint32(64).bool(message.isInitSeg);
        }
        if (message.sequenceNumber != 0) {
            writer.uint32(72).int64(message.sequenceNumber);
        }
        if (message.field10 != 0) {
            writer.uint32(80).int64(message.field10);
        }
        if (message.startMs != 0) {
            writer.uint32(88).int64(message.startMs);
        }
        if (message.durationMs != 0) {
            writer.uint32(96).int64(message.durationMs);
        }
        if (message.formatId != null) {
            FormatId.encode(message.formatId, writer.uint32(106).fork()).join();
        }
        if (message.contentLength != 0) {
            writer.uint32(112).int64(message.contentLength);
        }
        if (message.timeRange != null) {
            TimeRange.encode(message.timeRange, writer.uint32(122).fork()).join();
        }
        return writer;
    }
}


