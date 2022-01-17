package mx.kenzie.jupiter.stream;

import java.io.IOException;
import java.io.InputStream;

class NoCloseInputStream extends InputStream {
    
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
    
}
