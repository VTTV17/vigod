package api.dashboard.setting;

import api.dashboard.products.CreateProduct;
import io.restassured.response.Response;
import utilities.api.API;

import java.util.List;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;

public class VAT {
    public static List<Integer> taxList;
    public VAT getTaxList() {
        String API_TAX_LIST_PATH = "/storeservice/api/tax-settings/store/";
        Response taxResponse = new API().get(API_TAX_LIST_PATH + storeID, accessToken);
        taxResponse.then().statusCode(200);
        taxList = taxResponse.jsonPath().getList("id");
        return this;
    }
}
