package org.jftrl;

import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.jftrl.Label.FALSE;
import static org.jftrl.Label.TRUE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import org.apache.log4j.Logger;
import org.junit.Test;

public class FTRLTest {

    Logger LOG = Logger.getLogger(FTRLTest.class);

    @Test
    public void test_XOR() throws Exception {
        FTRL clf = new FTRL();
        clf.interactions = 2;
        clf.λ1 = 0.0;

        clf.fit("true true", FALSE);
        clf.fit("true false", TRUE);
        clf.fit("false true", TRUE);
        clf.fit("false false", FALSE);

        assertEquals(FALSE, clf.predict("true true"));
        assertEquals(TRUE, clf.predict("false true"));
        assertEquals(TRUE, clf.predict("true false"));
        assertEquals(FALSE, clf.predict("false false"));
    }

    @Test
    public void test_majority() throws Exception {
        FTRL clf = new FTRL();
        clf.λ1 = 0.1;
        clf.α = 0.1;

        clf.fit("true true", FALSE);
        clf.fit("true true", TRUE);
        clf.fit("true true", TRUE);
        assertEquals(Label.TRUE, clf.predict("true true"));
    }

    @Test
    public void test_feature_hashing() throws Exception {
        FTRL clf = new FTRL();

        assertArrayEquals(clf.features("2 A"), clf.features("2 A"));
        assertEquals(clf.features("2 A #")[0], clf.features("2 B #")[0]);
        assertNotEquals(clf.features("2 A #")[1], clf.features("2 B #")[1]);
        assertEquals(clf.features("2 A #")[2], clf.features("2 B #")[2]);
    }

    @Test
    public void test_num_feature_indices() throws Exception {
        assertThat(FTRL.numFeatures(new String[] { "A", "B", "C" }, 1), is(3));
        assertThat(FTRL.numFeatures(new String[] { "A", "B", "C" }, 2), is(6));
        assertThat(FTRL.numFeatures(new String[] { "A", "B", "C" }, 3), is(7));
        assertThat(FTRL.numFeatures(new String[] { "A", "B", "C", "D" }, 3), is(14));
        assertThat(FTRL.numFeatures(new String[] { "A", "B", "C", "D", "E" }, 3), is(25));
    }

    @Test
    public void test_feature_interactions() throws Exception {
        FTRL clf = new FTRL();
        clf.interactions = 1;
        assertThat(clf.features("1 2 3").length, is(3)); // 1, 2, 3
        clf.interactions = 2;
        assertThat(clf.features("1 2 3").length, is(6)); // 1, 2, 3, 12, 13, 23
        clf.interactions = 3;
        assertThat(clf.features("1 2 3").length, is(7)); // 1, 2, 3, 12, 13, 23, 123
        clf.interactions = 3;
        assertThat(clf.features("1 2 3 4").length, is(14)); // 1, 2, 3, 4, 12, 13, 14, 23, 24, 123, 124, 134, 234
    }

    @Test
    public void test_equal_seeding() throws Exception {
        FTRL clf1 = new FTRL();
        clf1.numFeatures = 100;
        FTRL clf2 = new FTRL();
        clf2.numFeatures = 100;

        for (int i = 0; i < 1000; i++) {
            Label yTrue = i % 2 == 0 ? TRUE : FALSE;
            clf1.fit(valueOf(i), yTrue);
            clf2.fit(valueOf(i), yTrue);
        }
        for (int i = 0; i < 1000; i++) {
            assertEquals(clf1.predict(valueOf(i)), clf2.predict(valueOf(i)));
            assertArrayEquals(clf1.features(valueOf(i)), clf2.features(valueOf(i)));
        }
    }

    @Test(timeout = 3000)
    public void test_hashing_speed() throws Exception {
        FTRL clf = new FTRL();
        clf.interactions = 3;

        for (int k = 0; k < 10; k++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                clf.features("foo bar baz zap dap hap map cap lab froz spoz sploz");
            }
            LOG.info("Hashing done in: " + (System.currentTimeMillis() - start) + "ms");
        }
    }
}
