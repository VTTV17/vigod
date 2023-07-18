package utilities.elementId;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import utilities.api.API;
import utilities.driver.InitAppiumDriver;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

public class ActionsWithElementByAPI {
    API api = new API();
    String url;
    String sessionId;

    public ActionsWithElementByAPI(String url) {
        this.url = url.replace(url.split(":")[2].split("/")[0], "%s".formatted(8200));
        sessionId = getSessionId();
    }

    @SneakyThrows
    String getSessionId() {
            RestAssured.baseURI = this.url;
            // Init API
            Response res = api.get("/sessions", "");
            res.then().statusCode(200);
            return res.jsonPath().getString("value[0].id");

    }

    public String getElementId(By xpathLocator, int... durationOfSeconds) {
        int duration = (durationOfSeconds.length == 0) ? 0 : durationOfSeconds[0];
        RestAssured.baseURI = this.url;
        String body = """
                {
                    "strategy": "xpath",
                    "selector": "%s",
                    "context": "",
                    "multiple": false
                }""".formatted(xpathLocator.toString().replace("By.xpath: ", ""));

        long epoch = Instant.now().toEpochMilli();
        while (true)
            if (api.post("/session/%s/element".formatted(sessionId), "", body).statusCode() == 200 || Instant.now().toEpochMilli() - epoch >= duration * 1000L)
                break;
        Response response = api.post("/session/%s/element".formatted(sessionId), "", body);
        return response.statusCode() == 200 ? response.jsonPath().getString("value.ELEMENT") : null;
    }

    public List<String> getListElementId(By xpathLocator, int... durationOfSeconds) {
        int duration = (durationOfSeconds.length == 0) ? 0 : durationOfSeconds[0];
        RestAssured.baseURI = this.url;
        String body = """
                {
                    "strategy": "xpath",
                    "selector": "%s",
                    "context": "",
                    "multiple": true
                }""".formatted(xpathLocator.toString().replace("By.xpath: ", ""));
        long epoch = Instant.now().toEpochMilli();
        while (true)
            if (api.post("/session/%s/elements".formatted(sessionId), "", body).statusCode() == 200 || Instant.now().toEpochMilli() - epoch >= duration * 1000L)
                break;
        Response response = api.post("/session/%s/elements".formatted(sessionId), "", body);
        return response.statusCode() == 200 ? response.jsonPath().getList("value.ELEMENT") : List.of();
    }

    public void click(By xpathLocator, int... durationOfSeconds) {
        RestAssured.baseURI = this.url;

        // get elementId
        String elementId = getElementId(xpathLocator, durationOfSeconds);

        String body = """
                {"id" : "%s"}""".formatted(elementId);
        api.post("/session/%s/element/%s/click".formatted(sessionId, elementId), "", body).then().statusCode(200);
    }

    public void clickWithIndex(By xpathLocator, int index, int... durationOfSeconds) {
        RestAssured.baseURI = this.url;

        // get elementId
        String elementId = getListElementId(xpathLocator, durationOfSeconds).get(index);

        String body = """
                {"id" : "%s"}""".formatted(elementId);
        api.post("/session/%s/element/%s/click".formatted(sessionId, elementId), "", body).then().statusCode(200);
    }

    public String getText(By xpathLocator, int... durationOfSeconds) {
        RestAssured.baseURI = this.url;

        // get elementId
        String elementId = getElementId(xpathLocator, durationOfSeconds);

        Response res = api.get("/session/%s/element/%s/text".formatted(sessionId, elementId), "");
        res.then().statusCode(200);
        return res.jsonPath().getString("value");
    }

    public String getTextWithIndex(By xpathLocator, int index, int... durationOfSeconds) {
        RestAssured.baseURI = this.url;

        // get elementId
        String elementId = getListElementId(xpathLocator, durationOfSeconds).get(index);

        Response res = api.get("/session/%s/element/%s/text".formatted(sessionId, elementId), "");
        res.then().statusCode(200);
        return res.jsonPath().getString("value");
    }

    public void sendKeys(By xpathLocator, String text, int... durationOfSeconds) {
        RestAssured.baseURI = this.url;

        // get elementId
        String elementId = getElementId(xpathLocator, durationOfSeconds);

        // clear
        api.post("/session/%s/element/%s/clear".formatted(sessionId, elementId), "").then().statusCode(200);

        // get elementId again
        elementId = getElementId(xpathLocator, durationOfSeconds);

        // setup body
        List<String> textToArrayList = List.of(text.split("\\.*?"));
        StringBuilder textToArraysString = new StringBuilder();
        IntStream.range(0, textToArrayList.size()).mapToObj(i -> (i == textToArrayList.size() - 1 ? "\"%s\"" : "\"%s\",").formatted(textToArrayList.get(i))).forEachOrdered(textToArraysString::append);
        String sendKeysBody = """
                {"elementId":"%s","text":"%s","value":[%s]}""".formatted(elementId, text, textToArraysString);

        // sendKeys
        api.post("/session/%s/element/%s/value".formatted(sessionId, elementId), "", sendKeysBody).then().statusCode(200);
    }

    public void sendKeysWithIndex(By xpathLocator, String text, int index, int... durationOfSeconds) {
        RestAssured.baseURI = this.url;

        // get elementId
        String elementId = getListElementId(xpathLocator, durationOfSeconds).get(index);

        // clear
        api.post("/session/%s/element/%s/clear".formatted(sessionId, elementId), "").then().statusCode(200);

        // get elementId again
        elementId = getListElementId(xpathLocator, durationOfSeconds).get(index);

        // setup body
        List<String> textToArrayList = List.of(text.split("\\.*?"));
        StringBuilder textToArraysString = new StringBuilder();
        IntStream.range(0, textToArrayList.size()).mapToObj(i -> (i == textToArrayList.size() - 1 ? "\"%s\"" : "\"%s\",").formatted(textToArrayList.get(i))).forEachOrdered(textToArraysString::append);
        String sendKeysBody = """
                {"elementId":"%s","text":"%s","value":[%s]}""".formatted(elementId, text, textToArraysString);

        // sendKeys
        api.post("/session/%s/element/%s/value".formatted(sessionId, elementId), "", sendKeysBody).then().statusCode(200);
    }
}
