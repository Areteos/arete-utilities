package types.tuples;

import java.util.Iterator;
import java.util.NoSuchElementException;

public record Pair<A, B>(A first, B second) implements Tuple {
    public static final Pair<?, ?> NULL = new Pair<>(null, null);

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < 2;
            }

            @Override
            public Object next() {
                return switch (index++) {
                    case 0 -> first();
                    case 1 -> second();
                    default -> throw new NoSuchElementException("Only 2 elements in a Pair");
                };
            }
        };
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(first, second);
    }
}
