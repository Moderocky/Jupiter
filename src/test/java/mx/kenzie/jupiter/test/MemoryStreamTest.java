package mx.kenzie.jupiter.test;

import mx.kenzie.jupiter.stream.impl.MemoryInputStream;
import mx.kenzie.jupiter.stream.impl.MemoryOutputStream;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MemoryStreamTest {
    
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
    
}
