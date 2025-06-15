package com.github.felipeucelli.javatube;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Predicate;

public class StreamQuery{
    private final List<Stream> fmtStreams;
    Map<Integer, Stream> itagIndex = new HashMap<>();
    
    public StreamQuery(List<Stream> fmt_streams){
        fmtStreams = fmt_streams;
        for (Stream fmt_stream : fmt_streams) {
            itagIndex.put(fmt_stream.getItag(), fmt_stream);
        }
    }
    @Override
    public String toString(){
        try {
            return fmtStreams.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Stream> getAll(){
        return fmtStreams;
    }

    public Stream get(int index){
        return fmtStreams.get(index);
    }

    public static class Filter {
        public final String res;
        public final Integer fps;
        public final String mimeType;
        public final String type;
        public final String subtype;
        public final String fileExtension;
        public final String abr;
        public final String bitrate;
        public final String videoCodec;
        public final String audioCodec;
        public final Boolean onlyAudio;
        public final Boolean onlyVideo;
        public final Boolean progressive;
        public final Boolean adaptive;
        public final Boolean sabr;
        public final String audioTrackName;
        public final List<Predicate<Stream>> customFilters;

        private Filter(Builder builder) {
            this.res = builder.res;
            this.fps = builder.fps;
            this.mimeType = builder.mimeType;
            this.type = builder.type;
            this.subtype = builder.subtype;
            this.fileExtension = builder.fileExtension;
            this.abr = builder.abr;
            this.bitrate = builder.bitrate;
            this.videoCodec = builder.videoCodec;
            this.audioCodec = builder.audioCodec;
            this.onlyAudio = builder.onlyAudio;
            this.onlyVideo = builder.onlyVideo;
            this.progressive = builder.progressive;
            this.adaptive = builder.adaptive;
            this.sabr = builder.sabr;
            this.audioTrackName = builder.audioTrackName;
            this.customFilters = Collections.unmodifiableList(new ArrayList<>(builder.customFilters));
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String res;
            private Integer fps;
            private String mimeType;
            private String type;
            private String subtype;
            private String fileExtension;
            private String abr;
            private String bitrate;
            private String videoCodec;
            private String audioCodec;
            private Boolean onlyAudio;
            private Boolean onlyVideo;
            private Boolean progressive;
            private Boolean adaptive;
            private Boolean sabr;
            private String audioTrackName;
            private final List<Predicate<Stream>> customFilters = new ArrayList<>();

            private Builder() {}

            // Fluent setters
            public Builder res(String res) { this.res = res; return this; }
            public Builder fps(Integer fps) { this.fps = fps; return this; }
            public Builder mimeType(String mimeType) { this.mimeType = mimeType; return this; }
            public Builder type(String type) { this.type = type; return this; }
            public Builder subtype(String subtype) { this.subtype = subtype; return this; }
            public Builder fileExtension(String fileExtension) { this.fileExtension = fileExtension; return this; }
            public Builder abr(String abr) { this.abr = abr; return this; }
            public Builder bitrate(String bitrate) { this.bitrate = bitrate; return this; }
            public Builder videoCodec(String videoCodec) { this.videoCodec = videoCodec; return this; }
            public Builder audioCodec(String audioCodec) { this.audioCodec = audioCodec; return this; }
            public Builder onlyAudio(Boolean onlyAudio) { this.onlyAudio = onlyAudio; return this; }
            public Builder onlyVideo(Boolean onlyVideo) { this.onlyVideo = onlyVideo; return this; }
            public Builder progressive(Boolean progressive) { this.progressive = progressive; return this; }
            public Builder adaptive(Boolean adaptive) { this.adaptive = adaptive; return this; }
            public Builder sabr(Boolean isSabr) { this.sabr = sabr; return this; }
            public Builder audioTrackName(String audioTrackName) { this.audioTrackName = audioTrackName; return this; }

            // Filter additions
            public Builder addCustomFilter(Predicate<Stream> predicate) {
                if (predicate != null) this.customFilters.add(predicate);
                return this;
            }

            public Builder addCustomFilter(List<Predicate<Stream>> predicates) {
                if (predicates != null) this.customFilters.addAll(predicates);
                return this;
            }

            public Filter build() {
                return new Filter(this);
            }
        }
    }

    public StreamQuery filter(Filter f) {
        List<Predicate<Stream>> filters = new ArrayList<>();

        if (f.res != null) filters.add(s -> s.getResolution().equals(f.res));
        if (f.fps != null) filters.add(s -> Objects.equals(s.getFps(), f.fps));
        if (f.mimeType != null) filters.add(s -> f.mimeType.equals(s.getMimeType()));
        if (f.type != null) filters.add(s -> f.type.equals(s.getType()));

        String subtypeOrExt = f.subtype != null ? f.subtype : f.fileExtension;
        if (subtypeOrExt != null) filters.add(s -> subtypeOrExt.equals(s.getSubType()));

        String abrOrBitrate = f.abr != null ? f.abr : f.bitrate;
        if (abrOrBitrate != null) filters.add(s -> abrOrBitrate.equals(s.getAbr()));

        if (f.videoCodec != null) filters.add(s -> f.videoCodec.equals(s.getVideoCodec()));
        if (f.audioCodec != null) filters.add(s -> f.audioCodec.equals(s.getAudioCodec()));

        if (Boolean.TRUE.equals(f.onlyAudio)) filters.add(s -> s.includeAudioTrack() && !s.includeVideoTrack());

        if (Boolean.TRUE.equals(f.onlyVideo)) filters.add(s -> s.includeVideoTrack() && !s.includeAudioTrack());

        if (Boolean.TRUE.equals(f.progressive) || Boolean.FALSE.equals(f.adaptive)) filters.add(Stream::isProgressive);
        if (Boolean.TRUE.equals(f.adaptive) || Boolean.FALSE.equals(f.progressive)) filters.add(Stream::isAdaptive);

        if (Boolean.TRUE.equals(f.sabr)) filters.add(Stream::getIsSabr);
        if (Boolean.FALSE.equals(f.sabr)) filters.add(s -> !s.getIsSabr());

        if (f.audioTrackName != null) filters.add(s -> f.audioTrackName.equals(s.getAudioTrackName()));
        if (f.customFilters != null) filters.addAll(f.customFilters);

        return _filter(filters);
    }

    private StreamQuery _filter(List<Predicate<Stream>> filters) {
        List<Stream> result = fmtStreams;
        for (Predicate<Stream> f : filters) {
            result = result.stream().filter(f).collect(Collectors.toList());
        }
        return new StreamQuery(result);
    }

    public Stream getByItag(int itag) {
        return itagIndex.get(itag);
    }

    private ArrayList<Stream> progressive(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(st.isProgressive()){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> adaptive(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(st.isAdaptive()){
                filter.add(st);
            }
        }
        return filter;
    }

    private List<Stream> reverseArrayList(List<Stream> aList) {
        ArrayList<Stream> revArrayList = new ArrayList<>();
        for (int i = aList.size() - 1; i >= 0; i--) {
            revArrayList.add(aList.get(i));
        }
        return revArrayList;
    }

    private static ArrayList<Stream> sortByValue(HashMap<Stream, Integer> hm) {
        List<Map.Entry<Stream, Integer>> list = new LinkedList<>(hm.entrySet());
        list.sort(Map.Entry.comparingByValue());
        ArrayList<Stream> ordered = new ArrayList<>();
        for (Map.Entry<Stream, Integer> aa : list) {
            ordered.add(aa.getKey());
        }
        return ordered;
    }

    public StreamQuery orderBy(String by) throws Exception {
        HashMap<Stream, Integer> map = new HashMap<>();
        for(Stream s : fmtStreams){
            if(Objects.equals(by, "res")){
                if(s.getResolution() != null){
                    map.put(s, Integer.parseInt(s.getResolution().replace("p", "")));
                }
            } else if (Objects.equals(by, "abr")) {
                if(s.getAbr() != null){
                    map.put(s, Integer.parseInt(s.getAbr().replace("kbps", "")));
                }
            } else if (Objects.equals(by, "fps")) {
                if(s.getFps() != null){
                    map.put(s, s.getFps());
                }
            }else{
                throw new Exception("InvalidParameter");
            }
        }
        return new StreamQuery(sortByValue(map));
    }

    public StreamQuery getOtf(Boolean otf){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream s : fmtStreams){
            if(otf){
                if(s.getIsOtf()){
                    filter.add(s);
                }
            }else {
                if(!s.getIsOtf()){
                    filter.add(s);
                }
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getDesc(){
        return new StreamQuery(reverseArrayList(fmtStreams));
    }

    public StreamQuery getAsc(){
        return new StreamQuery(fmtStreams);
    }

    public Stream getFirst(){
        return fmtStreams.get(0);
    }

    public Stream getLast(){
        return fmtStreams.get(fmtStreams.size() - 1);
    }

    public StreamQuery getProgressive(){
        return new StreamQuery(progressive());
    }

    public StreamQuery getAdaptive(){
        return new StreamQuery(adaptive());
    }

    public StreamQuery getDefaultAudioTracks(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(st.isDefaultAudioTrack() ){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getExtraAudioTracks(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(!st.isDefaultAudioTrack() && st.includeAudioTrack() && !st.includeVideoTrack()){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getExtraAudioTracksById(String id){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(st.includesMultipleAudioTracks()) {
                if (Objects.equals(st.getAudioTrackId(), id)) {
                    filter.add(st);
                }
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getExtraAudioTracksByName(String name){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(st.includesMultipleAudioTracks()){
                if(Objects.equals(st.getAudioTrackName(), name)){
                    filter.add(st);
                }
            }
        }
        return new StreamQuery(filter);
    }

    public Stream getOnlyAudio(){
        return filter(Filter.builder()
                .onlyAudio(true)
                .subtype("mp4")
                .build()
        ).getLast();
    }

    public Stream getDefaultResolution(){
        return filter(Filter.builder()
                .progressive(true)
                .subtype("mp4")
                .build()
        ).getFirst();
    }

    public Stream getLowestResolution(){
        return filter(Filter.builder()
                .progressive(false)
                .type("video")
                .subtype("mp4")
                .build()
        ).getLast();
    }

    public Stream getHighestResolution(){
        return filter(Filter.builder()
                .progressive(false)
                .type("video")
                .subtype("mp4")
                .build()
        ).getFirst();
    }

    /**
     *
     * @deprecated This method will be removed soon, see the documentation for the new implementation
     */
    @Deprecated
    public StreamQuery filter(HashMap<String, String> filters){

        ArrayList<Stream> streamFilter = new ArrayList<>();
        if(filters.containsKey("res")){
            if(!streamFilter.isEmpty()){
                streamFilter.retainAll(new ArrayList<>(getResolution(filters.get("res"))));
            }else{
                streamFilter.addAll(getResolution(filters.get("res")));
            }
            if(streamFilter.isEmpty()){
                filters.clear();
            }
        }

        if(filters.containsKey("fps")){
            if(!streamFilter.isEmpty()){
                streamFilter.retainAll(new ArrayList<>(getFps(filters.get("fps"))));
            }else{
                streamFilter.addAll(getFps(filters.get("fps")));
            }
            if(streamFilter.isEmpty()){
                filters.clear();
            }
        }

        if(filters.containsKey("mineType")){
            if(!streamFilter.isEmpty()){
                streamFilter.retainAll(new ArrayList<>(getMineType(filters.get("mineType"))));
            }else{
                streamFilter.addAll(getMineType(filters.get("mineType")));
            }
            if(streamFilter.isEmpty()){
                filters.clear();
            }
        }

        if(filters.containsKey("type")){
            if(!streamFilter.isEmpty()){
                streamFilter.retainAll(new ArrayList<>(getType(filters.get("type"))));
            }else{
                streamFilter.addAll(getType(filters.get("type")));
            }
            if(streamFilter.isEmpty()){
                filters.clear();
            }
        }

        if(filters.containsKey("subType")){
            if(!streamFilter.isEmpty()){
                streamFilter.retainAll(new ArrayList<>(getSubtype(filters.get("subType"))));
            }else{
                streamFilter.addAll(getSubtype(filters.get("subType")));
            }
            if(streamFilter.isEmpty()){
                filters.clear();
            }
        }

        if(filters.containsKey("abr")){
            if(!streamFilter.isEmpty()){
                streamFilter.retainAll(new ArrayList<>(getAbr(filters.get("abr"))));
            }else{
                streamFilter.addAll(getAbr(filters.get("abr")));
            }
            if(streamFilter.isEmpty()){
                filters.clear();
            }

        }

        if(filters.containsKey("videoCodec")){
            if(!streamFilter.isEmpty()){
                streamFilter.retainAll(new ArrayList<>(getVideoCodec(filters.get("videoCodec"))));
            }else{
                streamFilter.addAll(getVideoCodec(filters.get("videoCodec")));
            }
            if(streamFilter.isEmpty()){
                filters.clear();
            }
        }
        if(filters.containsKey("audioCodec")){
            if(!streamFilter.isEmpty()){
                streamFilter.retainAll(new ArrayList<>(getAudioCodec(filters.get("audioCodec"))));
            }else{
                streamFilter.addAll(getAudioCodec(filters.get("audioCodec")));
            }
            if(streamFilter.isEmpty()){
                filters.clear();
            }
        }
        if(filters.containsKey("onlyAudio")){
            if(Objects.equals(filters.get("onlyAudio"), "true")){
                if(!streamFilter.isEmpty()){
                    streamFilter.retainAll(new ArrayList<>(onlyAudio()));
                }else{
                    streamFilter.addAll(onlyAudio());
                }
                if(streamFilter.isEmpty()){
                    filters.clear();
                }
            }
        }
        if(filters.containsKey("onlyVideo")){
            if(Objects.equals(filters.get("onlyVideo"), "true")){
                if(!streamFilter.isEmpty()){
                    streamFilter.retainAll(new ArrayList<>(onlyVideo()));
                }else{
                    streamFilter.addAll(onlyVideo());
                }
                if(streamFilter.isEmpty()){
                    filters.clear();
                }
            }
        }
        if(filters.containsKey("progressive")){
            if(Objects.equals(filters.get("progressive"), "true")){
                if(!streamFilter.isEmpty()){
                    streamFilter.retainAll(new ArrayList<>(progressive()));
                }else{
                    streamFilter.addAll(progressive());
                }
                if(streamFilter.isEmpty()){
                    filters.clear();
                }
            }else if (Objects.equals(filters.get("progressive"), "false")){
                if(!streamFilter.isEmpty()){
                    streamFilter.retainAll(new ArrayList<>(adaptive()));
                }else{
                    streamFilter.addAll(adaptive());
                }
                if(streamFilter.isEmpty()){
                    filters.clear();
                }
            }
        }
        if(filters.containsKey("adaptive")){
            if(Objects.equals(filters.get("adaptive"), "true")){
                if(!streamFilter.isEmpty()){
                    streamFilter.retainAll(new ArrayList<>(adaptive()));
                }else{
                    streamFilter.addAll(adaptive());
                }
                if(streamFilter.isEmpty()){
                    filters.clear();
                }
            } else if (Objects.equals(filters.get("adaptive"), "false")) {
                if(!streamFilter.isEmpty()){
                    streamFilter.retainAll(new ArrayList<>(progressive()));
                }else{
                    streamFilter.addAll(progressive());
                }
                if(streamFilter.isEmpty()){
                    filters.clear();
                }
            }
        }

        return new StreamQuery(streamFilter);
    }

    private ArrayList<Stream> getResolution(String re){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.getResolution(), re)){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> getFps(String fps){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.getFps(), Integer.parseInt(fps))){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> getMineType(String mineType){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.getMimeType(), mineType)){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> getType(String type){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.getType(), type)){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> getSubtype(String subtype){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.getSubType(), subtype)){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> getAbr(String abr){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.getAbr(), abr)){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> getVideoCodec(String videoCodec){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.getVideoCodec(), videoCodec)){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> getAudioCodec(String audioCodec){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.getAudioCodec(), audioCodec)){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> onlyAudio(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if((st.includeAudioTrack()) && (!st.includeVideoTrack())){
                filter.add(st);
            }
        }
        return filter;
    }

    private ArrayList<Stream> onlyVideo(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if((st.includeVideoTrack() && (!st.includeAudioTrack()))){
                filter.add(st);
            }
        }
        return filter;
    }

}
