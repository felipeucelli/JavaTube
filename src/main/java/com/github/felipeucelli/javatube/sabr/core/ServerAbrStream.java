package com.github.felipeucelli.javatube.sabr.core;

import com.github.felipeucelli.javatube.Stream;
import com.github.felipeucelli.javatube.Youtube;
import com.github.felipeucelli.javatube.sabr.streaming.*;
import com.github.felipeucelli.javatube.exceptions.SABRError;
import com.github.felipeucelli.javatube.sabr.common.FormatId;

import java.util.*;
import java.net.URL;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.ByteArrayOutputStream;
import java.util.function.BiConsumer;


enum PART {
    ONESIE_HEADER(10),
    ONESIE_DATA(11),
    MEDIA_HEADER(20),
    MEDIA(21),
    MEDIA_END(22),
    LIVE_METADATA(31),
    HOSTNAME_CHANGE_HINT(32),
    LIVE_METADATA_PROMISE(33),
    LIVE_METADATA_PROMISE_CANCELLATION(34),
    NEXT_REQUEST_POLICY(35),
    USTREAMER_VIDEO_AND_FORMAT_DATA(36),
    FORMAT_SELECTION_CONFIG(37),
    USTREAMER_SELECTED_MEDIA_STREAM(38),
    FORMAT_INITIALIZATION_METADATA(42),
    SABR_REDIRECT(43),
    SABR_ERROR(44),
    SABR_SEEK(45),
    RELOAD_PLAYER_RESPONSE(46),
    PLAYBACK_START_POLICY(47),
    ALLOWED_CACHED_FORMATS(48),
    START_BW_SAMPLING_HINT(49),
    PAUSE_BW_SAMPLING_HINT(50),
    SELECTABLE_FORMATS(51),
    REQUEST_IDENTIFIER(52),
    REQUEST_CANCELLATION_POLICY(53),
    ONESIE_PREFETCH_REJECTION(54),
    TIMELINE_CONTEXT(55),
    REQUEST_PIPELINING(56),
    SABR_CONTEXT_UPDATE(57),
    STREAM_PROTECTION_STATUS(58),
    SABR_CONTEXT_SENDING_POLICY(59),
    LAWNMOWER_POLICY(60),
    SABR_ACK(61),
    END_OF_TRACK(62),
    CACHE_LOAD_POLICY(63),
    LAWNMOWER_MESSAGING_POLICY(64),
    PREWARM_CONNECTION(65),
    PLAYBACK_DEBUG_INFO(66),
    SNACKBAR_MESSAGE(67);

    private final int value;

