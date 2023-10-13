package ru.shchelkin.geometricmethod;

import ru.shchelkin.geometricmethod.EpsilonCylinder;
import ru.shchelkin.geometricmethod.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EpsilonCylinderCreator {
    private final double epsilon;
    private final List<Point> points;
    private final Set<EpsilonCylinder> cylinders;

    public EpsilonCylinderCreator(double epsilon, List<Point> points) {
        this.epsilon = epsilon;
        this.points = points;
        this.cylinders = new HashSet<>();
        createCylinders();
    }

    private void createCylinders() {
        Set<Point> uniquePoints = Set.copyOf(points);

        for (Point first : points) {
            for (Point second : points) {
                if (second.equals(first))
                    continue;

                List<Point> neighborhood = new ArrayList<>();
                for (Point point : points) {
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
