package javatube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StreamQuery{
    ArrayList<Stream> fmtStreams;
    Map<Integer, Stream> itagIndex = new HashMap<>();
    public StreamQuery(ArrayList<Stream> fmt_streams){
        fmtStreams = fmt_streams;
        for (Stream fmt_stream : fmt_streams) {
            itagIndex.put(fmt_stream.itag, fmt_stream);
        }
    }

    public StreamQuery getResolution(String re){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.resolution, re)){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getFps(Integer fps){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.fps, fps)){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getMineType(String mineType){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.mimeType, mineType)){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getType(String type){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.type, type)){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getSubtype(String subtype){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.subType, subtype)){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getAbr(String abr){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.abr, abr)){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getVideoCodec(String videoCodec){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.videoCodec, videoCodec)){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getAudioCodec(String audioCodec){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(Objects.equals(st.audioCodec, audioCodec)){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public Stream getOnlyAudio(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if((st.includeAudioTrack()) && (!st.includeVideoTrack())){
                filter.add(st);
            }
        }
        return new StreamQuery(filter).getLast();
    }

    public StreamQuery getOnlyVideo(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if((st.includeVideoTrack() && (!st.includeAudioTrack()))){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getProgressive(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(st.isProgressive()){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public StreamQuery getAdaptive(){
        ArrayList<Stream> filter = new ArrayList<>();
        for(Stream st : fmtStreams){
            if(st.isAdaptive()){
                filter.add(st);
            }
        }
        return new StreamQuery(filter);
    }

    public Stream getFirst(){
        return fmtStreams.get(0);
    }

    public Stream getLast(){
        return fmtStreams.get(fmtStreams.size() - 1);
    }

    public Stream getLowestResolution(){
        return getProgressive().getSubtype("mp4").getFirst();
    }

    public Stream getHighestResolution(){
        return getProgressive().getSubtype("mp4").getLast();
    }

}
