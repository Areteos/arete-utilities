package functions;

import types.geometry.Point;
import types.tuples.Pair;

import java.util.*;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Math.pow;

/**
 * A collection of static mathematical functions not provided in Java's basic libraries.
 */
@SuppressWarnings("unused")
public final class MathsFunctions {
    private MathsFunctions() {}

    /**
     * Find the minimum and maximum value of a given function over a given interval.
     * @param function The function whose minimum and maximum are to be found
     * @param lowerBound The lower bound of the interval (inclusive)
     * @param upperBound The upper bound of the interval (inclusive)
     * @param steps The amount of steps to take across the interval
     * @return A pair representing the minimum and maximum value (in that order) of the function over the given interval
     */
    public static Pair<Double, Double> findIntervalMinimumAndMaximum(DoubleUnaryOperator function, double lowerBound, double upperBound, int steps) {
        List<Double> yValues = new ArrayList<>();
        double range = upperBound - lowerBound;
        if (range < 0) {
            throw new IllegalArgumentException("Interval lower bound greater than interval upper bound");
        }
        if (range > 0) {
            double stepSize = range / (steps-1);
            for (double x = lowerBound; x <= upperBound - stepSize; x += stepSize) {
                yValues.add(function.applyAsDouble(x));
            }
        }

        yValues.add(function.applyAsDouble(upperBound));
        return IterableFunctions.getMinimumAndMaximum(yValues).orElseThrow();
    }

    /**
     * Round the input to a specified number of decimal places.
     * @param input The number to be rounded
     * @param decimalPlaces The number of decimal places to round the input to
     * @return The input, rounded to the specified number of decimal places
     * @see Math#round(double)
     */
    public static double round(Double input, int decimalPlaces) {
        Double tens = pow(10, decimalPlaces);
        return Math.round(input * tens) / tens;
    }

    /**
     * Round the input to the specified number of (decimal) significant figures. For example, 0.003467 rounded to 2
     * significant figures would become 0.0035.
     * @param input The number to rounded
     * @param significantFigures The number of significant figures to round the input to
     * @return The input, rounded to the specified number of significant figures
     * @see Math#round(double)
     */
    public static double roundToSignificantFigures(double input, int significantFigures) {
        double magnitude = Math.pow(10, Math.floor(Math.log10(Math.abs(input))));
        double significantFigureCorrection = Math.pow(10, significantFigures-1);
        return Math.round(input * significantFigureCorrection / magnitude) * magnitude / significantFigureCorrection;
    }


    /**
     * Returns true if a given number is strictly within a specified margin of another number, and false otherwise.
     * @param target The target number
     * @param margin The distance away from the target number that is considered "close enough"
     * @param x The number being checked for
     * @param <T> The Type of numbers being compared
     * @return true if x is strictly less than margin away from target, and false otherwise
     */
    public static <T extends Number> boolean isWithin(T target, T margin, T x) {
        return Math.abs(target.doubleValue() - x.doubleValue()) < margin.doubleValue();
    }

    /**
     * Given a base and a target, find the multiple of the base which is closest to the target. That is, the number that
     * is the product of the base and some integer, which is closest to the target.
     * @param base The number to multiply
     * @param target The number to get as close to as possible
     * @return The multiple of the base that is closest to the target
     */
    public static Double findClosestMultiple(Double base, Double target) {
        return base * Math.round(target/base);
    }


    /**
     * Numerically integrate a given function over a given interval in a given number of steps. This is done using
     * left-hand rectangular slices.
     * @param function The function to integrate
     * @param lowerBound The lower bound of the integration
     * @param upperBound The upper bound of the integration
     * @param steps The number of steps to us in the integration
     * @return An approximation of the definite integral of the input function over the given interval
     */
    public static double integrateApproximately(DoubleUnaryOperator function, double lowerBound, double upperBound, int steps) {
        double range = upperBound - lowerBound;
        double stepSize = range / steps;
        double sum = 0;
        for (double x = lowerBound; x <= upperBound - stepSize; x+=stepSize) {
            sum += function.applyAsDouble(x) * stepSize;
        }
        return sum;
    }

