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
            Response responseGetBearById = given().get("/bear/" + bear.getBearId());
            responseGetBearById.then().statusCode(200);

            //Здесь будет ошибка при GUMMY-медведях, т.к. там баг
            bear.setBearName(bear.getBearName().toUpperCase());
            Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);
            Assertions.assertEquals(bear, responseBear);
        }
    }

    //Флапающий тест из-за бага с GUMMY
    @Test
    public void testReadAllBears() {
        //Удалим все текущие данные в базе
        given().delete("/bear").then().statusCode(200);

        //Проверим, что правильно читаем пустую базу
        Response responseGetAllBears = given().get("/bear");
        responseGetAllBears.then().statusCode(200);
        List<Bear> responseBears = Arrays.asList(new Gson().fromJson(responseGetAllBears.asString(), Bear[].class));
        Assertions.assertEquals(responseBears.size(), 0);

        //Сгенерируем новых медведей, которых будем ожидать в базе
        List<Bear> expectedBears = Common.generateBears(10);
        for (Bear bear : expectedBears) {
            bear.setBearName(bear.getBearName().toUpperCase());
        }

        //Получим всех медведей в массиве
        responseGetAllBears = given().get("/bear");
        responseGetAllBears.then().statusCode(200);
        responseBears = Arrays.asList(new Gson().fromJson(responseGetAllBears.asString(), Bear[].class));

        //Тут не сравниваем отдельно листы, т.к. в возвращаемом ответе медведи в другом порядке
        assertTrue(responseBears.containsAll(expectedBears) && expectedBears.containsAll(responseBears),
                "Resulting array of bears differs from the expected");
    }

    @Test
    public void testReadUnsupportedBearId() {
        //Для !int значения ID - должна кидать ошибка
        Response response = given().get("/bear/" + "-1");
        response.then().statusCode(400);
        response.then().assertThat().body(equalTo("Invalid id"));

        response = given().get("/bear/" + "hello");
        response.then().statusCode(400);
        response.then().assertThat().body(equalTo("Invalid id"));

        response = given().get("/bear/" + "1.1");
        response.then().statusCode(400);
        response.then().assertThat().body(equalTo("Invalid id"));
    }

    @Test
    public void testReadNonexistentBear() {
        //Сгенерируем медведя, удалим его, и прочитаем значение в базе
        Bear bear = generateBears(1).get(0);

        Response responseDeleteAllBears = given().delete("/bear/" + bear.getBearId());
        responseDeleteAllBears.then().statusCode(200);
        responseDeleteAllBears.then().assertThat().body(equalTo("OK"));

        //Если не нашли медведя, то нужен code 404, а не 200
        //Сейчас code 200
        Response responseGetBearById = given().get("/bear/" + bear.getBearId());
        responseGetBearById.then().statusCode(404);
        responseGetBearById.then().assertThat().body(equalTo("EMPTY"));

        //Прочитаем медведя, id которого нет в базе
        //Т.к. медведи создаются по порядку, в два раза увеличим порядок сгенерированного медведя.
        //Если не нашли медведя, то нужен code 404, а не 200
        //Сейчас code 200
        Response responseNonexistentBear = given().get("/bear/" + bear.getBearId() + "00");
        responseNonexistentBear.then().statusCode(404);
        responseNonexistentBear.then().assertThat().body(equalTo("EMPTY"));
    }

    @Test
    public void testReadNullBear() {
        //Прочитаем медведя по id = null
        Response response = given().get("/bear/");
        response.then().statusCode(404);
        response.then().body(equalTo("Error. Set correct bear_id"));
    }
}
