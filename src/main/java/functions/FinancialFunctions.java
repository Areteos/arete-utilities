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

package functions;

import types.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

import static functions.IterableFunctions.inPairs;
import static java.lang.Math.*;

/**
 * A collection of static functions useful for basic financial calculations. Note that these do use doubles instead of
 * BigDecimals, so standard disclaimers about rounding floating point numbers apply!
 */
@SuppressWarnings("unused")
public final class FinancialFunctions {
    private FinancialFunctions() {}

    /**
     * Given a set of equities in the order that they were recorded, return a list containing the effective return
     * between each pair of equities.
     * @param equities Equity over a period
     * @return The fractional returns between each subsequent pair of equities
     */
    public static List<Double> getReturnsFromEquities(Iterable<Double> equities) {
        List<Double> returns = new ArrayList<>();

        for (Pair<Double, Double> previousAndCurrent : inPairs(equities)) {
            double previousEquity = previousAndCurrent.first(), equity = previousAndCurrent.second();
            returns.add(getReturn(previousEquity, equity));
        }

        return returns;
    }

    /**
     * Given a set of fractional returns, calculate the consistent return necessary to achieve the same overall return
     * over the same time period.
     * @param returns The actual returns over the time period
     * @return The return with which you could replace all given, actual returns and still end up with the same overall return
     */
    public static double getGeometricAverageReturn(Iterable<Double> returns) {
        double finalEquity = 1d;
        int size = 0;
        for (double datum : returns) {
            finalEquity *= (1 + datum);
            size++;
        }
        return expressReturnOverDifferentPeriod(getReturn(1, finalEquity), size);
    }

    /**
     * Given a set of returns and a minimum acceptable return, calculate the downside deviation of the returns.
     * @param returns The returns to consider
     * @param minimumAcceptableReturn The minimum acceptable return
     * @return The downside deviation of the set
     */
    public static Double getDownsideDeviation(Iterable<Double> returns, Double minimumAcceptableReturn) {
        double sumSquare = 0;
        int size = 0;
        for (double datum : returns) {
            if (datum < minimumAcceptableReturn) {
                sumSquare += pow(datum - minimumAcceptableReturn, 2);
            }
            size++;
        }
        return pow(sumSquare / size, 0.5);

    }

    /**
     * Given a minimum acceptable return and a set of actual returns, calculate the Sortino ratio of the actual returns.
     * @param returns The returns to consider
     * @param minimumAcceptableReturn The minimum acceptable return
     * @return The Sortino ratio of the returns
     */
    public static Double getSortinoRatio(List<Double> returns, Double minimumAcceptableReturn) {
        double downsideDeviation = getDownsideDeviation(returns, minimumAcceptableReturn);

        double averageReturn = getGeometricAverageReturn(returns);

        return (averageReturn - minimumAcceptableReturn) / downsideDeviation;
    }

    /**
     * Given an overall return for some period, find an equivalent return over a different time period, specified by the
     * ratio of the length of the old time period to the length of the new time period.
     * @param returnForPeriod The overall return for the original time period
     * @param ratioOfOldPeriodToNew The length of the original period divided by the length of the new period
     * @return The equivalent return over the new period, taking compounding into account
     */
    public static Double expressReturnOverDifferentPeriod(double returnForPeriod, double ratioOfOldPeriodToNew) {
        return pow(1 + returnForPeriod, 1 / ratioOfOldPeriodToNew) - 1;
    }


    /**
     * Given a list of equities, calculate the overall return from the first to the last equity.
     */
    public static double getOverallReturn(List<Double> equities) {
        return getReturn(equities.get(0), equities.get(equities.size()-1));
    }

    /**
     * Given an initial equity and final equity, calculate the return between the two.
     */
    public static double getReturn(double initialEquity, double finalEquity) {
        return (finalEquity - initialEquity) / initialEquity;
    }


    /**
     * Given a list of prices over time, work out the maximum return that a perfect trader with perfect prediction would
     * be able to achieve. This perfect trader is limited to making short and long trades of exactly equal size to his
     * equity when he makes them, and is only able to trade at the points in time corresponding to the prices in the list.
     * The perfect trader will, however, always correctly choose whether to make a long or a short trade.
     * @param prices The prices of the instrument being traded
     * @return The maximum possible return for the perfect trader described above
     */
    public static double getAbsoluteEarningPotential(Iterable<Double> prices) {
        double absoluteEarnings = 1d;

        for (Pair<Double, Double> previousAndCurrentPrice : inPairs(prices)) {
            double previousPrice = previousAndCurrentPrice.first(), price = previousAndCurrentPrice.second();
            absoluteEarnings *= (1 + abs(getReturn(previousPrice, price)));
        }

        return absoluteEarnings;
    }
}
