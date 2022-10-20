package utilities.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class API {
    public Response list(String path, String token) {
        return given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .get(path);
    }

    public Response search(String path, String token, String body) {
        return given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post(path);
    }

    public Response login(String path, String body) {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post(path);
    }

    public Response create(String path, String token, String body) {
        return given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post(path);
    }
}
