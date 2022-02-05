package mx.kenzie.jupiter.stream;

import mx.kenzie.jupiter.reader.StreamReader;

import java.io.InputStream;
import java.io.OutputStream;

public interface Stream extends AutoCloseable {
    
    static InputStreamController controller(InputStream stream) {
        return new InputStreamController(stream);
    }
    
    static OutputStreamController controller(OutputStream stream) {
        return new OutputStreamController(stream);
    }
    
    static SplitStream split(OutputStream... streams) {
        return new SplitStream(streams);
    }
    
    static StreamReader reader(InputStream stream) {
        return new StreamReader(stream);
    }
    
    static InputStream synchronize(InputStream stream) {
        return new SynchronizedInputStream(stream);
    }
    
    static OutputStream synchronize(OutputStream stream) {
        return new SynchronizedOutputStream(stream);
    }
    
    static InputStream keepalive(InputStream stream) {
        return new NoCloseInputStream(stream);
    }
    
    static OutputStream keepalive(OutputStream stream) {
        return new NoCloseOutputStream(stream);
    }
    
}
