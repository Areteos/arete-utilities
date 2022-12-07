# arete-utilities
A collection of QoL utilities that I've needed to write over the years.

These are split into Functions and Types. The Functions represent utility classes containing collections of static functions, while the Types represent a few classes of objects I've found useful in a wide set of applications.

Unit testing is not yet finished for the entire project, stay tuned!

Every complete item has JavaDoc explaining its use.

## Functions
### IterableFunctions
A collection of static functions for interacting with `Iterable`s. Most of these were originally written with `List`s in mind, but it eventually became clear that almost all of them work just as well with any given `Iterable`. 
Some of these functions deal with numbers in an `Iterable`, for instance summing or averaging. However, the majority of these functions take an `Iterable` and return a different `Iterable` that, when iterated over, represents some special way of iterating over the original. For instance, you could use the `inPairs()` function to replace this:
```
Foo previousValue = null;
for (Foo currentValue : valueList) {
  if (previousValue != null) {
    doStuff(previousValue, currentValue);
  }
  previousValue = currentValue
}
```
with this:
```
for (Pair<Foo, Foo> previousAndCurrent : inPairs(valueList)) {
  Foo   previousValue = previousAndCurrent.first(), currentValue = previousAndCurrent.second();
  doStuff(previousValue, currentValue);
}
```

Planned future work includes creating overloads that use List inputs where some optimisation could be gained by doing so.

### MathsFuntions
A collection of helpful static mathematical functions, including things like rounding to decimal places or significant figures, performing approximate numerical integration and differentiation on functions in R<sup>1</sup>, performing linear interpolations, simplifying arbitrary fractions, and finding prime factors of integers.

### FinancialFunctions
A collection of static functions for performing financial calculations. This class could conceivably be split between MathsFunctions and IterableFunctions, but I feel that financial applications are distinct enough to warrant a dedicated class. Note that these functions were written with machine learning, and therefore speed, in mind: they use primitive `double`s rather than `BigDecimal`s, and are more focused on evaluation of financial performance than precise accounting.

### Visualiser
Essentially a static wrapper for [JFreeChart](https://jfree.org/jfreechart/). Contains some functions and Types that I've personally found to make my standard uses of JFreeChart much easier, especially drawing line graphs directly from functions.

### MiscFunctions
A small set of functions that didn't quite fit in any of the other utility classes, or justify entire classes of their own. Currently a few I/O helpers and string manipulation functions.

## Types
### tuples
A very simple manual implementation of tuples in Java. These allow bundling dissimilar Types (for example, in the return value of a method) while preserving Type safety across scopes. Currently there are only `Pair`, `Triple`, and `Quad` Tuples, but I've found `Pair` especially to be incredibly helpful for a vast array of implementations. Even where a `Tuple` is being made of identical types, e.g. `Double` and `Double`, I've found using Tuples to be more readable and less verbose than using an array or List.

### geometry
Something of a stub, contains only `Point` and `Line` classes and subclasses. Lines are split into `DiagonalLine` and `VerticalLine`, all implementing the `Line` interface. All classes contained in geometry have some methods for interacting with themselves and other geometry classes, which can be helpful in certain 2D situations. In need of a vector-based overhaul at some point.
