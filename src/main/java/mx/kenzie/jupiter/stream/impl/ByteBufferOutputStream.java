package mx.kenzie.jupiter.stream.impl;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends NoThrowsOutputStream {
    
    protected final ByteBuffer buffer;
    
    public ByteBufferOutputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }
    
    @Override
    public synchronized void write(int b) {
        this.buffer.put((byte) b);
    }
    
    @Override
    public void write(byte @NotNull [] bytes) {
        this.write(bytes, 0, bytes.length);
    }
    
    @Override
    public void write(byte @NotNull [] bytes, int offset, int length) {
        for (int i = 0; i < length; i++) {
            this.write(bytes[offset + i]);
        }
    }
    
    @Override
    public void close() {
    }
}
