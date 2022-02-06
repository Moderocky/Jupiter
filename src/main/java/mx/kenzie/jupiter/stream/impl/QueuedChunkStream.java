package mx.kenzie.jupiter.stream.impl;

import mx.kenzie.jupiter.iterator.LazyIterator;
import mx.kenzie.jupiter.stream.ChunkStream;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

public class QueuedChunkStream<Element> extends ChunkStream<Element> {
    
    protected final Deque<Element> queue;
    protected final AtomicLong counter;
    protected volatile boolean closed;
    
    public QueuedChunkStream() {
        this.queue = new LinkedBlockingDeque<>();
        this.counter = new AtomicLong();
    }
    
    @Override
    public void feed(Element element) {
        this.queue.addLast(element);
    }
    
    @Override
    public Element[] readAllRemaining(Element[] array) {
        final List<Element> list = new ArrayList<>();
        for (final Element element : this) list.add(element);
        return list.toArray(array);
    }
    
    @Override
    public @NotNull Iterator<Element> iterator() {
        return new LazyIterator<>() {
            @Override
            public boolean hasNext() {
                return !closed || counter.get() + 1 < queue.size();
            }
            
            @Override
            public Element next() {
                return read();
            }
        };
    }
    
    @Override
    public boolean canRead() {
        return !this.queue.isEmpty();
    }
    
    @Override
    public Element read() {
        final Element element = this.queue.getFirst();
        this.counter.getAndIncrement();
        return element;
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
