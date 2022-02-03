package mx.kenzie.jupiter.memory;

import mx.kenzie.jupiter.stream.InternalAccess;

public class ObjectPointer
    implements InternalAccess.AccessUnsafe, AutoCloseable, Pointer {
    
    protected final long offset;
    protected Object handle;
    protected long length;
    
    public ObjectPointer(Object object) {
        this.handle = object;
        this.length = this.getSize(object);
        if (object.getClass().isArray())
            this.offset = this.getUnsafe().arrayBaseOffset(object.getClass());
        else this.offset = 12;
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
