package mx.kenzie.jupiter.stream;

import mx.kenzie.jupiter.iterator.IterableInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
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
        return new LazyByteIterator(stream);
    }
}
