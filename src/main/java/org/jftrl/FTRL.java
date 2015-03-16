package org.jftrl;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * "Follow the (Proximally) Regularized Leader"-Algorithm
 * 
 * @see http://goo.gl/iqIaH0
 * @see http://www.eecs.tufts.edu/~dsculley/papers/ad-click-prediction.pdf
 */
public class FTRL {
    private static final String US = "_";

    private static final String BLANKS = "\\s+";

    public static double ε = 1e-15;

    public int numFeatures = (int) Math.pow(2, 16);
    public double α = 0.1; // learning rate

    private double[] w;
    private double[] z;
    private double[] η;

    public int interactions = 1;

    public double λ1 = 1.0; // L1-Regularization
    public double λ2 = 1.0; // L2-Regularization
    public double β = 1.0;

    public long numRounds = 0;

    public double fit(String[][] lines, Label[] y) {
        double loss = 0.0;
        for (int i = 0; i < lines.length; i++) {
            loss += fit(lines[i], y[i]);
        }
        return loss / lines.length;
    }

    public double fit(String line, Label yTrue) {
        return fit(line.split(BLANKS), yTrue);
    }

    public double fit(String[] line, Label yTrue) {
        if (yTrue == null) {
            throw new RuntimeException("Label must not be <null> when fitting.");
        }

        int[] features = features(line);
        double yPred = predictProba(features);
        double loss = loss(yPred, yTrue);
        update(features, yPred, yTrue);
        numRounds++;

        return loss;
    }

    public Label predict(String data) {
        return predict(data.split(BLANKS));
    }

    public Label predict(String[] data) {
        return predict(features(data));
    }

    private Label predict(int[] features) {
        return predictProba(features) > 0.5 ? Label.TRUE : Label.FALSE;
    }

    public double predictProba(String data) {
        return predictProba(data.split(BLANKS));
    }

    public double predictProba(String[] data) {
        return predictProba(features(data));
    }

    public double[] predictProba(String[][] data) {
        double[] yPred = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            yPred[i] = predictProba(features(data[i]));
        }
        return yPred;
    }

    /**
     * Given a set of features indices, predicts wheather the instance belongs to the 0 or 1 class.
     * 
     * @param x feature indices
     * @return a probability between 0.0 and 1.0
     */
    public double predictProba(int[] x) {
        if (w == null) {
            η = new double[numFeatures];
            w = new double[numFeatures];
            z = new double[numFeatures];
        }

        double wTx = 0.0d;
        for (int i = 0; i < x.length; ++i) {
            int xi = x[i];
            int sign = z[xi] < 0 ? -1 : 1;
            if (sign * z[xi] < λ1) {
                w[xi] = 0.0;
            } else {
                w[xi] = -1.0 / ((β + sqrt(η[xi])) / α + λ2) * (z[xi] - sign * λ1);
            }

            wTx += w[xi];
        }
        return σ(wTx);
    }

    /**
     * Updates the weights given the following parameters:
     * 
     * @param x feature indices
     * @param p the predicted label
     * @param y the true label
     */
    public void update(int[] x, double p, Label y) {
        double gi = p - y.toDouble();
        double g2 = gi * gi;

        for (int i = 0; i < x.length; ++i) {
            int xi = x[i];
            double si = 1.0 / α * (sqrt(η[xi] + g2) - sqrt(η[xi]));
            z[xi] = z[xi] + gi - si * w[xi];
            η[xi] += g2;
        }
    }

    public int[] features(String parts) {
        return features(parts.split(BLANKS));
    }

    /**
     * Generates feature indices using feature hashing and based on the number of interaction terms (1-3 are currently supported)
     * 
     * @param parts a sample
     * @return feature indices
     */
    public int[] features(String[] parts) {
        if (interactions < 1 && interactions > 4) {
            throw new IllegalArgumentException("interactions must be either 1, 2 or 3");
        }

        int size = numFeatures(parts, interactions);

        int[] featureIndices = new int[size];
        int ix = 0;

        if (interactions >= 1) {
            for (int i = 0; i < parts.length; i++) {
                int hash = Objects.hash(i, US, parts[i]);
                featureIndices[ix++] = Math.abs(hash) % numFeatures;
            }
        }

        if (interactions >= 2) {
            for (int i = 0; i < parts.length; i++) {
                for (int j = i + 1; j < parts.length; j++) {
                    int hash = Objects.hash(i, US, j, US, parts[i], US, parts[j]);
                    featureIndices[ix++] = Math.abs(hash) % numFeatures;
                }
            }
        }
        if (interactions >= 3) {
            for (int i = 0; i < parts.length; i++) {
                for (int j = i + 1; j < parts.length; j++) {
                    for (int k = j + 1; k < parts.length; k++) {
                        int hash = Objects.hash(i, US, j, US, k, US, parts[i], US, parts[j], US, parts[k]);
                        featureIndices[ix++] = Math.abs(hash) % numFeatures;
                    }
                }
            }
        }

        if (featureIndices.length != ix) {
            throw new IllegalStateException("post-condition violated: pre-allocated array does not match the number of features");
        }
        return featureIndices;
    }

    public static int numFeatures(String[] parts, int interactions) {
        int ix = parts.length;

        if (interactions >= 2) {
            int n = parts.length - 1;
            ix += (n * (n + 1)) / 2;
        }

        if (interactions >= 3) {
            int n = parts.length;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    ix += n - (j + 1);
                }
            }
        }
        return ix;
    }

    /**
     * Sigmoid function
     */
    public static double σ(double x) {
        return 1.0 / (1 + exp(-max(min(x, 20), -20)));
    }

    /**
     * LogLoss function
     */
    public static double loss(double yPred, Label yTrue) {
        double p = max(min(yPred, 1 - ε), ε);
        return -log(yTrue == Label.TRUE ? p : (1 - p));
    }

    public static BufferedReader reader(String fileName) throws FileNotFoundException {
        return new BufferedReader(new FileReader(fileName));
    }

    public static PrintWriter writer(String filename) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(new File(filename))));
    }

    public static String[] slice(String[] values, int start) {
        String[] result = new String[values.length - start];
        System.arraycopy(values, start, result, 0, values.length - start);
        return result;
    }

    public static double logloss(double yTrue, double yPred) {
        double p = Math.max(Math.min(yPred, 1. - ε), ε);
        if (yTrue > 0.5) {
            return -log(p);
        } else {
            return -log(1.0 - p);
        }

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