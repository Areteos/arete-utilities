package types.tuples;

import java.util.Iterator;
import java.util.NoSuchElementException;

public record Quad<A, B, C, D> (A first, B second, C third, D fourth) implements Tuple {
    @Override
    public Iterator<Object> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < 4;
            }

            @Override
            public Object next() {
                return switch (index++) {
                    case 0 -> first();
                    case 1 -> second();
                    case 2 -> third();
                    case 3 -> fourth();
                    default -> throw new NoSuchElementException("Only 3 elements in a Triple");
                };
            }
        };
    }

    @Override
    public String toString() {
        return "(%s, %s, %s, %s)".formatted(first, second, third, fourth);
    }
}
