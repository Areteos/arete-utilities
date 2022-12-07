package functions;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IterableFunctionsTest {


    @Test
    void stitched() {
        class testClass {
            public static <T> Iterator<T> iteratorTestMethod(T first, T second) {
                return IterableFunctions.stitched(List.of(first).iterator(), List.of(second).iterator());
            }

            public static <T> Iterable<T> iterableTestMethod(T first, T second) {
                return IterableFunctions.stitched(List.of(first), List.of(second));
            }

        }

        Iterator<String> heapPollutionTestIterator = testClass.iteratorTestMethod("Thing1", "Thing2");
        while (heapPollutionTestIterator.hasNext()) {
            String testString = heapPollutionTestIterator.next();
            assertEquals(String.class, testString.getClass());
        }

        Iterable<String> heapPollutionTestIterable = testClass.iterableTestMethod("Thing1", "Thing2");
        for (String string : heapPollutionTestIterable) {
            assertEquals(String.class, string.getClass());
        }



        List<Integer>
                firstList = List.of(1, 2, 3, 4, 5),
                secondList = List.of(10, 20, 30, 40),
                thirdList = List.of(100, 200, 300, 400),
                fourthList = List.of(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000),
                expectedResultList = List.of(1, 10, 100, 1000, 2, 20, 200, 2000, 3, 30, 300, 3000, 4, 40, 400, 4000, 5),
                actualResults = new ArrayList<>();

        Iterator<Integer> iterator = IterableFunctions.stitched(firstList.iterator(), secondList.iterator(), thirdList.iterator(), fourthList.iterator());
        while (iterator.hasNext()) {
            actualResults.add(iterator.next());
        }

        assertIterableEquals(expectedResultList, actualResults);

        Iterable<Integer> iterable = IterableFunctions.stitched(firstList, secondList, thirdList, fourthList);
        assertIterableEquals(expectedResultList, iterable);
        assertIterableEquals(expectedResultList, iterable); // Second test to confirm no iterator weirdness
    }

    @Test
    void getArithmeticMean() {
        assertEquals(0, IterableFunctions.getArithmeticMean(List.of(0d,0d,0d,0d)).orElseThrow());
        assertEquals(1, IterableFunctions.getArithmeticMean(List.of(-2d,-1d,1d,2d, 5d)).orElseThrow());
        assertEquals(-2.5, IterableFunctions.getArithmeticMean(List.of(-1d,-2d,-3d,-4d)).orElseThrow());
        assertEquals(2.5, IterableFunctions.getArithmeticMean(List.of(1d,2d,3d,4d)).orElseThrow());
        assertEquals(Optional.empty(), IterableFunctions.getArithmeticMean(List.of()));
    }
}