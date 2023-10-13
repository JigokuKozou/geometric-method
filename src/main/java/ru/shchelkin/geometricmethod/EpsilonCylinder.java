package ru.shchelkin.geometricmethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpsilonCylinder {
    private final Point first;

    private final Point second;

    private final double epsilon;

    private final List<Point> neighborhood;

    public EpsilonCylinder(Point first, Point second, double epsilon, List<Point> neighborhood) {
        this.first = first;
        this.second = second;
        this.epsilon = epsilon;
        this.neighborhood = neighborhood;
    }

    public int countPoints() {
        return 2 + neighborhood.size();
    }

    public boolean contains(Point point) {
        return first.equals(point) || second.equals(point) || neighborhood.contains(point);
    }

    public boolean contains(EpsilonCylinder another) {
        for (var point : another.getAllPoints()) {
            if (!contains(point))
                return false;
        }

        return true;
    }

    public Point getFirst() {
        return first;
    }

    public Point getSecond() {
        return second;
    }

    public List<Point> getAllPoints() {
        ArrayList<Point> allPoints = new ArrayList<>(neighborhood);
        allPoints.add(first);
        allPoints.add(second);

        return allPoints;
    }

    public double calculateNormalizedAngle(Point lineFirst, Point lineSecond) {
        double lineAngle = Math.atan2(lineSecond.getY() - lineFirst.getY(), lineSecond.getX() - lineFirst.getX());
        double cylinderAngle = Math.atan2(second.getY() - first.getY(), second.getX() - first.getX());
        double angleInRadians = lineAngle - cylinderAngle;

        if (angleInRadians < 0) {
            angleInRadians += 2 * Math.PI;
        }

        return angleInRadians;
    }

    public static double distance(Point point, Point first, Point second) {
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
        Point projection = new Point(first.getX() + t * (second.getX() - first.getX()),
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
        if (Objects.equals(first, that.first) && Objects.equals(second, that.second) ||
        Objects.equals(second, that.first) && Objects.equals(first, that.second)) {
            return Objects.equals(neighborhood, that.neighborhood);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        temp = Double.doubleToLongBits(epsilon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (neighborhood != null ? neighborhood.hashCode() : 0);
        return result;
    }
}
