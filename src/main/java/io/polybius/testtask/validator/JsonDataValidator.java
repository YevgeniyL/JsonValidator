package io.polybius.testtask.validator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.polybius.testtask.utils.ExpressionUtil;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.polybius.testtask.validator.Operator.*;

/**
 * Class validating with json data
 */
public class JsonDataValidator {
    private final Pattern booleanPattern = Pattern.compile("&&|\\|\\|", Pattern.CASE_INSENSITIVE);
    private final Pattern logicSymbPattern = Pattern.compile("(?<gtOrEq>>\\s*=)|(?<ltOrEq><\\s*=)|(?<gt>>)|(?<lt><)|(?<eq>=)", Pattern.CASE_INSENSITIVE);


    /**
     * Validate json data by input rules (query)
     * Separate 'query' to List of string and use it in data validation
     *
     * @param query           - text with predicates like 'name=Bobby && age<=20'
     * @param jsonObjectsList - list of json objects to validate
     * @return - validated list of json objects
     */
    public List<JsonObject> run(String query, List<JsonObject> jsonObjectsList) {
        if (query == null || query.trim().isEmpty()) {
            return jsonObjectsList;
        }

        List<Predicate> predicateList = buildPredicateList(query);
        return jsonObjectsList.stream()
                .filter(jsonObject -> isValidJson(jsonObject, predicateList))
                .collect(Collectors.toList());
    }

    /**
     * Splitting to groups by pattern '&&|\|\|' the query
     * And find boolean in string.
     * And for every group try to build predicate from group
     *
     * @param query - input query-text with predicates
     * @return List<Predicate> - list of predicates
     */
    private List<Predicate> buildPredicateList(String query) {
        List<Predicate> predicateList = new LinkedList<>();
        Matcher boolMatcher = booleanPattern.matcher(query);
        Integer oldEndPos = null;
        while (boolMatcher.find()) {
            final int newBoolPos = boolMatcher.start();
            final String pair = query.substring(oldEndPos != null ? oldEndPos : 0, newBoolPos);
            final Operator bool = query.substring(newBoolPos, boolMatcher.end()).equals(AND.val()) ? AND : OR;
            addNewPredicateToList(pair, bool, predicateList);
            oldEndPos = boolMatcher.end();
        }

        addNewPredicateToList(oldEndPos == null ? query : query.substring(oldEndPos), Operator.NONE, predicateList);
        return predicateList;
    }

    private void addNewPredicateToList(String query, Operator bool, List<Predicate> predicateList) {
        final Predicate predicate = buildPredicate(query, bool);
        if (predicate != null) {
            predicateList.add(predicate);
        }
    }

    /**
     * Create filled predicate.
     * Find first matched text in 'pair' by pattern '(?<gtOrEq>>\s*=)|(?<ltOrEq><\s*=)|(?<gt>>)|(?<lt><)|(?<eq>=)'
     * And setup
     *
     * @param pair unparsed query parametr
     * @param bool input boolean operator
     * @return Filled predicate
     */
    private Predicate buildPredicate(String pair, Operator bool) {
        Matcher matcher = logicSymbPattern.matcher(pair);
        if (matcher.find()) {
            final Operator foundOperator = logicOperators().stream()
                    .filter(op -> matcher.group(op.name()) != null)
                    .findFirst().orElse(null);

            if (foundOperator != null) {
                String unformattedComparator = matcher.group(foundOperator.name());
                return buildNewPredicate(pair, unformattedComparator, foundOperator, bool);
            }
        }

        return null;
    }

    /**
     * Building a filled predicate.
     *
     * @param pair                String with unclean text
     * @param comparatorTextValue Unclean comparator text: '<=', '< =', '>    =', .....
     * @param compareOperator     Operator.gt, ...gtOrEq, ltOrEq, lt, eq
     * @param booleanOperator     Operator.AND or Operator.OR
     * @return Predicate
     */
    private Predicate buildNewPredicate(String pair, String comparatorTextValue, Operator compareOperator, Operator booleanOperator) {
        int index = pair.indexOf(comparatorTextValue);
        final String name = pair.substring(0, index).trim();
        final String value = pair.substring(index + comparatorTextValue.length()).replace(" ", "");
        return new Predicate(name, value, compareOperator, booleanOperator);
    }

