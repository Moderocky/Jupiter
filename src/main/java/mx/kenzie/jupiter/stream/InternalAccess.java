package mx.kenzie.jupiter.stream;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class InternalAccess {
    
    private static final Unsafe UNSAFE;
    
    static {
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static long getSize(Object object) {
        final Class<?> type = object.getClass();
        return getSize(type);
    }
    
    private static long getSize(Class<?> type) {
        if (type == null || type == Object.class) return 0;
        long size = 0;
        for (final Field field : type.getDeclaredFields()) {
            if (field.getType() == byte.class) size += 1;
            else if (field.getType() == boolean.class) size += 1;
            else if (field.getType() == short.class) size += 2;
            else if (field.getType() == char.class) size += 2;
            else if (field.getType() == int.class) size += 4;
            else if (field.getType() == float.class) size += 4;
            else if (field.getType() == long.class) size += 8;
            else if (field.getType() == double.class) size += 8;
            else size += 4;
        }
        size += getSize(type.getSuperclass());
        return size;
    }
    
    private static long getAddress(Object object) {
        final Object[] objects = new Object[]{object};
        final int offset = UNSAFE.arrayBaseOffset(objects.getClass());
        final int scale = UNSAFE.arrayIndexScale(objects.getClass());
        assert scale == 4;
        return (UNSAFE.getInt(objects, offset) & 0xFFFFFFFFL) * 8;
    }
    
    public interface AccessUnsafe {
        
        static long allocate(long length) {
            return UNSAFE.allocateMemory(length);
        }
        
        default Unsafe getUnsafe() {
            return UNSAFE;
        }
        
        default long getAddress(Object object) {
            return InternalAccess.getAddress(object);
        }
        
        default long getSize(Object object) {
            return InternalAccess.getSize(object);
        }
        
    }
    
}
