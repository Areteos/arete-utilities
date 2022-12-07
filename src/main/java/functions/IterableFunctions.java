package functions;

import types.tuples.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntFunction;

import static java.lang.Math.pow;

/**
 * A collection of useful static functions that provide alternate ways of iterating through iterables or using their
 * contents.
 */
@SuppressWarnings("unused")
public final class IterableFunctions {
    private IterableFunctions() {
    }

    /**
     * Sorts two equal-length lists simultaneously, using the natural ordering of one to arrange both. The value list
     * is the one whose natural ordering is considered, while each value in the companion list is treated as having the
     * same natural position as the value at the corresponding index in the value list. Note that generally speaking,
     * this function shouldn't be necessary. If there is some application that needs two lists of items correlated,
     * zipToList can often achieve that more conveniently.
     * @param valueList The list of values to base the sorting on
     * @param companionList A companion list of values to be sorted alongside
     * @param <T> The Type of the value list. Must be Comparable with itself
     * @param <E> The arbitrary Type of the companion list
     * @return The value and companion lists, both sorted according to the natural order of the value list
     */
    public static <T extends Comparable<T>, E> Pair<List<T>, List<E>> sortListsSimultaneously(List<T> valueList, List<E> companionList) {
        int n = valueList.size();
        if (n != companionList.size()) {
            throw new IllegalArgumentException("Mismatched list lengths");
        }
        Integer[] indexArray = new Integer[n];
        for (int i = 0; i < n; i++) {
            indexArray[i] = i;
        }
        Arrays.sort(indexArray, Comparator.comparing(valueList::get));

        List<T> sortedValues = new ArrayList<>(n);
        List<E> sortedCompanions = new ArrayList<>(n);

        for (Integer index : indexArray) {
            sortedValues.add(valueList.get(index));
            sortedCompanions.add(companionList.get(index));
        }

        return new Pair<>(sortedValues, sortedCompanions);
    }


    /**
     * A function to make iterating through lists in reverse convenient and compact, with minimal boilerplate.
     * @param original The list we want to be able to iterate over in reverse
     * @return An iterable that represents a reversed version of the original list
     */
    public static <T> Iterable<T> reversed(List<T> original) {
        return () -> {
            final List<T> finalCopy = List.copyOf(original);
            return new Iterator<>() {
                int index = finalCopy.size()-1;
                @Override
                public boolean hasNext() {
                    return index >= 0;
                }

                @Override
                public T next() {
                    if (hasNext()) {
                        return finalCopy.get(index--);
                    } else {
                        throw new NoSuchElementException();
                    }
                }
            };
        };
    }


    /**
     * <p>
     * Combines one or more iterators into a single iterator that returns items from the input iterators in cyclic
     * fashion. The input iterators are each queried once, in order, and then the process returns to the first iterator.
     * hasNext will return false as soon as the next iterator in queue would return false, meaning the resultant
     * iterator may not necessarily iterate through every item in all iterators.
     * </p>
     * <p>
     * NOTE that the iterators passed into this function are consumed, and should not be queried further in other
     * contexts unless taking account of the fact that they will be queried within the iterator output of this function.
     * </p>
     * @param first the first (required) iterator input
     * @param others any other iterators to stitch together with the first
     * @return an iterator that cycles through the input iterators with its next() method
     * @see IterableFunctions#stitched(Iterable, Iterable[])
     */
    @SafeVarargs
    public static <T> Iterator<T> stitched(Iterator<T> first, Iterator<T>... others) {
        final int numberOfIterators = others.length + 1;
        final List<Iterator<T>> iteratorList = new ArrayList<>(numberOfIterators);
        iteratorList.add(first);
        iteratorList.addAll(List.of(others));

        return new Iterator<>() {
            private int iteratorIndex = 0;

            @Override
            public boolean hasNext() {
                return iteratorList.get(iteratorIndex % numberOfIterators).hasNext();
            }

            @Override
            public T next() {
                return iteratorList.get(iteratorIndex++ % numberOfIterators).next();
            }
        };
    }

    /**
     * <p>
     * Combines one or more iterables into a single iterable that lists items from the input iterables in cyclic
     * fashion. The input iterables are each queried once, in order, and then the process returns to the first iterator.
     * hasNext will return false as soon as the next iterable in queue would return false, meaning the produced
     * iterators may not necessarily iterate through every item in all iterables.
     * </p>
     * @param first the first (required) iterable input
     * @param others any other iterables to stitch together with the first
     * @return an iterable that stitches together the values in the given iterables
     */
    @SafeVarargs
    public static <T> Iterable<T> stitched(Iterable<T> first, Iterable<T>... others) {
        return () -> stitched(first.iterator(), Arrays.stream(others).map(Iterable::iterator).toArray((IntFunction<Iterator<T>[]>) Iterator[]::new));
    }


