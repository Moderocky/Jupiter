package mx.kenzie.jupiter.memory;

import mx.kenzie.jupiter.stream.InternalAccess;

public record HeapPointer(long address, long length)
    implements InternalAccess.AccessUnsafe {
    
    public HeapPointer(long length) {
        this(InternalAccess.AccessUnsafe.allocate(length), length);
    }
    
}
