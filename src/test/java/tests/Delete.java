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

        Response responseDeleteBear = given().spec(request).delete("/bear/" + bear.getBearId());
        responseDeleteBear.then().statusCode(200);
        responseDeleteBear.then().assertThat().body(equalTo("OK"));

        //Проверим что по GET не найдем удаленного медведя
        Response responseGetBearById = given().spec(request).get("/bear/" + bear.getBearId());
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
            Response responseBear = given().spec(request).get("/bear/" + bear.getBearId());
            responseBear.then().assertThat().body(equalTo("EMPTY"));
        }
    }

    @Test
    public void testDeleteNonexistentBear() {
        //Проверим, что для несуществующих id возвращается успех
        Response responseDeleteBear = given().spec(request).delete("/bear/" + "-1");
        responseDeleteBear.then().statusCode(400);
        responseDeleteBear.then().assertThat().body(equalTo("Invalid id"));

        responseDeleteBear = given().spec(request).delete("/bear/" + "hello");
        responseDeleteBear.then().statusCode(400);
        responseDeleteBear.then().assertThat().body(equalTo("Invalid id"));

        responseDeleteBear = given().spec(request).delete("/bear/" + "1.1");
        responseDeleteBear.then().statusCode(400);
        responseDeleteBear.then().assertThat().body(equalTo("Invalid id"));
    }

    @Test
    public void testDeleteRemovedBear() {
        //Сгенерируем медведя и дважды удалим его
        Bear bear = generateBears(1).get(0);

        Response responseDeleteBear = given().spec(request).delete("/bear/" + bear.getBearId());
        responseDeleteBear.then().statusCode(200);
        responseDeleteBear.then().assertThat().body(equalTo("OK"));

        Response responseSecondDeletedBear = given().spec(request).delete("/bear/" + bear.getBearId());
        responseSecondDeletedBear.then().statusCode(200);
        responseSecondDeletedBear.then().assertThat().body(equalTo("OK"));
    }

    @Test
    public void testDeleteNullBear() {
        //Удалим медведя по id = null
        Response responseDeleteBear = given().spec(request).delete("/bear/");
        responseDeleteBear.then().statusCode(404);
        responseDeleteBear.then().assertThat().body(equalTo("Error. Set correct bear_id"));
    }
}
