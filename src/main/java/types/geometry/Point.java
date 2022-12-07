package types.geometry;


import functions.MathsFunctions;

import java.util.Optional;

public record Point(double x, double y) {

    @Override
    public String toString() {
        return "(%s,%s)".formatted(x, y);
    }

    public Optional<Double> getGradientTo(Point other) {
        return MathsFunctions.getGradient(this, other);
    }

    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }
    public Point subtract(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }
}
