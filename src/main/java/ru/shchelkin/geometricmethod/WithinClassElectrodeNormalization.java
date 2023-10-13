package ru.shchelkin.geometricmethod;

import java.util.List;

public class WithinClassElectrodeNormalization {

    public static double normalize(List<Integer> values, int value) {
        double mean = getMean(values);
        double std = getStd(values, mean);

        return (value - mean) / std;
    }

    private static double getMean(List<Integer> values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    private static double getStd(List<Integer> values, double mean) {
        double sum = 0;
        for (int value : values) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / values.size());
    }
}
