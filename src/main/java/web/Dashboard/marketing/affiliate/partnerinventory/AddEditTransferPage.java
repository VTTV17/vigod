package web.Dashboard.marketing.affiliate.partnerinventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import web.Dashboard.home.HomePage;

import java.util.List;

public class AddEditTransferPage extends AddEditTransferElement{
    final static Logger logger = LogManager.getLogger(AddEditTransferPage.class);
    WebDriver driver;
    UICommonAction common;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;

    public AddEditTransferPage(WebDriver driver) {
        this.driver = driver;
        common = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }
    public boolean isProductShowOnSelectProductList(String productName){
        common.inputText(loc_txtSearchProduct,productName);
        common.sleepInMiliSecond(200);
        new HomePage(driver).waitTillSpinnerDisappear1();
        List<WebElement> productNames = common.getElements(loc_lst_searchProductSuggestion,3);
        if (productNames.isEmpty()) return false;
        for (int i=0; i<productNames.size();i++) {
            if(common.getText(loc_lst_searchProductSuggestion,i).equalsIgnoreCase(productName))
                return true;
        }
        return false;
    }
}
