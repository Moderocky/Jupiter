package mx.kenzie.jupiter.stream;

import mx.kenzie.jupiter.iterator.LazyIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;

class LazyCharIterator implements LazyIterator<Character> {
    protected final BufferedReader reader;
    protected int next;
    protected boolean read;
    
    LazyCharIterator(BufferedReader reader) {
        this.reader = reader;
    }
    
    @Override
    public boolean hasNext() {
        if (next == -1) return false;
        this.next = this.readSafely();
        this.read = true;
        return next != -1;
    }
    
    protected int readSafely() {
        try {
            return reader.read();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    
    @Override
    public Character next() {
        if (read) {
            this.read = false;
            return (char) next;
        } else return (char) this.readSafely();
    }
}
