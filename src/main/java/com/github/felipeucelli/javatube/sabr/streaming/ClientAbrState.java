package com.github.felipeucelli.javatube.sabr.streaming;

import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryReader;
import com.github.felipeucelli.javatube.sabr.proto.Proto.BinaryWriter;

public class ClientAbrState {
    public Long timeSinceLastManualFormatSelectionMs;
    public Integer lastManualDirection;
    public Integer lastManualSelectedResolution;
    public Integer detailedNetworkType;
    public Integer clientViewportWidth;
    public Integer clientViewportHeight;
    public Long clientBitrateCapBytesPerSec;
    public Integer stickyResolution;
    public Boolean clientViewportIsFlexible;
    public Long bandwidthEstimate;
    public Integer minAudioQuality;
    public Integer maxAudioQuality;
    public Integer videoQualitySetting;
    public Integer audioRoute;
    public Long playerTimeMs;
    public Long timeSinceLastSeek;
    public Boolean dataSaverMode;
    public Integer networkMeteredState;
    public Integer visibility;
    public Float playbackRate;
    public Long elapsedWallTimeMs;
    public byte[] mediaCapabilities;
    public Long timeSinceLastActionMs;
    public Integer enabledTrackTypesBitfield;
    public Integer maxPacingRate;
    public Long playerState;
    public Boolean drcEnabled;
    public Integer Jda;
    public Integer qw;
    public Integer Ky;
    public Integer sabrReportRequestCancellationInfo;
    public Boolean l;
    public Long G7;
    public Boolean preferVp9;
    public Integer qj;
    public Integer Hx;
    public Boolean isPrefetch;
    public Integer sabrSupportQualityConstraints;
    public byte[] sabrLicenseConstraint;
    public Integer allowProximaLiveLatency;
    public Integer sabrForceProxima;
    public Integer Tqb;
    public Long sabrForceMaxNetworkInterruptionDurationMs;
    public String audioTrackId;

    public ClientAbrState() {
        this.timeSinceLastManualFormatSelectionMs = null;
        this.lastManualDirection = null;
        this.lastManualSelectedResolution = null;
        this.detailedNetworkType = null;
        this.clientViewportWidth = null;
        this.clientViewportHeight = null;
        this.clientBitrateCapBytesPerSec = null;
        this.stickyResolution = null;
        this.clientViewportIsFlexible = false;
        this.bandwidthEstimate = null;
        this.minAudioQuality = null;
        this.maxAudioQuality = null;
        this.videoQualitySetting = null;
        this.audioRoute = null;
        this.playerTimeMs = null;
        this.timeSinceLastSeek = null;
        this.dataSaverMode = false;
        this.networkMeteredState = null;
        this.visibility = null;
        this.playbackRate = null;
        this.elapsedWallTimeMs = null;
        this.mediaCapabilities = new byte[0];
        this.timeSinceLastActionMs = null;
        this.enabledTrackTypesBitfield = null;
        this.maxPacingRate = null;
        this.playerState = null;
        this.drcEnabled = false;
        this.Jda = null;
        this.qw = null;
        this.Ky = null;
        this.sabrReportRequestCancellationInfo = null;
        this.l = false;
        this.G7 = null;
        this.preferVp9 = false;
        this.qj = null;
        this.Hx = null;
        this.isPrefetch = false;
        this.sabrSupportQualityConstraints = null;
        this.sabrLicenseConstraint = new byte[0];
        this.allowProximaLiveLatency = null;
        this.sabrForceProxima = null;
        this.Tqb = null;
        this.sabrForceMaxNetworkInterruptionDurationMs = null;
        this.audioTrackId = null;
    }

