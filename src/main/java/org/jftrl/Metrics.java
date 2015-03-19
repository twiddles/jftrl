/*
 * Metrics.java
 *
 * Project: Unified Network Objects UNO
 *
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2013
 */
package org.jftrl;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.util.List;

public class Metrics {

    public static double ε = 1e-15;

    /**
     * LogLoss function
     */
    public static double loss(double yPred, Label yTrue) {
        double p = max(min(yPred, 1 - ε), ε);
        return -log(yTrue == Label.TRUE ? p : (1 - p));
    }

    public static double logloss(double yTrue, double yPred) {
        double p = Math.max(Math.min(yPred, 1. - ε), ε);
        if (yTrue > 0.5) {
            return -log(p);
        } else {
            return -log(1.0 - p);
        }

    }

    public static double mean(List<Double> xs) {
        double sum = 0.0;
        for (Double x : xs) {
            sum += x;
        }
        return sum / xs.size();
    }

    public static double mean(double[] xs) {
        double sum = 0.0;
        for (int i = 0; i < xs.length; i++) {
            sum += xs[i];
        }
        return sum / xs.length;
    }

    public static double accuracy(double[] yTrue, double[] yPred) {
        int correct = 0;
        for (int i = 0; i < yTrue.length; i++) {
            if (round(yTrue[i]) == round(yPred[i])) {
                correct++;
            }
        }
        return 1.0 * correct / yTrue.length;
    }

}
