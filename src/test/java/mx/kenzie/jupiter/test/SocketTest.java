package mx.kenzie.jupiter.test;

import mx.kenzie.jupiter.socket.SocketHub;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketTest {
    
    @Test
    public void hub() throws IOException {
        final SocketHub hub = new SocketHub(InetAddress.getLoopbackAddress(), 0);
        final int port = hub.getPort();
        final InetAddress address = hub.getAddress();
        final Socket first = SocketHub.connect(address, port);
        final Socket second = SocketHub.connect(address, port);
        assert first != second;
        hub.close();
    }
    
}
