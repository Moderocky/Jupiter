package mx.kenzie.jupiter.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class SplitStream extends OutputStream implements Stream {
    
    protected static final byte GROW_SIZE = 8;
    protected final byte grow;
    protected volatile OutputStream[] streams;
    
    protected SplitStream() {
        this.streams = new OutputStream[GROW_SIZE];
        this.grow = GROW_SIZE;
    }
    
    public SplitStream(byte grow) {
        this.streams = new OutputStream[grow];
        this.grow = grow;
    }
    
    public SplitStream(OutputStream stream) {
        this.streams = new OutputStream[]{stream};
        this.grow = GROW_SIZE;
    }
    
    public SplitStream(OutputStream stream, byte grow) {
        this.streams = new OutputStream[]{stream};
        this.grow = grow;
    }
    
    protected SplitStream(OutputStream... streams) {
        this.streams = Arrays.copyOf(streams, streams.length);
        this.grow = GROW_SIZE;
    }
    
    @Override
    public synchronized void write(int b) throws IOException {
        for (final OutputStream stream : streams) {
            if (stream != null) stream.write(b);
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        for (final OutputStream stream : streams) {
            if (stream != null) stream.close();
        }
    }
    
    public SplitStream fork(OutputStream stream) {
        this.add(stream);
        return this;
    }
    
    protected synchronized void add(OutputStream stream) {
        if (streams.length < 1) streams = new OutputStream[]{stream};
        else {
            int i;
            for (i = 0; i < streams.length; i++) {
                final OutputStream available = streams[i];
                if (available == stream) return;
                if (available != null) continue;
                this.streams[i] = stream;
                return;
            }
            this.streams = Arrays.copyOf(streams, streams.length + grow);
            this.streams[i] = stream;
        }
    }
}
