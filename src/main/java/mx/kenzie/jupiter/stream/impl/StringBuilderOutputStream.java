package mx.kenzie.jupiter.stream.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class StringBuilderOutputStream extends OutputStream {
    
    private static final int GROW = 16;
    
    protected final StringBuilder builder;
    protected volatile int mark;
    protected byte[] bytes;
    
    public StringBuilderOutputStream(StringBuilder builder) {
        this.builder = builder;
        this.bytes = new byte[GROW];
    }
    
    @Override
    public String toString() {
        return new String(completed());
    }
    
    protected byte[] completed() {
        return Arrays.copyOf(bytes, mark);
    }
    
    public void write(String string, Charset charset) throws IOException {
        this.write(string.getBytes(charset));
    }
    
    @Override
    public synchronized void write(int x) throws IOException {
        if (mark >= bytes.length) this.grow();
        this.bytes[mark++] = (byte) x;
    }
    
    protected void grow() {
        bytes = Arrays.copyOf(bytes, bytes.length + GROW);
    }
    
    @Override
    public void close() throws IOException {
        builder.append(this);
        super.close();
    }
    
}
