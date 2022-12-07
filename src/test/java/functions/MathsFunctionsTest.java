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

import types.geometry.Point;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MathsFunctionsTest {
    @Test
    void roundToSignificantFigures() {
        assertEquals(0.02, MathsFunctions.roundToSignificantFigures(0.0234567, 1));
        assertEquals(0.023, MathsFunctions.roundToSignificantFigures(0.0234567, 2));
        assertEquals(0.0235, MathsFunctions.roundToSignificantFigures(0.0234567, 3));
        assertEquals(100, MathsFunctions.roundToSignificantFigures(123, 1));
        assertEquals(120, MathsFunctions.roundToSignificantFigures(123, 2));
        assertEquals(123, MathsFunctions.roundToSignificantFigures(123, 3));
    }

    @Test
    void getGradient() {
        Point a = new Point(0, 0), b = new Point(1,1);
        assertEquals(1, MathsFunctions.getGradient(a , b).orElseThrow());

        b = new Point(1,2);
        assertEquals(2, MathsFunctions.getGradient(a , b).orElseThrow());

        b = new Point(2,1);
        assertEquals(0.5, MathsFunctions.getGradient(a , b).orElseThrow());

        b = new Point(-1,2);
        assertEquals(-2, MathsFunctions.getGradient(a , b).orElseThrow());

        b = new Point(1,-2);
        assertEquals(-2, MathsFunctions.getGradient(a , b).orElseThrow());

        a = new Point(-1,-2);
        assertEquals(0, MathsFunctions.getGradient(a , b).orElseThrow());

        a = new Point(-1,-4);
        assertEquals(1, MathsFunctions.getGradient(a , b).orElseThrow());

        a = new Point(1,2);
        assertEquals(Optional.empty(), MathsFunctions.getGradient(a, b));
    }

    @Test
    void getSequentialGradients() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0,0));
        points.add(new Point(0,1));  // null
        points.add(new Point(1, 0)); // -1
        points.add(new Point(2,2));  // 2
        points.add(new Point(3,6));  // 4
        points.add(new Point(2,1));  // 5
        points.add(new Point(-1,1));  // 0
        points.add(new Point(-0.5,-0.5));  // -3
        points.add(new Point(1,-1));  // -1/3

        List<Optional<Double>> gradients = MathsFunctions.getSequentialGradients(points);
        Iterator<Optional<Double>> gradientIterator = gradients.iterator();

        assertEquals(Optional.empty(), gradientIterator.next());
        assertEquals(-1, gradientIterator.next().orElseThrow());
        assertEquals(2, gradientIterator.next().orElseThrow());
        assertEquals(4, gradientIterator.next().orElseThrow());
        assertEquals(5, gradientIterator.next().orElseThrow());
        assertEquals(-0.0, gradientIterator.next().orElseThrow());
        assertEquals(-3, gradientIterator.next().orElseThrow());
        assertEquals(-1d/3d, gradientIterator.next().orElseThrow());

        assertEquals(8, gradients.size());


        points = new ArrayList<>();
        points.add(new Point(1,-1));
        points.add(new Point(-0.5,-0.5)); // -1/3
        points.add(new Point(-1,1)); // -3
        points.add(new Point(2,1)); // 0
        points.add(new Point(3,6)); // 5
        points.add(new Point(2,2));  // 4
        points.add(new Point(1, 0));  // 2
        points.add(new Point(0,1)); // -1
        points.add(new Point(0,0));  // null

        gradients = MathsFunctions.getSequentialGradients(points);
        gradientIterator = gradients.iterator();

        assertEquals(-1d/3d, gradientIterator.next().orElseThrow());
        assertEquals(-3, gradientIterator.next().orElseThrow());
        assertEquals(0, gradientIterator.next().orElseThrow());
        assertEquals(5, gradientIterator.next().orElseThrow());
        assertEquals(4, gradientIterator.next().orElseThrow());
        assertEquals(2, gradientIterator.next().orElseThrow());
        assertEquals(-1, gradientIterator.next().orElseThrow());
        assertEquals(Optional.empty(), gradientIterator.next());

        assertEquals(8, gradients.size());
    }
}