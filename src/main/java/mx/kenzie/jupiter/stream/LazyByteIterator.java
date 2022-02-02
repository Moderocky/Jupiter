package mx.kenzie.jupiter.stream;

import mx.kenzie.jupiter.iterator.LazyIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

class LazyByteIterator implements LazyIterator<Byte> {
    protected final InputStream stream;
    protected int next;
    protected boolean read;
    
    LazyByteIterator(InputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public boolean hasNext() {
        if (next == -1) return false;
        this.next = this.readSafely();
        this.read = true;
        return next != -1;
    }
    
    protected byte readSafely() {
        try {
            return (byte) stream.read();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    
    @Override
    public Byte next() {
        if (read) {
            this.read = false;
            return (byte) next;
        } else return this.readSafely();
    }
}
