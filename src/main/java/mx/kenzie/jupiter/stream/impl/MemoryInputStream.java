package mx.kenzie.jupiter.stream.impl;

import mx.kenzie.jupiter.iterator.IterableInputStream;
import mx.kenzie.jupiter.memory.Pointer;
import mx.kenzie.jupiter.stream.InternalAccess;
import mx.kenzie.jupiter.stream.LazyByteIterator;
import mx.kenzie.jupiter.stream.Stream;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class MemoryInputStream extends InputStream implements InternalAccess.AccessUnsafe, Stream, IterableInputStream {
    
    protected final Unsafe unsafe = this.getUnsafe();
    protected long address;
    protected long length;
    protected long pointer;
    protected long mark;
    
    public MemoryInputStream(Pointer pointer) {
        this(pointer.address(), pointer.length());
    }
    
    public MemoryInputStream(long address, long length) {
        this.address = address;
        this.length = length;
    }
    
    @Override
    public int read() {
        if (pointer >= length) return -1;
        return unsafe.getByte(address + pointer++);
    }
    
    @Override
    public int read(byte @NotNull [] bytes) {
        final int amount = (int) Math.min(bytes.length, this.remaining());
        final long target = this.getAddress(bytes) + unsafe.arrayBaseOffset(byte[].class);
        this.unsafe.copyMemory(address + pointer, target, amount);
        return amount;
    }
    
    @Override
    public int read(byte @NotNull [] bytes, int offset, int length) {
        final int amount = (int) Math.min(length, this.remaining());
        final long target = this.getAddress(bytes) + unsafe.arrayBaseOffset(byte[].class) + offset;
        this.unsafe.copyMemory(address + pointer, target, amount);
        return amount;
    }
    
    @Override
    public byte[] readAllBytes() {
        final byte[] bytes = new byte[(int) this.remaining()];
        final long target = this.getAddress(bytes) + unsafe.arrayBaseOffset(byte[].class);
        final int amount = bytes.length; // truncated
        this.unsafe.copyMemory(address + pointer, target, amount);
        return bytes;
    }
    
    @Override
    public void close() {
        this.unsafe.freeMemory(address);
    }
    
    @Override
    public synchronized void mark(int limit) {
        this.mark = pointer;
    }
    
    @Override
    public synchronized void reset() {
        this.pointer = mark;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public long transferTo(OutputStream out) throws IOException {
        if (!(out instanceof MemoryOutputStream stream)) return super.transferTo(out);
        final long source, target;
        source = address + pointer;
        target = stream.address + stream.pointer;
        final long amount = Math.min(this.remaining(), stream.remaining());
        this.unsafe.copyMemory(source, target, amount);
        this.pointer = length; // skip rest
        return amount;
    }
    
    protected long remaining() {
        return length - pointer;
    }
    
    @NotNull
    @Override
    public Iterator<Byte> iterator() {
        return new LazyByteIterator(this);
    }
    
    public long getAddress() {
        return address;
    }
    
    public long getLength() {
        return length;
    }
    
}
