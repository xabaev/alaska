package tests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Bear;
import model.BearType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utils.Common.generateBear;

public class Update extends BaseTest {

    @Test
    public void testUpdateExistingBearWithBearJson() {
        //Сгенерируем медведей GUMMY и какого-то !GUMMY
        Bear bearNonGummy = generateBear(BearType.BROWN);
        Bear bearGummy = generateBear(BearType.GUMMY);

        //Обновим !GUMMY полями от GUMMY
        Response responseUpdateBear = given().body(bearGummy).contentType(ContentType.JSON).put("/bear/" + bearNonGummy.getBearId());
        responseUpdateBear.then().statusCode(200);
        responseUpdateBear.then().assertThat().body(equalTo("OK"));

        //Проверим, что изменилось только имя медведя.
        Response responseGetBearById = given().get("/bear/" + bearNonGummy.getBearId());
        responseGetBearById.then().statusCode(200);
        Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);

        // Т.к. параметры у нас рандомные,можно быть в достаточной уверенности, что они не повторятся
        Assertions.assertEquals(responseBear.getBearId(), bearNonGummy.getBearId(), "PUT updates the id, but only needs the name");
        Assertions.assertEquals(responseBear.getBearType(), bearNonGummy.getBearType(), "PUT updates the type, but only needs the name");
        Assertions.assertEquals(responseBear.getBearAge(), bearNonGummy.getBearAge(), "PUT updates the age, but only needs the name");
        Assertions.assertEquals(responseBear.getBearName(), bearGummy.getBearName(), "PUT not updates the name");
    }

    @Test
    public void testUpdateJsonOnlyName() {
        //Сгенерируем какого-то медведя
        Bear bear = generateBear(BearType.BROWN);

        //Создадим JSON, в котором будет только поле bear_name
        JsonObject jsonName = new JsonObject();
        jsonName.addProperty("bear_name", "test bear NaMe !@#$#%$^&%*^(&)*_(+.,");

        //Обновим сгенерированного медведя созданным JSONом
        Response responseUpdateBear = given().body(jsonName.toString()).put("/bear/" + bear.getBearId());
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
        Response responseUpdateBear = given().body(bearNonGummy).put("/bear/" + bearGummy.getBearId());
        responseUpdateBear.then().statusCode(404);
        responseUpdateBear.then().assertThat().body(equalTo("Not found"));

        //Проверим, что данные действительно не обновились
        Response responseGetBearById = given().get("/bear/" + bearGummy.getBearId());
        responseGetBearById.then().statusCode(200);
        responseGetBearById.then().assertThat().body(equalTo("null"));
    }

    @Test
    public void testUpdateWithoutName() {
        //Сгенерируем медведей - один без имени, второй обычный.
        Bear bearWithoutName = generateBear(BearType.POLAR);
        bearWithoutName.setBearName(null);
        Bear updatedBear = generateBear(BearType.BLACK);

        //Обновим обычного медведя полями медведя без имени
        Response response = given().body(bearWithoutName).put("/bear/" + updatedBear.getBearId());
        response.then().statusCode(500);
        response.then().assertThat().body(equalTo("<html><body><h2>500 Internal Server Error</h2></body></html>"));

        //Проврим, что действительно не обновили медведя
        Response responseGetBearById = given().get("/bear/" + updatedBear.getBearId());
        responseGetBearById.then().statusCode(200);
        updatedBear.setBearName(updatedBear.getBearName().toUpperCase());
        Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);
        Assertions.assertEquals(responseBear, updatedBear);
    }
}
