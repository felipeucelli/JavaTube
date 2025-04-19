package com.github.felipeucelli.javatube;


import java.util.*;

public class FilterBuilder {

    public enum Filter {
        TYPE,
        UPLOAD_DATE,
        DURATION,
        FEATURES,
        SORT_BY
    }

    public enum Type {
        VIDEO("Video"),
        CHANNEL("Channel"),
        PLAYLIST("Playlist"),
        MOVIE("Movie");

        public final String label;
        Type(String label) { this.label = label; }
    }

    public enum UploadDate {
        LAST_HOUR("Last Hour"),
        TODAY("Today"),
        THIS_WEEK("This week"),
        THIS_MONTH("This month"),
        THIS_YEAR("This year");

        public final String label;
        UploadDate(String label) { this.label = label; }
    }

    public enum Duration {
        UNDER_4_MIN("Under 4 minutes"),
        OVER_20_MIN("Over 20 minutes"),
        BETWEEN_4_20("4 - 20 minutes");

        public final String label;
        Duration(String label) { this.label = label; }
    }

    public enum Feature {
        LIVE("Live"),
        _4K("4K"),
        HD("HD"),
        SUBTITLES("Subtitles/CC"),
        CREATIVE_COMMONS("Creative Commons"),
        _360("360"),
        VR180("VR180"),
        _3D("3D"),
        HDR("HDR"),
        LOCATION("Location"),
        PURCHASED("Purchased");

        public final String label;
        Feature(String label) { this.label = label; }
    }

    public enum SortBy {
        RELEVANCE("Relevance"),
        UPLOAD_DATE("Upload date"),
        VIEW_COUNT("View count"),
        RATING("Rating");

        public final String label;
        SortBy(String label) { this.label = label; }
    }

    private static final Map<String, Map<Integer, Map<Integer, Integer>>> UPLOAD_DATE = Map.of(
            "Last Hour", Map.of(2, Map.of(1, 1)),
            "Today", Map.of(2, Map.of(1, 2)),
            "This week", Map.of(2, Map.of(1, 3)),
            "This month", Map.of(2, Map.of(1, 4)),
            "This year", Map.of(2, Map.of(1, 5))
    );

    private static final Map<String, Map<Integer, Map<Integer, Integer>>> TYPE = Map.of(
            "Video", Map.of(2, Map.of(2, 1)),
            "Channel", Map.of(2, Map.of(2, 2)),
            "Playlist", Map.of(2, Map.of(2, 3)),
            "Movie", Map.of(2, Map.of(2, 4))
    );

    private static final Map<String, Map<Integer, Map<Integer, Integer>>> DURATION = Map.of(
            "Under 4 minutes", Map.of(2, Map.of(3, 1)),
            "Over 20 minutes", Map.of(2, Map.of(3, 2)),
            "4 - 20 minutes", Map.of(2, Map.of(3, 3))
    );

    private static final Map<String, Map<Integer, Map<Integer, Integer>>> FEATURES = Map.ofEntries(
            Map.entry("Live", Map.of(2, Map.of(8, 1))),
            Map.entry("4K", Map.of(2, Map.of(14, 1))),
            Map.entry("HD", Map.of(2, Map.of(4, 1))),
            Map.entry("Subtitles/CC", Map.of(2, Map.of(5, 1))),
            Map.entry("Creative Commons", Map.of(2, Map.of(6, 1))),
            Map.entry("360", Map.of(2, Map.of(15, 1))),
            Map.entry("VR180", Map.of(2, Map.of(26, 1))),
            Map.entry("3D", Map.of(2, Map.of(7, 1))),
            Map.entry("HDR", Map.of(2, Map.of(25, 1))),
            Map.entry("Location", Map.of(2, Map.of(23, 1))),
            Map.entry("Purchased", Map.of(2, Map.of(9, 1)))
    );

    private static final Map<String, Map<Integer, Integer>> SORT_BY = Map.of(
            "Relevance", Map.of(1, 0),
            "Upload date", Map.of(1, 2),
            "View count", Map.of(1, 3),
            "Rating", Map.of(1, 1)
    );

    public static String buildFilter(Map<Filter, Object> filters) {
        Map<Integer, Object> result = new LinkedHashMap<>();

        if (filters.containsKey(Filter.SORT_BY)) {
            SortBy sortBy = (SortBy) filters.get(Filter.SORT_BY);
            result.putAll(SORT_BY.get(sortBy.label));
        }

        for (Map.Entry<Filter, Object> entry : filters.entrySet()) {
            Filter filterType = entry.getKey();
            Object value = entry.getValue();

            if (filterType == Filter.SORT_BY) continue;

            switch (filterType) {
                case UPLOAD_DATE -> {
                    UploadDate param = (UploadDate) value;
                    mergeMap(result, getMapFromLabel(UPLOAD_DATE, param.label));
                }
                case TYPE -> {
                    Type param = (Type) value;
                    mergeMap(result, getMapFromLabel(TYPE, param.label));
                }
                case DURATION -> {
                    Duration param = (Duration) value;
                    mergeMap(result, getMapFromLabel(DURATION, param.label));
                }
                case FEATURES -> {
                    if (value instanceof List<?>){
                        @SuppressWarnings("unchecked")
                        List<Feature> features = (List<Feature>) value;
                        for (Feature f : features) {
                            mergeMap(result, getMapFromLabel(FEATURES, f.label));
                        }
                    }else if(value instanceof Feature param)
                        mergeMap(result, getMapFromLabel(FEATURES, param.label));
                }
            }
        }

        return mapToString(result);
    }


    private static Map<Integer, Map<Integer, Integer>> getMapFromLabel(
            Map<String, Map<Integer, Map<Integer, Integer>>> source, String label) {
        return source.getOrDefault(label, Map.of());
    }

    private static void mergeMap(Map<Integer, Object> target, Map<Integer, Map<Integer, Integer>> toMerge) {
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : toMerge.entrySet()) {
            Integer outerKey = entry.getKey();
            Map<Integer, Integer> innerMap = entry.getValue();

            if (!target.containsKey(outerKey)) {
                target.put(outerKey, new LinkedHashMap<>(innerMap));
            } else {
                @SuppressWarnings("unchecked")
                Map<Integer, Integer> existingMap = (Map<Integer, Integer>) target.get(outerKey);
                existingMap.putAll(innerMap);
            }
        }
    }

    private static String mapToString(Map<Integer, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<Integer, Object> entry : map.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append(": ");
            if (entry.getValue() instanceof Map<?, ?> nestedMap) {
                sb.append("{");
                boolean nestedFirst = true;
                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    if (!nestedFirst) sb.append(", ");
                    sb.append(nestedEntry.getKey()).append(": ").append(nestedEntry.getValue());
                    nestedFirst = false;
                }
                sb.append("}");
            } else {
                sb.append(entry.getValue());
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
