package tests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Bear;
import model.BearType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utils.Common.generateBear;
import static utils.Common.generateBears;

public class Update extends BaseTest {

    //Флапающий тест из-за бага с GUMMY
    @Test
    public void testUpdateExistingBearWithBearJson() {
        //Сгенерируем двух медведей
        List<Bear> bears = generateBears(2);

        //Обновим первого медведя полями второго
        Response responseUpdateBear = given().contentType(ContentType.JSON).body(bears.get(1)).put("/bear/" + bears.get(0).getBearId());
        responseUpdateBear.then().statusCode(200);
        responseUpdateBear.then().assertThat().body(equalTo("OK"));

        //Должно измениться имя и возраст медведя.
        //Сейчас меняется только имя
        Response responseGetBearById = given().get("/bear/" + bears.get(0).getBearId());
        responseGetBearById.then().statusCode(200);
        Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);

        // Т.к. параметры у нас рандомные,можно быть в достаточной уверенности, что они не повторятся
        Assertions.assertEquals(responseBear.getBearId(), bears.get(0).getBearId(), "PUT updates the id, but only needs the name and age");
        Assertions.assertEquals(responseBear.getBearType(), bears.get(0).getBearType(), "PUT updates the type, but only needs the name and age");
        Assertions.assertEquals(responseBear.getBearAge(), bears.get(1).getBearAge(), "PUT not updates the age");
        Assertions.assertEquals(responseBear.getBearName(), bears.get(1).getBearName(), "PUT not updates the name");
    }

    //Флапающий тест из-за бага с GUMMY
    @Test
    public void testUpdateJsonOnlyName() {
        //Сгенерируем какого-то медведя
        Bear bear = generateBears(1).get(0);

        //Создадим JSON, в котором будет только поле bear_name
        JsonObject jsonName = new JsonObject();
        jsonName.addProperty("bear_name", "test bear NaMe !@#$#%$^&%*^(&)*_(+., Привет");

        //Обновим сгенерированного медведя созданным JSONом
        Response responseUpdateBear = given().contentType(ContentType.JSON).body(jsonName.toString()).put("/bear/" + bear.getBearId());
        responseUpdateBear.then().statusCode(200);
        responseUpdateBear.then().assertThat().body(equalTo("OK"));

        //Получим обновленного медведя для сравненения
        Response responseGetBearById = given().get("/bear/" + bear.getBearId());
        responseGetBearById.then().statusCode(200);
        Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);

        //Проверим, что изменилось только имя медведя.
        Assertions.assertEquals(responseBear.getBearId(), bear.getBearId(), "PUT updates the id, but only needs the name");
        Assertions.assertEquals(responseBear.getBearType(), bear.getBearType(), "PUT updates the type, but only needs the name");
        Assertions.assertEquals(responseBear.getBearAge(), bear.getBearAge(), "PUT updates the age, but only needs the name");
        Assertions.assertEquals(responseBear.getBearName(), jsonName.get("bear_name").getAsString(), "PUT not updates the name");
    }

    //Тест обновление медведя с типом UNKNOWN
    //Единственный известный способ получения такого медведя - через создание GUMMY, хоть это и баг
    @Test
    public void testUpdateUnknownBear() {
        //Сгенерируем медведей GUMMY и какого-то !GUMMY
        Bear bearGummy = generateBear(BearType.GUMMY);
        Bear bearNonGummy = generateBear(BearType.BLACK);

        //Обновим GUMMY данными от !GUMMY
        Response responseUpdateBear = given().contentType(ContentType.JSON).body(bearNonGummy).put("/bear/" + bearGummy.getBearId());
        responseUpdateBear.then().statusCode(404);
        responseUpdateBear.then().assertThat().body(equalTo("Not found"));

        //Проверим, что данные действительно не обновились
        Response responseGetBearById = given().get("/bear/" + bearGummy.getBearId());
        responseGetBearById.then().statusCode(200);
        responseGetBearById.then().assertThat().body(equalTo("null"));
    }

    //Флапающий тест из-за бага с GUMMY
    @Test
    public void testUpdateWithoutName() {
        //Сгенерируем медведей - один без имени, второй обычный.
        List<Bear> bears = generateBears(2);
        Bear bearWithoutName = bears.get(0);
        Bear updatedBear = bears.get(1);

        //Обновим обычного медведя полями медведя без имени
        //Сейчас тут 500, но должно быть 400
        Response response = given().contentType(ContentType.JSON).body(bearWithoutName).put("/bear/" + updatedBear.getBearId());
        response.then().statusCode(400);
        response.then().assertThat().body(equalTo("Error. Set correct bear_name"));

        //Проверим, что действительно не обновили медведя
        Response responseGetBearById = given().get("/bear/" + updatedBear.getBearId());
        responseGetBearById.then().statusCode(200);
        updatedBear.setBearName(updatedBear.getBearName().toUpperCase());
        Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);
        Assertions.assertEquals(responseBear, updatedBear);
    }
}
