package mx.kenzie.jupiter.stream;

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
    
}
