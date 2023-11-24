package api.dashboard.setting;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.Tax.TaxInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.stream.IntStream;

public class VAT {
    LoginDashboardInfo loginInfo;

    LoginInformation loginInformation;
    public VAT(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
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

        // get tax name
        List<String> taxName = taxResponse.jsonPath().getList("name");
        // set tax name
        info.setTaxName(taxName);
        return info;
    }
}
