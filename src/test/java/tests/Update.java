package tests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
        Response responseUpdateBear = given().spec(requestPost).body(bearGummy).put("/bear/" + bearNonGummy.getBear_id());
        responseUpdateBear.then().statusCode(200);
        responseUpdateBear.then().assertThat().body(equalTo("OK"));

        //Проверим, что изменилось только имя медведя.
        Response responseGetBearById = given().spec(request).get("/bear/" + bearNonGummy.getBear_id());
        responseGetBearById.then().statusCode(200);
        Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);

        // Т.к. параметры у нас рандомные,можно быть в достаточной уверенности, что они не повторятся
        Assertions.assertEquals(responseBear.getBear_id(), bearNonGummy.getBear_id(), "PUT updates the id, but only needs the name");
        Assertions.assertEquals(responseBear.getBear_type(), bearNonGummy.getBear_type(), "PUT updates the type, but only needs the name");
        Assertions.assertEquals(responseBear.getBear_age(), bearNonGummy.getBear_age(), "PUT updates the age, but only needs the name");
        Assertions.assertEquals(responseBear.getBear_name(), bearGummy.getBear_name(), "PUT not updates the name");
    }

    @Test
    public void testUpdateJsonOnlyName() {
        //Сгенерируем какого-то медведя
        Bear bear = generateBear(BearType.BROWN);
        //Создадим JSON, в котором будет только поле bear_name
        JsonObject jsonName = new JsonObject();
        jsonName.addProperty("bear_name", "test bear NaMe !@#$#%$^&%*^(&)*_(+.,");

        //Обновим сгенерированного медведя созданным JSONом
        Response responseUpdateBear = given().spec(requestPost).body(jsonName.toString()).put("/bear/" + bear.getBear_id());
        responseUpdateBear.then().statusCode(200);
        responseUpdateBear.then().assertThat().body(equalTo("OK"));

        //Получим обновленного медведя для сравненения
        Response responseGetBearById = given().spec(request).get("/bear/" + bear.getBear_id());
        responseGetBearById.then().statusCode(200);
        Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);

        //Проверим, что изменилось только имя медведя.
        Assertions.assertEquals(responseBear.getBear_id(), bear.getBear_id(), "PUT updates the id, but only needs the name");
        Assertions.assertEquals(responseBear.getBear_type(), bear.getBear_type(), "PUT updates the type, but only needs the name");
        Assertions.assertEquals(responseBear.getBear_age(), bear.getBear_age(), "PUT updates the age, but only needs the name");
        Assertions.assertEquals(responseBear.getBear_name(), jsonName.get("bear_name").getAsString(), "PUT not updates the name");
    }

    @Test
    public void testUpdateGummyBear() {
        //Сгенерируем медведей GUMMY и какого-то !GUMMY
        Bear bearGummy = generateBear(BearType.GUMMY);
        Bear bearNonGummy = generateBear(BearType.BLACK);

        //Обновим GUMMY данными от !GUMMY
        Response responseUpdateBear = given().spec(requestPost).body(bearNonGummy).put("/bear/" + bearGummy.getBear_id());
        responseUpdateBear.then().statusCode(404);
        responseUpdateBear.then().assertThat().body(equalTo("Not found"));

        //Проверим, что данные действительно не обновились
        Response responseGetBearById = given().spec(request).get("/bear/" + bearGummy.getBear_id());
        responseGetBearById.then().statusCode(200);
        responseGetBearById.then().assertThat().body(equalTo("null"));
    }

    @Test
    public void testUpdateWithoutName() {
        //Сгенерируем медведей - один без имени, второй обычный.
        Bear bearWithoutName = generateBear(BearType.POLAR);
        bearWithoutName.setBear_name(null);
        Bear updatedBear = generateBear(BearType.BLACK);

        //Обновим обычного медведя полями медведя без имени
        Response response = given().spec(requestPost).body(bearWithoutName).put("/bear/" + updatedBear.getBear_id());
        response.then().statusCode(500);
        response.then().assertThat().body(equalTo("<html><body><h2>500 Internal Server Error</h2></body></html>"));

        //Проврим, что действительно не обновили медведя
        Response responseGetBearById = given().spec(request).get("/bear/" + updatedBear.getBear_id());
        responseGetBearById.then().statusCode(200);
        updatedBear.setBear_name(updatedBear.getBear_name().toUpperCase());
        Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);
        Assertions.assertEquals(responseBear, updatedBear);
    }
}
