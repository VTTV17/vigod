package utilities.elementId;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utilities.api.API;

import java.util.List;

public class ActionsWithElementByAPI {
    API api = new API();
    String url;
    public ActionsWithElementByAPI(String url) {
        this.url = url.replace(url.split(":")[2].split("/")[0], "8200");
        RestAssured.baseURI = url;
    }

    public List<String> getListElementId(String xpath) {
        // Init API
        Response res = api.get("/sessions", "");
        res.then().statusCode(200);
        String sessionId = res.jsonPath().getString("value[0].id");
        String body = """
                {
                    "strategy": "xpath",
                    "selector": "%s",
                    "context": "",
                    "multiple": true
                }""".formatted(xpath);
        Response elementIdRes = api.post("/session/%s/elements".formatted(sessionId), "", body);
        elementIdRes.then().statusCode(200);
        return elementIdRes.jsonPath().getList("value.ELEMENT");
    }

    public void click(String elementId) {

    }
}
