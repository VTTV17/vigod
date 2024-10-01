package utilities.api;

import io.restassured.RestAssured;
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
        RestAssured.proxy("localhost", 8888);
        baseURI = Links.URI;
    }

    public API(String URI) {
        baseURI = URI;
    }

    @SafeVarargs
    public final Response get(String path, String token, Map<String, ?>... headers) {
        return given().relaxedHTTPSValidation()
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
        return given().relaxedHTTPSValidation()
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
        return given().relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path);
    }

    @SafeVarargs
    public final Response post(String path, String token, Object body, Map<String, ?>... headers) {
        return given().relaxedHTTPSValidation()
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
        return given().relaxedHTTPSValidation()
                .auth()
                .oauth2(token)
                .header("Staffpermissions-Token", staffPermissionToken)
                .headers(headers.length == 0 ? Map.of() : headers[0])
                .contentType(ContentType.JSON)
                .when()
                .post(path);
    }
    
    public final Response postDesignatedForMailnesia(String path, String token) {
    	return given().relaxedHTTPSValidation()
    			.auth()
    			.oauth2(token)
    			.formParam("delete", 1)
    			.header("Content-Type", "application/x-www-form-urlencoded")
    			.when()
    			.post(path);
    }
    
    @SafeVarargs
    public final Response put(String path, String token, Object body, Map<String, ?>... headers) {
        return given().relaxedHTTPSValidation()
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
        return given().relaxedHTTPSValidation()
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
        return given().relaxedHTTPSValidation()
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
        return given().relaxedHTTPSValidation()
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
                .multiPart(new File(new DataGenerator().getPathOfFileInResourcesRoot(fileName)))
                .when()
                .post(path);
    }

    public RequestSpecification getMultiPartAdditionParams(String... additionParams) {
        int bound = ((additionParams.length % 2) == 0)
                ? (additionParams.length - 1)
                : (additionParams.length - 2);
        RequestSpecification requestSpecification = given().relaxedHTTPSValidation();
        for (int index = 0; index < bound; index += 2) {
            requestSpecification = requestSpecification.multiPart(additionParams[index], additionParams[index + 1]);
        }
        return requestSpecification;
    }
}
