package utilities.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Setter;
import utilities.data.DataGenerator;
import utilities.links.Links;

import java.io.File;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class API {
    @Setter
    public static String staffPermissionToken = "";

    public API() {
        baseURI = Links.URI;
    }

    public API(String URI) {
        baseURI = URI;
    }

    @SafeVarargs
    public final Response get(String path, String token, Map<String, ?>... headers) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headers.length == 0 ? Map.of() : headers[0])
                .contentType(ContentType.JSON)
                .when()
                .get(path);
    }

    @SafeVarargs
    public final Response search(String path, String token, String body, Map<String, ?>... headers) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headers.length == 0 ? Map.of() : headers[0])
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path);
    }

    public Response login(String path, String body) {
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path);
    }

    @SafeVarargs
    public final Response post(String path, String token, String body, Map<String, ?>... headers) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headers.length == 0 ? Map.of() : headers[0])
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path);
    }

    @SafeVarargs
    public final Response post(String path, String token, Map<String, ?>... headers) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headers.length == 0 ? Map.of() : headers[0])
                .contentType(ContentType.JSON)
                .when()
                .post(path);
    }

    @SafeVarargs
    public final Response put(String path, String token, String body, Map<String, ?>... headers) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headers.length == 0 ? Map.of() : headers[0])
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(path);
    }

    @SafeVarargs
    public final Response delete(String path, String token, Map<String, ?>... headers) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headers.length == 0 ? Map.of() : headers[0])
                .contentType(ContentType.JSON)
                .when()
                .delete(path);
    }

    @SafeVarargs
    public final Response put(String path, String token, Map<String, ?>... headers) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headers.length == 0 ? Map.of() : headers[0])
                .contentType(ContentType.JSON)
                .when()
                .put(path);
    }

    @SafeVarargs
    public final Response deleteRequest(String path, String token, String body, Map<String, ?>... headers) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headers.length == 0 ? Map.of() : headers[0])
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .delete(path);
    }

    public Response importFile(String path, String token, String fileName, String... additionParams) {
        return getMultiPartAdditionParams(additionParams)
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .multiPart(new File(new DataGenerator().getFilePath(fileName)))
                .when()
                .post(path);
    }

    public RequestSpecification getMultiPartAdditionParams(String... additionParams) {
        int bound = ((additionParams.length % 2) == 0)
                ? (additionParams.length - 1)
                : (additionParams.length - 2);
        RequestSpecification requestSpecification = given();
        for (int index = 0; index < bound; index += 2) {
            requestSpecification = requestSpecification.multiPart(additionParams[index], additionParams[index + 1]);
        }
        return requestSpecification;
    }
}
