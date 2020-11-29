package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Info extends BaseTest {
    @Test
    public void testGetInfo() {
        String expectedBody = "Welcome to Alaska!\n" +
                "This is CRUD service for bears in alaska.\n" +
                "CRUD routes presented with REST naming notation:\n\n" +
                "POST\t\t\t/bear - create\n" +
                "GET\t\t\t/bear - read all bears\n" +
                "GET\t\t\t/bear/:id - read specific bear\n" +
                "PUT\t\t\t/bear/:id - update specific bear\n" +
                "DELETE\t\t\t/bear - delete all bears\n" +
                "DELETE\t\t\t/bear/:id - delete specific bear\n\n" +
                "Example of ber json: {\"bear_type\":\"BLACK\",\"bear_name\":\"mikhail\",\"bear_age\":17.5}.\n" +
                "Available types for bears are: POLAR, BROWN, BLACK and GUMMY.";
        Response response = given().get("/info");
        response.then().statusCode(200);
        response.then().body(equalTo(expectedBody));
    }
}