    /**
     * Given a function, location, and delta value, calculate the approximate gradient of the function at the location.
     * The delta value is used to select a point on the curve of the function on either side of the input location. The
     * gradient between these points is taken as approximating the gradient of the function at the location.
     * @param function The function whose gradient is being found
     * @param x The location at which to find the gradient
     * @param delta How far on either side of x to go to approximate the gradient
     * @return The approximate gradient of the given function at x
     */
    public static Double findApproximateGradientAtPoint(DoubleUnaryOperator function, double x, double delta) {
        return (function.applyAsDouble(x + delta) - function.applyAsDouble(x - delta)) / (2 * delta);
    }

    /**
     * Given a function and a delta value, return a function that approximates the derivative of the input function
     * @param function The function to approximately derive
     * @param delta The delta value to use in calculating the approximate derivative
     * @return A function that approximates the derivative of the input
     * @see MathsFunctions#findApproximateGradientAtPoint(DoubleUnaryOperator, double, double)
     */
    public static DoubleUnaryOperator findApproximateDerivative(DoubleUnaryOperator function, double delta) {
        return x -> findApproximateGradientAtPoint(function, x, delta);
    }

    /**
     * Given a probability distribution, domain, and maximum y value for the probability distribution, pseudorandomly
     * generate the desired number of points in accordance with the probability distribution.
     * @param probabilityDistribution The probability distribution to draw points from
     * @param minimumX The lower bound of the domain
     * @param maximumX The upper bound of the domain
     * @param maximumY The maximum y value in the distribution over the domain
     * @param number The number of points to generate
     * @return A list of points on the domain whose location and frequency correspond to the probability distribution
     * over the domain
     * @see Random
     */
    public static List<Double> generatePoints(DoubleUnaryOperator probabilityDistribution, double minimumX, double maximumX, double maximumY, int number) {
        Random random = new Random();

        final double range = maximumX - minimumX;

        List<Double> points = new ArrayList<>();
        while (points.size() < number) {
            double point = random.nextDouble() * range + minimumX;
            double probability = probabilityDistribution.applyAsDouble(point);
            if (random.nextDouble() * maximumY < probability) {
                points.add(point);
            }
        }

        return points;
    }

    /**
     * Finds the prime factors of a given integer, and returns them in a map where the keys represent unique factors and
     * the values represent the powers of those factors. The result is that if you exponentiate every key by its
     * associated value and then find the product of the results, you will arrive at the original input value. Note that
     * if the input is negative, the returned map will include a factor of -1 with power 1, and otherwise be identical
     * to the result of inputting the absolute value of the same number.
     * @param integer The number for which to find the prime factors
     * @return A map representing the prime factors of the input
     */
    public static Map<Integer, Integer> findPrimeFactors(int integer) {
        Map<Integer, Integer> primeFactors = new HashMap<>();

        if (integer < 0) {  // If the input is negative, add a factor of -1 and continue with the absolute of the input
            primeFactors.put(-1, 1);
            integer = Math.abs(integer);
        }

        double quotient = integer / 2.0;  // First divide by 2 until we can't anymore
        while (Math.floor(quotient) == quotient) {
            primeFactors.merge(2, 1, Integer::sum);
            integer = (int) quotient;
            quotient = integer / 2.0;
        }

        for (double i = 3; i < quotient; i+=2) {  // Then do the same thing with all the odd numbers. Note that we do
            // not need to test for primes, because any non-prime odd numbers will have prime factors smaller than
            // themselves, and so will always have already been divided out of the quotient by the time they come up.
            quotient = integer / i;
            while (Math.floor(quotient) == quotient) {
                primeFactors.merge((int) i, 1, Integer::sum);
                integer = (int) quotient;
                quotient = integer / i;
            }
        }

        if (primeFactors.isEmpty()) {  // If no prime factors were found, the input must be prime
            primeFactors.put(integer, 1);
        }

        return primeFactors;
    }

