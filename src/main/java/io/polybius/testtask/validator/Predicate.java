package io.polybius.testtask.validator;

/**
 * Contain parsed pair data from query
 */
public class Predicate {
    private final String name;
    private final String value;
    private final Operator compareOperator;
    private final Operator booleanOperator;

    public Predicate(String name, String value, Operator compareOperator, Operator booleanOperator) {
        this.name = name;
        this.value = value;
        this.compareOperator = compareOperator;
        this.booleanOperator = booleanOperator;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Operator getCompareOperator() {
        return compareOperator;
    }

    public Operator getBooleanOperator() {
        return booleanOperator;
    }
}
