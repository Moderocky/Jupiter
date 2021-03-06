package mx.kenzie.jupiter.memory;

import mx.kenzie.jupiter.stream.InternalAccess;

public class HeapPointer
    implements InternalAccess.AccessUnsafe, AutoCloseable, Pointer {
    
    protected final long offset = this.getUnsafe().arrayBaseOffset(byte[].class);
    protected byte[] handle;
    protected int length;
    
    protected HeapPointer(byte[] handle) {
        this.handle = handle;
        this.length = handle.length;
    }
    
    @Override
    public void close() {
        this.handle = null;
        this.length = 0;
    }
    
    @Override
    public long length() {
        return length;
    }
    
    @Override
    public long address() {
        return this.getAddress(handle) + offset;
    }
}
