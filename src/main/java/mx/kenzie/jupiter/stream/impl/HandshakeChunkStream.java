package mx.kenzie.jupiter.stream.impl;

import mx.kenzie.jupiter.stream.ChunkStream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HandshakeChunkStream<Element> extends ChunkStream<Element> {
    
    protected final AtomicReference<Element> reference;
    protected final Object lock = new Object();
    protected volatile boolean closed;
    protected volatile boolean read;
    
    public HandshakeChunkStream() {
        this.reference = new AtomicReference<>();
    }
    
    @Override
    public void feed(Element element) {
        boolean read;
        synchronized (this) {
            if (closed) return;
            read = this.read;
        }
        if (!read) synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ignore) {}
        }
        this.reference.set(element);
        if (read) synchronized (lock) {
            lock.notify();
        }
        synchronized (this) {
            this.read = false;
        }
    }
    
    @Override
    public Element read() {
        boolean read;
        synchronized (this) {
            if (closed) throw new IllegalStateException("Chunk-stream has been closed.");
            read = this.read;
        }
        if (read) synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ignore) {}
        }
        final Element element = reference.get();
        this.reference.set(null);
        synchronized (this) {
            this.read = true;
        }
        synchronized (lock) {
            lock.notify();
        }
        return element;
    }
    
    @Override
    public Element[] readAllRemaining(Element[] array) {
        final List<Element> list = new ArrayList<>();
        while (!this.isClosed()) list.add(this.read());
        return list.toArray(array);
    }
    
    @Override
    public synchronized boolean isClosed() {
        return closed;
    }
    
    @Override
    public synchronized void close() {
        this.closed = true;
    }
    
}
