package com.github.felipeucelli.javatube.sabr.proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Proto {

    public static void assertUint32(long value) {
        if (!(0 <= value && value <= 0xFFFFFFFFL)) {
            throw new IllegalArgumentException("Value is not a valid uint32");
        }
    }

    public static void assertInt32(int value) {
        if (!(-0x80000000 <= value && value <= 0x7FFFFFFF)) {
            throw new IllegalArgumentException("Value is not a valid int32");
        }
    }

    public static void varint32write(long value, List<Byte> buf) {
        while (value > 0x7FL) {
            buf.add((byte) ((value & 0x7FL) | 0x80L));
            value >>= 7;
        }
        buf.add((byte) value);
    }

    public static void varint64write(long lo, long hi, List<Byte> buf) {
        for (int i = 0; i < 9; i++) { // max 10 bytes
            if (hi == 0 && lo < 0x80L) {
                buf.add((byte) lo);
                return;
            }
            buf.add((byte) ((lo & 0x7FL) | 0x80L));
            long carry = (lo >> 7);
            lo = ((hi << 25) | (lo >> 7)) & 0xFFFFFFFFL;
            hi = hi >> 7;
        }
        buf.add((byte) lo);
    }

    public static Pair<Long, Integer> readVarint32(byte[] buf, int pos) {
        long result = 0;
        int shift = 0;
        while (true) {
            if (pos >= buf.length) {
                throw new IndexOutOfBoundsException("Unexpected end of buffer while reading varint32");
            }
            byte b = buf[pos];
            pos++;
            result |= (long) (b & 0x7F) << shift;
            if (!((b & 0x80) != 0)) {
                break;
            }
            shift += 7;
            if (shift > 35) {
                throw new IllegalArgumentException("Varint32 too long");
            }
        }
        return new Pair<>(result, pos);
    }

    public static Triple<Long, Long, Integer> readVarint64(byte[] buf, int pos) {
        long lowBits = 0;
        long highBits = 0;

        for (int shift = 0; shift < 28; shift += 7) {
            byte b = buf[pos];
            pos++;
            lowBits |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return new Triple<>(lowBits, highBits, pos);
            }
        }

        byte middleByte = buf[pos];
        pos++;
        lowBits |= (long) (middleByte & 0x0F) << 28;
        highBits = (long) (middleByte & 0x70) >> 4;
        if ((middleByte & 0x80) == 0) {
            return new Triple<>(lowBits, highBits, pos);
        }

        for (int shift = 3; shift < 32; shift += 7) {
            byte b = buf[pos];
            pos++;
            highBits |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return new Triple<>(lowBits, highBits, pos);
            }
        }
        throw new IllegalArgumentException("invalid varint");
    }

    public static long decodeInt64(long lo, long hi) {
        long value = (hi << 32) | lo;
        if ((hi & 0x80000000L) != 0) {
            value -= (1L << 64);
        }
        return value;
    }

    public static long decodeUint64(long lo, long hi) {
        return (hi << 32) | lo;
    }

    public static class ProtoInt64 {
        public static Pair<Long, Long> enc(long value) {
            if (value < 0) {
                value += (1L << 64);
            }
            long lo = value & 0xFFFFFFFFL;
            long hi = (value >> 32) & 0xFFFFFFFFL;
            return new Pair<>(lo, hi);
        }

        public static Pair<Long, Long> uEnc(long value) {
            long lo = value & 0xFFFFFFFFL;
            long hi = (value >> 32) & 0xFFFFFFFFL;
            return new Pair<>(lo, hi);
        }
    }

    public static class BinaryWriter {
        private Function<String, byte[]> encodeUtf8;
        private List<Pair<List<byte[]>, List<Byte>>> stack;
        private List<byte[]> chunks;
        private List<Byte> buf;

        public BinaryWriter() {
            this(s -> s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        public BinaryWriter(Function<String, byte[]> encodeUtf8) {
            this.encodeUtf8 = encodeUtf8;
            this.stack = new ArrayList<>();
            this.chunks = new ArrayList<>();
            this.buf = new ArrayList<>();
        }

        public byte[] finish() {
            if (!buf.isEmpty()) {
                byte[] currentBuf = new byte[buf.size()];
                for (int i = 0; i < buf.size(); i++) {
                    currentBuf[i] = buf.get(i);
                }
                chunks.add(currentBuf);
                buf.clear();
            }
            int totalLength = 0;
            for (byte[] chunk : chunks) {
                totalLength += chunk.length;
            }
            ByteBuffer result = ByteBuffer.allocate(totalLength);
            for (byte[] chunk : chunks) {
                result.put(chunk);
            }
            return result.array();
        }

        public BinaryWriter fork() {
            stack.add(new Pair<>(new ArrayList<>(chunks), new ArrayList<>(buf)));
            chunks = new ArrayList<>();
            buf = new ArrayList<>();
            return this;
        }

        public BinaryWriter join() {
            byte[] chunk = finish();
            if (stack.isEmpty()) {
                throw new IllegalStateException("Invalid state, fork stack empty");
            }
            Pair<List<byte[]>, List<Byte>> popped = stack.remove(stack.size() - 1);
            chunks = popped.getKey();
            buf = popped.getValue();
            uint32(chunk.length);
            return raw(chunk);
        }

        public BinaryWriter tag(int fieldNo, int wireType) {
            return uint32((fieldNo << 3) | wireType);
        }

        public BinaryWriter raw(byte[] chunk) {
            if (!buf.isEmpty()) {
                byte[] currentBuf = new byte[buf.size()];
                for (int i = 0; i < buf.size(); i++) {
                    currentBuf[i] = buf.get(i);
                }
                chunks.add(currentBuf);
                buf.clear();
            }
            chunks.add(chunk);
            return this;
        }

        public BinaryWriter uint32(long value) {
            assertUint32(value);
            varint32write(value, buf);
            return this;
        }

        public BinaryWriter int32(int value) {
            assertInt32(value);
            varint32write(value & 0xFFFFFFFFL, buf);
            return this;
        }

        public BinaryWriter bool(boolean value) {
            buf.add((byte) (value ? 1 : 0));
            return this;
        }

        public BinaryWriter bytes(byte[] value) {
            uint32(value.length);
            return raw(value);
        }

        public BinaryWriter string(String value) {
            byte[] encoded = encodeUtf8.apply(value);
            uint32(encoded.length);
            return raw(encoded);
        }

        public BinaryWriter writeFloat(float value) {
            ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putFloat(value);
            return raw(buffer.array());
        }

        public BinaryWriter doubleVal(double value) {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putDouble(value);
            return raw(buffer.array());
        }

        public BinaryWriter fixed32(long value) {
            assertUint32(value);
            ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt((int) value);
            return raw(buffer.array());
        }

        public BinaryWriter sfixed32(int value) {
            assertInt32(value);
            ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt(value);
            return raw(buffer.array());
        }

        public BinaryWriter sint32(int value) {
            assertInt32(value);
            long encoded = ((long) value << 1) ^ (value >> 31);
            varint32write(encoded, buf);
            return this;
        }

        public BinaryWriter sfixed64(long value) {
            Pair<Long, Long> tc = ProtoInt64.enc(value);
            ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt((int) tc.getKey().longValue());
            buffer.putInt((int) tc.getValue().longValue());
            return raw(buffer.array());
        }

        public BinaryWriter fixed64(long value) {
            Pair<Long, Long> tc = ProtoInt64.uEnc(value);
            ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt((int) tc.getKey().longValue());
            buffer.putInt((int) tc.getValue().longValue());
            return raw(buffer.array());
        }

        public BinaryWriter int64(long value) {
            Pair<Long, Long> tc = ProtoInt64.enc(value);
            varint64write(tc.getKey(), tc.getValue(), buf);
            return this;
        }

        public BinaryWriter sint64(long value) {
            Pair<Long, Long> tc = ProtoInt64.enc(value);
            long sign = tc.getValue() >> 31;
            long lo = (tc.getKey() << 1) ^ sign;
            long hi = ((tc.getValue() << 1) | (tc.getKey() >> 31)) ^ sign;
            varint64write(lo, hi, buf);
            return this;
        }

        public BinaryWriter uint64(long value) {
            Pair<Long, Long> tc = ProtoInt64.uEnc(value);
            varint64write(tc.getKey(), tc.getValue(), buf);
            return this;
        }
    }

    public static class BinaryReader {
        private Function<byte[], String> decodeUtf8;
        private byte[] buf;
        private int len;
        private int pos;

        public BinaryReader(byte[] buf) {
            this(buf, b -> new String(b, java.nio.charset.StandardCharsets.UTF_8));
        }

        public BinaryReader(byte[] buf, Function<byte[], String> decodeUtf8) {
            this.decodeUtf8 = decodeUtf8;
            this.buf = buf;
            this.len = buf.length;
            this.pos = 0;
        }
        public int getPos() {
            return pos;
        }

        public int len(){
            return len;
        }

        public Pair<Integer, Integer> tag() {
            Pair<Long, Integer> varintResult = readVarint32(buf, pos);
            int tag = varintResult.getKey().intValue();
            pos = varintResult.getValue();
            int fieldNo = tag >> 3;
            int wireType = tag & 0x7;
            if (fieldNo <= 0 || wireType < 0 || wireType > 5) {
                throw new IllegalArgumentException(String.format("Illegal tag: field no %d wire type %d", fieldNo, wireType));
            }
            return new Pair<>(fieldNo, wireType);
        }

        public byte[] skip(int wireType) {
            return skip(wireType, 0);
        }

        public byte[] skip(int wireType, Integer fieldNo) {
            int start = pos;
            if (wireType == 0) { // Varint
                while ((buf[pos] & 0x80) != 0) {
                    pos++;
                }
                pos++;
            } else if (wireType == 1) { // 64-bit
                pos += 8;
            } else if (wireType == 2) { // Length-delimited
                Pair<Long, Integer> varintResult = readVarint32(buf, pos);
                int length = varintResult.getKey().intValue();
                pos = varintResult.getValue();
                pos += length;
            } else if (wireType == 3) { // Start group
                while (true) {
                    Pair<Integer, Integer> tagResult = tag();
                    int fn = tagResult.getKey();
                    int wt = tagResult.getValue();
                    if (wt == 4) { // End group
                        if (fieldNo != null && fn != fieldNo) {
                            throw new IllegalArgumentException("Invalid end group tag");
                        }
                        break;
                    }
                    skip(wt, fn);
                }
            } else if (wireType == 5) {
                pos += 4;
            } else {
                throw new IllegalArgumentException(String.format("Can't skip unknown wire type %d", wireType));
            }
            assertBounds();
            return Arrays.copyOfRange(buf, start, pos);
        }

        public void assertBounds() {
            if (pos > len) {
                throw new IndexOutOfBoundsException("Premature EOF");
            }
        }

        public long uint32() {
            Pair<Long, Integer> varintResult = readVarint32(buf, pos);
            pos = varintResult.getValue();
            return varintResult.getKey();
        }

        public int int32() {
            return (int) uint32();
        }

        public int sint32() {
            long value = uint32();
            return (int) ((value >> 1) ^ -(value & 1));
        }

        public Pair<Long, Long> varint64() {
            Triple<Long, Long, Integer> varintResult = readVarint64(buf, pos);
            pos = varintResult.getThird();
            return new Pair<>(varintResult.getFirst(), varintResult.getSecond());
        }

        public long int64() {
            Pair<Long, Long> varintResult = varint64();
            return decodeInt64(varintResult.getKey(), varintResult.getValue());
        }

        public long uint64() {
            Pair<Long, Long> varintResult = varint64();
            return decodeUint64(varintResult.getKey(), varintResult.getValue());
        }

        public long sint64() {
            Pair<Long, Long> varintResult = varint64();
            long lo = varintResult.getKey();
            long hi = varintResult.getValue();
            long sign = -(lo & 1);
            lo = ((lo >> 1) | ((hi & 1) << 31)) ^ sign;
            hi = (hi >> 1) ^ sign;
            return decodeInt64(lo, hi);
        }

        public boolean bool() {
            Pair<Long, Long> varintResult = varint64();
            long lo = varintResult.getKey();
            long hi = varintResult.getValue();
            return lo != 0 || hi != 0;
        }

        public long fixed32() {
            ByteBuffer buffer = ByteBuffer.wrap(buf, pos, 4).order(ByteOrder.LITTLE_ENDIAN);
            long value = buffer.getInt() & 0xFFFFFFFFL;
            pos += 4;
            return value;
        }

        public int sfixed32() {
            ByteBuffer buffer = ByteBuffer.wrap(buf, pos, 4).order(ByteOrder.LITTLE_ENDIAN);
            int value = buffer.getInt();
            pos += 4;
            return value;
        }

        public long fixed64() {
            long lo = sfixed32() & 0xFFFFFFFFL;
            long hi = sfixed32() & 0xFFFFFFFFL;
            return decodeUint64(lo, hi);
        }

        public long sfixed64() {
            long lo = sfixed32() & 0xFFFFFFFFL;
            long hi = sfixed32() & 0xFFFFFFFFL;
            return decodeInt64(lo, hi);
        }

        public float readFloat() {
            ByteBuffer buffer = ByteBuffer.wrap(buf, pos, 4).order(ByteOrder.LITTLE_ENDIAN);
            float value = buffer.getFloat();
            pos += 4;
            return value;
        }

        public double doubleVal() {
            ByteBuffer buffer = ByteBuffer.wrap(buf, pos, 8).order(ByteOrder.LITTLE_ENDIAN);
            double value = buffer.getDouble();
            pos += 8;
            return value;
        }

        public byte[] bytes() {
            int length = (int) uint32();
            int start = pos;
            pos += length;
            assertBounds();
            return Arrays.copyOfRange(buf, start, pos);
        }

        public String string() {
            return decodeUtf8.apply(bytes());
        }
    }

    public static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    public static class Triple<K, V, T> {
        private final K first;
        private final V second;
        private final T third;

        public Triple(K first, V second, T third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public K getFirst() {
            return first;
        }

        public V getSecond() {
            return second;
        }

        public T getThird() {
            return third;
        }
    }
}


