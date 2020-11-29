package tests;

import com.google.gson.Gson;
import io.restassured.response.Response;
import model.Bear;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.Common;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.Common.generateBears;

public class Read extends BaseTest {

    @Test
    public void testReadExistingBears() {
        //Сгенерируем пачку медведей и попытаемся проверить всех
        List<Bear> expectedBears = Common.generateBears(10);
        for (Bear bear : expectedBears) {
            Response responseGetBearById = given().spec(request).get("/bear/" + bear.getBearId());
            responseGetBearById.then().statusCode(200);

            //Если медведь GUMMY, то придет null
            //Если медведь не GUMMY, передаем пришедшего медведя в модель и сравниваем
            if (bear.getBearType().equals("GUMMY"))
                responseGetBearById.then().assertThat().body(equalTo("null"));
            else {
                bear.setBearName(bear.getBearName().toUpperCase());
                Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);
                Assertions.assertEquals(bear, responseBear);
            }
        }
    }

    @Test
    public void testReadAllBears() {
        //Удалим все текущие данные в базе
        given().spec(request).delete("/bear").then().statusCode(200);

        //Проверим, что правильно читаем пустую базу
        Response responseGetAllBears = given().spec(request).get("/bear");
        responseGetAllBears.then().statusCode(200);
        List<Bear> responseBears = Arrays.asList(new Gson().fromJson(responseGetAllBears.asString(), Bear[].class));
        Assertions.assertEquals(responseBears.size(), 0);

        //Сгенерируем новых медведей, которых будем ожидать в базе
        List<Bear> expectedBears = Common.generateBears(10);
        for (Bear bear : expectedBears) {
            //Преобразуем переданных GUMMY в UNKNOWN, что бы ожидать их в ответе
            if (bear.getBearType().equals("GUMMY")) {
                bear.setBearType("UNKNOWN");
                bear.setBearAge(0.0);
                bear.setBearName("EMPTY_NAME");
            } else
                bear.setBearName(bear.getBearName().toUpperCase());
        }

        //Получим всех медведей в массиве
        responseGetAllBears = given().spec(request).get("/bear");
        responseGetAllBears.then().statusCode(200);
        responseBears = Arrays.asList(new Gson().fromJson(responseGetAllBears.asString(), Bear[].class));

        //Тут не сравниваем отдельно листы, т.к. в возвращаемом ответе медведи в другом порядке
        assertTrue(responseBears.containsAll(expectedBears) && expectedBears.containsAll(responseBears),
                "Resulting array of bears differs from the expected");
    }

    @Test
    public void testReadNonexistentBear() {
        //Проверим, что для несуществующих id возвращается успех
        Response response = given().spec(request).get("/bear/" + "-1");
        response.then().statusCode(400);
        response.then().assertThat().body(equalTo("Invalid id"));

        response = given().spec(request).get("/bear/" + "hello");
        response.then().statusCode(400);
        response.then().assertThat().body(equalTo("Invalid id"));

        response = given().spec(request).get("/bear/" + "1.1");
        response.then().statusCode(400);
        response.then().assertThat().body(equalTo("Invalid id"));
    }

    @Test
    public void testReadRemovedBear() {
        //Сгенерируем медведя, удалим его, и проверим удаление
        Bear bear = generateBears(1).get(0);

        Response responseDeleteAllBears = given().spec(request).delete("/bear/" + bear.getBearId());
        responseDeleteAllBears.then().statusCode(200);
        responseDeleteAllBears.then().assertThat().body(equalTo("OK"));

        Response responseGetBearById = given().spec(request).get("/bear/" + bear.getBearId());
        responseGetBearById.then().statusCode(200);
        responseGetBearById.then().assertThat().body(equalTo("EMPTY"));
    }

    @Test
    public void testReadNullBear() {
        //Прочитаем медведя по id = null
        Response response = given().spec(request).get("/bear/");
        response.then().statusCode(404);
        response.then().body(equalTo("Error. Set correct bear_id"));
    }
}
