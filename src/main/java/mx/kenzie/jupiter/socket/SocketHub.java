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
    private static final ByteBuffer BUFFER = ByteBuffer.allocate(4);
    protected final ServerSocket central;
    protected final List<Socket> list = Collections.synchronizedList(new ArrayList<>());
    protected SocketOpeningProcess process;
    
    public SocketHub(int port) throws IOException {
        this(new ServerSocket(), InetAddress.getLocalHost(), port, null);
    }
    
    protected SocketHub(ServerSocket central, InetAddress address, int port, SocketOpeningProcess process) throws IOException {
        this.central = central;
        this.central.bind(new InetSocketAddress(address, port));
        this.process = process;
        this.start();
    }
    
    public SocketHub(int port, SocketOpeningProcess process) throws IOException {
        this(new ServerSocket(), InetAddress.getLocalHost(), port, process);
    }
    
    public SocketHub(InetAddress address, int port) throws IOException {
        this(new ServerSocket(), address, port, null);
    }
    
    public SocketHub(InetAddress address, int port, SocketOpeningProcess process) throws IOException {
        this(new ServerSocket(), address, port, process);
    }
    
    public static Socket connect(InetAddress address, int port) throws IOException {
        final Socket linker = new Socket();
        linker.connect(new InetSocketAddress(address, port));
        linker.setKeepAlive(true);
        return linker;
    }
    
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
    
    public boolean isClosed() {
        return central.isClosed();
    }
    
    public int getPort() {
        return central.getLocalPort();
    }
    
    public InetAddress getAddress() {
        return central.getInetAddress();
    }
    
    @Override
    public void run() {
        while (!central.isClosed()) {
            try {
                synchronized (central) {
                    final Socket socket = central.accept();
                    this.list.add(socket);
                    if (process != null) process.open(socket);
                }
            } catch (IOException ignore) {
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.central.close();
        for (final Socket socket : list) {
            socket.close();
        }
    }
    
}
