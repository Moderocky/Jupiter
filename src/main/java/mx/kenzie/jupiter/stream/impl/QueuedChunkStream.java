package mx.kenzie.jupiter.stream.impl;

import mx.kenzie.jupiter.stream.ChunkStream;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class QueuedChunkStream<Element> extends ChunkStream<Element> {
    
    protected final Deque<Element> queue;
    protected volatile boolean closed;
    
    public QueuedChunkStream() {
        this.queue = new LinkedBlockingDeque<>();
    }
    
    @Override
    public void feed(Element element) {
        this.queue.addLast(element);
    }
    
    @Override
    public Element read() {
        return this.queue.getFirst();
    }
    
    @Override
    public Element[] readAllRemaining(Element[] array) {
        final List<Element> list = new ArrayList<>();
        for (final Element element : this) list.add(element);
        return list.toArray(array);
    }
    
    @Override
    public synchronized boolean isClosed() {
        return closed;
    }
    
    @Override
    public boolean canRead() {
        return !this.queue.isEmpty();
    }
    
    @Override
    public synchronized void close() {
        this.closed = true;
    }
}
