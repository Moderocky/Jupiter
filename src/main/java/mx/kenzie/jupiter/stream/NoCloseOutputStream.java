package mx.kenzie.jupiter.stream;

import java.io.IOException;
import java.io.OutputStream;

class NoCloseOutputStream extends OutputStream {
    
    protected final OutputStream stream;
    
    protected NoCloseOutputStream(OutputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
    }
    
    @Override
    public void close() {
    }
}
