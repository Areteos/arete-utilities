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
