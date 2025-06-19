package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.common.FormatId;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

import java.util.List;
import java.util.ArrayList;

public class VideoPlaybackAbrRequest {
    public ClientAbrState clientAbrState;
    public List<FormatId> selectedAudioFormatIds;
    public List<FormatId> selectedVideoFormatIds;
    public List<FormatId> selectedFormatIds;
    public byte[] videoPlaybackUstreamerConfig;
    public StreamerContext streamerContext;
    public List<BufferedRange> bufferedRanges;
    public List<Object> field1000;

    public VideoPlaybackAbrRequest() {
        this.clientAbrState = null;
        this.selectedAudioFormatIds = new ArrayList<>();
        this.selectedVideoFormatIds = new ArrayList<>();
        this.selectedFormatIds = new ArrayList<>();
        this.videoPlaybackUstreamerConfig = null;
        this.streamerContext = null;
        this.bufferedRanges = null;
        this.field1000 = null;
    }

    public static BinaryWriter encode(VideoPlaybackAbrRequest message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.clientAbrState != null){
            writer.uint32(10);
            ClientAbrState.encode(message.clientAbrState, writer.fork());
            writer.join();
        }

        for(FormatId v : message.selectedFormatIds){
            writer.uint32(18);
            FormatId.encode(v, writer.fork());
            writer.join();
        }

        for(BufferedRange v : message.bufferedRanges){
            writer.uint32(26);
            BufferedRange.encode(v, writer.fork());
            writer.join();
        }

        if (message.videoPlaybackUstreamerConfig != null){
            writer.uint32(42).bytes(message.videoPlaybackUstreamerConfig);
        }

        for(FormatId v : message.selectedAudioFormatIds){
            writer.uint32(130);
            FormatId.encode(v, writer.fork());
            writer.join();
        }

        for(FormatId v : message.selectedVideoFormatIds){
            writer.uint32(138);
            FormatId.encode(v, writer.fork());
            writer.join();
        }

        if (message.streamerContext != null){
            writer.uint32(154);
            StreamerContext.encode(message.streamerContext, writer.fork());
            writer.join();
        }
        return writer;
    }
    public static VideoPlaybackAbrRequest decode(Object inputData) {
        return decode(inputData, null);
    }

    public static VideoPlaybackAbrRequest decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        VideoPlaybackAbrRequest message = new VideoPlaybackAbrRequest();
        while (reader.getPos() < end) {
            int tag = (int) reader.uint32();
            switch (tag >>> 3) {
                case 1:
                    if (tag == 10) {
                        message.clientAbrState = ClientAbrState.decode(reader, (int) reader.uint32());
                        continue;
                    }
                    break;
                case 2:
                    if (tag == 18) {
                        message.selectedFormatIds.add(FormatId.decode(reader, (int) reader.uint32()));
                        continue;
                    }
                    break;
                case 3:
                    if (tag == 26) {
                        message.bufferedRanges.add(BufferedRange.decode(reader, (int) reader.uint32()));
                        continue;
                    }
                    break;
                case 5:
                    if (tag == 42) {
                        message.videoPlaybackUstreamerConfig = reader.bytes();
                        continue;
                    }
                    break;
                case 16:
                    if (tag == 130) {
                        message.selectedAudioFormatIds.add(FormatId.decode(reader, (int) reader.uint32()));
                        continue;
                    }
                    break;
                case 17:
                    if (tag == 138) {
                        message.selectedVideoFormatIds.add(FormatId.decode(reader, (int) reader.uint32()));
                        continue;
                    }
                    break;
                case 19:
                    if (tag == 154) {
                        message.streamerContext = StreamerContext.decode(reader, (int) reader.uint32());
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


