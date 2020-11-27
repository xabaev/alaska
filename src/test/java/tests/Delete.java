package tests;

import io.restassured.response.Response;
import model.Bear;
import org.junit.jupiter.api.Test;
import utils.Common;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utils.Common.generateBears;

public class Delete extends BaseTest {

    @Test
    public void testDeleteExistingBear() {
        //Сгенерируем одного медведя и проверим, что медведь создался
        Bear bear = generateBears(1).get(0);

        Response responseDeleteBear = given().spec(request).delete("/bear/" + bear.getBear_id());
        responseDeleteBear.then().statusCode(200);
        responseDeleteBear.then().assertThat().body(equalTo("OK"));

        //Проверим что по GET не найдем удаленного медведя
        Response responseGetBearById = given().spec(request).get("/bear/" + bear.getBear_id());
        responseGetBearById.then().statusCode(200);
        responseGetBearById.then().assertThat().body(equalTo("EMPTY"));
    }

    @Test
    public void testDeleteAllBears() {
        //Сгенерируем пачку медведей и попытаемся удалить всех
        List<Bear> generatedBears = Common.generateBears(10);

        Response responseDeleteAllBears = given().spec(request).delete("/bear");
        responseDeleteAllBears.then().statusCode(200);
        responseDeleteAllBears.then().assertThat().body(equalTo("OK"));

        //Для каждого медведя из сгенерированных попробуем достать данные из базы
        for (Bear bear : generatedBears) {
            Response responseBear = given().spec(request).get("/bear/" + bear.getBear_id());
            responseBear.then().assertThat().body(equalTo("EMPTY"));
        }
    }

    @Test
    public void testDeleteNonexistentBear() {
        //Проверим, что для несуществующих id возвращается успех
        Response responseDeleteBear = given().spec(request).delete("/bear/" + "-1");
        responseDeleteBear.then().statusCode(200);
        responseDeleteBear.then().assertThat().body(equalTo("OK"));

        responseDeleteBear = given().spec(request).delete("/bear/" + "hello");
        responseDeleteBear.then().statusCode(200);
        responseDeleteBear.then().assertThat().body(equalTo("OK"));

        responseDeleteBear = given().spec(request).delete("/bear/" + "1.1");
        responseDeleteBear.then().statusCode(200);
        responseDeleteBear.then().assertThat().body(equalTo("OK"));
    }

    @Test
    public void testDeleteRemovedBear() {
        //Сгенерируем медведя и дважды удалим его
        Bear bear = generateBears(1).get(0);

        Response responseDeleteBear = given().spec(request).delete("/bear/" + bear.getBear_id());
        responseDeleteBear.then().statusCode(200);
        responseDeleteBear.then().assertThat().body(equalTo("OK"));

        Response responseSecondDeletedBear = given().spec(request).delete("/bear/" + bear.getBear_id());
        responseSecondDeletedBear.then().statusCode(200);
        responseSecondDeletedBear.then().assertThat().body(equalTo("OK"));
    }

    @Test
    public void testDeleteNullBear() {
        //Удалим медведя по id = null
        Response responseDeleteBear = given().spec(request).delete("/bear/");
        responseDeleteBear.then().statusCode(404);
        //TODO: Я бы здесь ожидал в body что то вроде "Set bear id", а это считал бы багом
        responseDeleteBear.then().assertThat().body(equalTo("<html><body><h2>404 Not found</h2></body></html>"));
    }
}
