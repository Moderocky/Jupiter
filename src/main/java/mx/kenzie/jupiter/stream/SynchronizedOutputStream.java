package mx.kenzie.jupiter.stream;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

class SynchronizedOutputStream extends OutputStream {
    
    protected final OutputStream stream;
    
    SynchronizedOutputStream(OutputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public void write(byte @NotNull [] bytes) throws IOException {
        synchronized (stream) {
            super.write(bytes);
        }
    }
    
    @Override
    public void write(byte @NotNull [] bytes, int offset, int length) throws IOException {
        synchronized (stream) {
            super.write(bytes, offset, length);
        }
    }
    
    @Override
    public void flush() throws IOException {
        synchronized (stream) {
            stream.flush();
        }
    }
    
    @Override
    public void write(int b) throws IOException {
        synchronized (stream) {
            stream.write(b);
        }
    }
    
    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
