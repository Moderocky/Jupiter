package mx.kenzie.jupiter.stream.impl;

import mx.kenzie.jupiter.iterator.IterableInputStream;
import mx.kenzie.jupiter.memory.HeapPointer;
import mx.kenzie.jupiter.stream.InternalAccess;
import mx.kenzie.jupiter.stream.LazyByteIterator;
import mx.kenzie.jupiter.stream.Stream;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.io.InputStream;
import java.util.Iterator;

public class MemoryInputStream extends InputStream implements InternalAccess.AccessUnsafe, Stream, IterableInputStream {
    
    protected final Unsafe unsafe = this.getUnsafe();
    protected long address;
    protected long length;
    protected long pointer;
    
    public MemoryInputStream(HeapPointer pointer) {
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
