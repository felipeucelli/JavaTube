package com.github.felipeucelli.javatube.sabr.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkedDataBuffer {
    public List<byte[]> chunks;
    public int currentChunkIndex;
    public int currentChunkOffset;
    public ByteBuffer currentDataView;
    public int totalLength;

    public ChunkedDataBuffer() {
        this(null);
    }

    public ChunkedDataBuffer(List<byte[]> chunks) {
        this.chunks = new ArrayList<>();
        this.currentChunkIndex = 0;
        this.currentChunkOffset = 0;
        this.currentDataView = null;
        this.totalLength = 0;

        if (chunks != null) {
            for (byte[] chunk : chunks) {
                this.append(chunk);
            }
        }
    }

    public int getLength() {
        return this.totalLength;
    }

    public void append(byte[] chunk) {
        if (canMergeWithLastChunk(chunk)) {
            byte[] lastChunk = this.chunks.remove(this.chunks.size() - 1);
            byte[] merged = new byte[lastChunk.length + chunk.length];
            System.arraycopy(lastChunk, 0, merged, 0, lastChunk.length);
            System.arraycopy(chunk, 0, merged, lastChunk.length, chunk.length);
            this.chunks.add(merged);
            resetFocus();
        } else {
            this.chunks.add(chunk);
            this.totalLength += chunk.length;
        }
    }

    public Object[] split(int position) {
        ChunkedDataBuffer extractedBuffer = new ChunkedDataBuffer();
        ChunkedDataBuffer remainingBuffer = new ChunkedDataBuffer();
        int remainingPos = position;

        for (byte[] chunk : this.chunks) {
            int chunkLen = chunk.length;
            if (remainingPos >= chunkLen) {
                extractedBuffer.append(chunk);
                remainingPos -= chunkLen;
            } else if (remainingPos > 0) {
                byte[] extractedPart = new byte[remainingPos];
                System.arraycopy(chunk, 0, extractedPart, 0, remainingPos);
                extractedBuffer.append(extractedPart);

                byte[] remainingPart = new byte[chunkLen - remainingPos];
                System.arraycopy(chunk, remainingPos, remainingPart, 0, chunkLen - remainingPos);
                remainingBuffer.append(remainingPart);
                remainingPos = 0;
            } else {
                remainingBuffer.append(chunk);
            }
        }
        return new Object[]{extractedBuffer, remainingBuffer};
    }

    public boolean isFocused(int position) {
        if (chunks.isEmpty()) return false;
        byte[] chunk = this.chunks.get(this.currentChunkIndex);
        return this.currentChunkOffset <= position && position < this.currentChunkOffset + chunk.length;
    }

    public void focus(int position) {
        if (!isFocused(position)) {
            if (position < this.currentChunkOffset) {
                resetFocus();
            }
            while (this.currentChunkOffset + this.chunks.get(this.currentChunkIndex).length <= position && this.currentChunkIndex < this.chunks.size() - 1) {
                this.currentChunkOffset += this.chunks.get(this.currentChunkIndex).length;
                this.currentChunkIndex++;
            }
            this.currentDataView = null;
        }
    }

    public boolean canReadBytes(int position, int length) {
        return position + length <= this.totalLength;
    }

    public int getUint8(int position) {
        focus(position);
        byte[] chunk = this.chunks.get(this.currentChunkIndex);
        return chunk[position - this.currentChunkOffset] & 0xFF;
    }

    public boolean canMergeWithLastChunk(byte[] chunk) {
        if (this.chunks.isEmpty()) {
            return false;
        }

        return true;
    }

    public void resetFocus() {
        this.currentDataView = null;
        this.currentChunkIndex = 0;
        this.currentChunkOffset = 0;
    }
}


