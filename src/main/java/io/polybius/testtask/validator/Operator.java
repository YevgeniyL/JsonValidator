package io.polybius.testtask.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum with all parsed operators
 * Fields is a Nickname 'gtOrEq' and clean value '>=' of operator
 * Has list of only 'logicOperators'
 */
public enum Operator {
    gtOrEq(">="),
    ltOrEq("<="),
    gt(">"),
    lt("<"),
    eq("="),
    AND("&&"),
    OR("||"),
    NONE(null);

    private String value;

    private final static List<Operator> logicOperators = Arrays.stream(Operator.values())
            .filter(operator -> !"&&".equals(operator.val()) && !"||".equals(operator.val()))
            .collect(Collectors.toList());

    Operator(String value) {
        this.value = value;
    }

    public static List<Operator> logicOperators() {
        return logicOperators;
    }

    public String val() {
        return this.value == null ? "" : this.value;
    }
}