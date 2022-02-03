package mx.kenzie.jupiter.stream;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class InternalAccess {
    
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
    
    
    private static long getAddress(Object object) {
        final Object[] objects = new Object[]{object};
        final int offset = UNSAFE.arrayBaseOffset(objects.getClass());
        final int scale = UNSAFE.arrayIndexScale(objects.getClass());
        return switch (scale) {
            case 4 -> (UNSAFE.getInt(objects, offset) & 0xFFFFFFFFL) * 8;
            default -> throw new IllegalStateException("Unexpected value: " + scale);
        };
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
        
    }
    
}
