package mx.kenzie.jupiter.socket;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class SocketHub extends Thread implements AutoCloseable {
    
    private static final ByteBuffer BUFFER = ByteBuffer.allocate(4);
    protected final ServerSocket central;
    protected final List<Socket> list = Collections.synchronizedList(new ArrayList<>());
    protected SocketOpeningProcess process;
    
    public SocketHub(int port) throws IOException {
        this(new ServerSocket(), InetAddress.getLocalHost(), port, null);
    }
    
    protected SocketHub(ServerSocket central, InetAddress address, int port, SocketOpeningProcess process) throws IOException {
        this.central = central;
        if (!central.isBound()) this.central.bind(new InetSocketAddress(address, port));
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
    
    public static SocketHub create(InetAddress address, int port, SocketOpeningProcess process) throws IOException {
        return new SocketHub(address, port, process);
    }
    
    public static SocketHub createSecure(int port, SocketOpeningProcess process) throws IOException {
        final ServerSocket socket = SSLServerSocketFactory.getDefault().createServerSocket(port, 50);
        return new SocketHub(socket, socket.getInetAddress(), port, process);
    }
    
    public static Socket connectSecure(InetAddress address, int port) throws IOException {
        return connect(SSLSocketFactory.getDefault().createSocket(), address, port);
    }
    
    public static Socket connect(Socket linker, InetAddress address, int port) throws IOException {
        linker.connect(new InetSocketAddress(address, port));
        linker.setKeepAlive(true);
        return linker;
    }
    
    public static Socket connect(InetAddress address, int port) throws IOException {
        return connect(new Socket(), address, port);
    }
    
    private static byte[] convert(int port) {
        synchronized (BUFFER) {
            BUFFER.putInt(0, port);
            return BUFFER.array();
        }
    }
    
    private static int convert(byte[] port) {
        synchronized (BUFFER) {
            BUFFER.put(0, port);
            return BUFFER.getInt(0);
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
