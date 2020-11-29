package tests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import model.Bear;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class Create extends BaseTest {
    static Stream<Arguments> successBearProvider() {
        return Stream.of(
                arguments(new Bear("POLAR", "mike", (double) 20), 200, new Bear("POLAR", "MIKE", 20.0)),
                arguments(new Bear("POLAR", "'", 0.00000001), 200, new Bear("POLAR", "'", 1.0E-8)),
                arguments(new Bear("BROWN", "MiKe", 1.5), 200, new Bear("BROWN", "MIKE", 1.5)),
                arguments(new Bear("BROWN", "", 100.00000001), 200, new Bear("BROWN", "", 0.0)),
                arguments(new Bear("BROWN", "My name is Mike, and yours?", (double) 50), 200, new Bear("BROWN", "MY NAME IS MIKE, AND YOURS?", 50.0)),
                arguments(new Bear("BLACK", "!@#$%^&*()_+\\\"№;%:?*-=.,", (double) 10), 200, new Bear("BLACK", "!@#$%^&*()_+\\\"№;%:?*-=.,", 10.0)),
                arguments(new Bear("BLACK", "ЁшкаМатрёшка", (double) 1000000), 200, new Bear("BLACK", "ЁШКАМАТРЁШКА", 0.0)),
                arguments(new Bear("BLACK", "\u003d", -0.1), 200, new Bear("BLACK", "\u003d", 0.0)),
                arguments(new Bear("BLACK", " . ", 5.5), 200, new Bear("BLACK", " . ", 5.5)),
                arguments(new Bear("GUMMY", "BearName", 5.5), 200, new Bear("GUMMY", "BEARNAME", 5.5))

        );
    }

    static Stream<Arguments> unsuccessfulBearProvider() {
        return Stream.of(
                //Предполагаю, что должен броситься примерно такой ответ. Сейчас 404
                arguments(new Bear("UNKNOWN", "name", (double) 1), 400, "Error. Set correct bear_type"),
                arguments(new Bear("bear", "bearName", (double) 1), 400, "Error. Set correct bear_type"),
                //Сейчас тут code 200, но должно быть 400
                arguments(new Bear(null, "hello", (double) 1), 400, "Error. Pls fill all parameters"),
                arguments(new Bear("GUMMY", null, (double) 1), 400, "Error. Pls fill all parameters"),
                arguments(new Bear("GUMMY", "hello", null), 400, "Error. Pls fill all parameters"),
                arguments(new Bear(null, null, null), 400, "Error. Pls fill all parameters")
        );
    }

    static Stream<Arguments> BearsWithIncorrectParametersTypeProvider() { //Проверим, что при передаче null в JSON будет ошибка
        return Stream.of(
                //Во всех случаях должно быть Error. Pls fill all parameters, и code 400
                arguments(new Bear(null, "BearName", 1.1), 400, "Error. Pls fill all parameters"),
                arguments(new Bear("POLAR", null, 1.1), 400, "Error. Pls fill all parameters"),
                arguments(new Bear("POLAR", "NAME", null), 400, "Error. Pls fill all parameters")
        );
    }


    @ParameterizedTest
    @MethodSource("successBearProvider")
    public void testCreateSuccessBear(Bear bear, int code, Bear expectedBear) {
        //Возьмем медведя из датапровайдера, и создадим его
        Response responseCreateBear = given().spec(requestPost).body(bear).post("/bear");
        responseCreateBear.then().statusCode(code);

        //Получим id медведя из ответа на POST
        int id = -1;
        try {
            id = Integer.parseInt(responseCreateBear.asString());
            expectedBear.setBearId(id);
        } catch (Exception e) {
            Assertions.assertEquals("Response does not contain the database entry number", e.getMessage());
        }

        //Проверим существование переданного медведя по этому id
        Response responseGetBearById = given().spec(request).get("/bear/" + id);
        responseGetBearById.then().statusCode(200);

        //Если в ответе null - то создадим медведя с null-параметрами
        Bear responseBear = responseGetBearById.asString().equals("null")
                ? new Bear(null, null, null)
                : new Gson().fromJson(responseGetBearById.asString(), Bear.class);
        Assertions.assertEquals(expectedBear, responseBear);
    }

    @ParameterizedTest
    @MethodSource("unsuccessfulBearProvider")
    public void testCreateUnsuccessfulBear(Bear bear, int code, String expectedBody) {
        //Возьмем медведя из датапровайдера, и создадим его. Ожидаемо будет ошибка
        Response response = given().spec(requestPost).body(bear).post("/bear");
        response.then().statusCode(code);
        response.then().assertThat().body(equalTo(expectedBody));
    }

    @ParameterizedTest
    @MethodSource("BearsWithIncorrectParametersTypeProvider")
    public void testCreateBearWithNullParameter(Bear bear, int code, String expectedBody) {
        //Возьмем медведя из датапровайдера, и создадим его.
        //Т.к. параметры специфичны (например, null), передаем в виде строк
        JsonObject jsonBear = new JsonObject();
        jsonBear.addProperty("bear_type", bear.getBearType());
        jsonBear.addProperty("bear_name", bear.getBearName());
        jsonBear.addProperty("bear_age", bear.getBearAge());

        Response responseCreateBear = given().spec(requestPost).body(jsonBear.toString()).post("/bear");
        responseCreateBear.then().statusCode(code);
        responseCreateBear.then().assertThat().body(equalTo(expectedBody));
    }
}
