package mx.kenzie.jupiter.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketPair extends Thread implements AutoCloseable {
    
    protected final Object lock = new Object();
    protected Socket target;
    protected ServerSocket host;
    protected SocketOpeningProcess process;
    
    protected SocketPair() {
    
    }
    
    public static Builder host(int port) {
        final Builder builder = new Builder();
        builder.hostPort = port;
        return builder;
    }
    
    public static Builder create() {
        return new Builder();
    }
    
    public Socket getConnection() {
        return target;
    }
    
    public boolean isHosting() {
        return host != null;
    }
    
    public void awaitReady() {
        while (true) {
            if (target != null && target.isConnected()) return;
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.target.close();
        this.host.close();
    }
    
    @Override
    public void run() {
        try {
            final Socket socket = host.accept();
            synchronized (this) {
                this.target = socket;
                if (this.process != null) process.open(socket);
                synchronized (this.lock) {
                    this.lock.notify();
                }
            }
        } catch (IOException ignore) {
        }
    }
    
    public static class Builder {
        
        int hostPort;
        int targetPort;
        InetAddress targetAddress = InetAddress.getLoopbackAddress();
        SocketOpeningProcess process;
        
        Builder() {
        }
        
        public Builder host(int port) {
            this.hostPort = port;
            return this;
        }
        
        public Builder target(int port) {
            this.targetPort = port;
            return this;
        }
        
        public Builder target(InetAddress address, int port) {
            this.targetAddress = address;
            this.targetPort = port;
            return this;
        }
        
        public Builder onOpen(SocketOpeningProcess process) {
            this.process = process;
            return this;
        }
        
        public SocketPair build() throws IOException {
            final SocketPair pair = new SocketPair();
            check:
            if (targetPort > 0) {
                try {
                    pair.target = new Socket();
                    pair.process = process;
                    pair.target.connect(new InetSocketAddress(targetAddress, targetPort));
                    pair.process.open(pair.target);
                    synchronized (pair.lock) {
                        pair.lock.notify();
                    }
                    break check;
                } catch (IOException ex) { // don't want to send a connection if not the host
                    pair.target.close();
                }
                pair.host = new ServerSocket(hostPort);
                pair.start();
            }
            return pair;
        }
        
    }
    
}
