package mx.kenzie.jupiter.stream;

import mx.kenzie.jupiter.iterator.IterableInputStream;
import mx.kenzie.jupiter.iterator.LazyIterator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;

class NoCloseInputStream extends InputStream implements IterableInputStream {
    
    protected final InputStream stream;
    
    protected NoCloseInputStream(InputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public int read() throws IOException {
        return stream.read();
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @NotNull
    @Override
    public Iterator<Byte> iterator() {
        return new ByteIterator();
    }
    
    class ByteIterator implements LazyIterator<Byte> {
        protected int next;
        protected boolean read;
        
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
}
