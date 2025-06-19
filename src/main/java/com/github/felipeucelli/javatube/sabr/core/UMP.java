package com.github.felipeucelli.javatube.sabr.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UMP {
    private ChunkedDataBuffer chunkedDataBuffer;

    public UMP(ChunkedDataBuffer chunkedDataBuffer) {
        this.chunkedDataBuffer = chunkedDataBuffer;
    }

    public Map<String, Object> parse(Consumer<Map<String, Object>> handlePart) {
        while (true) {
            int offset = 0;
            long[] partTypeResult = readVarint(offset);
            long partType = partTypeResult[0];
            offset = (int) partTypeResult[1];

            long[] partSizeResult = readVarint(offset);
            long partSize = partSizeResult[0];
            offset = (int) partSizeResult[1];

            if (partType < 0 || partSize < 0) {
                break;
            }

            if (!this.chunkedDataBuffer.canReadBytes(offset, (int) partSize)) {
                if (!this.chunkedDataBuffer.canReadBytes(offset, 1)) {
                    break;
                }
                Map<String, Object> result = new HashMap<>();
                result.put("type", partType);
                result.put("size", partSize);
                result.put("data", this.chunkedDataBuffer);
                return result;
            }

            Object[] splitResult = ((ChunkedDataBuffer) this.chunkedDataBuffer.split(offset)[1]).split((int) partSize);

            offset = 0;
            Map<String, Object> partData = new HashMap<>();
            partData.put("type", partType);
            partData.put("size", partSize);
            partData.put("data", splitResult[0]);
            handlePart.accept(partData);
            this.chunkedDataBuffer = (ChunkedDataBuffer) splitResult[1];
        }
        return null;
    }

    private long[] readVarint(int offset) {
        if (this.chunkedDataBuffer.canReadBytes(offset, 1)) {
            int firstByte = this.chunkedDataBuffer.getUint8(offset);
            int byteLength;
            if (firstByte < 128) {
                byteLength = 1;
            } else if (firstByte < 192) {
                byteLength = 2;
            } else if (firstByte < 224) {
                byteLength = 3;
            } else if (firstByte < 240) {
                byteLength = 4;
            } else {
                byteLength = 5;
            }

            if (byteLength < 1 || !this.chunkedDataBuffer.canReadBytes(offset, byteLength)) {
                return new long[]{-1, offset};
            }

            long value;
            if (byteLength == 1) {
                value = this.chunkedDataBuffer.getUint8(offset);
                offset += 1;
            } else if (byteLength == 2) {
                int byte1 = this.chunkedDataBuffer.getUint8(offset);
                int byte2 = this.chunkedDataBuffer.getUint8(offset + 1);
                value = (byte1 & 0x3F) + 64 * byte2;
                offset += 2;
            } else if (byteLength == 3) {
                int byte1 = this.chunkedDataBuffer.getUint8(offset);
                int byte2 = this.chunkedDataBuffer.getUint8(offset + 1);
                int byte3 = this.chunkedDataBuffer.getUint8(offset + 2);
                value = (byte1 & 0x1F) + 32 * (byte2 + 256 * byte3);
                offset += 3;
            } else if (byteLength == 4) {
                int byte1 = this.chunkedDataBuffer.getUint8(offset);
                int byte2 = this.chunkedDataBuffer.getUint8(offset + 1);
                int byte3 = this.chunkedDataBuffer.getUint8(offset + 2);
                int byte4 = this.chunkedDataBuffer.getUint8(offset + 3);
                value = (byte1 & 0x0F) + 16 * (byte2 + 256 * (byte3 + 256 * byte4));
                offset += 4;
            } else { // byteLength == 5
                int tempOffset = offset + 1;
                this.chunkedDataBuffer.focus(tempOffset);
                if (canReadFromCurrentChunk(tempOffset, 4)) {
                    ByteBuffer view = getCurrentDataView();
                    int offsetInChunk = tempOffset - this.chunkedDataBuffer.currentChunkOffset;
                    value = view.getInt(offsetInChunk) & 0xFFFFFFFFL; // Read as unsigned int
                } else {
                    int byte3 = (this.chunkedDataBuffer.getUint8(tempOffset + 2) + 256 * this.chunkedDataBuffer.getUint8(tempOffset + 3));
                    value = (this.chunkedDataBuffer.getUint8(tempOffset) + 256 * (this.chunkedDataBuffer.getUint8(tempOffset + 1) + 256 * byte3));
                }
                offset += 5;
            }
            return new long[]{value, offset};
        }
        return new long[]{-1, offset};
    }

    private boolean canReadFromCurrentChunk(int offset, int length) {
        if (this.chunkedDataBuffer.chunks.isEmpty()) return false;
        byte[] currentChunk = this.chunkedDataBuffer.chunks.get(this.chunkedDataBuffer.currentChunkIndex);
        return (offset - this.chunkedDataBuffer.currentChunkOffset + length <= currentChunk.length);
    }

    private ByteBuffer getCurrentDataView() {
        if (this.chunkedDataBuffer.currentDataView == null) {
            byte[] chunk = this.chunkedDataBuffer.chunks.get(this.chunkedDataBuffer.currentChunkIndex);
            this.chunkedDataBuffer.currentDataView = ByteBuffer.wrap(chunk).order(ByteOrder.LITTLE_ENDIAN);
        }
        return this.chunkedDataBuffer.currentDataView;
    }
}


