package mx.kenzie.jupiter.stream;

import java.io.OutputStream;

public interface Stream extends AutoCloseable {
    
    static SplitStream split(OutputStream... streams) {
        return new SplitStream(streams);
    }

}
