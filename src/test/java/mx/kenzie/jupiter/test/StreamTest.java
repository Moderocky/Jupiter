package mx.kenzie.jupiter.test;

import mx.kenzie.jupiter.stream.InputStreamController;
import mx.kenzie.jupiter.stream.OutputStreamController;
import mx.kenzie.jupiter.stream.Stream;
import mx.kenzie.jupiter.stream.impl.ByteBufferOutputStream;
import mx.kenzie.jupiter.stream.impl.NoThrowsOutputStream;
import mx.kenzie.jupiter.stream.impl.StringBuilderOutputStream;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StreamTest {
    
    @Test
    public void builderStream() throws IOException {
        final StringBuilder builder = new StringBuilder();
        try (final OutputStream stream = new StringBuilderOutputStream(builder)) {
            stream.write("hello".getBytes());
            stream.write(" there".getBytes());
        }
        assert builder.toString().equals("hello there");
    }
    
    @Test
    public void transferTo() throws IOException {
        final StringBuilder builder = new StringBuilder();
        try (final InputStream source = new ByteArrayInputStream("hello there".getBytes());
             final OutputStream stream = new StringBuilderOutputStream(builder)) {
            Stream.controller(source).stream(stream);
        }
        assert builder.toString().equals("hello there");
    }
    
    @Test
    public void transferFrom() throws IOException {
        final StringBuilder builder = new StringBuilder();
        try (final InputStream source = new ByteArrayInputStream("hello there".getBytes());
             final OutputStream stream = new StringBuilderOutputStream(builder)) {
            Stream.controller(stream).stream(source);
        }
        assert builder.toString().equals("hello there");
    }
    
    @Test
    public void fork() throws IOException {
        final StringBuilder builder = new StringBuilder(), second = new StringBuilder();
        try (final OutputStream a = new StringBuilderOutputStream(builder);
             final OutputStream b = new StringBuilderOutputStream(second)) {
            Stream.split(a, b).write("hello there".getBytes());
        }
        assert builder.toString().equals("hello there");
        assert second.toString().equals("hello there");
    }
    
    @Test
    public void buffer() {
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        try (final NoThrowsOutputStream stream = new ByteBufferOutputStream(buffer)) {
            stream.write(10);
            stream.write(40);
            stream.write(22);
            stream.write(new byte[]{2, 43});
        }
        assert buffer.get(0) == 10;
        assert buffer.get(1) == 40;
        assert buffer.get(2) == 22;
        assert buffer.get(3) == 2;
        assert buffer.get(4) == 43;
    }
    
    @Test
    public void preventClose() throws IOException {
        final ByteArrayOutputStream original = new ByteArrayOutputStream();
        try (final OutputStream stream = Stream.keepalive(original)) {
            stream.write(10);
            stream.write(20);
        }
        original.write(30);
        final byte[] bytes = original.toByteArray();
        assert bytes[0] == 10;
        assert bytes[1] == 20;
        assert bytes[2] == 30;
    }
    
    @Test
    public void controller() throws IOException {
        final ByteArrayOutputStream original = new ByteArrayOutputStream();
        try (final OutputStreamController stream = Stream.controller(original)) {
            stream.write(10);
            stream.write(20);
            stream.writeLong(1000000);
        }
        final ByteArrayInputStream target = new ByteArrayInputStream(original.toByteArray());
        try (final InputStreamController stream = Stream.controller(target)) {
            assert stream.read() == 10;
            assert stream.read() == 20;
            assert stream.readLong() == 1000000;
        }
    }
    
    @Test
    public void loop() throws IOException {
        final ByteArrayOutputStream target = new ByteArrayOutputStream();
        final ByteArrayInputStream original = new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8));
        try (final InputStreamController stream = Stream.controller(original)) {
            for (Byte b : stream) {
                target.write(b);
            }
        }
        assert target.toString(StandardCharsets.UTF_8).equals("hello there");
    }
    
    @Test
    public void lines() {
        final String test = """
            hello :)
            there :)
            test""";
        final ByteArrayInputStream original = new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8));
        int count = 0;
        for (final String line : Stream.controller(original).lines()) {
            assert line != null;
            assert !line.isBlank();
            count++;
        }
        assert count == 3;
    }
    
    @Test
    public void chars() {
        final ByteArrayInputStream stream = new ByteArrayInputStream("hello there".getBytes(StandardCharsets.UTF_8));
        int count = 0;
        for (final Character c : Stream.controller(stream).chars()) {
            count += c;
        }
        int check = 0;
        for (final char c : "hello there".toCharArray()) {
            check += c;
        }
        assert check == count;
    }
    
}
