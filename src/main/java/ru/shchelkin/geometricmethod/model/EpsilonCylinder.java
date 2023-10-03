package ru.shchelkin.geometricmethod.model;

import javafx.geometry.Point2D;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EpsilonCylinder {
    private final Point2D first;

    private final Point2D second;

    private final double epsilon;

    private final Set<Point2D> neighborhood;

    public EpsilonCylinder(Point2D first, Point2D second, double epsilon, Set<Point2D> neighborhood) {
        this.first = first;
        this.second = second;
        this.epsilon = epsilon;
        this.neighborhood = neighborhood;
    }

    public int countPoints() {
        return 2 + neighborhood.size();
    }

    public boolean contains(Point2D point) {
        return first.equals(point) || second.equals(point) || neighborhood.contains(point);
    }

    public boolean contains(EpsilonCylinder another) {
        for (var point : another.getAllPoints()) {
            if (!contains(point))
                return false;
        }

        return true;
    }

    public Point2D getFirst() {
        return first;
    }

    public Point2D getSecond() {
        return second;
    }

    public Set<Point2D> getNeighborhood() {
        return neighborhood;
    }

    public Set<Point2D> getAllPoints() {
        Set<Point2D> allPoints = new HashSet<>(neighborhood);
        allPoints.add(first);
        allPoints.add(second);

        return allPoints;
    }

    public double calculateNormalizedAngle(Point2D first, Point2D second) {
        double angleInRadians = Math.atan2(second.getY() - first.getY(), second.getX() - first.getX()) -
                Math.atan2(this.second.getY() - this.first.getY(), this.second.getX() - this.first.getX());

        if (angleInRadians < 0) {
            angleInRadians += 2 * Math.PI;
        }

        return angleInRadians;
    }

    public static double distance(Point2D point, Point2D first, Point2D second) {
        double length = first.distance(second);
        if (length == 0) {
            return point.distance(first);
        }
        double t = ((point.getX() - first.getX()) * (second.getX() - first.getX()) +
                (point.getY() - first.getY()) * (second.getY() - first.getY())) / (length * length);
        if (t < 0) {
            return point.distance(first);
        }
        if (t > 1) {
            return point.distance(second);
        }
        Point2D projection = new Point2D(first.getX() + t * (second.getX() - first.getX()),
                first.getY() + t * (second.getY() - first.getY()));
        return point.distance(projection);
    }

    @Override
    public String toString() {
        return "[" + first.getX() + "," + first.getY() + ", " + second.getX()+ "," + second.getY() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EpsilonCylinder that = (EpsilonCylinder) o;

        if (Double.compare(that.epsilon, epsilon) != 0) return false;
        if ((Objects.equals(first, that.first) && Objects.equals(second, that.second)) ||
                (Objects.equals(first, that.second) && Objects.equals(second, that.first))) {
            return Objects.equals(neighborhood, that.neighborhood);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (first != null ? first.hashCode() : 0) + (second != null ? second.hashCode() : 0);
        temp = Double.doubleToLongBits(epsilon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (neighborhood != null ? neighborhood.hashCode() : 0);
        return result;
    }
}