    /**
     * Takes two not-necessarily-same-type Iterables, zips them together using the ListUtils.zipped() function, and then
     * creates a List of Pairs out of the resulting Iterable.
     * @see IterableFunctions#zipped
     */
    public static <T, E> List<Pair<T, E>> zipToList(Iterable<T> firstIterable, Iterable<E> secondIterable) {
        List<Pair<T, E>> list = new ArrayList<>();
        for (Pair<T, E> pair : zipped(firstIterable, secondIterable)) {
            list.add(pair);
        }
        return list;
    }

    /**
     * <p>
     * Takes two not-necessarily-same-type Iterables and returns an Iterable of Pairs of same-index values from the
     * two Iterables. E.g. zipping together two lists [1, 2, 3] and ["foo", "bar", "baz"] would give you an iterable
     * [(1, "foo"), (2, "bar"), (3, "baz")].
     * </p>
     * <p>
     * Note that if the given Iterables are of mismatched length, the resulting zipped Iterable will have the length of
     * the shorter
     * </p>
     * @param firstIterable the Iterable whose values will appear first in the resulting Pairs
     * @param secondIterable the Iterable whose values will appear second in the resulting Pairs
     * @return an Iterable of Pairs of corresponding values
     */
    public static <T, E> Iterable<Pair<T, E>> zipped(Iterable<T> firstIterable, Iterable<E> secondIterable) {
        return () -> new Iterator<>() {
            final Iterator<T> iteratorT = firstIterable.iterator();
            final Iterator<E> iteratorE = secondIterable.iterator();

            @Override
            public boolean hasNext() {
                return iteratorT.hasNext() && iteratorE.hasNext();
            }

            @Override
            public Pair<T, E> next() {
                return new Pair<>(iteratorT.next(), iteratorE.next());
            }
        };
    }

    /**
     * Given a zipped iterable (as produced by the zipped() function, for instance), unzip it into two separate lists.
     * @param zippedSet An iterable over a set of pairs
     * @param <T> The type of the first item in each pair
     * @param <E> The type of the second item in each pair
     * @return A pair of lists, the first corresponding to the first item in each pair in the input, the second to the
     * second. These lists will each contain every one of their respective pair items, in the same order as the input.
     * @see IterableFunctions#zipped(Iterable, Iterable)
     */
    public static <T, E> Pair<List<T>, List<E>> unzipped(Iterable<Pair<T, E>> zippedSet) {
        List<T> tList = new ArrayList<>();
        List<E> eList = new ArrayList<>();

        for (Pair<T, E> pair : zippedSet) {
            tList.add(pair.first());
            eList.add(pair.second());
        }
        return new Pair<>(tList, eList);
    }

    /**
     * A function that allows for easy two-at-a-time iteration over any Iterable. If the number of items in the input
     * Iterable is odd, the last Pair returned by generated Iterators will have an empty second value.
     * @param original The Iterable we want a two-at-a-time version of
     * @return An Iterable that returns pairs of consecutive values from the original Iterable. The second value in each
     * Pair is an Optional, since the second value would be null in the case of odd-sized input Iterables
     */
    public static <T> Iterable<Pair<T, Optional<T>>> twoAtATime(Iterable<T> original) {
        return () -> new Iterator<>() {
            private final Iterator<T> iterator = original.iterator();
            private Pair<T, Optional<T>> loadedPair;
            {
                loadNextPair();
            }

            private void loadNextPair() {
                if (iterator.hasNext()) {
                    T firstValue = iterator.next();
                    Optional<T> secondValue = iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
                    loadedPair = new Pair<>(firstValue, secondValue);
                } else {
                    loadedPair = null;
                }
            }

            @Override
            public boolean hasNext() {
                return loadedPair != null;
            }

            @Override
            public Pair<T, Optional<T>> next() {
                if (loadedPair == null) {
                    throw new NoSuchElementException();
                }

                Pair<T, Optional<T>> returnValue = loadedPair;
                loadNextPair();
                return returnValue;
            }
        };
    }

