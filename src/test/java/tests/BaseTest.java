package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    public static String baseURI = "http://localhost";
    public static int port = 8091;

    @BeforeAll
    public static void beforeMethod()
    {
        RestAssured.baseURI = baseURI;
        RestAssured.port = port;
    }
}
