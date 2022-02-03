package mx.kenzie.jupiter.memory;

import mx.kenzie.jupiter.stream.InternalAccess;

import java.util.Objects;

public class NativePointer
    implements InternalAccess.AccessUnsafe, AutoCloseable, Pointer {
    protected final long address;
    protected final long length;
    
    public NativePointer(long length) {
        this(InternalAccess.AccessUnsafe.allocate(length), length);
    }
    
    public NativePointer(long address, long length) {
        this.address = address;
        this.length = length;
    }
    
    public NativePointer resize(long length) {
        final long target = this.getUnsafe().reallocateMemory(address, length);
        return new NativePointer(target, length);
    }
    
    @Override
    public void close() {
        this.getUnsafe().freeMemory(address);
    }
    
    public long length() {
        return length;
    }
    
    public long address() {
        return address;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(address, length);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NativePointer) obj;
        return this.address == that.address &&
            this.length == that.length;
    }
    
    @Override
    public String toString() {
        return "NativePointer[" +
            "address=" + address + ", " +
            "length=" + length + ']';
    }
    
}
