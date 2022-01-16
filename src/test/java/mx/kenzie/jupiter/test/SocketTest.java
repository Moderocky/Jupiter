package mx.kenzie.jupiter.test;

import mx.kenzie.jupiter.socket.SocketHub;
import mx.kenzie.jupiter.socket.SocketPair;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketTest {
    
    @Test
    public void hub() throws IOException {
        try (final SocketHub hub = new SocketHub(InetAddress.getLoopbackAddress(), 0, socket -> socket.getOutputStream()
            .write(6))) {
            assert !hub.isClosed();
            final int port = hub.getPort();
            final InetAddress address = hub.getAddress();
            final Socket first = SocketHub.connect(address, port);
            assert first.getInputStream().read() == 6;
            final Socket second = SocketHub.connect(address, port);
            assert second.getInputStream().read() == 6;
            assert first != second;
        }
    }
    
    @Test
    public void pair() throws IOException {
        final SocketPair pair = SocketPair.create()
            .onOpen(socket -> socket.getOutputStream().write(7))
            .host(1302).target(1301).build();
        final SocketPair second = SocketPair.create()
            .onOpen(socket -> socket.getOutputStream().write(7))
            .host(1301).target(1302).build();
        pair.awaitReady();
        assert pair.getConnection().getInputStream().read() == 7;
        assert second.getConnection().getInputStream().read() == 7;
    }
    
}