    public static BinaryWriter encode(ClientAbrState message, BinaryWriter writer) {
        writer = writer != null ? writer : new BinaryWriter();
        if (message.timeSinceLastManualFormatSelectionMs != null) {
            writer.uint32(104).int64(message.timeSinceLastManualFormatSelectionMs);
        }
        if (message.lastManualDirection != null) {
            writer.uint32(112).sint32(message.lastManualDirection);
        }
        if (message.lastManualSelectedResolution != null) {
            writer.uint32(128);
            writer.int32(message.lastManualSelectedResolution);
        }
        if (message.detailedNetworkType != null) {
            writer.uint32(136).int32(message.detailedNetworkType);
        }
        if (message.clientViewportWidth != null) {
            writer.uint32(144).int32(message.clientViewportWidth);
        }
        if (message.clientViewportHeight != null) {
            writer.uint32(152).int32(message.clientViewportHeight);
        }
        if (message.clientBitrateCapBytesPerSec != null) {
            writer.uint32(160).int64(message.clientBitrateCapBytesPerSec);
        }
        if (message.stickyResolution != null) {
            writer.uint32(168).int32(message.stickyResolution);
        }
        if (message.clientViewportIsFlexible) {
            writer.uint32(176).bool(message.clientViewportIsFlexible);
        }
        if (message.bandwidthEstimate != null) {
            writer.uint32(184).int64(message.bandwidthEstimate);
        }
        if (message.minAudioQuality != null) {
            writer.uint32(192).int32(message.minAudioQuality);
        }
        if (message.maxAudioQuality != null) {
            writer.uint32(200).int32(message.maxAudioQuality);
        }
        if (message.videoQualitySetting != null) {
            writer.uint32(208).int32(message.videoQualitySetting);
        }
        if (message.audioRoute != null) {
            writer.uint32(216).int32(message.audioRoute);
        }
        if (message.playerTimeMs != null) {
            writer.uint32(224).int64(message.playerTimeMs);
        }
        if (message.timeSinceLastSeek != null) {
            writer.uint32(232).int64(message.timeSinceLastSeek);
        }
        if (message.dataSaverMode) {
            writer.uint32(240).bool(message.dataSaverMode);
        }
        if (message.networkMeteredState != null) {
            writer.uint32(256).int32(message.networkMeteredState);
        }
        if (message.visibility != null) {
            writer.uint32(272).int32(message.visibility);
        }
        if (message.playbackRate != null) {
            writer.uint32(285).writeFloat(message.playbackRate);;
        }
        if (message.elapsedWallTimeMs != null) {
            writer.uint32(288).int64(message.elapsedWallTimeMs);
        }
        if (message.mediaCapabilities != null && message.mediaCapabilities.length > 0) {
            writer.uint32(306).bytes(message.mediaCapabilities);
        }
        if (message.timeSinceLastActionMs != null) {
            writer.uint32(312).int64(message.timeSinceLastActionMs);
        }
        if (message.enabledTrackTypesBitfield != null) {
            writer.uint32(320).int32(message.enabledTrackTypesBitfield);
        }
        if (message.maxPacingRate != null) {
            writer.uint32(344).int32(message.maxPacingRate);
        }
        if (message.playerState != null) {
            writer.uint32(352).int64(message.playerState);
        }
        if (message.drcEnabled) {
            writer.uint32(368).bool(message.drcEnabled);
        }
        if (message.Jda != null) {
            writer.uint32(384).int32(message.Jda);
        }
        if (message.qw != null) {
            writer.uint32(400).int32(message.qw);
        }
        if (message.Ky != null) {
            writer.uint32(408).int32(message.Ky);
        }
        if (message.sabrReportRequestCancellationInfo != null) {
            writer.uint32(432).int32(message.sabrReportRequestCancellationInfo);
        }
        if (message.l) {
            writer.uint32(448).bool(message.l);
        }
        if (message.G7 != null) {
            writer.uint32(456).int64(message.G7);
        }
        if (message.preferVp9) {
            writer.uint32(464).bool(message.preferVp9);
        }
        if (message.qj != null) {
            writer.uint32(472).int32(message.qj);
        }
        if (message.Hx != null) {
            writer.uint32(480).int32(message.Hx);
        }
        if (message.isPrefetch) {
            writer.uint32(488).bool(message.isPrefetch);
        }
        if (message.sabrSupportQualityConstraints != null) {
            writer.uint32(496).int32(message.sabrSupportQualityConstraints);
        }
        if (message.sabrLicenseConstraint != null && message.sabrLicenseConstraint.length > 0) {
            writer.uint32(506).bytes(message.sabrLicenseConstraint);
        }
        if (message.allowProximaLiveLatency != null) {
            writer.uint32(512).int32(message.allowProximaLiveLatency);
        }
        if (message.sabrForceProxima != null) {
            writer.uint32(528).int32(message.sabrForceProxima);
        }
        if (message.Tqb != null) {
            writer.uint32(536).int32(message.Tqb);
        }
        if (message.sabrForceMaxNetworkInterruptionDurationMs != null) {
            writer.uint32(544).int64(message.sabrForceMaxNetworkInterruptionDurationMs);
        }
        if (message.audioTrackId != null && !message.audioTrackId.isEmpty()) {
            writer.uint32(554).string(message.audioTrackId);
        }
        return writer;
    }

