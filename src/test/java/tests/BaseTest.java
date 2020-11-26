package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseTest {
    public static String baseURI = "http://localhost";
    public static int basePort = 8091;

    public static RequestSpecification request = new RequestSpecBuilder()
            .log(LogDetail.METHOD)
            .setBaseUri(baseURI)
            .setPort(basePort)
            .log(LogDetail.URI)
            .log(LogDetail.BODY)
            .build();

    public static RequestSpecification requestPost = new RequestSpecBuilder()
            .log(LogDetail.METHOD)
            .setBaseUri(baseURI)
            .setPort(basePort)
            .setContentType(ContentType.JSON)
            .log(LogDetail.URI)
            .log(LogDetail.BODY)
            .build();

}
