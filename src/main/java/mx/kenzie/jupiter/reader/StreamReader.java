package mx.kenzie.jupiter.reader;

import mx.kenzie.jupiter.stream.LazyCharIterator;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Iterator;

public class StreamReader extends Reader implements Iterable<Character> {
    
    protected final InputStream stream;
    protected final BufferedReader reader;
    
    public StreamReader(InputStream stream) {
        this.stream = stream;
        this.reader = new BufferedReader(new InputStreamReader(stream));
    }
    
    @Override
    public int read(char @NotNull [] chars, int offset, int length) throws IOException {
        return reader.read(chars, offset, length);
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
        this.stream.close();
    }
    
    @NotNull
    @Override
    public Iterator<Character> iterator() {
        return new LazyCharIterator(reader);
    }
}
