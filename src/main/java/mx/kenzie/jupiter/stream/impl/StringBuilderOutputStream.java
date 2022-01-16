package mx.kenzie.jupiter.stream.impl;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Arrays;

@SuppressWarnings("unused")
public class StringBuilderOutputStream extends NoThrowsOutputStream {
    
    private static final int GROW = 16;
    
    protected final StringBuilder builder;
    protected final Charset charset;
    protected volatile int mark;
    protected volatile byte[] bytes;
    
    public StringBuilderOutputStream(StringBuilder builder) {
        this(builder, Charset.defaultCharset());
    }
    
    public StringBuilderOutputStream(StringBuilder builder, Charset charset) {
        this(builder, charset, GROW);
    }
    
    public StringBuilderOutputStream(StringBuilder builder, Charset charset, int initialSize) {
        this.builder = builder;
        this.bytes = new byte[initialSize];
        this.charset = charset;
    }
    
    @Override
    public String toString() {
        return new String(completed());
    }
    
    protected synchronized byte[] completed() {
        return Arrays.copyOf(bytes, mark);
    }
    
    public void write(String string) {
        this.write(string, charset);
    }
    
    public void write(String string, Charset charset) {
        this.write(string.getBytes(charset));
    }
    
    @Override
    public synchronized void write(int x) {
        if (mark >= bytes.length) this.grow();
        this.bytes[mark++] = (byte) x;
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
        this.builder.append(this);
    }
    
    protected synchronized void grow() {
        this.bytes = Arrays.copyOf(bytes, bytes.length + GROW);
    }
    
}