    /**
     * A function that allows easy iteration through every consecutive pair in an Iterable. Iterating through the returned
     * iterable will result in seeing every item in the original twice, except for the first and last values, which will
     * appear only once each. If the input has fewer than 2 items, will return an empty iterable: there are no pairs to
     * iterate through.
     * @param original The original Iterable
     * @param <T> The Type being iterated
     * @return an Iterable over every consecutive pair of items in the original
     */
    public static <T> Iterable<Pair<T, T>> inPairs(Iterable<T> original) {
        return () -> new Iterator<>() {
            private final Iterator<T> iterator = original.iterator();
            private Pair<T, T> loadedPair;
            {
                if (iterator.hasNext()) {
                    T firstValue = iterator.next();
                    if (iterator.hasNext()) {
                        loadedPair = new Pair<>(firstValue, iterator.next());
                    } else {
                        loadedPair = null;
                    }
                } else {
                    loadedPair = null;
                }
            }

            private void loadNextPair() {
                if (iterator.hasNext()) {
                    loadedPair = new Pair<>(loadedPair.second(), iterator.next());
                } else {
                    loadedPair = null;
                }
            }

            @Override
            public boolean hasNext() {
                return loadedPair != null;
            }

            @Override
            public Pair<T, T> next() {
                if (loadedPair == null) {
                    throw new NoSuchElementException();
                }

                Pair<T, T> returnValue = loadedPair;
                loadNextPair();
                return returnValue;
            }
        };
    }

