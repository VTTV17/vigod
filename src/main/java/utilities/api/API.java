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

    public Response get(String path, String token) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .contentType(ContentType.JSON)
                .when()
                .get(path);
    }

    public Response search(String path, String token, String body) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
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

    public Response post(String path, String token, String... body) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .contentType(ContentType.JSON)
                .body(body.length > 0 ? body[0] : "")
                .when()
                .post(path);
    }
    
    public Response post(String path, String token, Map<String, String> headerMap, String... body) {
    	return given()
    			.auth()
    			.oauth2(token)
    			.header("Staffpermissions-Token", staffPermissionToken)
    			.headers(headerMap)
    			.contentType(ContentType.JSON)
    			.body(body.length > 0 ? body[0] : "")
    			.when().log().ifValidationFails()
    			.post(path);
    }
    
    public Response put(String path, String token, String body) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(path);
    }

    public Response delete(String path, String token) {
    	return given()
    			.auth()
    			.oauth2(token)
    			.header("Staffpermissions-Token", staffPermissionToken)
    			.contentType(ContentType.JSON)
    			.when()
    			.delete(path);
    }

    public Response delete(String path, String token, Map<String, String> headerMap) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headerMap)
                .contentType(ContentType.JSON)
                .when().log().ifValidationFails()
                .delete(path);
    }    
    
    public Response put(String path, String token) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .contentType(ContentType.JSON)
                .when()
                .put(path);
    }

    public Response deleteRequest(String path, String token, String body) {
        return given()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .delete(path);
    }

    public Response importFile(String path, String token, String fileName, String... additionParams) {
        int bound = ((additionParams.length % 2) == 0) ? (additionParams.length - 1) : (additionParams.length - 2);

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
        for (int index = 0; index < bound; index++) {
            requestSpecification = requestSpecification.multiPart( additionParams[0], additionParams[1]);
        }
        return requestSpecification;
    }
}
