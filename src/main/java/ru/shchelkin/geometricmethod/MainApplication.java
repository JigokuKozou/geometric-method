package ru.shchelkin.geometricmethod;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.shchelkin.geometricmethod.model.DataRow;
import ru.shchelkin.geometricmethod.model.EpsilonCylinder;
import ru.shchelkin.geometricmethod.model.Groups;
import ru.shchelkin.geometricmethod.util.CsvReader;
import ru.shchelkin.geometricmethod.util.EpsilonCylinderCreator;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {

        long start = System.currentTimeMillis();

        Groups selectedGroup = Groups.CONTROLS;

        double epsilon = 1e-8d;
        int selectedSensor1 = 11;
        int selectedSensor2 = 13;

        Point2D comparisonLinePoint1 = new Point2D(0.0, 0.0);
        Point2D comparisonLinePoint2 = new Point2D(1.0, 1.0);

        final Map<String, List<DataRow>> groups = readDataFromFile(
                "C:\\Users\\ilyas\\Downloads\\Префиксные коды дендрограмм\\21-12-2022_16-21_human_prefixes_Bin5MaverageRF_F10-D790_Wd600_SSt1_NoFastICA_Conf0_v1p.csv");

        final Map<Groups, Set<Point2D>> groupsPoints = convertTOGroupsSetPoint2DsMap(selectedSensor1, selectedSensor2, groups);
        groups.clear();

        final EpsilonCylinderCreator epsilonCylinderCreator = new EpsilonCylinderCreator(epsilon, groupsPoints.get(selectedGroup));

        final Map<Double, Set<EpsilonCylinder>> classesEpsilonCylinders =
                classificationByDeviationAngle(comparisonLinePoint1, comparisonLinePoint2, epsilonCylinderCreator.getCylinders());

        for (double angle : classesEpsilonCylinders.keySet()) {
            Set<EpsilonCylinder> epsilonCylinders = classesEpsilonCylinders.get(angle);

            List<EpsilonCylinder> cylindersToRemove = new ArrayList<>();

            for (EpsilonCylinder cylinder : epsilonCylinders) {
                for (EpsilonCylinder otherCylinder : epsilonCylinders) {
                    if (cylinder != otherCylinder && otherCylinder.contains(cylinder)) {
                        cylindersToRemove.add(cylinder);
                        break;
                    }
                }
            }

            epsilonCylinders.removeAll(cylindersToRemove);
        }

        double angleOfMaxPointsEpsilonCylindersClass = -1;
        Set<EpsilonCylinder> maxPointsEpsilonCylindersClass = null;
        int maxCountPoints = 0;
        for (double angle : classesEpsilonCylinders.keySet()) {
            Set<EpsilonCylinder> current = classesEpsilonCylinders.get(angle);
            int countPoints = current.stream().mapToInt(cylinder -> cylinder.getAllPoints().size()).sum();
            if (maxCountPoints < countPoints) {
                angleOfMaxPointsEpsilonCylindersClass = angle;
                maxPointsEpsilonCylindersClass = current;
                maxCountPoints = countPoints;
            }
        }

        for (double angle : classesEpsilonCylinders.keySet()) {
            System.out.println(Math.toDegrees(angle) + ": " + classesEpsilonCylinders.get(angle));
        }
        System.out.println("Max: " + Math.toDegrees(angleOfMaxPointsEpsilonCylindersClass) + ": " + maxPointsEpsilonCylindersClass + "\n" +
                maxCountPoints + "/" + groupsPoints.get(selectedGroup).size() + " = " + maxPointsEpsilonCylindersClass.size()/groupsPoints.get(selectedGroup).size() * 100);

        // ============================================ Draw ============================================

        final List<XYChart.Series<Number, Number>> seriesesChart = new ArrayList<>();

        drawComparisonLine(comparisonLinePoint1, comparisonLinePoint2, seriesesChart);


        for (var set : groupsPoints.entrySet()) {
            final XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(set.getKey().getName());
            if (set.getKey() != Groups.CONTROLS)
                continue;

            for (Point2D point : set.getValue()) {
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            }

            seriesesChart.add(series);
        }

        for (var epsilonCylinder : maxPointsEpsilonCylindersClass) {
            Point2D first = epsilonCylinder.getFirst();
            Point2D second = epsilonCylinder.getSecond();

            final XYChart.Series<Number, Number> epsilonCylindersChart = new XYChart.Series<>();
            epsilonCylindersChart.getData().add(new XYChart.Data<>(first.getX(), first.getY()));
            epsilonCylindersChart.getData().add(new XYChart.Data<>(second.getX(), second.getY()));

            seriesesChart.add(epsilonCylindersChart);
        }

        final NumberAxis XAxis = new NumberAxis(Integer.toString(selectedSensor1), 0, 1, 0.2);
        final NumberAxis YAxis = new NumberAxis(Integer.toString(selectedSensor2), 0, 1, 0.2);
        final LineChart<Number,Number> sc = new LineChart<>(XAxis,YAxis);

        sc.setAnimated(false);
        sc.setCreateSymbols(true);

        sc.getData().addAll(seriesesChart);

        Scene scene  = new Scene(sc, 700, 700);
        scene.getStylesheets().add(getClass().getResource("root.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        System.out.println(System.currentTimeMillis() - start);
    }

    private static Map<Double, Set<EpsilonCylinder>> classificationByDeviationAngle(Point2D comparisonLinePoint1, Point2D comparisonLinePoint2, Set<EpsilonCylinder> epsilonCylinders) {
        final Map<Double, Set<EpsilonCylinder>> cylindersByAngle = new HashMap<>();
        for (EpsilonCylinder cylinder : epsilonCylinders) {
            double angle = cylinder.calculateNormalizedAngle(comparisonLinePoint1, comparisonLinePoint2);
            double scale = Math.pow(10, 2);
            double roundedAngle = Math.round(angle * scale) / scale;

            Set<EpsilonCylinder> cylinders = cylindersByAngle.getOrDefault(roundedAngle, new HashSet<>());
            cylinders.add(cylinder);
            cylindersByAngle.put(roundedAngle, cylinders);
        }
        return cylindersByAngle;
    }

    private static void drawComparisonLine(Point2D comparisonLinePoint1, Point2D comparisonLinePoint2, List<XYChart.Series<Number, Number>> seriesesChart) {
        final XYChart.Series<Number, Number> comparisonLineChart = new XYChart.Series<>();
        comparisonLineChart.getData().add(new XYChart.Data<>(comparisonLinePoint1.getX(), comparisonLinePoint1.getY()));
        comparisonLineChart.getData().add(new XYChart.Data<>(comparisonLinePoint2.getX(), comparisonLinePoint2.getY()));

        seriesesChart.add(comparisonLineChart);
    }

    private Map<Groups, Set<Point2D>> convertTOGroupsSetPoint2DsMap(int selectedSensor1, int selectedSensor2, Map<String, List<DataRow>> groups) {
        final Map<Groups, Set<Point2D>> groupsPoints = new LinkedHashMap<>();
        for (var entry : groups.entrySet()) {
            Groups currentGroup = Groups.getGroupByFileName(entry.getKey());

            Set<Point2D> points = groupsPoints.getOrDefault(currentGroup, new HashSet<>());
            List<DataRow> rows = entry.getValue();
            rows.stream().forEach(row ->
                    points.add(new Point2D(normalize(rows, row, selectedSensor1), normalize(rows, row, selectedSensor2))));

            groupsPoints.put(currentGroup, points);
        }
        return groupsPoints;
    }

    private double normalize(List<DataRow> rows, DataRow row, int sensorIndex) {
        double value = row.getSensor(sensorIndex);
        double min = rows.stream().mapToDouble(r -> r.getSensor(sensorIndex)).min().orElseThrow();
        double max = rows.stream().mapToDouble(r -> r.getSensor(sensorIndex)).max().orElseThrow();
        return (value - min) / (max - min);
    }

    private Map<String, List<DataRow>> readDataFromFile(String filePath) {

        final Map<String, List<DataRow>> groups = new LinkedHashMap<>();

        final CsvReader csvReader = new CsvReader(filePath);
        for (DataRow row : csvReader.readData()) {
            if (groups.containsKey(row.getFileName()))
                groups.get(row.getFileName()).add(row);
            else {
                final List<DataRow> group = new ArrayList<>();
                group.add(row);

                groups.put(row.getFileName(), group);
            }
        }

        return groups;
    }
}