package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

import java.util.List;

public class StreamerContext {
    public List<Object> field5;
    public List<Object> field6;
    public byte[] poToken;
    public PlaybackCookie playbackCookie;
    public StreamerContextClientInfo clientInfo;

    public StreamerContext() {
        this.field5 = null;
        this.field6 = null;
        this.poToken = null;
        this.playbackCookie = null;
        this.clientInfo = null;
    }

    public static BinaryWriter encode(StreamerContext message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.clientInfo != null) {
            StreamerContextClientInfo.encode(message.clientInfo, writer.uint32(10).fork()).join();
        }
        if (message.poToken != null){
            writer.uint32(18).bytes(message.poToken);
        }
        if (message.playbackCookie != null){
            writer.uint32(26).bytes(PlaybackCookie.encode(message.playbackCookie).finish());
        }

        return writer;
    }

    public static StreamerContext decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        StreamerContext message = new StreamerContext();
        while (reader.getPos() < end) {
            int tag = Math.toIntExact(reader.uint32());
            switch (tag >>> 3) {
                case 1:
                    if (tag == 10) {
                        message.clientInfo = StreamerContextClientInfo.decode(reader, (int) reader.uint32());
                        continue;
                    }
                    break;
                case 2:
                    if (tag == 18){
                        message.poToken = reader.bytes();
                        continue;
                    }
                    break;
                case 3:
                    if (tag == 26){
                        message.playbackCookie = PlaybackCookie.decode(reader, (int) reader.uint32());
                    }

            }
            if ((tag & 7) == 4 || tag == 0) {
                break;
            } else {
                reader.skip(tag & 7);
            }
        }
        return message;
    }

    public static class StreamerContextClientInfo {
        public String locale;
        public String deviceMake;
        public String deviceModel;
        public Integer clientName;
        public String clientVersion;
        public String platform;
        public String osName;
        public String osVersion;
        public String acceptLanguage;
        public String acceptRegion;
        public Integer screenWidthPoints;
        public Integer screenHeightPoints;
        public Float screenWidthInches;
        public Float screenHeightInches;
        public Integer screenPixelDensity;
        public Integer clientFormFactor;
        public Integer gmscoreVersionCode;
        public Integer windowWidthPoints;
        public Integer windowHeightPoints;
        public Integer androidSdkVersion;
        public Float screenDensityFloat;
        public Long utcOffsetMinutes;
        public String timeZone;
        public String chipset;
        public StreamerContextGLDeviceInfo glDeviceInfo;

        public StreamerContextClientInfo() {
            this.locale = null;
            this.deviceMake = null;
            this.deviceModel = null;
            this.clientName = null;
            this.clientVersion = null;
            this.platform = null;
            this.osName = null;
            this.osVersion = null;
            this.acceptLanguage = null;
            this.acceptRegion = null;
            this.screenWidthPoints = null;
            this.screenHeightPoints = null;
            this.screenWidthInches = null;
            this.screenHeightInches = null;
            this.screenPixelDensity = null;
            this.clientFormFactor = null;
            this.gmscoreVersionCode = null;
            this.windowWidthPoints = null;
            this.windowHeightPoints = null;
            this.androidSdkVersion = null;
            this.screenDensityFloat = null;
            this.utcOffsetMinutes = null;
            this.timeZone = null;
            this.chipset = null;
            this.glDeviceInfo = null;
        }

        public static BinaryWriter encode(StreamerContextClientInfo message, BinaryWriter writer) {
            writer = writer != null ? writer : new BinaryWriter();
            if (message.locale != null && !message.locale.isEmpty()) {
                writer.uint32(10).string(message.locale);
            }
            if (message.deviceMake != null && !message.deviceMake.isEmpty()) {
                writer.uint32(98).string(message.deviceMake);
            }
            if (message.deviceModel != null && !message.deviceModel.isEmpty()) {
                writer.uint32(106).string(message.deviceModel);
            }
            if (message.clientName != null && message.clientName != 0) {
                writer.uint32(128).int32(message.clientName);
            }
            if (message.clientVersion != null && !message.clientVersion.isEmpty()) {
                writer.uint32(138).string(message.clientVersion);
            }
            if (message.osName != null && !message.osName.isEmpty()) {
                writer.uint32(146).string(message.osName);
            }
            if (message.osVersion != null && !message.osVersion.isEmpty()) {
                writer.uint32(154).string(message.osVersion);
            }
            if (message.acceptLanguage != null && !message.acceptLanguage.isEmpty()) {
                writer.uint32(170).string(message.acceptLanguage);
            }
            if (message.acceptRegion != null && !message.acceptRegion.isEmpty()) {
                writer.uint32(178).string(message.acceptRegion);
            }
            if (message.screenWidthPoints != null && message.screenWidthPoints != 0) {
                writer.uint32(296).int32(message.screenWidthPoints);
            }
            if (message.screenHeightPoints != null && message.screenHeightPoints != 0) {
                writer.uint32(304).int32(message.screenHeightPoints);
            }
            if (message.screenWidthInches != null && message.screenWidthInches != 0) {
                writer.uint32(317).writeFloat(message.screenWidthInches);
            }
            if (message.screenHeightInches != null && message.screenHeightInches != 0) {
                writer.uint32(325).writeFloat(message.screenHeightInches);
            }
            if (message.screenPixelDensity != null && message.screenPixelDensity != 0) {
                writer.uint32(328).int32(message.screenPixelDensity);
            }
            if (message.clientFormFactor != null && message.clientFormFactor != 0) {
                writer.uint32(368).int32(message.clientFormFactor);
            }
            if (message.gmscoreVersionCode != null && message.gmscoreVersionCode != 0) {
                writer.uint32(400).int32(message.gmscoreVersionCode);
            }
            if (message.windowWidthPoints != null && message.windowWidthPoints != 0) {
                writer.uint32(440).int32(message.windowWidthPoints);
            }
            if (message.windowHeightPoints != null && message.windowHeightPoints != 0) {
                writer.uint32(448).int32(message.windowHeightPoints);
            }
            if (message.androidSdkVersion != null && message.androidSdkVersion != 0) {
                writer.uint32(512).int32(message.androidSdkVersion);
            }
            if (message.screenDensityFloat != null && message.screenDensityFloat != 0) {
                writer.uint32(525).writeFloat(message.screenDensityFloat);
            }
            if (message.utcOffsetMinutes != null && message.utcOffsetMinutes != 0) {
                writer.uint32(536).int64(message.utcOffsetMinutes);
            }
            if (message.timeZone != null && !message.timeZone.isEmpty()) {
                writer.uint32(642).string(message.timeZone);
            }
            if (message.chipset != null && !message.chipset.isEmpty()) {
                writer.uint32(738).string(message.chipset);
            }
            if (message.glDeviceInfo != null) {
                StreamerContextGLDeviceInfo.encode(message.glDeviceInfo, writer.uint32(818).fork()).join();
            }
            return writer;
        }

        public static StreamerContextClientInfo decode(Object inputData, Integer length) {
            BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
            int end = length == null ? reader.len() : reader.getPos() + length;
            StreamerContext.StreamerContextClientInfo message = new StreamerContext.StreamerContextClientInfo();
            while (reader.getPos() < end) {
                int tag = Math.toIntExact(reader.uint32());
                switch (tag >>> 3) {
                    case 1:
                        if (tag == 10) {
                            message.locale = reader.string();
                            continue;
                        }
                        break;
                    case 12:
                        if (tag == 98) {
                            message.deviceMake = reader.string();
                            continue;
                        }
                        break;
                    case 13:
                        if (tag == 106) {
                            message.deviceModel = reader.string();
                            continue;
                        }
                        break;
                    case 16:
                        if (tag == 128) {
                            message.clientName = reader.int32();
                            continue;
                        }
                        break;
                    case 17:
                        if (tag == 138) {
                            message.clientVersion = reader.string();
                            continue;
                        }
                        break;
                    case 18:
                        if (tag == 146) {
                            message.osName = reader.string();
                            continue;
                        }
                        break;
                    case 19:
                        if (tag == 154) {
                            message.osVersion = reader.string();
                            continue;
                        }
                        break;
                    case 21:
                        if (tag == 170) {
                            message.acceptLanguage = reader.string();
                            continue;
                        }
                        break;
                    case 22:
                        if (tag == 178) {
                            message.acceptRegion = reader.string();
                            continue;
                        }
                        break;
                    case 37:
                        if (tag == 296) {
                            message.screenWidthPoints = reader.int32();
                            continue;
                        }
                        break;
                    case 38:
                        if (tag == 304) {
                            message.screenHeightPoints = reader.int32();
                            continue;
                        }
                        break;
                    case 39:
                        if (tag == 317) {
                            message.screenWidthInches = reader.readFloat();
                            continue;
                        }
                        break;
                    case 40:
                        if (tag == 325) {
                            message.screenHeightInches = reader.readFloat();
                            continue;
                        }
                        break;
                    case 41:
                        if (tag == 328) {
                            message.screenPixelDensity = reader.int32();
                            continue;
                        }
                        break;
                    case 46:
                        if (tag == 368) {
                            message.clientFormFactor = reader.int32();
                            continue;
                        }
                        break;
                    case 50:
                        if (tag == 400) {
                            message.gmscoreVersionCode = reader.int32();
                            continue;
                        }
                        break;
                    case 55:
                        if (tag == 440) {
                            message.windowWidthPoints = reader.int32();
                            continue;
                        }
                        break;
                    case 56:
                        if (tag == 448) {
                            message.windowHeightPoints = reader.int32();
                            continue;
                        }
                        break;
                    case 64:
                        if (tag == 512) {
                            message.androidSdkVersion = reader.int32();
                            continue;
                        }
                        break;
                    case 65:
                        if (tag == 525) {
                            message.screenDensityFloat = reader.readFloat();
                            continue;
                        }
                        break;
                    case 67:
                        if (tag == 536) {
                            message.utcOffsetMinutes = BufferedRange.longToNumber(reader.int64());
                            continue;
                        }
                        break;
                    case 80:
                        if (tag == 642) {
                            message.timeZone = reader.string();
                            continue;
                        }
                        break;
                    case 92:
                        if (tag == 738) {
                            message.chipset = reader.string();
                            continue;
                        }
                        break;
                    case 102:
                        if (tag == 818) {
                            message.glDeviceInfo = StreamerContextGLDeviceInfo.decode(reader, (int) reader.uint32());
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
    public static class StreamerContextGLDeviceInfo {
        public String glRenderer;
        public int glEsVersionMajor;
        public int glEsVersionMinor;

        public StreamerContextGLDeviceInfo() {
            this.glRenderer = "";
            this.glEsVersionMajor = 0;
            this.glEsVersionMinor = 0;
        }

        public static BinaryWriter encode(StreamerContextGLDeviceInfo message, BinaryWriter writer) {
            writer = writer != null ? writer : new BinaryWriter();
            if (message.glRenderer != null && !message.glRenderer.isEmpty()) {
                writer.uint32(10).string(message.glRenderer);
            }
            if (message.glEsVersionMajor != 0) {
                writer.uint32(16).int32(message.glEsVersionMajor);
            }
            if (message.glEsVersionMinor != 0) {
                writer.uint32(24).int32(message.glEsVersionMinor);
            }
            return writer;
        }

        public static StreamerContextGLDeviceInfo decode(Object inputData, Integer length) {
            BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
            int end = length == null ? reader.len() : reader.getPos() + length;
            StreamerContext.StreamerContextGLDeviceInfo message = new StreamerContext.StreamerContextGLDeviceInfo();
            while (reader.getPos() < end) {
                int tag = Math.toIntExact(reader.uint32());
                switch (tag >>> 3) {
                    case 1:
                        if (tag == 10) {
                            message.glRenderer = reader.string();
                            continue;
                        }
                        break;
                    case 2:
                        if (tag == 16) {
                            message.glEsVersionMajor = reader.int32();
                            continue;
                        }
                        break;
                    case 3:
                        if (tag == 24) {
                            message.glEsVersionMinor = reader.int32();
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




