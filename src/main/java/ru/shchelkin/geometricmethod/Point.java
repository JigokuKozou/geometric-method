package ru.shchelkin.geometricmethod;

import javafx.geometry.Point2D;

import java.util.Objects;

public class Point {
    int humanId;

    Point2D point;


    public Point(double x, double y) {
        this(new Point2D(x, y), -1);
    }

    public Point(double x, double y, int humanId) {
        this(new Point2D(x, y), humanId);
    }

    public Point(Point2D point, int humanId) {
        this.point = point;
        this.humanId = humanId;
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public double distance(Point point) {
        return this.point.distance(point.point);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point1 = (Point) o;

        if (humanId != point1.humanId) return false;
        return Objects.equals(point, point1.point);
    }

    @Override
    public int hashCode() {
        int result = humanId;
        result = 31 * result + (point != null ? point.hashCode() : 0);
        return result;
    }
}
