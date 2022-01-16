package mx.kenzie.jupiter.test;

import mx.kenzie.jupiter.stream.Stream;
import mx.kenzie.jupiter.stream.impl.StringBuilderOutputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    
}
