package mx.kenzie.jupiter.stream;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Iterator;

@SuppressWarnings("unused")
public class InputStreamController extends InputStream implements StreamController {
    
    protected final InputStream stream;
    private final ByteBuffer converter = ByteBuffer.allocate(8);
    private final byte[] charBytes = new byte[2];
    private final byte[] intBytes = new byte[4];
    private final byte[] longBytes = new byte[8];
    
    protected InputStreamController(InputStream stream) {
        this.stream = stream;
    }
    
    public Iterable<String> lines() {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final Iterator<String> iterator = new LazyLineIterator(reader);
        return () -> iterator;
    }
    
    @Override
    public int read() throws IOException {
        return stream.read();
    }
    
    public String read(int chars) throws IOException {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars; i++) builder.append(this.readChar());
        return builder.toString();
    }
    
    public char readChar() throws IOException {
        synchronized (converter) {
            this.stream.read(charBytes);
            this.converter.put(0, charBytes);
            return converter.getChar(0);
        }
    }
    
    public short readShort() throws IOException {
        synchronized (converter) {
            this.stream.read(charBytes);
            this.converter.put(0, charBytes);
            return converter.getShort(0);
        }
    }
    
    public int readInt() throws IOException {
        synchronized (converter) {
            this.stream.read(intBytes);
            this.converter.put(0, intBytes);
            return converter.getInt(0);
        }
    }
    
    public float readFloat() throws IOException {
        synchronized (converter) {
            this.stream.read(intBytes);
            this.converter.put(0, intBytes);
            return converter.getFloat(0);
        }
    }
    
    public long readLong() throws IOException {
        synchronized (converter) {
            this.stream.read(longBytes);
            this.converter.put(0, longBytes);
            return converter.getLong(0);
        }
    }
    
    public double readDouble() throws IOException {
        synchronized (converter) {
            this.stream.read(longBytes);
            this.converter.put(0, longBytes);
            return converter.getDouble(0);
        }
    }
    
    public int stream(OutputStream target) throws IOException {
        int count = 0;
        synchronized (stream) {
            int data;
            while ((data = stream.read()) > -1) {
                target.write(data);
                count++;
            }
        }
        return count;
    }
    
}
