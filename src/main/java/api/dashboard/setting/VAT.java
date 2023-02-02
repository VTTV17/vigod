package api.dashboard.setting;

import io.restassured.response.Response;
import utilities.api.API;

import java.util.List;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

public class VAT {
    public static List<Integer> apiTaxList;
    public static List<Float> apiTaxRate;
    public void getTaxList() {
        String API_TAX_LIST_PATH = "/storeservice/api/tax-settings/store/";
        Response taxResponse = new API().get(API_TAX_LIST_PATH + apiStoreID, accessToken);
        taxResponse.then().statusCode(200);
        apiTaxList = taxResponse.jsonPath().getList("id");
        apiTaxRate = taxResponse.jsonPath().getList("rate");
        IntStream.range(0, apiTaxRate.size()).filter(i -> apiTaxRate.get(i) == null).forEachOrdered(i -> apiTaxRate.set(i, 0F));
    }
}
