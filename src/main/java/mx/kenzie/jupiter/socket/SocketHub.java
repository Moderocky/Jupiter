package mx.kenzie.jupiter.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SocketHub extends Thread implements AutoCloseable {
    
    public static final byte
        JOIN = 1,
        FAIL = 2,
        LEAVE = 3;
    
    protected final ServerSocket central;
    protected final List<Socket> list = Collections.synchronizedList(new ArrayList<>());
    
    public SocketHub(int port) throws IOException {
        this(new ServerSocket(), InetAddress.getLocalHost(), port);
    }
    
    public SocketHub(InetAddress address, int port) throws IOException {
        this(new ServerSocket(), address, port);
    }
    
    protected SocketHub(ServerSocket central, InetAddress address, int port) throws IOException {
        this.central = central;
        this.central.bind(new InetSocketAddress(address, port));
        this.start();
    }
    
    public int getPort() {
        return central.getLocalPort();
    }
    
    public InetAddress getAddress() {
        return central.getInetAddress();
    }
    
    public static Socket connect(InetAddress address, int port) throws IOException {
        try (final Socket linker = new Socket()) {
            linker.connect(new InetSocketAddress(address, port));
            linker.setKeepAlive(true);
            return linker;
        }
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                final Socket socket = central.accept();
                this.list.add(socket);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.stop();
        } catch (ThreadDeath ignored) {}
        this.central.close();
        for (final Socket socket : list) {
            socket.close();
        }
    }
    
    private static final ByteBuffer BUFFER = ByteBuffer.allocate(4);
    
    private static byte[] convert(int port) {
        synchronized (BUFFER) {
            BUFFER.putInt(0, port);
            return BUFFER.array();
        }
    }
    
    private static int convert(byte[] port) {
        synchronized (BUFFER) {
            BUFFER.put(port);
            return BUFFER.getInt();
        }
    }
    
}