    /**
     * Given two integers, find all prime factors they have in common. Returns a map where the keys represent unique
     * factors and the values represent the common power of those factors.
     * @param int1 first input
     * @param int2 second input
     * @return A map representing the common prime factors of the two inputs
     * @see MathsFunctions#findPrimeFactors(int)
     */
    public static Map<Integer, Integer> findCommonPrimeFactors(int int1, int int2) {
        Map<Integer, Integer> factors1 = findPrimeFactors(int1);
        Map<Integer, Integer> factors2 = findPrimeFactors(int2);

        Map<Integer, Integer> commonFactors = new HashMap<>();

        Set<Integer> keySet1 = factors1.keySet();
        Set<Integer> keySet2 = factors2.keySet();

        for (int factor : keySet1) {
            if (keySet2.contains(factor)) {
                commonFactors.put(factor, Math.min(factors1.get(factor), factors2.get(factor)));
            }
        }

        return commonFactors;
    }

    /**
     * Given two integers, find their greatest common factor.
     * @param int1 first input
     * @param int2 second input
     * @return The greatest common factor of the two inputs
     * @see MathsFunctions#findCommonPrimeFactors(int, int)
     */
    public static int findGreatestCommonFactor(int int1, int int2) {
        Map<Integer, Integer> commonPrimeFactors = findCommonPrimeFactors(int1, int2);

        int product = 1;
        for (int factor : commonPrimeFactors.keySet()) {
            product *= Math.pow(factor, commonPrimeFactors.get(factor));
        }

        return product;
    }

    /**
     * Given an arbitrarily-valued numerator and denominator, simplify the fraction as far as possible. Will return
     * empty for all denominators of 0, and otherwise 0/1 for all numerators of 0.
     * @param numerator The numerator of the fraction to be simplified
     * @param denominator The denominator of the fraction to be simplified
     * @return The simplified integer numerator and denominator, in that order. Will be empty if denominator is empty
     */
    public static Optional<Pair<Integer, Integer>> simplifyFraction(double numerator, double denominator) {
        if (denominator == 0) {
            return Optional.empty();
        }
        if (numerator == 0) {
            return Optional.of(new Pair<>(0, 1));
        }

        while (numerator % 1 != 0 || denominator % 1 != 0) {
            numerator *= 2;
            denominator *= 2;
        }

        int commonFactor = findGreatestCommonFactor((int) numerator, (int) denominator);
        numerator = numerator / commonFactor;
        denominator = denominator / commonFactor;

        return Optional.of(new Pair<>((int) numerator, (int) denominator));
    }


    /**
     * Given two locations and their respective values, infers the expected value at a third location using linear
     * interpolation.
     * @param location1 The location of a known point
     * @param location2 The location of another, distinct known point
     * @param value1 The value at the first point
     * @param value2 The value at the second point
     * @param queryLocation The location for which we want to know the value
     * @return The interpolated value at the query location
     */
    public static double interpolateLinearly(double location1, double location2, double value1, double value2, double queryLocation) {
        if (location1 == location2) {
            throw new IllegalArgumentException("Input locations for interpolation cannot be equal");
        }
        return (queryLocation - location1) * (value2 - value1) / (location2 - location1) + value1;
    }


