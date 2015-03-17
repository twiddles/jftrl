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
import java.util.List;

/**
 * 
 * 
 * @version $Id: $
 * @since 10.7.0
 */
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

}
