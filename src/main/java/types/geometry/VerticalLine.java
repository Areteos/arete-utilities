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

public record VerticalLine(double x) implements Line {
    @Override
    public Optional<Point> getIntersectionWith(Line other) {
        if (other.getClass() == DiagonalLine.class) {
            return Optional.of(((DiagonalLine) other).getIntersectionWith(this));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Double> yValueAt(double x) {
        return Optional.empty();
    }

    @Override
    public Optional<Double> xValueAt(double y) {
        return Optional.of(x);
    }

    @Override
    public boolean contains(Point point) {
        return point.x() == x;
    }

    @Override
    public double distanceTo(Point point) {
        return Math.abs(x - point.x());
    }

}
