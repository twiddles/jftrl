package org.jftrl;

import static java.lang.String.format;
import static org.jftrl.Data.reader;
import static org.jftrl.Data.split;
import static org.jftrl.Data.sublist;
import static org.jftrl.Data.toArray;
import static org.jftrl.Data.writer;
import static org.jftrl.Label.fromString;
import static org.jftrl.Metrics.mean;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Sample code for the "Titanic: Machine Learning from disaster"-competition hosted at Kaggle.
 * 
 * Includes cross-validation, fitting and the creation of the submission file.
 * 
 * http://www.kaggle.com/c/titanic-gettingStarted
 */
public class KaggleTitanicTest {

    private static Logger LOG = Logger.getLogger(KaggleTitanicTest.class);
    private static final int NUM_CV_FOLDS = 20;
    private static final int NUM_PASSES = 10;

    @Test
    public void testPipeline() throws IOException {
        // Create a new instance of FTRL and set some parameters
        FTRL clf = new FTRL();
        clf.numFeatures = (int) Math.pow(2, 18);
        clf.interactions = 1;
        clf.λ1 = 0.01;
        clf.λ2 = 0.01;
        clf.β = 0.1;

        crossValidate(clf);
        fit(clf);
        predict(clf);
    }

    private static void crossValidate(FTRL clf) throws IOException {
        List<List<String>> trainingData = new ArrayList<>();
        List<Label> y = new ArrayList<>();

        BufferedReader train = reader("data/titanic/train.csv");
        // skip header info
        train.readLine();

        // PassengerId,Survived,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked
        String line = null;
        while ((line = train.readLine()) != null) {
            List<String> values = split(line, ",");
            List<String> x = toFeatures(sublist(values, 2)); // Pclass,Name,....
            trainingData.add(x);
            y.add(fromString(values.get(1))); // Survived
        }

        int numFolds = NUM_CV_FOLDS;
        List<Double> scores = FTRL.crossValidate(clf, toArray(trainingData), y.toArray(new Label[0]), numFolds, NUM_PASSES);
        double meanScore = mean(scores);
        LOG.info(format("Cross Validation with %s-fold CV results in mean accuracy of %s.", numFolds, meanScore));

        assertEquals("FTRL should have a CV-Score of about 0.76-0.84", 0.80, meanScore, 0.04);
    }

    private void fit(FTRL clf) throws FileNotFoundException, IOException {
        LOG.info("Fitting in " + NUM_PASSES + " passes.");
        for (int pass = 0; pass < NUM_PASSES; pass++) {
            BufferedReader train = reader("data/titanic/train.csv");
            // skip header info
            train.readLine();

            // PassengerId,Survived,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked
            String line = null;
            while ((line = train.readLine()) != null) {
                List<String> values = split(line, ",");
                List<String> x = toFeatures(sublist(values, 2)); // Pclass,Name,....
                Label y = fromString(values.get(1)); // Survived
                clf.fit(x.toArray(new String[0]), y);
            }
        }
    }

    /**
     * Writes the submission file according to the specified format by Kaggle.
     * 
     * @param clf The classifier used to perform the predictions.
     */
    private void predict(FTRL clf) throws IOException, FileNotFoundException {
        // Write the submission file
        PrintWriter submission = writer("data/titanic/submission.csv");
        submission.println("PassengerId,survived");

        BufferedReader test = reader("data/titanic/test.csv");
        // skip header info
        test.readLine();
        // PassengerId,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked
        String line = null;
        while ((line = test.readLine()) != null) {
            List<String> values = split(line, ",");
            String passengerId = values.get(0); // PassengerId
            List<String> x = toFeatures(sublist(values, 1));
            Label survived = clf.predict(x.toArray(new String[0])); // predict
            submission.println(passengerId + "," + survived.intValue()); // 1 - survived, 0 - did not surive
        }
        submission.close();
    }

    /**
     * Some minimal/lazy feature engineering (e.g. split the cabin id into two additional columns "B45" -> "B", "45".
     */
    private static List<String> toFeatures(List<String> x) {
        x = new ArrayList<>(x);
        String cabin = x.get(9);
        if (!"".equals(cabin)) {
            x.add("" + cabin.charAt(0));
            x.add(cabin.replaceAll("[a-zA-Z]", ""));
        } else {
            x.add("");
            x.add("");
        }
        return x;
    }
}
