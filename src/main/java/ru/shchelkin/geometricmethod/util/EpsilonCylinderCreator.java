package ru.shchelkin.geometricmethod.util;

import javafx.geometry.Point2D;
import ru.shchelkin.geometricmethod.model.EpsilonCylinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EpsilonCylinderCreator {
    private final double epsilon;
    private final Set<Point2D> points;
    private final Set<EpsilonCylinder> cylinders;

    public EpsilonCylinderCreator(double epsilon, Set<Point2D> points) {
        this.epsilon = epsilon;
        this.points = points;
        this.cylinders = new HashSet<>();
        createCylinders();
    }

    private void createCylinders() {
        Point2D[] arrayPoints = points.toArray(new Point2D[0]);
        for (int i = 0; i < arrayPoints.length - 1; i++) {
            Point2D first = arrayPoints[i];
            for (int j = i + 1; j < arrayPoints.length; j++) {
                Point2D second = arrayPoints[j];
                Set<Point2D> neighborhood = new HashSet<>();
                for (Point2D point : arrayPoints) {
                    if (point.equals(first) || point.equals(second))
                        continue;

                    if (EpsilonCylinder.distance(point, first, second) <= epsilon) {
                        neighborhood.add(point);
                    }
                }
                EpsilonCylinder cylinder = new EpsilonCylinder(first, second, epsilon, neighborhood);
                cylinders.add(cylinder);
            }
        }
    }

    public Set<EpsilonCylinder> getCylinders() {
        return cylinders;
    }
}
