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
    
    public boolean isEmpty() throws IOException {
        this.mark(1);
        final int i = this.read();
        this.reset();
        return i == -1;
    }
    
    public char upcoming() throws IOException {
        this.mark(1);
        final int i = this.read();
        this.reset();
        return (char) i;
    }
    
    public String readUntil(char c, boolean escape) throws IOException {
        if (!escape) return this.readUntil(c);
        final StringBuilder builder = new StringBuilder();
        boolean skip = false;
        int i;
        while ((i = this.read()) != -1) {
            final char x = (char) i;
            builder.append(x);
            if (x == c && !skip) break;
            skip = x == '\\';
        }
        return builder.toString();
    }
    
    public String readUntil(char c) {
        final StringBuilder builder = new StringBuilder();
        for (final Character character : this) {
            if (character == c) break;
            builder.append(character);
        }
        return builder.toString();
    }
    
    public String readWord() throws IOException {
        final StringBuilder builder = new StringBuilder();
        this.mark(1);
        for (final Character character : this) {
            final char c = character;
            if (c >= 97 && c <= 122) builder.append(c);
            else if (c >= 65 && c <= 90) builder.append(c);
            else if (c >= 48 && c <= 57) builder.append(c);
            else if (c == 95) builder.append(c);
            else {
                this.reset();
                break;
            }
            this.mark(1);
        }
        return builder.toString();
    }
    
    public void skip() throws IOException {
        this.skip(1);
    }
    
    public String readWhitespace() throws IOException {
        final StringBuilder builder = new StringBuilder();
        this.mark(2);
        int i;
        while ((i = this.read()) != -1) {
            final char c = (char) i;
            if (c <= 32) builder.append(c);
            else if (c == 160) builder.append(c);
            else {
                this.reset();
                break;
            }
            this.mark(2);
        }
        return builder.toString();
    }
    
    public String readUntilWhitespace() throws IOException {
        final StringBuilder builder = new StringBuilder();
        this.mark(1);
        for (final Character character : this) {
            final char c = character;
            if (c > 32 && c != 160) builder.append(c);
            else {
                this.reset();
                break;
            }
            this.mark(1);
        }
        return builder.toString();
    }
    
    @Override
    public int read(char @NotNull [] chars, int offset, int length) throws IOException {
        return reader.read(chars, offset, length);
    }
    
    @Override
    public void mark(int readAheadLimit) throws IOException {
        this.reader.mark(readAheadLimit);
    }
    
    @Override
    public void reset() throws IOException {
        this.reader.reset();
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
