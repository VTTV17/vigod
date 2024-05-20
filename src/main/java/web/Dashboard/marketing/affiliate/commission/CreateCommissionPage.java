package web.Dashboard.marketing.affiliate.commission;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.links.Links;
import web.Dashboard.home.HomePage;

import java.util.List;

public class CreateCommissionPage extends CreateCommissionElement{
    final static Logger logger = LogManager.getLogger(CreateCommissionPage.class);
    WebDriver driver;
    UICommonAction common;
    public CreateCommissionPage(WebDriver driver){
        this.driver = driver;
        common = new UICommonAction(driver);
    }
    public String inputCommissionName(String...name){
        String commissionName;
        if(name.length==0){
            commissionName = "Commission "+ new DataGenerator().generateString(10);
        }else commissionName = name[0];
        common.inputText(loc_txtCommissionName,commissionName);
        logger.info("Input commission name: "+commissionName);
        return commissionName;
    }
    public String inputCommissionPercent(String...percent){
        String commissionPercent;
        if(percent.length==0){
            commissionPercent = new DataGenerator().generateNumber(2);
        }else commissionPercent = percent[0];
        common.inputText(loc_txtCommissionPercent,commissionPercent);
        return commissionPercent;
    }

    /**
     *
     * @param condition: operators-value, with operators: EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL. Value is numberic
     */
    public void inputApplicableOrderLevel(String...condition){
        if(condition.length==0){
            common.inputText(loc_lst_txtOrderLevelValue,new DataGenerator().generateNumber(1));
        }else {
            for (int i=0;i<condition.length;i++){
                String operator = condition[i].split("-")[0];
                String value = condition[i].split("-")[1];
                common.selectDropdownOptionByValue(loc_lst_ddlOrderLevelOperators,i,operator);
                common.inputText(loc_lst_txtOrderLevelValue,i,value);
                if(i < condition.length-1){
                    common.click(loc_btnAddCondition);
                }
            }
        }
    }
    public void clickOnSaveBtn(){
        common.click(loc_btnSave);
        logger.info("Click on Save button");
    }
    public CreateCommissionPage createSimpleCommission(){
        inputCommissionName();
        inputCommissionPercent();
        inputApplicableOrderLevel();
        clickOnSaveBtn();
        return this;
    }
    public CreateCommissionPage clickOnSpecificProduct(){
        common.click(loc_lblSpecificProducts);
        logger.info("Click on Specific Product.");
        return this;
    }
    public CreateCommissionPage clickOnAddProductLink(){
        common.click(loc_lnkAddProduct);
        logger.info("Click on Add product link.");
        return this;
    }
    public boolean isProductShowOnSelectProductList(String productName){
        common.inputText(loc_txtSearch,productName);
        common.sleepInMiliSecond(500);
        new HomePage(driver).waitTillSpinnerDisappear1();
        List<WebElement> productNames = common.getElements(loc_lst_lblProductName,5);
        if (productNames.isEmpty()) return false;
        for (int i=0; i<productNames.size();i++) {
            if(common.getText(loc_lst_lblProductName,i).equalsIgnoreCase(productName))
                return true;
        }
        return false;
    }
    public CreateCommissionPage clickOnSpecificCollections(){
        common.click(loc_lblSpecificCollection);
        logger.info("Click on Specific collection.");
        return this;
    }
    public CreateCommissionPage clickOnAddCollectionsLink(){
        common.click(loc_lnkAddCollection);
        logger.info("Click on Add collections link.");
        return this;
    }
    public CreateCommissionPage navigateByUrl(){
        String url = Links.DOMAIN + "/affiliate/commission/create";
        common.navigateToURL(url);
        logger.info("Navigate to url: "+url);
        return this;
    }
}
