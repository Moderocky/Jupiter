package mx.kenzie.jupiter.socket;

import java.io.IOException;
import java.net.Socket;

public interface SocketOpeningProcess {
    
    void open(Socket socket) throws IOException;
    
}
