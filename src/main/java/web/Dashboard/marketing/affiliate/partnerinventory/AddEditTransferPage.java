package web.Dashboard.marketing.affiliate.partnerinventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import web.Dashboard.home.HomePage;

import java.util.Collections;
import java.util.List;

public class AddEditTransferPage extends AddEditTransferElement {
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

    public void clickSelectPartner() {
        common.click(loc_txtSearchPartner);
        logger.info("Click on select partner.");
    }

    public void inputPartner(String partnerName) {
        common.inputText(loc_txtSearchPartner, partnerName);
        logger.info("Input partner name: " + partnerName);
    }

    public boolean isPartnerSuggestionShow() {
        List<WebElement> partnerSuggestionList = common.getElements(loc_lst_searchPartnerSuggestion, 2);
        return partnerSuggestionList.size() > 0;
    }

    public void clickBranch() {
        common.click(loc_ddlOrigin);
        logger.info("Click Branch origin dropdown.");
    }

    public List<String> getBranchListShow() {
        List<String> branchNames = common.getListText(loc_ddlOrigin_options);
        Collections.sort(branchNames);
        return branchNames;
    }

    public boolean isProductShowOnSelectProductList(String productName) {
        common.inputText(loc_txtSearchProduct, productName);
        common.sleepInMiliSecond(200);
        new HomePage(driver).waitTillSpinnerDisappear1();
        List<WebElement> productNames = common.getElements(loc_lst_searchProductSuggestion, 3);
        if (productNames.isEmpty()) return false;
        for (int i = 0; i < productNames.size(); i++) {
            if (common.getText(loc_lst_searchProductSuggestion, i).equalsIgnoreCase(productName))
                return true;
        }
        return false;
    }

    public void selectProduct(String... productNames) {
        if (productNames.length == 0) {
            common.click(loc_txtSearchProduct);
            common.click(loc_lst_searchProductSuggestion, 0);
        } else {
            for (String productName : productNames) {
                common.inputText(loc_txtSearchProduct, productName);
                common.sleepInMiliSecond(200);
                new HomePage(driver).waitTillSpinnerDisappear1();
                List<WebElement> productSuggestion = common.getElements(loc_lst_searchProductSuggestion, 3);
                boolean isClicked = false;
                for (int i = 0; i < productSuggestion.size(); i++) {
                    if (common.getText(loc_lst_searchProductSuggestion, i).equalsIgnoreCase(productName)) {
                        common.click(loc_lst_searchProductSuggestion, productSuggestion.indexOf(productName));
                        isClicked = true;
                        break;
                    }
                }
                if(!isClicked)
                    try {
                        throw new Exception("%s not found.".formatted(productName));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            }
        }
    }

    public void selectPartnerDestination(String... partnerName) {
        if (partnerName.length == 0) {
            clickSelectPartner();
            common.click(loc_lst_searchPartnerSuggestion, 0);
        } else {
            inputPartner(partnerName[0]);
            new HomePage(driver).waitTillSpinnerDisappear1();
            List<WebElement> partnerSuggestion = common.getElements(loc_lst_searchPartnerSuggestion, 3);
            boolean isClicked = false;
            for (int i = 0; i < partnerSuggestion.size(); i++) {
                if (common.getText(loc_lst_searchProductSuggestion, i).equalsIgnoreCase(partnerName[0])) {
                    common.click(loc_lst_searchProductSuggestion, partnerSuggestion.indexOf(partnerName[0]));
                    isClicked = true;
                    break;
                }
            }
            if (!isClicked)
                try {
                    throw new Exception("%s not found.".formatted(partnerName[0]));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
    }
    public void clickSave(){
        common.click(loc_btnSave);
        logger.info("Click on Save button.");
    }
    public AddEditTransferPage inputInfoToCreateTransferRandom(){
        selectPartnerDestination();
        selectProduct();
        return this;
    }
    public void deleteAllTransferProduct(){
        List<WebElement> deleteListElements = common.getElements(loc_lst_btnDelete,2);
        for (WebElement deleteElement: deleteListElements){
            common.click(loc_lst_btnDelete,0);
        }
        logger.info("Delete all transfer product.");
    }
}