    public static ClientAbrState decode(Object inputData, Integer length) {
        BinaryReader reader = inputData instanceof BinaryReader ? (BinaryReader) inputData : new BinaryReader((byte[]) inputData);
        int end = length == null ? reader.len() : reader.getPos() + length;
        ClientAbrState message = new ClientAbrState();
        while (reader.getPos() < end) {
            int tag = (int) reader.uint32();
            switch (tag >>> 3) {
                case 13:
                    if (tag == 104) {
                        message.timeSinceLastManualFormatSelectionMs = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 14:
                    if (tag == 112) {
                        message.lastManualDirection = reader.sint32();
                        continue;
                    }
                    break;
                case 16:
                    if (tag == 128) {
                        message.lastManualSelectedResolution = reader.int32();
                        continue;
                    }
                    break;
                case 17:
                    if (tag == 136) {
                        message.detailedNetworkType = reader.int32();
                        continue;
                    }
                    break;
                case 18:
                    if (tag == 144) {
                        message.clientViewportWidth = reader.int32();
                        continue;
                    }
                    break;
                case 19:
                    if (tag == 152) {
                        message.clientViewportHeight = reader.int32();
                        continue;
                    }
                    break;
                case 20:
                    if (tag == 160) {
                        message.clientBitrateCapBytesPerSec = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 21:
                    if (tag == 168) {
                        message.stickyResolution = reader.int32();
                        continue;
                    }
                    break;
                case 22:
                    if (tag == 176) {
                        message.clientViewportIsFlexible = reader.bool();
                        continue;
                    }
                    break;
                case 23:
                    if (tag == 184) {
                        message.bandwidthEstimate = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 24:
                    if (tag == 192) {
                        message.minAudioQuality = reader.int32();
                        continue;
                    }
                    break;
                case 25:
                    if (tag == 200) {
                        message.maxAudioQuality = reader.int32();
                        continue;
                    }
                    break;
                case 26:
                    if (tag == 208) {
                        message.videoQualitySetting = reader.int32();
                        continue;
                    }
                    break;
                case 27:
                    if (tag == 216) {
                        message.audioRoute = reader.int32();
                        continue;
                    }
                    break;
                case 28:
                    if (tag == 224) {
                        message.playerTimeMs = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 29:
                    if (tag == 232) {
                        message.timeSinceLastSeek = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 30:
                    if (tag == 240) {
                        message.dataSaverMode = reader.bool();
                        continue;
                    }
                    break;
                case 32:
                    if (tag == 256) {
                        message.networkMeteredState = reader.int32();
                        continue;
                    }
                    break;
                case 34:
                    if (tag == 272) {
                        message.visibility = reader.int32();
                        continue;
                    }
                    break;
                case 35:
                    if (tag == 285) {
                        message.playbackRate = reader.readFloat();
                        continue;
                    }
                    break;
                case 36:
                    if (tag == 288) {
                        message.elapsedWallTimeMs = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 38:
                    if (tag == 306) {
                        message.mediaCapabilities = reader.bytes();
                        continue;
                    }
                    break;
                case 39:
                    if (tag == 312) {
                        message.timeSinceLastActionMs = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 40:
                    if (tag == 320) {
                        message.enabledTrackTypesBitfield = reader.int32();
                        continue;
                    }
                    break;
                case 43:
                    if (tag == 344) {
                        message.maxPacingRate = reader.int32();
                        continue;
                    }
                    break;
                case 44:
                    if (tag == 352) {
                        message.playerState = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 46:
                    if (tag == 368) {
                        message.drcEnabled = reader.bool();
                        continue;
                    }
                    break;
                case 48:
                    if (tag == 384) {
                        message.Jda = reader.int32();
                        continue;
                    }
                    break;
                case 50:
                    if (tag == 400) {
                        message.qw = reader.int32();
                        continue;
                    }
                    break;
                case 51:
                    if (tag == 408) {
                        message.Ky = reader.int32();
                        continue;
                    }
                    break;
                case 54:
                    if (tag == 432) {
                        message.sabrReportRequestCancellationInfo = reader.int32();
                        continue;
                    }
                    break;
                case 56:
                    if (tag == 448) {
                        message.l = reader.bool();
                        continue;
                    }
                    break;
                case 57:
                    if (tag == 456) {
                        message.G7 = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 58:
                    if (tag == 464) {
                        message.preferVp9 = reader.bool();
                        continue;
                    }
                    break;
                case 59:
                    if (tag == 472) {
                        message.qj = reader.int32();
                        continue;
                    }
                    break;
                case 60:
                    if (tag == 480) {
                        message.Hx = reader.int32();
                        continue;
                    }
                    break;
                case 61:
                    if (tag == 488) {
                        message.isPrefetch = reader.bool();
                        continue;
                    }
                    break;
                case 62:
                    if (tag == 496) {
                        message.sabrSupportQualityConstraints = reader.int32();
                        continue;
                    }
                    break;
                case 63:
                    if (tag == 506) {
                        message.sabrLicenseConstraint = reader.bytes();
                        continue;
                    }
                    break;
                case 64:
                    if (tag == 512) {
                        message.allowProximaLiveLatency = reader.int32();
                        continue;
                    }
                    break;
                case 66:
                    if (tag == 528) {
                        message.sabrForceProxima = reader.int32();
                        continue;
                    }
                    break;
                case 67:
                    if (tag == 536) {
                        message.Tqb = reader.int32();
                        continue;
                    }
                    break;
                case 68:
                    if (tag == 544) {
                        message.sabrForceMaxNetworkInterruptionDurationMs = BufferedRange.longToNumber(reader.int64());
                        continue;
                    }
                    break;
                case 69:
                    if (tag == 554) {
                        message.audioTrackId = reader.string();
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


