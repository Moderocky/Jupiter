package mx.kenzie.jupiter.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public class OutputStreamController extends OutputStream implements StreamController {
    
    protected final OutputStream stream;
    private final ByteBuffer converter = ByteBuffer.allocate(8);
    private final byte[] charBytes = new byte[2];
    private final byte[] intBytes = new byte[4];
    private final byte[] longBytes = new byte[8];
    
    protected OutputStreamController(OutputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
    }
    
    public void write(String string) throws IOException {
        for (final char c : string.toCharArray()) {
            this.writeChar(c);
        }
    }
    
    public void writeChar(char c) throws IOException {
        synchronized (converter) {
            this.converter.putChar(0, c);
            this.converter.get(0, charBytes);
            this.stream.write(charBytes);
        }
    }
    
    public void writeShort(short s) throws IOException {
        synchronized (converter) {
            this.converter.putShort(0, s);
            this.converter.get(0, charBytes);
            this.stream.write(charBytes);
        }
    }
    
    public void writeInt(int i) throws IOException {
        synchronized (converter) {
            this.converter.putInt(0, i);
            this.converter.get(0, intBytes);
            this.stream.write(intBytes);
        }
    }
    
    public void writeFloat(float f) throws IOException {
        synchronized (converter) {
            this.converter.putFloat(0, f);
            this.converter.get(0, intBytes);
            this.stream.write(intBytes);
        }
    }
    
    public void writeLong(long l) throws IOException {
        synchronized (converter) {
            this.converter.putLong(0, l);
            this.converter.get(0, longBytes);
            this.stream.write(longBytes);
        }
    }
    
    public void writeDouble(double d) throws IOException {
        synchronized (converter) {
            this.converter.putDouble(0, d);
            this.converter.get(0, longBytes);
            this.stream.write(longBytes);
        }
    }
    
    public int stream(InputStream target) throws IOException {
        int count = 0;
        synchronized (stream) {
            int data;
            while ((data = target.read()) > -1) {
                stream.write(data);
                count++;
            }
        }
        return count;
    }
    
}
