package mx.kenzie.jupiter.stream;

import mx.kenzie.jupiter.iterator.IterableInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

class SynchronizedInputStream extends InputStream implements IterableInputStream {
    
    protected final InputStream stream;
    
    SynchronizedInputStream(InputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public int read() throws IOException {
        synchronized (stream) {
            return stream.read();
        }
    }
    
    @Override
    public int read(byte @NotNull [] bytes) throws IOException {
        synchronized (stream) {
            return stream.read(bytes);
        }
    }
    
    @Override
    public int read(byte @NotNull [] b, int offset, int length) throws IOException {
        synchronized (stream) {
            return stream.read(b, offset, length);
        }
    }
    
    @Override
    public byte[] readAllBytes() throws IOException {
        synchronized (stream) {
            return stream.readAllBytes();
        }
    }
    
    @Override
    public byte[] readNBytes(int len) throws IOException {
        synchronized (stream) {
            return stream.readNBytes(len);
        }
    }
    
    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        synchronized (stream) {
            return super.readNBytes(b, off, len);
        }
    }
    
    @Override
    public long skip(long n) throws IOException {
        synchronized (stream) {
            return super.skip(n);
        }
    }
    
    @Override
    public void skipNBytes(long n) throws IOException {
        synchronized (stream) {
            super.skipNBytes(n);
        }
    }
    
    @Override
    public int available() throws IOException {
        synchronized (stream) {
            return super.available();
        }
    }
    
    @Override
    public void close() throws IOException {
        this.stream.close();
    }
    
    @NotNull
    @Override
    public Iterator<Byte> iterator() {
        return new LazyByteIterator(stream);
    }
}
