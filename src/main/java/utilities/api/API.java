package utilities.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class API {
    public Response get(String path, String token) {
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

    public Response post(String path, String token, String... body) {
        return given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .body(body.length > 0 ? body[0] : "")
                .post(path);
    }

    public Response put(String path, String token, String body) {
        return given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .put(path);
    }

    public Response delete(String path, String token) {
        return given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .delete(path);
    }

    public Response put(String path, String token) {
        return given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .put(path);
    }
    public Response deleteRequest(String path, String token, String body) {
        return given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .delete(path);
    }
}
