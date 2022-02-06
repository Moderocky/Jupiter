package mx.kenzie.jupiter.stream;

import mx.kenzie.jupiter.iterator.LazyIterator;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.Iterator;

/**
 * A semi-lazy stream-style bottleneck for passing elements
 * between an input and output process.
 * <p>
 * Some implementations may be buffered and thread-safe,
 * whereas others may be throttling and act as more of a
 * handshake system.
 * <p>
 * Unlike Java's native I/O streams, chunk streams are designed
 * to be closed from either end as a mark of them being "finished"
 * or, if closed from the receiver, to stop accepting more elements.
 */
public abstract class ChunkStream<Element> implements AutoCloseable, Closeable, Stream, Iterable<Element> {
    
    public abstract void feed(Element element);
    
    public abstract Element[] readAllRemaining(Element[] array);
    
    @NotNull
    @Override
    public Iterator<Element> iterator() {
        return new LazyIterator<>() {
            @Override
            public boolean hasNext() {
                return canRead();
            }
            
            @Override
            public Element next() {
                return read();
            }
        };
    }
    
    public boolean canRead() {
        return !this.isClosed();
    }
    
    public abstract Element read();
    
    public abstract boolean isClosed();
    
    @Override
    public abstract void close();
}
