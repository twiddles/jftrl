package org.jftrl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LabelTest {

    @Test
    public void test_conversion() throws Exception {
        assertEquals(Label.TRUE, Label.fromString("1"));
        assertEquals(Label.TRUE, Label.fromString("True"));
        assertEquals(Label.TRUE, Label.fromString("TRUE"));
        assertEquals(Label.TRUE, Label.fromString("true"));

        assertEquals(Label.FALSE, Label.fromString("0"));
        assertEquals(Label.FALSE, Label.fromString("false"));
        assertEquals(Label.FALSE, Label.fromString("False"));
        assertEquals(Label.FALSE, Label.fromString("FALSE"));
    }

}
