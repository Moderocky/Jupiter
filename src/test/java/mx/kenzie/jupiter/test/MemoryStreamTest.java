package mx.kenzie.jupiter.test;

import mx.kenzie.jupiter.stream.InternalAccess;
import mx.kenzie.jupiter.stream.impl.MemoryInputStream;
import mx.kenzie.jupiter.stream.impl.MemoryOutputStream;
import org.junit.Test;
import sun.misc.Unsafe;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class MemoryStreamTest implements InternalAccess.AccessUnsafe {
    
    @Test
    public void simple() throws IOException {
        final String test = "hello there";
        final byte[] bytes = test.getBytes(StandardCharsets.UTF_8);
        final MemoryOutputStream stream = new MemoryOutputStream(bytes.length);
        for (final byte b : bytes) {
            stream.write(b);
        }
        final long address = stream.getAddress();
        final byte[] target = new byte[bytes.length];
        try (final InputStream input = new MemoryInputStream(address, bytes.length)) {
            for (int i = 0; i < target.length; i++) {
                target[i] = (byte) input.read();
            }
        }
        final String result = new String(target, StandardCharsets.UTF_8);
        assert result.equals(test);
    }
    
    @Test
    public void array() throws IOException {
        final String test = "hello there";
        final byte[] bytes = test.getBytes(StandardCharsets.UTF_8);
        final MemoryOutputStream stream = new MemoryOutputStream(bytes.length);
        stream.write(bytes);
        final long address = stream.getAddress();
        final byte[] target = new byte[bytes.length];
        try (final InputStream input = new MemoryInputStream(address, bytes.length)) {
            assert input.read(target) == target.length;
        }
        final String result = new String(target, StandardCharsets.UTF_8);
        assert result.equals(test);
    }
    
    @Test
    public void allBytes() throws IOException {
        final String test = "hello there";
        final byte[] bytes = test.getBytes(StandardCharsets.UTF_8);
        final MemoryOutputStream stream = new MemoryOutputStream(bytes.length);
        stream.write(bytes);
        final long address = stream.getAddress();
        final byte[] target;
        try (final InputStream input = new MemoryInputStream(address, bytes.length)) {
            target = input.readAllBytes();
        }
        final String result = new String(target, StandardCharsets.UTF_8);
        assert result.equals(test);
    }
    
    @Test
    public void dynamicReallocation() {
        final MemoryOutputStream stream = new MemoryOutputStream(8, true);
        assert stream.getLength() == 8;
        stream.write("hello there".getBytes(StandardCharsets.UTF_8));
        assert stream.getLength() != 8;
        stream.write("hello there".getBytes(StandardCharsets.UTF_8));
        assert stream.getLength() == 30;
    }
    
    @Test
    public void transfer() throws IOException {
        final String test = "hello there";
        final byte[] bytes = test.getBytes(StandardCharsets.UTF_8);
        final long address, holder;
        try (final MemoryOutputStream stream = new MemoryOutputStream(bytes.length)) {
            stream.write(bytes);
            address = stream.getAddress();
        }
        try (
            final MemoryInputStream stream = new MemoryInputStream(address, bytes.length);
            final MemoryOutputStream target = new MemoryOutputStream(bytes.length)
        ) {
            stream.transferTo(target);
            holder = target.getAddress();
        }
        try (final MemoryInputStream stream = new MemoryInputStream(holder, bytes.length)) {
            final byte[] result = stream.readAllBytes();
            assert new String(result, StandardCharsets.UTF_8).equals(test);
        }
    }
    
    @Test
    public void size() throws Throwable {
        final Unsafe unsafe = this.getUnsafe();
        final Field x = Thing.class.getDeclaredField("x");
        final Field y = Thing.class.getDeclaredField("y");
        final long a = unsafe.objectFieldOffset(x);
        final long b = unsafe.objectFieldOffset(y);
        assert b - a == 4;
    }
    
    private static class Thing {
        int x;
        Object y;
    }
    
}
