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

package types.geometry;

import functions.MathsFunctions;

import java.util.Optional;

public interface Line {
    /**
     * Given another Line, find the point of intersection if it exists.
     * @param other The Line with which to find an intersection.
     * @return The Point of intersection. Will be empty if the PoI does not exist.
     */
    Optional<Point> getIntersectionWith(Line other);

    /**
     * Returns the y-value that this line has at a particular x.
     * @param x The x-value at which to find the line's y-value.
     * @return The y-value of the line at the given x. Will be empty for vertical lines.
     */
    Optional<Double> yValueAt(double x);

    /**
     * Returns the x-value that this line has at a particular y.
     * @param y The y-value at which to find the line's x-value.
     * @return The x-value of the line at the given y. Will be empty for horizontal lines.
     */
    Optional<Double> xValueAt(double y);

    /**
     * Determines whether this line exactly contains a particular point.
     * @param point The Point to test
     * @return True if the point lies exactly on this line, false otherwise.
     */
    boolean contains(Point point);

    double distanceTo(Point point);

    /**
     * Given two Points, return the line that passes through both of them.
     * @param point1 First Point
     * @param point2 Second Point
     * @return A Line through both points
     */
    static Line fromPoints(Point point1, Point point2) {
        Optional<Double> gradient = MathsFunctions.getGradient(point1, point2);
        if (gradient.isEmpty()) {
            return new VerticalLine(point1.x());
        } else {
            double intercept = point1.y() - gradient.orElseThrow() * point1.x();
            return new DiagonalLine(gradient.orElseThrow(), intercept);
        }

    }
}
