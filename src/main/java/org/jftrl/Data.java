/*
 * Data.java
 *
 * Project: Unified Network Objects UNO
 *
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2013
 */
package org.jftrl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Data {

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

    public static List<String> split(String line, String string) {
        return new ArrayList<String>(Arrays.asList(line.split(",")));
    }

    public static List<String> sublist(List<String> values, int start) {
        return values.subList(start, values.size());
    }

    public static String[][] toArray(List<List<String>> data) {
        String[][] result = new String[data.size()][];
        int i = 0;
        for (List<String> row : data) {
            result[i++] = (row.toArray(new String[0]));
        }
        return result;
    }

    /**
     * Shuffles two array in-place.
     */
    public static void shuffle(String[][] X, Label[] y) {
        String[][] X_copy = new String[X.length][];
        Label[] y_copy = new Label[y.length];
    
        System.arraycopy(X, 0, X_copy, 0, X.length);
        System.arraycopy(y, 0, y_copy, 0, y.length);
    
        List<Integer> idx = range(X.length);
        Collections.shuffle(idx);
    
        for (int i = 0; i < X.length; i++) {
            X[i] = X_copy[idx.get(i)];
            y[i] = y_copy[idx.get(i)];
        }
    }

    /**
     * Returns a list of integer from 0 to length-1.
     * 
     * <pre>
     * range(1) = [0]
     * range(2) = [0, 1]
     * range(3) = [0, 1, 2]
     * </pre>
     */
    public static List<Integer> range(int length) {
        List<Integer> idx = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            idx.add(i);
        }
        return idx;
    }

}