    PART(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class ServerAbrStream {
    private final Stream stream;
    private final BiConsumer<byte[], Long> writeChunk;
    private final Youtube youtube;
    private final String poToken;
    private String serverAbrStreamingUrl;
    private String videoPlaybackUstreamerConfig;
    private final int totalDurationMs;
    private Long bytesReceived;
    private final List<Map<String, Object>> initializedFormats;
    private final Map<String, Map<String, Object>> formatsByKey;
    private PlaybackCookie playbackCookie;
    private final Map<Integer, String> headerIdToFormatKeyMap;
    private final Map<String, List<Integer>> previousSequences;
    private boolean RELOAD;
    private int maximumReloadAttempt;


    public ServerAbrStream(Stream stream, BiConsumer<byte[], Long> writeChunk, Youtube youtube) {
        this.stream = stream;
        this.writeChunk = writeChunk;
        this.youtube = youtube;
        this.poToken = stream.getPoToken();
        this.serverAbrStreamingUrl = stream.getUrl();
        this.videoPlaybackUstreamerConfig = stream.getVideoPlaybackUstreamerConfig();
        this.totalDurationMs = stream.getDurationMs();
        this.bytesReceived = 0L;
        this.initializedFormats = new ArrayList<>();
        this.formatsByKey = new HashMap<>();
        this.playbackCookie = null;
        this.headerIdToFormatKeyMap = new HashMap<>();
        this.previousSequences = new HashMap<>();
        this.RELOAD = false;
        this.maximumReloadAttempt = 3;
    }

    @SuppressWarnings("unchecked")
    public void emit(Map<String, Object> data) {
        List<Map<String, Object>> initializedFormats = (List<Map<String, Object>>) data.get("initialized_formats");
        if (initializedFormats == null) return;

        for (Map<String, Object> formatId : initializedFormats) {
            FormatId formatIdMap = (FormatId) formatId.get("formatId");
            if (formatIdMap != null && formatIdMap.itag == stream.getItag()) {
                List<byte[]> mediaChunks = (List<byte[]>) formatId.get("mediaChunks");
                if (mediaChunks == null) continue;
                for (byte[] chunk : mediaChunks) {
                    bytesReceived += chunk.length;
                    this.writeChunk.accept(chunk, bytesReceived);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void start() throws Exception {
        List<Map<String, Object>> audioFormat = new ArrayList<>();
        List<Map<String, Object>> videoFormat = new ArrayList<>();

        String streamType = stream.getType();
        int itag = stream.getItag();
        long lastModified = stream.getLastModified();
        String xtags = stream.getXtags();

        Map<String, Object> formatInfo = new HashMap<>();
        formatInfo.put("itag", itag);
        formatInfo.put("lastModified", lastModified);
        formatInfo.put("xtags", xtags);

        if ("audio".equals(streamType)) {
            audioFormat.add(formatInfo);
        } else if ("video".equals(streamType)) {
            videoFormat.add(formatInfo);
        }

        Map<String, Object> clientAbrState = new HashMap<>();
        clientAbrState.put("lastManualDirection", 0);
        clientAbrState.put("timeSinceLastManualFormatSelectionMs", 0L);
        String resolution = stream.getResolution();
        int parsedResolution = videoFormat.isEmpty() ? 720 : Integer.parseInt(resolution.replace("p", ""));
        clientAbrState.put("lastManualSelectedResolution", parsedResolution);
        clientAbrState.put("stickyResolution", parsedResolution);
        clientAbrState.put("playerTimeMs", 0L);
        clientAbrState.put("visibility", 0);
        clientAbrState.put("drcEnabled", stream.getIsDrc());
        clientAbrState.put("enabledTrackTypesBitfield", videoFormat.isEmpty() ? 1 : 0);

        while (((Long) clientAbrState.get("playerTimeMs")) < totalDurationMs && maximumReloadAttempt > 0) {
            Map<String, Object> data = fetchMedia(clientAbrState, audioFormat, videoFormat);
            SabrError sabrError = (SabrError) data.get("sabr_error");
            if (sabrError != null) {
                reload();
            }
            emit(data);

            Map<String, Object> mainFormat = null;
            if (((Integer) clientAbrState.get("enabledTrackTypesBitfield")) == 0) {
                for (Map<String, Object> fmt : initializedFormats) {
                    String mimeType = (String) fmt.get("mimeType");
                    if (mimeType != null && mimeType.contains("video")) {
                        mainFormat = fmt;
                        break;
                    }
                }
            } else {
                if (!initializedFormats.isEmpty()) {
                    mainFormat = initializedFormats.get(0);
                }
            }

            for (Map<String, Object> fmt : initializedFormats) {
                String formatKey = (String) fmt.get("formatKey");
                List<Map<String, Object>> sequenceList = (List<Map<String, Object>>) fmt.get("sequenceList");
                List<Integer> sequenceNumbers = new ArrayList<>();
                if (sequenceList != null) {
                    for (Map<String, Object> seq : sequenceList) {
                        sequenceNumbers.add(((Number) seq.getOrDefault("sequenceNumber", 0)).intValue());
                    }
                }
                previousSequences.put(formatKey, sequenceNumbers);
            }

            if (!RELOAD && (mainFormat == null || (mainFormat.containsKey("sequenceList") && ((List) mainFormat.get("sequenceList")).isEmpty()))) {
                reload();
            }

            if (maximumReloadAttempt > 0 && RELOAD) {
                RELOAD = false;
                continue;
            } else if (maximumReloadAttempt <= 0) {
                throw new SABRError("Maximum reload attempts reached");
            }

            if (mainFormat == null || ((Number) mainFormat.get("sequenceCount")).intValue() == ((Number) ((List<Map<String, Object>>) mainFormat.get("sequenceList")).get(((List) mainFormat.get("sequenceList")).size() - 1).get("sequenceNumber")).intValue()) {
                break;
            }

            long totalSequenceDuration = 0;
            List<Map<String, Object>> mainFormatSequenceList = (List<Map<String, Object>>) mainFormat.get("sequenceList");
            if (mainFormatSequenceList != null) {
                for (Map<String, Object> seq : mainFormatSequenceList) {
                    totalSequenceDuration += ((Number) seq.getOrDefault("durationMs", 0L)).longValue();
                }
            }
            clientAbrState.put("playerTimeMs", ((Long) clientAbrState.get("playerTimeMs")) + totalSequenceDuration);
        }
    }

    private void reload() throws Exception {
        this.RELOAD = true;
        this.maximumReloadAttempt--;

        youtube.setVidInfo(null);

        String refreshUrl = youtube.getServerAbrStreamingUrl();
        if (refreshUrl.isEmpty()){
            throw new RuntimeException("Invalid SABR refresh");
        }
        serverAbrStreamingUrl = refreshUrl;
        videoPlaybackUstreamerConfig = youtube.getVideoPlaybackUstreamerConfig();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchMedia(Map<String, Object> clientAbrState, List<Map<String, Object>> selectedAudioFormatIds, List<Map<String, Object>> selectedVideoFormatIds) throws IOException {
        VideoPlaybackAbrRequest requestBody = new VideoPlaybackAbrRequest();
        requestBody.clientAbrState = new ClientAbrState();

        ClientAbrState clientAbrStateObj = new ClientAbrState();
            clientAbrStateObj.lastManualDirection = (Integer) clientAbrState.get("lastManualDirection");
            clientAbrStateObj.timeSinceLastManualFormatSelectionMs = (Long) clientAbrState.get("timeSinceLastManualFormatSelectionMs");
            clientAbrStateObj.lastManualSelectedResolution = (Integer) clientAbrState.get("lastManualSelectedResolution");
            clientAbrStateObj.stickyResolution = (Integer) clientAbrState.get("stickyResolution");
            clientAbrStateObj.playerTimeMs = (Long) clientAbrState.get("playerTimeMs");
            clientAbrStateObj.visibility = (Integer) clientAbrState.get("visibility");
            clientAbrStateObj.drcEnabled = (Boolean) clientAbrState.get("drcEnabled");
            clientAbrStateObj.enabledTrackTypesBitfield = (Integer) clientAbrState.get("enabledTrackTypesBitfield");
        requestBody.clientAbrState = clientAbrStateObj;

        requestBody.selectedAudioFormatIds = new ArrayList<>();
        for (Map<String, Object> audioFmt : selectedAudioFormatIds) {
            FormatId formatId = new FormatId();
            formatId.itag = (Integer) audioFmt.get("itag");
            formatId.lastModified = (Long) audioFmt.get("lastModified");
            formatId.xtags = (String) audioFmt.get("xtags");
            requestBody.selectedAudioFormatIds.add(formatId);
        }

        requestBody.selectedVideoFormatIds = new ArrayList<>();
        for (Map<String, Object> videoFmt : selectedVideoFormatIds) {
            FormatId formatId = new FormatId();
            formatId.itag = (Integer) videoFmt.get("itag");
            formatId.lastModified = (Long) videoFmt.get("lastModified");
            formatId.xtags = (String) videoFmt.get("xtags");
            requestBody.selectedVideoFormatIds.add(formatId);
        }

        requestBody.selectedFormatIds = new ArrayList<>();
        for (Map<String, Object> fmt : initializedFormats) {
            FormatId formatId = new FormatId();
            formatId.itag = ((FormatId) fmt.get("formatId")).itag;
            formatId.lastModified = ((FormatId) fmt.get("formatId")).lastModified;
            formatId.xtags = ((FormatId) fmt.get("formatId")).xtags;
            requestBody.selectedFormatIds.add(formatId);
        }

        requestBody.videoPlaybackUstreamerConfig = base64ToU8(videoPlaybackUstreamerConfig);

        StreamerContext streamerContext = new StreamerContext();
            streamerContext.field5 = new ArrayList<>();
            streamerContext.field6 = new ArrayList<>();
            streamerContext.poToken = poToken != null ? base64ToU8(poToken) : null;
            streamerContext.playbackCookie = playbackCookie;
            StreamerContext.StreamerContextClientInfo clientInfo = new StreamerContext.StreamerContextClientInfo();
                clientInfo.clientName = 1;
                clientInfo.clientVersion = "2.20250523.01.00";
                clientInfo.osName = "Windows";
                clientInfo.osVersion = "10.0";
                clientInfo.platform = "DESKTOP";
            streamerContext.clientInfo = clientInfo;
        requestBody.streamerContext = streamerContext;

        requestBody.bufferedRanges = new ArrayList<>();
        for (Map<String, Object> fmt : initializedFormats) {
            BufferedRange buf = new BufferedRange();
            buf.formatId = (FormatId) ((Map<String, Object>) fmt.get("_state")).get("formatId");
            buf.startTimeMs = (long) ((Map<String, Object>) fmt.get("_state")).get("startTimeMs");
            buf.durationMs = (long) ((Map<String, Object>) fmt.get("_state")).get("durationMs");
            buf.startSegmentIndex = (long) ((Map<String, Object>) fmt.get("_state")).get("startSegmentIndex");
            buf.endSegmentIndex = (long) ((Map<String, Object>) fmt.get("_state")).get("endSegmentIndex");

            requestBody.bufferedRanges.add(buf);
        }
        requestBody.field1000 = new ArrayList<>();

        byte[] body = VideoPlaybackAbrRequest.encode(requestBody, null).finish();

        URL url = new URL(serverAbrStreamingUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept-Language", "en-US,en");
        connection.setRequestProperty("Content-Type", "application/vnd.yt-ump");
        connection.setDoOutput(true);

        connection.getOutputStream().write(body);

        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = connection.getInputStream().read(buffer)) != -1) {
            responseStream.write(buffer, 0, bytesRead);
        }
        byte[] responseBytes = responseStream.toByteArray();

        return parseUmpResponse(responseBytes);
    }

    private Map<String, Object> parseUmpResponse(byte[] response) {
        headerIdToFormatKeyMap.clear();
        for (Map<String, Object> fmt : initializedFormats) {
            fmt.put("sequenceList", new ArrayList<>());
            fmt.put("mediaChunks", new ArrayList<>());
        }

        final SabrError[] sabrError = {null};
        final SabrRedirect[] sabrRedirect = {null};
        final StreamProtectionStatus[] streamProtectionStatus = {null};

        UMP ump = new UMP(new ChunkedDataBuffer(Collections.singletonList(response)));

        ump.parse(part -> {
            List<byte[]> data = new ArrayList<>();
            if (part.get("data") instanceof ChunkedDataBuffer cdb) {
                if (cdb.chunks != null && !cdb.chunks.isEmpty()) {
                    data.add(cdb.chunks.get(0));
                }
            }

            long partType = (long) part.get("type");
            if (partType == PART.MEDIA_HEADER.getValue()) {
                processMediaHeader(data);

            } else if (partType == PART.MEDIA.getValue()) {
                processMediaData((ChunkedDataBuffer) part.get("data"));

            } else if (partType == PART.MEDIA_END.getValue()) {
                processEndOfMedia((ChunkedDataBuffer) part.get("data"));

            } else if (partType == PART.NEXT_REQUEST_POLICY.getValue()) {
                processNextRequestPolicy(data);

            } else if (partType == PART.FORMAT_INITIALIZATION_METADATA.getValue()) {
                processFormatInitialization(data);

            } else if (partType == PART.SABR_ERROR.getValue()) {
                sabrError[0] = SabrError.decode(data.get(0));

            } else if (partType == PART.SABR_REDIRECT.getValue()) {
                sabrRedirect[0] = processSabrRedirect(data.get(0));

            } else if (partType == PART.STREAM_PROTECTION_STATUS.getValue()) {
                streamProtectionStatus[0] = StreamProtectionStatus.decode(data.get(0));

            } else if (partType == PART.RELOAD_PLAYER_RESPONSE.getValue()) {
                RELOAD = true;

            }
        });

        Map<String, Object> result = new HashMap<>();
        result.put("initialized_formats", initializedFormats);
        result.put("sabr_error", sabrError[0]);
        result.put("sabr_redirect", sabrRedirect[0]);
        result.put("stream_protection_status", streamProtectionStatus[0]);
        return result;
    }

    @SuppressWarnings("unchecked")
    private void processMediaHeader(List<byte[]> data) {
        MediaHeader mediaHeader = MediaHeader.decode(data.get(0));

        if (mediaHeader.formatId == null){
            return;
        }

        String formatKey = getFormatKey(mediaHeader.formatId);
        Map<String, Object> currentFormat = formatsByKey.get(formatKey);

        if (currentFormat == null){
            return;
        }

        Integer sequenceNumber = mediaHeader.sequenceNumber;
        if (sequenceNumber != null){
            if(previousSequences.containsKey(formatKey)){
                if (previousSequences.get(formatKey).contains(sequenceNumber)){
                    return;
                }
            }
        }

        Integer headerId = mediaHeader.headerId;
        if(headerId != null){
            if(!headerIdToFormatKeyMap.containsKey(headerId)){
                headerIdToFormatKeyMap.put(headerId, formatKey);
            }
        }

        boolean exists = false;
        int mediaSeq = (mediaHeader.sequenceNumber != null) ? mediaHeader.sequenceNumber : 0;
        for (Map<String, Object> seq : (ArrayList<Map<String, Object>>) currentFormat.get("sequenceList")) {
            Object seqNumObj = seq.get("sequenceNumber");
            int seqNum = (seqNumObj instanceof Number) ? ((Number) seqNumObj).intValue() : 0;

            if (seqNum == mediaSeq) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            ((ArrayList<Map<String, Object>>) currentFormat.get("sequenceList")).add(new HashMap<>(){{
                put("itag", mediaHeader.itag);
                put("formatId", mediaHeader.formatId);
                put("isInitSegment", mediaHeader.isInitSeg);
                put("durationMs", mediaHeader.durationMs);
                put("startMs", mediaHeader.startMs);
                put("startDataRange", mediaHeader.startRange);
                put("sequenceNumber", mediaHeader.sequenceNumber);
                put("contentLength", mediaHeader.contentLength);
                put("timeRange", mediaHeader.timeRange);
            }});

            Long durationMs = (Long) ((Map<String, Object>) currentFormat.get("_state")).get("durationMs");
            durationMs += mediaHeader.durationMs;

            ((Map<String, Object>) currentFormat.get("_state")).put("durationMs", durationMs);

            Long endSegmentIndex = (Long) ((Map<String, Object>) currentFormat.get("_state")).get("endSegmentIndex");
            endSegmentIndex += 1;

            ((Map<String, Object>) currentFormat.get("_state")).put("endSegmentIndex", endSegmentIndex);
        }
    }

    @SuppressWarnings("unchecked")
    private void processMediaData(ChunkedDataBuffer data) {
        int headerId = data.getUint8(0);
        ChunkedDataBuffer streamData = (ChunkedDataBuffer) data.split(1)[1];
        String formatKey = headerIdToFormatKeyMap.get(headerId);
        if (formatKey == null){
            return;
        }

        Map<String, Object> currentFormat = formatsByKey.get(formatKey);
        if (currentFormat == null){
            return;
        }

        ((List<byte[]>) currentFormat.get("mediaChunks")).add(streamData.chunks.get(0));
    }

    private void processEndOfMedia(ChunkedDataBuffer data) {
        int headerId = data.getUint8(0);
        headerIdToFormatKeyMap.remove(headerId);
    }

    private void processNextRequestPolicy(List<byte[]> data) {
        NextRequestPolicy policy = NextRequestPolicy.decode(data.get(0));
        playbackCookie = policy.playbackCookie;
    }

    private void processFormatInitialization(List<byte[]> data) {
        FormatInitializationMetadata metadata = FormatInitializationMetadata.decode(data.get(0));
        registerFormat(metadata);
    }

    private SabrRedirect processSabrRedirect(byte[] data) {
        SabrRedirect sabrRedirect = SabrRedirect.decode(data);
        if (sabrRedirect.url.isEmpty()){
            throw new RuntimeException("Invalid SABR redirect");
        }
        serverAbrStreamingUrl = sabrRedirect.url;
        return sabrRedirect;
    }

    private String getFormatKey(FormatId format_id){
        return format_id.itag + ";" + format_id.lastModified + ";";
    }

    private void registerFormat(FormatInitializationMetadata data){
        String formatKey = getFormatKey(data.formatId);

        if (!formatsByKey.containsKey(formatKey)) {
            Map<String, Object> newFormat = new HashMap<>();
            newFormat.put("formatId", data.formatId);
            newFormat.put("formatKey", formatKey);
            newFormat.put("durationMs", data.durationMs);
            newFormat.put("mimeType", data.mimeType);
            newFormat.put("sequenceCount", data.endSegmentNumber);
            newFormat.put("sequenceList", new ArrayList<HashMap<String, Object>>());
            newFormat.put("mediaChunks", new ArrayList<>());

            Map<String, Object> state = new HashMap<>();
            state.put("formatId", data.formatId);
            state.put("startTimeMs", 0L);
            state.put("durationMs", 0L);
            state.put("startSegmentIndex", 1L);
            state.put("endSegmentIndex", 0L);

            newFormat.put("_state", state);
            formatsByKey.put(formatKey, newFormat);
            initializedFormats.add(newFormat);
        }
    }

    public static byte[] base64ToU8(String base64Str) {
        String standardBase64 = base64Str.replace('-', '+').replace('_', '/');
        int paddingLength = (4 - (standardBase64.length() % 4)) % 4;
        StringBuilder paddedBase64 = new StringBuilder(standardBase64);
        for (int i = 0; i < paddingLength; i++) {
            paddedBase64.append("=");
        }
        return Base64.getDecoder().decode(paddedBase64.toString());
    }
}
