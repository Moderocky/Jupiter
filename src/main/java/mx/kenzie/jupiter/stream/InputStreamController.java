package mx.kenzie.jupiter.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class InputStreamController extends InputStream implements StreamController {
    
    protected final InputStream stream;
    private final ByteBuffer converter = ByteBuffer.allocate(8);
    private final byte[] charBytes = new byte[2];
    private final byte[] intBytes = new byte[4];
    
    protected InputStreamController(InputStream stream) {
        this.stream = stream;
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
    
    public int readInt() throws IOException {
        synchronized (converter) {
            this.stream.read(intBytes);
            this.converter.put(0, intBytes);
            return converter.getInt(0);
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
