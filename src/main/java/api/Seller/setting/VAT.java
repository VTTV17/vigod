package api.Seller.setting;

import api.Seller.login.Login;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.Tax.TaxInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.CreatePermission;

import java.util.List;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VAT {
	final static Logger logger = LogManager.getLogger(VAT.class);

	String generalTaxPath = "/storeservice/api/tax-settings";

	API api = new API();
    LoginDashboardInfo loginInfo;
    private final static Cache<LoginInformation, TaxInfo> taxCache = CacheBuilder.newBuilder().build();

    LoginInformation loginInformation;
    public VAT(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }


    public TaxInfo getInfo() {
        TaxInfo info = taxCache.getIfPresent(loginInformation);

        if (info == null) {
            // init tax information model
            info = new TaxInfo();

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

            // save cache
            taxCache.put(loginInformation, info);
        }
        return info;
    }

    public Response createTax(String name, String description, int taxRate, String taxType, boolean isDefault) {
        String body = """
                {
					"description": "%s",
					"name": "%s",
					"rate": %s,
					"storeId": %s,
					"taxType": "%s",
					"useDefault": %s
                 }""".formatted(description, name, taxRate, loginInfo.getStoreID(), taxType, isDefault);

        return api.post(generalTaxPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body).then().statusCode(201).extract().response();
    }

    public int createSellingTax(String name, String description, int taxRate, boolean isDefault) {
    	return createTax(name, description, taxRate, "SELL", isDefault).jsonPath().getInt("id");
    }

    public void deleteTax(int taxID) {
        Response response = api.delete(generalTaxPath + "/" + taxID , loginInfo.getAccessToken());
        response.then().statusCode(200);
        logger.info("Deleted taxId: " + taxID);
    }


}
