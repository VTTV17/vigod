package mobile.buyer.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonMobile;
import utilities.utils.PropertiesUtil;

import java.time.Duration;
import java.util.List;

public class BuyerSearchDetailPage extends UICommonMobile {
    final static Logger logger = LogManager.getLogger(BuyerSearchDetailPage.class);
    WebDriver driver;
    WebDriverWait wait;
    public BuyerSearchDetailPage(WebDriver driver)  {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    By SEARCH_SUGGESTION = By.xpath("//android.widget.TextView[contains(@resource-id,'adapter_hot_deal_item_name')]");
    By SEARCH_INPUT = By.xpath("//android.widget.AutoCompleteTextView[contains(@resource-id,'search_src_text')]");
    By SEARCH_SUGGESTION_PRICE = By.xpath("//android.widget.TextView[contains(@resource-id,'adapter_hot_deal_item_promotion_price')]");
    By CANCEL_SEARCH = By.xpath("//android.widget.TextView[contains(@resource-id,'search_module_btn_cancel')]");
    public BuyerSearchDetailPage inputKeywordToSearch(String keyword){
        inputText(SEARCH_INPUT,keyword);
        logger.info("Input %s into search field".formatted(keyword));
//        sleepInMiliSecond(3000);
        return this;
    }
    public BuyerSearchDetailPage tapSearchSuggestion() {
        waitForElementVisible(getElement(SEARCH_SUGGESTION,20));
        List<WebElement> suggestions_el = getElements(SEARCH_SUGGESTION);
        clickElement(suggestions_el.get(0));
        return this;
    }
    public BuyerSearchDetailPage verifySearchSuggestion(String itemName, String price) throws Exception {
        waitForElementVisible(getElement(SEARCH_SUGGESTION,5));
        List<WebElement> suggestionsName_el = getElements(SEARCH_SUGGESTION);
        List<WebElement> suggestionsPrice_el = getElements(SEARCH_SUGGESTION_PRICE);
        Assert.assertEquals(getText(suggestionsName_el.get(0)).toLowerCase(),itemName.toLowerCase());
        if(!price.equals("")){
            String priceActual = getText(suggestionsPrice_el.get(0));
            priceActual = String.join("",priceActual.split(","));
            Assert.assertEquals(priceActual,price+" đ");
        }else Assert.assertEquals(getText(suggestionsPrice_el.get(0)).toLowerCase(),PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.contactTxt"));
        return this;
    }
    public BuyerSearchDetailPage verifySearchSuggestion(String itemName) {
        waitForElementVisible(getElement(SEARCH_SUGGESTION,5));
        List<WebElement> suggestionsName_el = getElements(SEARCH_SUGGESTION);
        Assert.assertEquals(getText(suggestionsName_el.get(0)).toLowerCase(),itemName.toLowerCase());
        return this;
    }
    public BuyerSearchPage tapCancelSearch(){
        clickElement(CANCEL_SEARCH);
        logger.info("Tap on Cancel button.");
        return new BuyerSearchPage(driver);
    }
    public BuyerSearchDetailPage verifySearchNotFound(String itemName){
//        waitForElementVisible(getElement(SEARCH_SUGGESTION,5));
        sleepInMiliSecond(2000);
        List<WebElement> suggestionsName_el = getElements(SEARCH_SUGGESTION);
        for (int i=0;i<suggestionsName_el.size();i++){
            if(getText(suggestionsName_el.get(i)).equalsIgnoreCase(itemName)){
                Assert.assertFalse(true,"Search has result.");
            }
        }
        Assert.assertFalse(false,"Search result: No item match keyword");
        return this;
    }

}