    /**
     * Given a function and an interval, uses brute force to find the local minima and maxima of the function over the
     * interval. TODO: optimise
     * @param function The function whose extrema need to be found
     * @param lowerBound The lower bound of the interval
     * @param upperBound The upper bound of the interval
     * @param steps The amount of steps to take over the interval
     * @return The minima and maxima found over the interval, in that order. Note that extrema are listed according to
     * their locations in the domain, leaving it up to the calling scope to evaluate the function at these locations if
     * necessary.
     */
    public static Pair<List<Double>, List<Double>> findLocalExtrema(DoubleUnaryOperator function, double lowerBound, double upperBound, int steps) {
        double range = upperBound - lowerBound;
        double stepSize = range / steps;

        Double previousGradient = null;
        Double previousValue = null;

        List<Double> localMaxima = new ArrayList<>();
        List<Double> localMinima = new ArrayList<>();

        List<Double> currentFlat = new ArrayList<>();
        Double flatEntryGradient = null;

        Boolean minimumFirst = null;

        for (double x = lowerBound; x <= upperBound - stepSize; x+=stepSize) {
            double value = function.applyAsDouble(x);
            double gradient;
            if (previousValue != null) {
                gradient = (value - previousValue) / stepSize;

                if (previousGradient != null) {
                    double gradientProduct = gradient * previousGradient;
                    if (gradientProduct < 0) {
                        if (currentFlat.isEmpty()) {
                            if (previousGradient > 0) {
                                localMaxima.add(x);
                            } else {
                                localMinima.add(x);
                            }
                        } else {
                            if (flatEntryGradient * gradient < 0) {
                                if (previousGradient < 0) {
                                    localMaxima.add(currentFlat.get(currentFlat.size()/2));
                                } else {
                                    localMinima.add(currentFlat.get(currentFlat.size()/2));
                                }
                            }
                            currentFlat.clear();
                        }
                    } else if (gradientProduct == 0) {
                        if (flatEntryGradient == null) {
                            flatEntryGradient = previousGradient;
                        }
                        if (gradient != 0) {
                            if (!currentFlat.isEmpty() && flatEntryGradient * gradient < 0) {
                                if (previousGradient < 0) {
                                    localMaxima.add(currentFlat.get((currentFlat.size()+1)/2 - 1));
                                } else {
                                    localMinima.add(currentFlat.get((currentFlat.size()+1)/2 - 1));
                                }
                            }
                            currentFlat.clear();
                            flatEntryGradient = null;
                        } else {
                            currentFlat.add(x);
                        }

                    }
                }

                previousGradient = gradient;
            }
            previousValue = value;
        }
        return new Pair<>(localMinima, localMaxima);
    }

    /**
     * Given a pair of points in the original space and their corresponding positions in the new space, constructs a
     * linear mapping function from the original space to the new space.
     * @param originalValue1 A point in the original space
     * @param originalValue2 A DIFFERENT point in the original space
     * @param newValue1 The new location of the first point
     * @param newValue2 The new location of the second point (if new locations are equal, the function is equivalent to x->newLocation)
     * @return An Optional DoubleUnaryOperator. Will be empty if the two original values are equal.
     */
    public static Optional<DoubleUnaryOperator> getLinearMappingFunction(double originalValue1, double originalValue2, double newValue1, double newValue2) {
        double originalRange = originalValue2 - originalValue1;
        if (originalRange == 0) {
            return Optional.empty();
        }
        double newRange = newValue2 - newValue1;
        double normalisationFactor = newRange/originalRange;

        return Optional.of(x -> (x - originalValue1) * normalisationFactor + newValue1);
    }

    /** Note that because the gradient function assumes a Cartesian plane, the order the points are in does not matter
     * @param point1 First point
     * @param point2 Second point
     * @return The gradient between the two points. Returns empty if x1==x2.
     */
    public static Optional<Double> getGradient(Point point1, Point point2) {
        double denominator = (point2.x() - point1.x());
        if (denominator == 0) {
            return Optional.empty();
        }
        return Optional.of((point2.y() - point1.y()) / denominator);
    }

    /**
     * Given a sequence of Points, return a list of gradients between each pair of consecutive points
     * @param points An ordered sequence of points
     * @return A List containing the gradient between each point and the following point.
     */
    public static List<Optional<Double>> getSequentialGradients(Iterable<Point> points) {
        List<Optional<Double>> gradients = new ArrayList<>();
        Point previousPoint = null;
        for (Point point : points) {
            if (previousPoint != null) {
                gradients.add(getGradient(previousPoint, point));
            }
            previousPoint = point;
        }
        return gradients;
    }
}
