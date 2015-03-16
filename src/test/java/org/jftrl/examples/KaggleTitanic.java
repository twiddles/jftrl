/*
 * KaggleTitanic.java
 *
 * Project: Unified Network Objects UNO
 *
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2013
 */
package org.jftrl.examples;

import static org.jftrl.FTRL.reader;
import static org.jftrl.FTRL.slice;
import static org.jftrl.FTRL.writer;
import static org.jftrl.Label.fromString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.jftrl.FTRL;
import org.jftrl.Label;

public class KaggleTitanic {
    public static void main(String[] args) throws IOException {
        // Create a new instance of FTRL and set some parameters
        FTRL clf = new FTRL();
        clf.numFeatures = (int) Math.pow(2, 24);
        clf.interactions = 3;
        clf.λ1 = 0.0;

        for (int pass = 0; pass < 10; pass++) {
            System.out.println("Current pass over data: " + (pass + 1));

            BufferedReader train = reader("data/titanic/train.csv");
            // skip header info
            train.readLine();

            // PassengerId,Survived,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked
            String line = null;
            while ((line = train.readLine()) != null) {
                String[] values = line.split(",");
                String[] x = slice(values, 2); // Pclass,Name,....
                Label y = fromString(values[1]); // Survived
                clf.fit(x, y);
            }
        }

        // Write the submission file
        PrintWriter submission = writer("data/titanic/submission.csv");
        submission.println("PassengerId,survived");

        BufferedReader test = reader("data/titanic/test.csv");
        // skip header info
        test.readLine();
        // PassengerId,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked
        String line = null;
        while ((line = test.readLine()) != null) {
            String[] values = line.split(",");
            String passengerId = values[0]; // PassengerId
            String[] x = slice(values, 1); // Pclass,Name,..
            Label survived = clf.predict(x); // predict
            submission.println(passengerId + "," + survived.intValue()); // 1 - survived, 0 - did not surive
        }
        submission.close();
    }
}
