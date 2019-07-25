package io.polybius.testtask.validator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Tests for validator json objects by input query
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class JsonValidatorTest {
    private static final double DELTA = 0;
    private static final Gson gson = new Gson();

    /**
     * Then query is empty, result need contain all objects
     */
    @Test
    public void testExpectAllObjectsThenEmptyIsQuery() {
        final String query = "";
        final String jsonName = "name";
        final String jsonValue = "Bobby";
        List<JsonObject> list = new LinkedList<>();
        JsonObject obj1 = new JsonObject();
        obj1.add(jsonName, new JsonPrimitive(jsonValue));
        list.add(obj1);
        List<JsonObject> result = new JsonDataValidator().run(query, list);

        Assert.assertEquals(1, result.size());
        final JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(jsonName));
        Assert.assertEquals(jsonValue, jsonObject.get(jsonName).getAsString());
    }

    /**
     * For strings json need to contain a value from query ignore case
     */
    @Test
    public void testEqualityForString() {
        final String query = "name=BoB";
        final String fieldName = "name";
        final String valid1 = "BoB";
        final String valid2 = "Bobby";
        final String notValid = "Rob";
        JsonObject jsonObj1 = new JsonObject();
        jsonObj1.add(fieldName, new JsonPrimitive(valid1));
        JsonObject jsonObj2 = new JsonObject();
        jsonObj2.add(fieldName, new JsonPrimitive(valid2));
        JsonObject jsonObj3 = new JsonObject();
        jsonObj3.add(fieldName, new JsonPrimitive(notValid));
        List<JsonObject> list = new LinkedList<>(Arrays.asList(jsonObj1, jsonObj2, jsonObj3));
        List<JsonObject> result = new JsonDataValidator().run(query, list);

        Assert.assertEquals(2, result.size());
        final JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldName));
        Assert.assertEquals(valid1, jsonObject.get(fieldName).getAsString());

        final JsonObject jsonObject2 = result.get(1);
        Assert.assertTrue(jsonObject2.has(fieldName));
        Assert.assertEquals(valid2, jsonObject2.get(fieldName).getAsString());
    }

    @Test
    public void testEqualityForNumbers() {
        String query = "age=10";
        final String fieldAge = "age";
        final int valueInt = 10;
        List<JsonObject> result = getValidatedIntData(query, fieldAge, valueInt);
        Assert.assertEquals(1, result.size());
        JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(valueInt, jsonObject.get(fieldAge).getAsInt());

        query = "age=10.0";
        final BigDecimal valueBigDecimal = new BigDecimal("10.0");
        result = getValidatedBIgDecimalData(query, fieldAge, valueBigDecimal);
        Assert.assertEquals(1, result.size());
        jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(valueBigDecimal, jsonObject.get(fieldAge).getAsBigDecimal());
    }

    @Test
    public void testEqualityNumberForWrongInt() {
        List<JsonObject> result = getValidatedIntData("age=10", "age", 9);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEqualityNumberForWrongDecimal() {
        List<JsonObject> result = getValidatedBIgDecimalData("age=10", "age", new BigDecimal("10.1"));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGreaterThanOrEqualInts() {
        final String query = "age>=10";
        final String fieldAge = "age";
        int value = 10;
        List<JsonObject> result = getValidatedIntData(query, fieldAge, value);
        Assert.assertEquals(1, result.size());
        JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(value, jsonObject.get(fieldAge).getAsInt());

        value = 11;
        result = getValidatedIntData(query, fieldAge, value);
        Assert.assertEquals(1, result.size());
        jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(value, jsonObject.get(fieldAge).getAsInt());
    }

    @Test
    public void testGreaterThanOrEqualBigDecimals() {
        final String query = "age>=10.1";
        final String fieldAge = "age";
        BigDecimal bigDecimal = new BigDecimal("10.1");
        List<JsonObject> result = getValidatedBIgDecimalData(query, fieldAge, bigDecimal);
        Assert.assertEquals(1, result.size());
        JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(bigDecimal, jsonObject.get(fieldAge).getAsBigDecimal());

        bigDecimal = new BigDecimal("11.5");
        result = getValidatedBIgDecimalData("age>=10.1", fieldAge, bigDecimal);
        Assert.assertEquals(1, result.size());
        jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(bigDecimal, jsonObject.get(fieldAge).getAsBigDecimal());
    }

    @Test
    public void testGreaterThanOrEqualForWrongInts() {
        List<JsonObject> result = getValidatedIntData("age>=10", "age", 9);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGreaterThanOrEqualForWrongDecimal() {
        List<JsonObject> result = getValidatedBIgDecimalData("age>=10.0", "age", new BigDecimal("9.9"));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGreaterThanInts() {
        final String query = "age>10";
        final String fieldAge = "age";
        final int value = 11;
        List<JsonObject> result = getValidatedIntData(query, fieldAge, value);

        Assert.assertEquals(1, result.size());
        JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(value, jsonObject.get(fieldAge).getAsInt());
    }

    @Test
    public void testGreaterThanBigDecimals() {
        final String query = "age>10.1";
        final String fieldAge = "age";
        BigDecimal bigDecimal = new BigDecimal("10.9");
        List<JsonObject> result = getValidatedBIgDecimalData(query, fieldAge, bigDecimal);

        Assert.assertEquals(1, result.size());
        JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(bigDecimal, jsonObject.get(fieldAge).getAsBigDecimal());
    }

    @Test
    public void testGreaterThanForWrongInt() {
        List<JsonObject> result = getValidatedIntData("age>10", "age", 10);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGreaterThanForWrongDecimal() {
        List<JsonObject> result = getValidatedBIgDecimalData("age>10.1", "age", new BigDecimal("10.1"));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLessThanOrEqualInts() {
        final String query = "age<=10";
        final String fieldAge = "age";
        int value = 10;
        List<JsonObject> result = getValidatedIntData(query, fieldAge, value);
        Assert.assertEquals(1, result.size());
        JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(value, jsonObject.get(fieldAge).getAsInt());

        value = 9;
        result = getValidatedIntData(query, fieldAge, value);
        Assert.assertEquals(1, result.size());
        jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(value, jsonObject.get(fieldAge).getAsInt());
    }

    @Test
    public void testLessThanOrEqual() {
        final String query = "age<=10.0";
        final String fieldAge = "age";
        BigDecimal decimalValue = new BigDecimal("10.0");
        List<JsonObject> result = getValidatedBIgDecimalData(query, fieldAge, decimalValue);
        Assert.assertEquals(1, result.size());
        JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(decimalValue, jsonObject.get(fieldAge).getAsBigDecimal());

        decimalValue = new BigDecimal("9.9");
        result = getValidatedBIgDecimalData(query, fieldAge, decimalValue);
        Assert.assertEquals(1, result.size());
        jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(decimalValue, jsonObject.get(fieldAge).getAsBigDecimal());
    }

    @Test
    public void testLessThanOrEqualForWrongInt() {
        List<JsonObject> result = getValidatedIntData("age<=10", "age", 11);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLessThanOrEqualForWrongDecimal() {
        List<JsonObject> result = getValidatedBIgDecimalData("age<=10.0", "age", new BigDecimal("10.1"));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLessThanInts() {
        final String query = "age<10";
        final String fieldAge = "age";
        final int value = 9;
        List<JsonObject> result = getValidatedIntData(query, fieldAge, value);

        Assert.assertEquals(1, result.size());
        JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(value, jsonObject.get(fieldAge).getAsInt());
    }

    @Test
    public void testLessThanBigDecimals() {
        final String query = "age<10.0";
        final String fieldAge = "age";
        BigDecimal decimalValue = new BigDecimal("9.9");
        List<JsonObject> result = getValidatedBIgDecimalData(query, fieldAge, decimalValue);

        Assert.assertEquals(1, result.size());
        JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldAge));
        Assert.assertEquals(decimalValue, jsonObject.get(fieldAge).getAsBigDecimal());
    }

    @Test
    public void testLessThanForWrongInt() {
        List<JsonObject> result = getValidatedIntData("age<10", "age", 10);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testLessThanForWrongDecimal() {
        List<JsonObject> result = getValidatedBIgDecimalData("age<10.0", "age", new BigDecimal("10.0"));
        Assert.assertEquals(0, result.size());
    }

    /**
     * result = true && true && true
     */
    @Test
    public void testForExpectAllValidInputData() {
        final String query = "name =Bob &&age>=10 && age<=40";
        final String fieldName1 = "name";
        final String value1 = "Bobby";
        final String fieldName2 = "age";
        final int jsonValue2 = 20;
        List<JsonObject> list = new LinkedList<>();
        JsonObject obj1 = new JsonObject();
        obj1.add(fieldName1, new JsonPrimitive(value1));
        obj1.add(fieldName2, new JsonPrimitive(jsonValue2));
        list.add(obj1);
        List<JsonObject> result = new JsonDataValidator().run(query, list);

        Assert.assertEquals(1, result.size());
        final JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldName1));
        Assert.assertTrue(jsonObject.has(fieldName2));
        Assert.assertEquals(value1, jsonObject.get(fieldName1).getAsString());
        Assert.assertEquals(jsonValue2, jsonObject.get(fieldName2).getAsJsonPrimitive().getAsInt());
    }

    /**
     * Put two valid objects, and wait two valid objects
     */
    @Test
    public void testForCollectAllValidInputDataToArray() {
        final String query = "name =Bob &&age>=10 && age<=40";
        final String fieldName1 = "name";
        final String value1 = "Bobby";
        final String fieldName2 = "age";
        final int jsonValue2 = 20;
        List<JsonObject> list = new LinkedList<>();
        JsonObject obj1 = new JsonObject();
        obj1.add(fieldName1, new JsonPrimitive(value1));
        obj1.add(fieldName2, new JsonPrimitive(jsonValue2));
        list.add(obj1);
        list.add(obj1);

        List<JsonObject> result = new JsonDataValidator().run(query, list);
        Assert.assertEquals(2, result.size());
        for (int i = 0; i < 2; i++) {
            final JsonObject jsonObject = result.get(i);
            Assert.assertTrue(jsonObject.has(fieldName1));
            Assert.assertTrue(jsonObject.has(fieldName2));
            Assert.assertEquals(value1, jsonObject.get(fieldName1).getAsString());
            Assert.assertEquals(jsonValue2, jsonObject.get(fieldName2).getAsJsonPrimitive().getAsInt());
        }
    }

    /**
     * result = all false
     */
    @Test
    public void testForAllFailedLogicCombinations() {
        final String query = "name =Rob || age=100 || age<20||age>20||age>=21";
        final String fieldName1 = "name";
        final String value1 = "Bobby";
        final String fieldName2 = "age";
        final int jsonValue2 = 20;
        List<JsonObject> list = new LinkedList<>();
        JsonObject obj1 = new JsonObject();
        obj1.add(fieldName1, new JsonPrimitive(value1));
        obj1.add(fieldName2, new JsonPrimitive(jsonValue2));
        list.add(obj1);

        List<JsonObject> result = new JsonDataValidator().run(query, list);
        Assert.assertEquals(0, result.size());
    }

    private List<JsonObject> getValidatedIntData(String query, String fieldName, int jsonValue) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.add(fieldName, new JsonPrimitive(jsonValue));
        return validate(query, jsonObj);
    }

    private List<JsonObject> getValidatedBIgDecimalData(String query, String fieldName, BigDecimal jsonValue) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.add(fieldName, new JsonPrimitive(jsonValue));
        return validate(query, jsonObj);
    }

    private List<JsonObject> validate(String query, JsonObject jsonObj) {
        List<JsonObject> list = new LinkedList<>(Collections.singletonList(jsonObj));
        return new JsonDataValidator().run(query, list);
    }
}
