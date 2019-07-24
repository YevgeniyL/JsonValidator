package io.polybius.testtask.utils;

import static io.polybius.testtask.validator.Operator.AND;
import static io.polybius.testtask.validator.Operator.OR;

/**
 * Util for work with expressions
 */
public class ExpressionUtil {
    private static String trueText = "true";
    private static String falseText = "false";
    private static int operatorsLength = 2;
    private static int lengthOfTrue = 4;
    private static int lengthOfFalse = 5;

    /**
     * Simplify input data and return result as 'true' or 'false' with priority on '&&'
     * Input data like 'true&&false||false.......'
     *
     * @param text - only 'true','false,'&&','||' words as input data
     * @return string 'true' or 'false'
     */
    public static String simplify(String text) {
        text = simplifyAnd(text);
        text = simplifyOr(text);
        return text;
    }

    /**
     * Simplifying logic with many combinations 'AND' to replace this pairs by one word 'true' or 'false'
     * Operator '&&' has priority in string. Perform it first.
     *
     * @param text - any words 'true', 'false', '||'
     * @return text without '&&'
     */
    private static String simplifyAnd(String text) {
        if (text.contains(AND.val())) {
            final int operatorsPosition = text.indexOf(AND.val());
            Integer leftPos = null;
            Integer rigthPos = null;

            //Find left operator near first '&&'
            String left = text.substring(operatorsPosition - lengthOfTrue, operatorsPosition);
            if (left.equals(trueText)) {
                leftPos = operatorsPosition - lengthOfTrue;
            } else {
                left = text.substring(operatorsPosition - lengthOfFalse, operatorsPosition);
                if (left.equals(falseText)) {
                    leftPos = operatorsPosition - lengthOfFalse;
                }
            }

            //Find first operator after first '&&'
            String right = text.substring(operatorsPosition + operatorsLength, operatorsPosition + operatorsLength + lengthOfTrue);
            if (right.equals(trueText)) {
                rigthPos = operatorsPosition + operatorsLength + lengthOfTrue;
            } else {
                right = text.substring(operatorsPosition + operatorsLength, operatorsPosition + operatorsLength + lengthOfFalse);
                if (right.equals(falseText)) {
                    rigthPos = operatorsPosition + operatorsLength + lengthOfFalse;
                }
            }

            if (leftPos == null || rigthPos == null) {
                //need do throw exception, but in Main.class not have catch
                System.out.println("Exception in simplifyAnd block");
                return "false";
            }

            //Text replacer 'true&&true' to once 'true' else to once 'false'
            if (left.equals(trueText) && right.equals(trueText)) {
                text = text.substring(0, leftPos) + trueText + text.substring(rigthPos);
            } else {
                text = text.substring(0, leftPos) + falseText + text.substring(rigthPos);
            }

            return simplifyAnd(text);
        }

        return text;
    }

    /**
     * Simplifying logic with many combinations 'OR' to one word 'true' or 'false'
     *
     * @param text - any words 'true', 'false', '||'
     * @return text without '||'
     */
    private static String simplifyOr(String text) {
        if (text.contains(OR.val())) {
            final String val = text.substring(0, text.indexOf(OR.val()));
            if (val.equals(trueText)) {
                return trueText;
            } else {
                text = text.substring(7); // remove first 'false||'
            }

            return simplifyOr(text);
        }

        return text;
    }


}
