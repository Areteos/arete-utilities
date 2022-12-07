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

import java.util.Optional;

public record DiagonalLine(double gradient, double intercept) implements Line {

    /**
     * Get intersection with another diagonal line. No (defined) intersection point exists for parallel lines.
     * @param other Another DiagonalLine
     * @return The Point of intersection. Will be empty if the lines are parallel.
     */
    public Optional<Point> getIntersectionWith(DiagonalLine other) {
        double relativeGradient = gradient - other.gradient;
        if (relativeGradient == 0) {
            return Optional.empty();
        }
        double x = (other.intercept - intercept) / relativeGradient;
        double y = yValueAt(x).orElseThrow();
        return Optional.of(new Point(x, y));
    }

    /**
     * Get intersection with a vertical line. This will always be a real point.
     * @param other A VerticalLine
     * @return The Point of intersection.
     */
    public Point getIntersectionWith(VerticalLine other) {
        double y = yValueAt(other.x()).orElseThrow();
        return new Point(other.x(), y);
    }

    @Override
    public Optional<Point> getIntersectionWith(Line other) {
        if (other.getClass() == VerticalLine.class) {
            return Optional.of(getIntersectionWith((VerticalLine) other));
        } else {
            return getIntersectionWith((DiagonalLine) other);
        }
    }

    @Override
    public Optional<Double> yValueAt(double x) {
        return Optional.of(x * gradient + intercept);
    }

    @Override
    public Optional<Double> xValueAt(double y) {
        if (gradient == 0) {
            return Optional.empty();
        }
        return Optional.of((y - intercept) / gradient);
    }

    @Override
    public boolean contains(Point point) {
        return point.x() * gradient + intercept == point.y();
    }

    @Override
    public double distanceTo(Point point) {
        if (gradient == 0) {
            return Math.abs(point.y() - intercept);
        }
        double inverseGradient = 1 / gradient;
        return 1 / (gradient + inverseGradient) * Math.sqrt(
                Math.pow(point.y() - intercept - gradient * point.x(), 2)
                + Math.pow(point.x() + intercept * inverseGradient - inverseGradient * point.y(), 2)
        );
    }

    public boolean runsBelow(Point point) {
        return point.y() > yValueAt(point.x()).orElseThrow();
    }

    public boolean runsAbove(Point point) {
        return point.y() < yValueAt(point.x()).orElseThrow();
    }
}