    /**
     * Creates an Iterable that, when generating a new Iterator, immediately feeds the first item to the given Consumer,
     * and subsequently returns the second item on the first call to next(). Will usually immediately throw a
     * NoSuchElementException if given an empty Iterable. Intended for use with enhanced for loops.
     * @param original Iterable to take items from
     * @param whatToDoWithFirstItem A consumer for the first item
     * @return an Iterable identical to the input, except that it always immediately feeds its first item to the given
     * Consumer when it generates a new Iterator, and proceeds from the second item
     */
    public static <T> Iterable<T> butFirst(Iterable<T> original, Consumer<T> whatToDoWithFirstItem) {
        return () -> {
            Iterator<T> iterator = original.iterator();
            whatToDoWithFirstItem.accept(iterator.next());

            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public T next() {
                    return iterator.next();
                }
            };
        };
    }

    /**
     * A simple function for finding the sum of an Iterable.
     * @param numbers An Iterable of numbers to be summed
     * @return The sum of the numbers in the given Iterable
     */
    public static <T extends Number> double getSum(Iterable<T> numbers) {
        double sum = 0d;
        for (T number : numbers) {
            sum += number.doubleValue();
        }
        return sum;
    }

    /**
     * Simple brute-force iteration method to find the minimum of a set of numbers
     * @return An Optional number of the same Type as those given. Will be empty if the input is empty.
     */
    public static <T extends Number> Optional<T> getMinimum(Iterable<T> numbers) {
        T minimum = null;
        for (T number : numbers) {
            if (minimum == null) {
                minimum = number;
            } else if (number.doubleValue() < minimum.doubleValue()) {
                minimum = number;
            }
        }
        return Optional.ofNullable(minimum);
    }

    /**
     * Simple brute-force iteration method to find the maximum of a set of numbers
     * @return An Optional number of the same Type as those given. Will be empty if the input is empty.
     */
    public static <T extends Number> Optional<T> getMaximum(Iterable<T> numbers) {
        T maximum = null;
        for (T number : numbers) {
            if (maximum == null) {
                maximum = number;
            } else if (number.doubleValue() > maximum.doubleValue()) {
                maximum = number;
            }
        }
        return Optional.ofNullable(maximum);
    }

    /**
     * Two-at-a-time comparison method to find the minimum and maximum of a set of numbers simultaneously.
     * @return an Optional Pair with the minimum first and the maximum second. Will be empty if the input is empty.
     */
    public static <T extends Number> Optional<Pair<T, T>> getMinimumAndMaximum(Iterable<T> numbers) {
        T   minimum = null,
            maximum = null;

        for (Pair<T, Optional<T>> pair : twoAtATime(numbers)) {
            T first = pair.first();
            double doubleFirst = first.doubleValue();

            if (minimum == null) {
                minimum = first;
                maximum = first;
            }

            if (pair.second().isEmpty()) {
                if (doubleFirst < minimum.doubleValue()) {
                    minimum = pair.first();
                } else if (doubleFirst > maximum.doubleValue()) {
                    maximum = pair.first();
                }
            } else {

                T second = pair.second().orElseThrow();
                double doubleSecond = second.doubleValue();

                if (doubleFirst > doubleSecond) {
                    if (doubleFirst > maximum.doubleValue()) {
                        maximum = first;
                    }
                    if (doubleSecond < minimum.doubleValue()) {
                        minimum = second;
                    }
                } else {
                    if (doubleFirst < minimum.doubleValue()) {
                        minimum = first;
                    }
                    if (doubleSecond > maximum.doubleValue()) {
                        maximum = second;
                    }
                }
            }
        }

        return minimum == null ? Optional.empty() : Optional.of(new Pair<>(minimum, maximum));
    }


    /**
     * Calculates the arithmetic mean of a given Iterable of numbers.
     * @return an Optional Double. Will be empty if input is empty.
     */
    public static <T extends Number> Optional<Double> getArithmeticMean(Iterable<T> numbers) {
        double sum = 0d;
        int i = 0;
        for (T number : numbers) {
            sum += number.doubleValue();
            i++;
        }
        if (i==0) {
            return Optional.empty();
        }
        return Optional.of(sum / i);
    }

    /**
     * Calculates the product of a given Iterable of numbers.
     */
    public static <T extends Number> double getProduct(Iterable<T> numbers) {
        double product = 1;
        for (T number : numbers) {
            product *= number.doubleValue();
        }
        return product;
    }

    /**
     * Calculates the geometric mean of a given Iterable of numbers.
     * @return an Optional Double. Will be empty if input is empty.
     */
    public static <T extends Number> Optional<Double> getGeometricMean(LinkedList<T> numbers) {
        double product = 1;
        int i = 0;
        for (T number : numbers) {
            product *= number.doubleValue();
            i++;
        }
        if (i == 0) {
            return Optional.empty();
        }
        return Optional.of(product);
    }

    /**
     * Given a sample of values from a population, estimates the standard deviation of the entire population.
     * @return An Optional Double. Will be empty if the input is empty.
     */
    public static <T extends Number> Optional<Double> getSampleStandardDeviation(Iterable<T> numbers) {
        double sum = 0d;
        Optional<Double> optional = getArithmeticMean(numbers);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        double mean = optional.orElseThrow();
        int i = 0;
        for (T number : numbers) {
            sum += pow(number.doubleValue() - mean, 2);
            i++;
        }
        return Optional.of(Math.sqrt(sum / i));
    }

    /**
     * Given all values for an entire population, calculates the standard deviation of the population.
     * @return An Optional Double. Will be empty if the input is empty.
     */
    public static <T extends Number> Optional<Double> getPopulationStandardDeviation(List<T> numbers) {
        double sum = 0d;
        Optional<Double> optional = getArithmeticMean(numbers);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        double mean = optional.orElseThrow();
        for (T number : numbers) {
            sum += pow(number.doubleValue() - mean, 2);
        }
        return Optional.of(Math.sqrt(sum / (numbers.size() - 1)));
    }

    /**
     * Given a collection of values and a new minimum and maximum, linearly remaps the contents of the collection to a
     * list such that the new minimum and maximum are as given.
     * @param originalValues A collection of numbers to be remapped
     * @param newMinimum The new minimum for the List
     * @param newMaximum The new maximum for the List
     * @param <T> The Type of Number in the collection (Integer, Double, etc.)
     * @return A List of Doubles representing a linear remap of the original collection to the new minimum and maximum
     */
    public static <T extends Number> List<Double> mapLinearly(Collection<T> originalValues, double newMinimum, double newMaximum) {
        if (originalValues.isEmpty()) return new ArrayList<>();

        Pair<T, T> minMax = getMinimumAndMaximum(originalValues).orElseThrow();
        T
                minimum = minMax.first(),
                maximum = minMax.second();

        DoubleUnaryOperator normalisationFunction = MathsFunctions.getLinearMappingFunction(minimum.doubleValue(), maximum.doubleValue(), newMinimum, newMaximum).orElse(x->x);
        return originalValues.stream().map(x -> normalisationFunction.applyAsDouble(x.doubleValue())).toList();
    }

    /**
     * GIven an iterable over some items, cast all the items to a child class.
     * @param clazz The child class to which to cast
     * @param items The items to be cast
     * @param <T> The parent class being cast from
     * @param <E> The child class being cast to
     * @return A List containing all the input items cast to the input class
     * @throws ClassCastException if any items in the input cannot be cast to the given class
     */
    public static <T, E extends T> List<E> castList(Class<E> clazz, Iterable<T> items) throws ClassCastException {
        List<E> result = new ArrayList<>();
        for (T item : items) {
                result.add(clazz.cast(item));
        }
        return result;
    }
}
