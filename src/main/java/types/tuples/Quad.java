/*
 *    Copyright 2022 Glenn Mamacos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
