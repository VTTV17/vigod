package app.Buyer.collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import web.Dashboard.home.HomePage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonMobile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Collection {
    final static Logger logger = LogManager.getLogger(Collection.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;

    public Collection (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    By PRODUCT_NAME_LIST = By.xpath("//*[ends-with(@resource-id,'tv_product_name')]");
    By BACK_ICON = By.xpath("//android.widget.ImageView[contains(@resource-id,'iv_back')]");
    By COUNT_ITEM = By.xpath("//*[ends-with(@resource-id,'tv_items_count')]");
    By NO_PRODUCT_MESSAGE = By.xpath("//*[ends-with(@resource-id,'tv_empty')]");
    public Collection verifyProductsInCollection(List<String> expected, boolean hasCheckPriority){
        common.sleepInMiliSecond(2000);
        if(expected.size()==0){
            verifyNoProductMessage();
        }
        List<WebElement> productNameElements = common.getElements(PRODUCT_NAME_LIST);
        List<String> productNamesActual =new ArrayList<>();
        if (productNameElements.size() !=0){
            productNamesActual = common.getListElementText(PRODUCT_NAME_LIST);
        }
        List<String> productNamesActualToLowerCase =new ArrayList<>();
        for (String productName:productNamesActual) {
            productNamesActualToLowerCase.add(productName.toLowerCase());
        }
        List<String> toExpectedSort = new ArrayList<>();
        toExpectedSort.addAll(expected);
        if (!hasCheckPriority){
            Collections.sort(toExpectedSort);
            Collections.sort(productNamesActualToLowerCase);
        }
        Assert.assertEquals(productNamesActualToLowerCase,toExpectedSort);
        logger.info("Verify products in collection.");
        return this;
    }
    public HomePage tapBackIcon(){
        common.clickElement(BACK_ICON);
        logger.info("Tap on back icon.");
        return new HomePage(driver);
    }
    public Collection verifyCountProduct(int countExpected){
        String countExpectedText;
        try {
            countExpectedText = PropertiesUtil.getPropertiesValueBySFLang("buyerApp.collection.countProduct").formatted(countExpected);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(common.getText(COUNT_ITEM),countExpectedText);
        return this;
    }
    public Collection verifyNoProductMessage(){
        try {
            Assert.assertEquals(common.getText(NO_PRODUCT_MESSAGE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.collection.noProductMessage"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Verify no product message.");
        return this;
    }
}
