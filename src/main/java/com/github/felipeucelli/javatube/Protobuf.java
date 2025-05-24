package com.github.felipeucelli.javatube;

import java.io.*;
import java.util.*;

public class Protobuf {

    enum WireType {
        VARINT(0), I64(1), LEN(2), SGROUP(3), EGROUP(4), I32(5);
        final int value;
        WireType(int value) { this.value = value; }
        static WireType from(int value) { return values()[value]; }
    }

    public static String encodeProtobuf(String input) throws IOException {
        Map<Integer, Object> map = parseMap(new StringReader(input.trim()));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        encode(map, output);
        return Base64.getEncoder().encodeToString(output.toByteArray());
    }

    public static String decodeProtobuf(String base64) throws IOException {
        byte[] data = Base64.getDecoder().decode(base64);
        Map<Integer, Object> map = decode(new ByteArrayInputStream(data));
        return mapToString(map);
    }

    private static Map<Integer, Object> parseMap(StringReader reader) throws IOException {
        Map<Integer, Object> map = new LinkedHashMap<>();
        int c;
        skipWhitespace(reader); // Skip opening '{'
        if (reader.read() != '{') throw new IllegalArgumentException("Expected '{'");

        while (true) {
            skipWhitespace(reader);
            c = reader.read();
            if (c == '}') break;
            if (c == -1) throw new EOFException("Unexpected end of input");

            reader.skip(-1); // unread
            int key = readInt(reader);
            skipWhitespace(reader);
            if (reader.read() != ':') throw new IllegalArgumentException("Expected ':'");
            skipWhitespace(reader);

            c = reader.read();
            if (c == '{') {
                reader.skip(-1);
                Object value = parseMap(reader);
                map.put(key, value);
            } else {
                StringBuilder number = new StringBuilder();
                number.append((char) c);
                while ((c = reader.read()) != -1 && Character.isDigit(c)) {
                    number.append((char) c);
                }
                if (c != -1) reader.skip(-1); // unread non-digit
                map.put(key, Integer.parseInt(number.toString()));
            }

            skipWhitespace(reader);
            c = reader.read();
            if (c == ',') continue;
            if (c == '}') break;
            if (c == -1) break;
            throw new IllegalArgumentException("Unexpected character: " + (char) c);
        }

        return map;
    }

    private static void skipWhitespace(StringReader reader) throws IOException {
        reader.mark(1);
        int c;
        while ((c = reader.read()) != -1) {
            if (!Character.isWhitespace(c)) {
                reader.reset();
                return;
            }
            reader.mark(1);
        }
    }

    private static int readInt(StringReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1 && Character.isDigit(c)) {
            sb.append((char) c);
        }
        if (c != -1) reader.skip(-1); // unread
        return Integer.parseInt(sb.toString());
    }

    private static void encode(Map<Integer, Object> data, OutputStream out) throws IOException {
        for (Map.Entry<Integer, Object> entry : data.entrySet()) {
            encodeRecord(entry.getValue(), entry.getKey(), out);
        }
    }

    private static Map<Integer, Object> decode(InputStream in) throws IOException {
        Map<Integer, List<Object>> result = new LinkedHashMap<>();
        while (in.available() > 0) {
            int tag = readVarint(in);
            int wireId = tag >> 3;
            WireType type = WireType.from(tag & 0b111);

            Object value;
            switch (type) {
                case VARINT:
                    value = readVarint(in);
                    break;
                case I64:
                    value = in.readNBytes(8);
                    break;
                case I32:
                    value = in.readNBytes(4);
                    break;
                case LEN:
                    int length = readVarint(in);
                    byte[] sub = in.readNBytes(length);
                    try (ByteArrayInputStream subIn = new ByteArrayInputStream(sub)) {
                        value = decode(subIn);
                    } catch (Exception e) {
                        value = sub;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported wire type");
            }

            result.computeIfAbsent(wireId, k -> new ArrayList<>()).add(value);
        }

        Map<Integer, Object> finalResult = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<Object>> entry : result.entrySet()) {
            if (entry.getValue().size() == 1) {
                finalResult.put(entry.getKey(), entry.getValue().get(0));
            } else {
                finalResult.put(entry.getKey(), entry.getValue());
            }
        }

        return finalResult;
    }
    @SuppressWarnings("unchecked")
    private static void encodeRecord(Object value, int wireId, OutputStream out) throws IOException {
        if (value instanceof Integer) {
            int v = (Integer) value;
            if (v < 0) v = signedToZigZag(v);
            writeVarint((wireId << 3) | WireType.VARINT.value, out);
            writeVarint(v, out);
        } else if (value instanceof Map) {
            ByteArrayOutputStream nested = new ByteArrayOutputStream();
            encode((Map<Integer, Object>) value, nested);
            byte[] bytes = nested.toByteArray();
            writeVarint((wireId << 3) | WireType.LEN.value, out);
            writeVarint(bytes.length, out);
            out.write(bytes);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
        }
    }

    private static int signedToZigZag(int value) {
        return (value << 1) ^ (value >> 31);
    }

    private static void writeVarint(int value, OutputStream out) throws IOException {
        while ((value & ~0x7F) != 0) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value);
    }

    private static int readVarint(InputStream in) throws IOException {
        int shift = 0, result = 0;
        int b;
        do {
            b = in.read();
            if (b == -1) throw new EOFException();
            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static String mapToString(Map<Integer, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Iterator<Map.Entry<Integer, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            sb.append(entry.getKey()).append(": ");
            if (entry.getValue() instanceof Map) {
                sb.append(mapToString((Map<Integer, Object>) entry.getValue()));
            } else {
                sb.append(entry.getValue());
            }
            if (it.hasNext()) sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }
}
