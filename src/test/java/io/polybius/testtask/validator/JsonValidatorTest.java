package io.polybius.testtask.validator;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

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
    public void testExpectResultThenEmptyIsQuery() {
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
        for (int i = 0; i < 2 ;i++ ){
            final JsonObject jsonObject = result.get(i);
            Assert.assertTrue(jsonObject.has(fieldName1));
            Assert.assertTrue(jsonObject.has(fieldName2));
            Assert.assertEquals(value1, jsonObject.get(fieldName1).getAsString());
            Assert.assertEquals(jsonValue2, jsonObject.get(fieldName2).getAsJsonPrimitive().getAsInt());
        }
    }

    /**
     * Put  false || false || true
     * wait true
     */
    @Test
    public void testForFindOneValidBoolean() {
        final String query = "name =Bob  ||age=100 || age=20";
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
