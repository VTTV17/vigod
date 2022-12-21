package api.dashboard.setting;

import io.restassured.response.Response;
import utilities.api.API;

import java.util.List;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;

public class VAT {
    public static List<Integer> taxList;
    public static List<Float> taxRate;
    public void getTaxList() {
        String API_TAX_LIST_PATH = "/storeservice/api/tax-settings/store/";
        Response taxResponse = new API().get(API_TAX_LIST_PATH + storeID, accessToken);
        taxResponse.then().statusCode(200);
        taxList = taxResponse.jsonPath().getList("id");
        taxRate = taxResponse.jsonPath().getList("rate");
        IntStream.range(0, taxRate.size()).filter(i -> taxRate.get(i) == null).forEachOrdered(i -> taxRate.set(i, 0F));
    }
}
