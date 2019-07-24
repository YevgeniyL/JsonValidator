package io.polybius.testtask.utils;

import io.polybius.testtask.validator.Operator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static io.polybius.testtask.utils.ExpressionUtil.simplify;

/**
 * Tests for ExpressionUtil
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CompareUtilTest {

    private final String and = Operator.AND.val();
    private final String or = Operator.OR.val();

    /**
     * Test for validation the simplifier with '&&' combinations
     */
    @Test
    public void testForExpressionAnd() {
        Assert.assertEquals("true", simplify("true" + and + "true"));
        Assert.assertEquals("true", simplify("true" + and + "true" + and + "true"));
        Assert.assertEquals("false", simplify("true" + and + "false" + and + "true"));
        Assert.assertEquals("false", simplify("true" + and + "false"));
        Assert.assertEquals("false", simplify("true" + and + "true" + and + "false"));
    }

    /**
     * Test for validation the simplifier with '||' combinations
     */
    @Test
    public void testForExpressionOr() {
        Assert.assertEquals("true", simplify("false" + or + "true"));
        Assert.assertEquals("true", simplify("true" + or + "false" + or + "false"));
        Assert.assertEquals("true", simplify("false" + or + "false" + or + "true"));
        Assert.assertEquals("false", simplify("false" + or + "false"));
        Assert.assertEquals("false", simplify("false" + or + "false" + or + "false"));
    }

    /**
     * Test for validation the simplifier with '||' and '&&' combinations
     */
    @Test
    public void testOfCombinationsAndOr() {
        Assert.assertEquals("true", simplify("true" + and + "false" + or + "true"));
        Assert.assertEquals("true", simplify("false" + or + "false" + and + "false" + or + "false" + or + "true"));
        Assert.assertEquals("true", simplify("true" + and + "false" + or + "true" + and + "true"));
        Assert.assertEquals("false", simplify("true" + and + "false" + or + "false"));
        Assert.assertEquals("false", simplify("false" + and + "false" + or + "false"));
    }
}
