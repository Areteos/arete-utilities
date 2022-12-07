package types.tuples;

import java.util.Iterator;

public interface Tuple extends Iterable<Object> {
    @Override
    Iterator<Object> iterator();

    @Override
    String toString();
}
