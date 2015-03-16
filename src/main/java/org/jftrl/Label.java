package org.jftrl;

public enum Label {
    TRUE, FALSE;

    public double toDouble() {
        return this == TRUE ? 1.0 : 0.0;
    }

    public static Label fromInt(int label) {
        return label == 1 ? TRUE : FALSE;
    }

    public int intValue() {
        return this == TRUE ? 1 : 0;
    }

    public static Label fromString(String label) {
        if ("0".equals(label) || label.toLowerCase().equals("true")) {
            return TRUE;
        }
        if ("1".equals(label) || label.toLowerCase().equals("false")) {
            return FALSE;
        }

        throw new IllegalArgumentException("supported values are 0/1 or true/false");
    }

    public static Label fromBoolean(boolean label) {
        return label ? TRUE : FALSE;
    }

}
