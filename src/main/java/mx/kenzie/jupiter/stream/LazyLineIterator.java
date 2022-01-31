package mx.kenzie.jupiter.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

class LazyLineIterator implements Iterator<String> {
    
    protected final BufferedReader reader;
    protected String next;
    
    LazyLineIterator(BufferedReader reader) {
        this.reader = reader;
    }
    
    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        } else {
            try {
                this.next = reader.readLine();
                return (next != null);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
    
    @Override
    public String next() {
        if (next != null || this.hasNext()) {
            final String line = next;
            this.next = null;
            return line;
        } else {
            throw new NoSuchElementException();
        }
    }
}
