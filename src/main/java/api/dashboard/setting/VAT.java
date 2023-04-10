package api.dashboard.setting;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.Tax.TaxInfo;

import java.util.List;
import java.util.stream.IntStream;

public class VAT {
    public static List<Integer> apiTaxList;
    public static List<Float> apiTaxRate;
    LoginDashboardInfo loginInfo;

    {
        loginInfo = new Login().getInfo();
    }

    public void getTaxList() {
        String API_TAX_LIST_PATH = "/storeservice/api/tax-settings/store/%s";
        Response taxResponse = new API().get(API_TAX_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        taxResponse.then().statusCode(200);
        apiTaxList = taxResponse.jsonPath().getList("id");
        apiTaxRate = taxResponse.jsonPath().getList("rate");
        IntStream.range(0, apiTaxRate.size()).filter(i -> apiTaxRate.get(i) == null).forEachOrdered(i -> apiTaxRate.set(i, 0F));
    }

    public TaxInfo getInfo() {
        // init tax information model
        TaxInfo info = new TaxInfo();

        // get tax information from API
        String API_TAX_LIST_PATH = "/storeservice/api/tax-settings/store/%s";
        Response taxResponse = new API().get(API_TAX_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        taxResponse.then().statusCode(200);

        // set tax list
        info.setTaxID(taxResponse.jsonPath().getList("id"));

        // get tax rate
        List<Float> taxRate = taxResponse.jsonPath().getList("rate");
        IntStream.range(0, taxRate.size()).filter(i -> taxRate.get(i) == null).forEachOrdered(i -> taxRate.set(i, 0F));
        //set tax rate
        info.setTaxRate(taxRate);
        return info;
    }
}
