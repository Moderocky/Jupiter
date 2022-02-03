package mx.kenzie.jupiter.stream.impl;

import mx.kenzie.jupiter.memory.Pointer;
import mx.kenzie.jupiter.stream.InternalAccess;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public class MemoryOutputStream extends NoThrowsOutputStream implements InternalAccess.AccessUnsafe {
    
    protected final Unsafe unsafe = this.getUnsafe();
    protected long address;
    protected long length;
    protected long pointer;
    protected boolean resize;
    
    public MemoryOutputStream() {
        this.length = 16;
        this.address = unsafe.allocateMemory(length);
        this.resize = true;
    }
    
    public MemoryOutputStream(long length) {
        this(length, false);
    }
    
    public MemoryOutputStream(long length, boolean resize) {
        this.length = length;
        this.address = unsafe.allocateMemory(length);
        this.resize = resize;
    }
    
    public MemoryOutputStream(Pointer pointer) {
        this(pointer, false);
    }
    
    public MemoryOutputStream(Pointer pointer, boolean resize) {
        this.address = pointer.address();
        this.length = pointer.length();
        this.resize = resize;
    }
    
    @Override
    public void write(int b) {
        if (pointer >= length) this.resize(16);
        final byte bi = (byte) b;
        this.unsafe.putByte(address + pointer++, bi);
    }
    
    @Override
    public void write(byte @NotNull [] bytes) {
        final long target = this.getAddress(bytes) + unsafe.arrayBaseOffset(byte[].class);
        if (!this.canWrite(bytes.length)) this.resize(bytes.length);
        this.unsafe.copyMemory(target, address + pointer, bytes.length);
        this.pointer += bytes.length;
    }
    
    @Override
    public void write(byte @NotNull [] bytes, int offset, int length) {
        final long target = this.getAddress(bytes) + unsafe.arrayBaseOffset(byte[].class) + offset;
        if (!this.canWrite(length)) this.resize(length);
        this.unsafe.copyMemory(target, address + pointer, length);
        this.pointer += length;
    }
    
    @Override
    public void close() {
        // closing does not free the memory
    }
    
    protected boolean canWrite(int amount) {
        return pointer + amount <= length;
    }
    
    protected void resize(int amount) {
        if (!resize) throw new IllegalStateException("Unable to resize this address.");
        this.address = this.unsafe.reallocateMemory(address, pointer + amount);
        this.length = length + amount;
    }
    
    public long getAddress() {
        return address;
    }
    
    public boolean canResize() {
        return resize;
    }
    
    public long getLength() {
        return length;
    }
    
    protected long remaining() {
        return length - pointer;
    }
}
