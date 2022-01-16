package mx.kenzie.jupiter.stream.impl;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

public abstract class NoThrowsOutputStream extends OutputStream {
    
    public void write(byte b) {
        this.write((int) b);
    }
    
    public abstract void write(int b);
    
    public abstract void write(byte @NotNull [] bytes);
    
    public abstract void write(byte @NotNull [] bytes, int offset, int length);
    
    public abstract void close();
    
}
