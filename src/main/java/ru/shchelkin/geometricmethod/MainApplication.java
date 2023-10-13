package ru.shchelkin.geometricmethod;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainApplication extends Application {
    public static final double EPSILON = 1e-8d;
    private TextField filePathField;
    private File selectedConfigFile;
    private ComboBox<Groups> groupsComboBox;
    private ComboBox<Integer> xSensorComboBox;
    private ComboBox<Integer> ySensorComboBox;

    Spinner<Double> linePoint1XSpiner;
    Spinner<Double> linePoint1YSpiner;

    Spinner<Double> linePoint2XSpiner;
    Spinner<Double> linePoint2YSpiner;

    private double angleOfMaxPointsEpsilonCylindersClass;
    private Set<EpsilonCylinder> maxPointsEpsilonCylindersClass;
    private int maxCountPoints;
    private String configFileName;
    private Groups selectedGroup;
    private int selectedSensor1;
    private int selectedSensor2;
    private Point comparisonLinePoint1;
    private Point comparisonLinePoint2;

    public MainApplication() {
        this.filePathField = new TextField();

        this.groupsComboBox = new ComboBox<>(FXCollections.observableArrayList(Groups.values()));
        groupsComboBox.setValue(Groups.CONTROLS);

        this.xSensorComboBox = new ComboBox<>(
                FXCollections.observableArrayList(IntStream.rangeClosed(1, 19).boxed().collect(Collectors.toList())));
        xSensorComboBox.setValue(11);
        this.ySensorComboBox = new ComboBox<>(
                FXCollections.observableArrayList(IntStream.rangeClosed(1, 19).boxed().collect(Collectors.toList())));
        ySensorComboBox.setValue(13);

        linePoint1XSpiner = new Spinner<>();
        SpinnerValueFactory<Double> linePoint1XValueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 0.0, 0.1);
        linePoint1YSpiner = new Spinner<>();
        SpinnerValueFactory<Double> linePoint1YValueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 0.0, 0.1);
        linePoint1XSpiner.setValueFactory(linePoint1XValueFactory);
        linePoint1YSpiner.setValueFactory(linePoint1YValueFactory);

        linePoint2XSpiner = new Spinner<>();
        SpinnerValueFactory<Double> linePoint2XValueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 1.0, 0.1);
        linePoint2YSpiner = new Spinner<>();
        SpinnerValueFactory<Double> linePoint2YValueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 1.0, 0.1);
        linePoint2XSpiner.setValueFactory(linePoint2XValueFactory);
        linePoint2YSpiner.setValueFactory(linePoint2YValueFactory);
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Button createChartButton = new Button("Создать график");
        createChartButton.setOnAction(event -> ShowChart());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Конфигурационныe файлы", "*.csv", "*.txt"));
        Button selectFileButton = new Button("Выбрать");
        selectFileButton.setOnAction((event) -> {
            selectedConfigFile = fileChooser.showOpenDialog(stage);
            filePathField.setText(selectedConfigFile.getAbsolutePath());
        });
        HBox fileChooserGroup = new HBox(
                filePathField,
                selectFileButton
        );

        HBox sensors = new HBox(
                new Label("Сенсор X:"),
                xSensorComboBox,
                new Label("Сенсор Y:"),
                ySensorComboBox);
        sensors.setSpacing(5);
        sensors.setAlignment(Pos.CENTER_LEFT);

        HBox linePoint1Coord = new HBox(
                linePoint1XSpiner,
                linePoint1YSpiner);
        HBox linePoint2Coord = new HBox(
                linePoint2XSpiner,
                linePoint2YSpiner);
        linePoint1Coord.setSpacing(5);
        linePoint2Coord.setSpacing(5);

        VBox root = new VBox();
        root.setSpacing(5);
        root.setPadding(new Insets(5));
        root.getChildren().addAll(
                new Label("Конфигурационный файл:"),
                fileChooserGroup,
                new Label("Группа:"),
                groupsComboBox,
                sensors,
                new Label("Линия сравнения"),
                new Label("Координата первой точки (X, Y):"),
                linePoint1Coord,
                new Label("Координата второй точки (X, Y):"),
                linePoint2Coord,
                createChartButton
        );

        stage.setResizable(false);
        Scene scene = new Scene(root, 300, 350);
        stage.setScene(scene);
        stage.show();
    }

    private void ShowChart() {
        long start = System.currentTimeMillis();

        configFileName = filePathField.getText();

        selectedGroup = groupsComboBox.getValue();

        selectedSensor1 = xSensorComboBox.getValue();
        selectedSensor2 = ySensorComboBox.getValue();

        comparisonLinePoint1 = new Point(linePoint1XSpiner.getValue(), linePoint1YSpiner.getValue());
        comparisonLinePoint2 = new Point(linePoint2XSpiner.getValue(), linePoint2YSpiner.getValue());

        final Map<Groups, Map<Integer, List<DataRow>>> groupsHumans = readDataFromFile(configFileName);

        final Map<Groups, List<Point>> groupsPoints =
                convertToGroupsPointsMap(selectedSensor1, selectedSensor2, groupsHumans);
        groupsHumans.clear();

        final EpsilonCylinderCreator epsilonCylinderCreator =
                new EpsilonCylinderCreator(EPSILON, groupsPoints.get(selectedGroup));

        final Map<Double, Set<EpsilonCylinder>> classesEpsilonCylinders =
                classificationByDeviationAngle(comparisonLinePoint1, comparisonLinePoint2,
                        epsilonCylinderCreator.getCylinders());

        removingNestedEpsilonCylinders(classesEpsilonCylinders);

        angleOfMaxPointsEpsilonCylindersClass = -1;
        maxPointsEpsilonCylindersClass = null;
        maxCountPoints = 0;
        for (double angle : classesEpsilonCylinders.keySet()) {
            Set<EpsilonCylinder> current = classesEpsilonCylinders.get(angle);

            int countPoints = current.stream()
                    .mapToInt(cylinder -> cylinder.countPoints())
                    .sum();

            if (maxCountPoints < countPoints) {
                angleOfMaxPointsEpsilonCylindersClass = angle;
                maxPointsEpsilonCylindersClass = current;
                maxCountPoints = countPoints;
            }
        }
        System.out.println(System.currentTimeMillis() - start);
        // ============================================ Draw ============================================
        Stage stage = new Stage();
        stage.setTitle(selectedGroup.getName());

        int countSelectedGroupPoints = groupsPoints.get(selectedGroup).size();
        HBox root = new HBox();
        VBox info = new VBox(
                new Label("Конфигурационный файл:\n\t" + selectedConfigFile.getName().replace("_", "_\n\t")),
                new Label("Угол отклонения: " + angleOfMaxPointsEpsilonCylindersClass),
                new Label("(захваченные)/(все) = (процент)): \n" +
                        maxCountPoints + "/" + countSelectedGroupPoints + " = " +
                        Math.round(((double) maxCountPoints)/countSelectedGroupPoints * 1000)/10.0 + "%"),
                new Label("Легенда:")
        );
        info.setSpacing(5);
        info.setPadding(new Insets(5));
        info.setMaxWidth(200);
        info.setMinWidth(200);
        info.setFillWidth(true);
        for (Groups group: Groups.values()) {
            Circle circle = new Circle(5);
            circle.setStyle("-fx-fill: "+ group.getColor() +"; -fx-stroke: #BA15BA; -fx-stroke-width: 2;");
            HBox groupBox = new HBox(
                    circle,
                    new Label(" " + group.getName().toLowerCase())
            );
            groupBox.setAlignment(Pos.CENTER_LEFT);

            info.getChildren().add(groupBox);
        }

        root.getChildren().add(info);

        final LineChart<Number, Number> lineChart = getLineChart(groupsPoints);

        lineChart.setMinSize(700, 700);
        lineChart.setMaxSize(700, 700);
        root.getChildren().add(lineChart);
        root.setFillHeight(true);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private LineChart<Number, Number> getLineChart(Map<Groups, List<Point>> groupsPoints) {
        final List<XYChart.Series<Number, Number>> seriesesChart = new ArrayList<>();

        drawComparisonLine(comparisonLinePoint1, comparisonLinePoint2, seriesesChart);

        int countEpsilonCylinders = 0;
        for (var epsilonCylinder : maxPointsEpsilonCylindersClass) {
            Point first = epsilonCylinder.getFirst();
            Point second = epsilonCylinder.getSecond();

            final XYChart.Series<Number, Number> epsilonCylindersChart = new XYChart.Series<>();

            Node node1 = new StackPane();
            node1.setStyle("-fx-background-color: transparent, transparent;");
            Node node2 = new StackPane();
            node2.setStyle("-fx-background-color: transparent, transparent;");

            XYChart.Data<Number, Number> data1 = new XYChart.Data<>(first.getX(), first.getY());
            data1.setNode(node1);
            XYChart.Data<Number, Number> data2 = new XYChart.Data<>(second.getX(), second.getY());
            data2.setNode(node2);

            epsilonCylindersChart.getData().add(data1);
            epsilonCylindersChart.getData().add(data2);

            seriesesChart.add(epsilonCylindersChart);
            countEpsilonCylinders++;
        }

        int countPointSeries = 0;
        for (Map.Entry<Groups, List<Point>> set : groupsPoints.entrySet()) {
            final XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(set.getKey().getName());

            for (Point point : set.getValue()) {
                XYChart.Data<Number, Number> data = new XYChart.Data<>(point.getX(), point.getY());
                Node node = new StackPane();
                node.setStyle("-fx-background-color: #BA15BA, " + set.getKey().getColor() + ";");
                data.setNode(node);
                series.getData().add(data);
            }

            seriesesChart.add(series);
            countPointSeries++;
        }

        final NumberAxis XAxis = new NumberAxis(Integer.toString(selectedSensor1), 0, 1, 0.2);
        final NumberAxis YAxis = new NumberAxis(Integer.toString(selectedSensor2), 0, 1, 0.2);
        final LineChart<Number,Number> lineChart = new LineChart<>(XAxis, YAxis);

        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(true);

        lineChart.getData().addAll(seriesesChart);

        seriesesChart.get(0).getNode().setStyle("-fx-stroke: black; ");

        int offsetEpsilonCylinders = 1;
        for (int i = offsetEpsilonCylinders; i < countEpsilonCylinders + offsetEpsilonCylinders; i++) {
            seriesesChart.get(i).getNode().setStyle("-fx-stroke: #BA15BA; ");
        }

        int offsetPoints = offsetEpsilonCylinders + countEpsilonCylinders;
        for (int i = offsetPoints; i < countPointSeries + offsetPoints; i++) {
            seriesesChart.get(i).getNode().setStyle("-fx-stroke: transparent; ");
        }
        return lineChart;
    }

    private static void removingNestedEpsilonCylinders(Map<Double, Set<EpsilonCylinder>> classesEpsilonCylinders) {
        for (double angle : classesEpsilonCylinders.keySet()) {
            Set<EpsilonCylinder> epsilonCylinders = classesEpsilonCylinders.get(angle);

            Iterator<EpsilonCylinder> iterator = epsilonCylinders.iterator();

            while (iterator.hasNext()) {
                EpsilonCylinder cylinder = iterator.next();

                for (EpsilonCylinder otherCylinder : epsilonCylinders) {
                    if (cylinder != otherCylinder && otherCylinder.contains(cylinder)) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    private static Map<Double, Set<EpsilonCylinder>> classificationByDeviationAngle(
            Point comparisonLinePoint1, Point comparisonLinePoint2, Set<EpsilonCylinder> epsilonCylinders) {
        final Map<Double, Set<EpsilonCylinder>> cylindersByAngle = new HashMap<>();
        for (EpsilonCylinder cylinder : epsilonCylinders) {
            double angle = Math.toDegrees(cylinder.calculateNormalizedAngle(comparisonLinePoint1, comparisonLinePoint2));
            double roundedAngle = Math.round(angle);
            if (roundedAngle > 180) {
                roundedAngle -= 180;
            }
            if (roundedAngle == 45 || roundedAngle == 135)
                continue;

            Set<EpsilonCylinder> cylinders = cylindersByAngle.getOrDefault(roundedAngle, new HashSet<>());
            cylinders.add(cylinder);
            cylindersByAngle.put(roundedAngle, cylinders);
        }
        return cylindersByAngle;
    }

    private static void drawComparisonLine(Point comparisonLinePoint1, Point comparisonLinePoint2,
                                           List<XYChart.Series<Number, Number>> seriesesChart) {
        final XYChart.Series<Number, Number> comparisonLineSeries = new XYChart.Series<>();
        Node node = new StackPane();
        Node node2 = new StackPane();

        node.setStyle("-fx-background-color: transparent, transparent;");
        node2.setStyle("-fx-background-color: transparent, transparent;");
        XYChart.Data<Number, Number> dataFirst =
                new XYChart.Data<>(comparisonLinePoint1.getX(), comparisonLinePoint1.getY());
        dataFirst.setNode(node);
        XYChart.Data<Number, Number> dataSecond =
                new XYChart.Data<>(comparisonLinePoint2.getX(), comparisonLinePoint2.getY());
        dataSecond.setNode(node2);
        comparisonLineSeries.getData().add(dataFirst);
        comparisonLineSeries.getData().add(dataSecond);

        seriesesChart.add(comparisonLineSeries);
    }

    private Map<Groups, List<Point>> convertToGroupsPointsMap(
            int selectedSensor1, int selectedSensor2, Map<Groups, Map<Integer, List<DataRow>>> groupsHumans) {
        final Map<Groups, List<Point>> groupsPoints = new LinkedHashMap<>();
        for (Groups group : groupsHumans.keySet()) {
            List<Point> points = new ArrayList<>();

            List<DataRow> groupData = groupsHumans.get(group).entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream()).toList();
            List<Integer> xValues = groupData.stream()
                    .map(row -> row.getSensor(selectedSensor1))
                    .toList();
            List<Integer> yValues = groupData.stream()
                    .map(row -> row.getSensor(selectedSensor2))
                    .toList();

            for (int humanId: groupsHumans.get(group).keySet()) {
                final List<DataRow> dataRows = groupsHumans.get(group).get(humanId);

                int x = dataRows.get(11).getSensor(selectedSensor1);
                int y = dataRows.get(11).getSensor(selectedSensor2);

                points.add(new Point(WithinClassElectrodeNormalization.normalize(xValues, x),
                        WithinClassElectrodeNormalization.normalize(yValues, y),
                        humanId));
            }

            groupsPoints.put(group, points);
        }

        List<Point> allPoints = groupsPoints.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .toList();

        double xMax = allPoints.stream().mapToDouble(Point::getX).max().orElseThrow();
        double xMin = allPoints.stream().mapToDouble(Point::getX).min().orElseThrow();

        double yMax = allPoints.stream().mapToDouble(Point::getY).max().orElseThrow();
        double yMin = allPoints.stream().mapToDouble(Point::getY).min().orElseThrow();

        for (var group : groupsPoints.keySet()) {
            List<Point> points = groupsPoints.get(group);
            points = points.stream()
                    .map(point -> new Point(normalize(xMax, xMin, point.getX()),
                            normalize(yMax, yMin, point.getY())))
                    .toList();

            groupsPoints.put(group, points);
        }

        return groupsPoints;
    }

    private double normalize(double max, double min, double value) {
        return (value - min) / (max - min);
    }

    private Map<Groups, Map<Integer, List<DataRow>>> readDataFromFile(String filePath) {

        final Map<Groups, Map<Integer, List<DataRow>>> groupsHumans = new LinkedHashMap<>();

        final CsvReader csvReader = new CsvReader(filePath);
        Groups currentGroup = null;
        Integer humanId = null;
        for (DataRow row : csvReader.readData()) {
            currentGroup = Groups.getGroupByFileName(row.getFileName());
            humanId = row.getHumanId();

            Map<Integer, List<DataRow>> humansPoints =
                    groupsHumans.getOrDefault(currentGroup, new HashMap<>());
            List<DataRow> points = humansPoints.getOrDefault(humanId, new ArrayList<>());

            points.add(row);

            humansPoints.put(humanId, points);
            groupsHumans.put(currentGroup, humansPoints);
        }

        return groupsHumans;
    }
}