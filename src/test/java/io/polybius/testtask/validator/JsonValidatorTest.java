package io.polybius.testtask.validator;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Tests for validator json objects by input query
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class JsonValidatorTest {

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

    @Test
    public void testEqualityForString() {
        final String query = "name=Bobby";
        final String fieldName = "name";
        final String valid1 = "Bobby";
        final String valid2 = "BoB";
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
    public void testEqualityForNumber() {
        final String query = "age=10";
        final String fieldName = "age";
        final int valid1 = 10;
        final double valid2 = 10.0;
        final long notValid1 = 11;
        final double notValid2 = 11.0;
        JsonObject jsonObj1 = new JsonObject();
        jsonObj1.add(fieldName, new JsonPrimitive(valid1));
        JsonObject jsonObj2 = new JsonObject();
        jsonObj2.add(fieldName, new JsonPrimitive(valid2));
        JsonObject jsonObj3 = new JsonObject();
        jsonObj3.add(fieldName, new JsonPrimitive(notValid1));
        JsonObject jsonObj4 = new JsonObject();
        jsonObj3.add(fieldName, new JsonPrimitive(notValid2));
        List<JsonObject> list = new LinkedList<>(Arrays.asList(jsonObj1, jsonObj2, jsonObj3, jsonObj4));

        List<JsonObject> result = new JsonDataValidator().run(query, list);
        Assert.assertEquals(2, result.size());
        final JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldName));
        Assert.assertEquals(valid1, jsonObject.get(fieldName).getAsInt());

        final JsonObject jsonObject2 = result.get(1);
        Assert.assertTrue(jsonObject2.has(fieldName));
        Assert.assertEquals(valid2, jsonObject2.get(fieldName).getAsDouble(), 2);
    }

    @Test
    public void testGtOrEq() {
        final String query = "age>=10";
        final String fieldName = "age";
        final int jsonValue1 = 10;
        final int jsonValue2 = 20;
        final int jsonValue3 = 9;
        JsonObject jsonObj1 = new JsonObject();
        jsonObj1.add(fieldName, new JsonPrimitive(jsonValue1));
        JsonObject jsonObj2 = new JsonObject();
        jsonObj2.add(fieldName, new JsonPrimitive(jsonValue2));
        JsonObject jsonObj3 = new JsonObject();
        jsonObj3.add(fieldName, new JsonPrimitive(jsonValue3));
        List<JsonObject> list = new LinkedList<>(Arrays.asList(jsonObj1, jsonObj2, jsonObj3));

        List<JsonObject> result = new JsonDataValidator().run(query, list);
        Assert.assertEquals(2, result.size());
        final JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldName));
        Assert.assertEquals(jsonValue1, jsonObject.get(fieldName).getAsInt());

        final JsonObject jsonObject2 = result.get(1);
        Assert.assertTrue(jsonObject2.has(fieldName));
        Assert.assertEquals(jsonValue2, jsonObject2.get(fieldName).getAsInt());
    }

    @Test
    public void testGt() {
        final String query = "age>10";
        final String fieldName = "age";
        final int jsonValue1 = 11;
        final int jsonValue2 = 10;
        JsonObject jsonObj1 = new JsonObject();
        jsonObj1.add(fieldName, new JsonPrimitive(jsonValue1));
        JsonObject jsonObj2 = new JsonObject();
        jsonObj2.add(fieldName, new JsonPrimitive(jsonValue2));
        List<JsonObject> list = new LinkedList<>(Arrays.asList(jsonObj1, jsonObj2));

        List<JsonObject> result = new JsonDataValidator().run(query, list);
        Assert.assertEquals(1, result.size());
        final JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldName));
        Assert.assertEquals(jsonValue1, jsonObject.get(fieldName).getAsInt());
    }

    @Test
    public void testLtOrEq() {
        final String query = "age<=10";
        final String fieldName = "age";
        final int jsonValue1 = 10;
        final int jsonValue2 = 9;
        final int jsonValue3 = 11;
        JsonObject jsonObj1 = new JsonObject();
        jsonObj1.add(fieldName, new JsonPrimitive(jsonValue1));
        JsonObject jsonObj2 = new JsonObject();
        jsonObj2.add(fieldName, new JsonPrimitive(jsonValue2));
        JsonObject jsonObj3 = new JsonObject();
        jsonObj3.add(fieldName, new JsonPrimitive(jsonValue3));
        List<JsonObject> list = new LinkedList<>(Arrays.asList(jsonObj1, jsonObj2, jsonObj3));

        List<JsonObject> result = new JsonDataValidator().run(query, list);
        Assert.assertEquals(2, result.size());
        final JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldName));
        Assert.assertEquals(jsonValue1, jsonObject.get(fieldName).getAsInt());
        final JsonObject jsonObject2 = result.get(1);
        Assert.assertTrue(jsonObject2.has(fieldName));
        Assert.assertEquals(jsonValue2, jsonObject2.get(fieldName).getAsInt());
    }

    @Test
    public void testLt() {
        final String query = "age<10";
        final String fieldName = "age";
        final int jsonValue1 = 9;
        final int jsonValue2 = 10;
        JsonObject jsonObj1 = new JsonObject();
        jsonObj1.add(fieldName, new JsonPrimitive(jsonValue1));
        JsonObject jsonObj2 = new JsonObject();
        jsonObj2.add(fieldName, new JsonPrimitive(jsonValue2));
        List<JsonObject> list = new LinkedList<>(Arrays.asList(jsonObj1, jsonObj2));

        List<JsonObject> result = new JsonDataValidator().run(query, list);
        Assert.assertEquals(1, result.size());
        final JsonObject jsonObject = result.get(0);
        Assert.assertTrue(jsonObject.has(fieldName));
        Assert.assertEquals(jsonValue1, jsonObject.get(fieldName).getAsInt());
    }

    /**
     * result = true && true && true
     */
    @Test
    public void testForExpectAllValidInputData() {
        final String query = "name =Bob &&age>=10 && age<=40.0";
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
        final String query = "name =Bob &&age>=10 && age<=40.0";
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
}
