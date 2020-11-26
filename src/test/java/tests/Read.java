package tests;

import com.google.gson.Gson;
import io.restassured.response.Response;
import model.Bear;
import org.junit.jupiter.api.Test;
import utils.Common;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.Common.generateBears;

public class Read extends BaseTest {

    @Test
    public void testReadExistingBears() {
        List<Bear> expectedBears = Common.generateBears(10);
        System.out.println(expectedBears);

        for (Bear bear : expectedBears) {
            Response responseGetBearById = given().spec(request).get("/bear/" + bear.getBear_id());
            responseGetBearById.then().statusCode(200);

            //Если медведь GUMMY, то ожидаемо придет на выходе null
            //Если медведь не GUMMY, передаем пришедшего медведя в модель и сравниваем
            if (bear.getBear_type().equals("GUMMY"))
                responseGetBearById.then().assertThat().body(equalTo("null"));
            else {
                bear.setBear_name(bear.getBear_name().toUpperCase());
                Bear responseBear = new Gson().fromJson(responseGetBearById.asString(), Bear.class);
                assertEquals(bear, responseBear);
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
        assertEquals(responseBears.size(), 0);

        //Сгенерируем новых медведей, которых будем ожидать в базе
        List<Bear> expectedBears = Common.generateBears(10);
        for (Bear bear : expectedBears) {
            //Преобразуем переданных GUMMY в UNKNOWN, что бы ожидать их в ответе
            if (bear.getBear_type().equals("GUMMY")) {
                bear.setBear_type("UNKNOWN");
                bear.setBear_age(0.0);
                bear.setBear_name("EMPTY_NAME");
            } else bear.setBear_name(bear.getBear_name().toUpperCase());
        }


        responseGetAllBears = given().spec(request).get("/bear");
        responseGetAllBears.then().statusCode(200);
        responseBears = Arrays.asList(new Gson().fromJson(responseGetAllBears.asString(), Bear[].class));

        //Тут не сравниваем отдельно листы, т.к. в возвращаемом ответе медведи в другом порядке
        //Одного вхождения не хватит. т.к. может быть случай пустого ответа или одного и того же медведя в ответе
        assertTrue(responseBears.containsAll(expectedBears) && expectedBears.containsAll(responseBears),
                "Resulting array of bears differs from the expected");
    }

    @Test
    public void testReadNonexistentBear() {
        Response response = given().spec(request).get("/bear/" + "-1");

        response.then().statusCode(200);
        response.then().assertThat().body(equalTo("EMPTY"));
    }

    @Test
    public void testReadRemovedBear() {
        Bear bear = generateBears(1).get(0);
        Response responseDeleteAllBears = given().spec(request).delete("/bear/" + bear.getBear_id());

        responseDeleteAllBears.then().statusCode(200);
        responseDeleteAllBears.then().assertThat().body(equalTo("OK"));

        Response responseGetBearById = given().spec(request).get("/bear/" + bear.getBear_id());

        responseGetBearById.then().statusCode(200);
        responseGetBearById.then().assertThat().body(equalTo("EMPTY"));
    }

    @Test
    public void testReadNullBear() {
        Response response = given().spec(request).get("/bear/");

        response.then().statusCode(404);
        //TODO: Я бы здесь ожидал в body что то вроде "Set bear id", а это считал бы багом
        response.then().body(equalTo("<html><body><h2>404 Not found</h2></body></html>"));
    }
}
