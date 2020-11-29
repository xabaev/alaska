package utils;

import io.restassured.response.Response;
import model.Bear;
import model.BearType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static tests.BaseTest.request;

public class Common {

    public static List<Bear> generateBears(int count) {
        List<Bear> bearList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Random random = new Random();
            String bearType = BearType.values()[random.nextInt(BearType.values().length)].toString();
            String bearName = RandomStringUtils.randomAlphanumeric(10) + " " + RandomStringUtils.randomAlphanumeric(10);
            double randomValue = 0 + 100 * random.nextDouble();
            Bear bear = new Bear(bearType, bearName, randomValue);

            Response response = given().spec(request).body(bear).post("/bear");
            String responseBody = response.getBody().asString();
            try {
                int id = Integer.parseInt(responseBody);
                bear.setBearId(id);
            } catch (Exception e) {
                Assertions.assertEquals("Response does not contain the database entry number", e.getMessage());
            }
            bearList.add(bear);
        }
        return bearList;
    }

    public static Bear generateBear(BearType type) {
        Random rand = new Random();
        String bearName = RandomStringUtils.randomAlphanumeric(10) + " " + RandomStringUtils.randomAlphanumeric(10);
        double randomValue = 0 + 100 * rand.nextDouble();
        Bear bear = new Bear(type.toString(), bearName, randomValue);

        Response response = given().spec(request).body(bear).post("/bear");
        String responseBody = response.getBody().asString();
        try {
            int id = Integer.parseInt(responseBody);
            bear.setBearId(id);
        } catch (Exception e) {
            Assertions.assertEquals("Response does not contain the database entry number", e.getMessage());
        }
        return bear;
    }

}
