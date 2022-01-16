package mx.kenzie.jupiter.stream.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class StringBuilderOutputStream extends OutputStream {
    
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
    
    public void write(String string, Charset charset) throws IOException {
        this.write(string.getBytes(charset));
    }
    
    @Override
    public synchronized void write(int x) {
        if (mark >= bytes.length) this.grow();
        this.bytes[mark++] = (byte) x;
    }
    
    protected synchronized void grow() {
        this.bytes = Arrays.copyOf(bytes, bytes.length + GROW);
    }
    
    @Override
    public void close() throws IOException {
        this.builder.append(this);
        super.close();
    }
    
}