    /**
     * Method return a result of comparing jsons objects with all predicates.
     * Checking containing of all predicates in json
     * Compare predicate and json value and return result as boolean
     *
     * @return boolean result of json object validation
     */
    private boolean isValidJson(JsonObject jsonObj, List<Predicate> predicates) {
        boolean isContained = predicates.stream()
                .allMatch(predicate -> jsonObj.has(predicate.getName()));

        if (!isContained) {
            return false;
        }

        StringBuilder query = new StringBuilder();
        Boolean isValidComparing;
        for (Predicate predicate : predicates) {
            isValidComparing = null;
            JsonElement jsonElem = jsonObj.get(predicate.getName());
            switch (predicate.getCompareOperator()) {
                case eq:
                    isValidComparing = isEqual(jsonElem, predicate);
                    break;
                case gt:
                    isValidComparing = isGreaterThan(jsonElem, predicate);
                    break;
                case gtOrEq:
                    isValidComparing = isGreaterThanOrEqualTo(jsonElem, predicate);
                    break;
                case lt:
                    isValidComparing = isLessThan(jsonElem, predicate);
                    break;
                case ltOrEq:
                    isValidComparing = isLessThanOrEqualTo(jsonElem, predicate);
            }

            query.append(isValidComparing).append(predicate.getBooleanOperator().val());
        }

        String result = ExpressionUtil.simplify(query.toString());
        return Boolean.parseBoolean(result);
    }

    /**
     * Compare by 'EQUAL' jsonObject value with predicate value.
     * If json is a string - check for contain rule in json if ignore case
     *
     * @return Boolean
     */
    private boolean isEqual(JsonElement jsonElem, Predicate predicate) {
        if (jsonElem.getAsJsonPrimitive().isString()) {
            return jsonElem.getAsString().toLowerCase().contains(predicate.getValue().toLowerCase());
        }

        return isComparableNumbers(jsonElem, predicate)
                && jsonElem.getAsJsonPrimitive().getAsBigDecimal().compareTo(new BigDecimal(predicate.getValue())) == 0;
    }

    /**
     * Compare by 'GreaterThan' jsonObject value with predicate value
     *
     * @return Boolean
     */
    private boolean isGreaterThan(JsonElement jsonElem, Predicate predicate) {
        return isComparableNumbers(jsonElem, predicate)
                && jsonElem.getAsJsonPrimitive().getAsBigDecimal().compareTo(new BigDecimal(predicate.getValue())) > 0;
    }

    /**
     * Compare by 'GreaterThanOrEqualTo' jsonObject value with predicate value
     *
     * @return Boolean
     */
    private boolean isGreaterThanOrEqualTo(JsonElement jsonElem, Predicate predicate) {
        return isComparableNumbers(jsonElem, predicate)
                && jsonElem.getAsJsonPrimitive().getAsBigDecimal().compareTo(new BigDecimal(predicate.getValue())) >= 0;
    }

    /**
     * Compare by 'LessThan' jsonObject value with predicate value
     *
     * @return Boolean
     */
    private boolean isLessThan(JsonElement jsonElem, Predicate predicate) {
        return isComparableNumbers(jsonElem, predicate)
                && jsonElem.getAsJsonPrimitive().getAsBigDecimal().compareTo(new BigDecimal(predicate.getValue())) < 0;
    }

    /**
     * Compare by 'LessThanOrEqualTo' jsonObject value with predicate value
     *
     * @return Boolean
     */
    private boolean isLessThanOrEqualTo(JsonElement jsonElem, Predicate predicate) {
        return isComparableNumbers(jsonElem, predicate)
                && jsonElem.getAsJsonPrimitive().getAsBigDecimal().compareTo(new BigDecimal(predicate.getValue())) <= 0;
    }

    /**
     * Method check that jsonElement is a number type and check scale size on two types.
     *
     * @return boolean false if not number. Or it is a int and decimal
     */
    private boolean isComparableNumbers(JsonElement jsonElement, Predicate predicate) {
        if (!jsonElement.getAsJsonPrimitive().isNumber())
            return false;

        final BigDecimal predicateDecimal = new BigDecimal(predicate.getValue());
        final BigDecimal jsonDecimal = jsonElement.getAsJsonPrimitive().getAsBigDecimal();
        final int predicateScale = predicateDecimal.scale();
        final int jsonScale = jsonDecimal.scale();
        return predicateScale == jsonScale || predicateScale != 0 && jsonScale != 0;
    }
}
