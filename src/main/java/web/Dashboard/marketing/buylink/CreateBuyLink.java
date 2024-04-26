package web.Dashboard.marketing.buylink;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import web.Dashboard.home.HomePage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

public class CreateBuyLink extends HomePage {

    final static Logger logger = LogManager.getLogger(CreateBuyLink.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();

    public CreateBuyLink(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }
    By loc_lblPopupTitle = By.cssSelector(".modal-title");
    By loc_txtSearch = By.cssSelector(".buy-link-created-modal__search-wrapper input");
    By loc_lst_lblSearchSuggestion = By.cssSelector(".product-item-row");
    By loc_lst_lblProductNameSuggestion = By.cssSelector(".product-item-row__product-summary >h6");
    By loc_lblNoSelectedProduct = By.cssSelector(".buy-link-created-modal__selected-product >div");
    By loc_lblProductNameCol = By.xpath("//div[@class='buy-link-created-modal__selected-product']//th[1]");
    By loc_lblQuantityCol = By.xpath("//div[@class='buy-link-created-modal__selected-product']//th[2]");
    By loc_lblUnitCol = By.xpath("//div[@class='buy-link-created-modal__selected-product']//th[3]");
    By loc_lblSelectedProductNumber = By.cssSelector(".customer-list-barcode-printer__selected");
    By loc_btnNext = By.cssSelector(".modal-footer .gs-button__green");
    By loc_btnCancel = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By loc_btnFinish = By.cssSelector(".modal-footer .gs-button__green");
    By loc_btnBack = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By loc_lst_lblCouponName = By.cssSelector(".buy-link-created-modal__coupon-row h6");
    By loc_lst_btnDeleteProduct = By.cssSelector(".buy-link-created-modal__selected-product .gs-action-button");
    By loc_dlgProductSelection = By.cssSelector(".buy-link-created-modal");
    public boolean isProductSelectionDialogDisplayed() {
        commonAction.sleepInMiliSecond(1000);
        return !commonAction.isElementNotDisplay(driver.findElements(loc_dlgProductSelection));
    }

    public CreateBuyLink inputToSearchProduct(String keyword) {
        commonAction.sendKeys(loc_txtSearch, keyword);
        return this;
    }

    public CreateBuyLink searchAndSelectProduct(String... productNames){
        for (String productName : productNames) {
            commonAction.sendKeys(loc_txtSearch, productName);
            commonAction.sleepInMiliSecond(500);
            waitTillLoadingDotsDisappear();
            commonAction.sleepInMiliSecond(500);
            if (commonAction.getText(loc_lst_lblProductNameSuggestion,0).equalsIgnoreCase(productName)) {
                commonAction.click(loc_lst_lblProductNameSuggestion,0);
            } else {
                try {
                    throw new Exception(productName + ": Product Not Found");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return this;
    }

    public CreateBuyLink searchAndSelectPromotion(String promotionName) throws Exception {
        commonAction.sendKeys(loc_txtSearch, promotionName);
        boolean isSelected = false;
        commonAction.waitForListLoaded(loc_lst_lblCouponName,2);
        List<WebElement> couponNameElements = commonAction.getElements(loc_lst_lblCouponName);
        for (WebElement el : couponNameElements) {
            if (commonAction.getText(el).equalsIgnoreCase(promotionName)) {
                commonAction.clickElement(el);
                isSelected = true;
            }
        }
        if (!isSelected) {
            throw new Exception(promotionName + ": Promotion Not Found");
        }
        return this;
    }

    public CreateBuyLink clickOnNextBtn() {
        commonAction.click(loc_btnNext);
        logger.info("Click on Next button");
        return this;
    }

    public BuyLinkManagement clickOnFinishBTN() {
        commonAction.click(loc_btnFinish);
        logger.info("Click on Finish button");
        return new BuyLinkManagement(driver);
    }

    public CreateBuyLink VerifyText() throws Exception {
        Assert.assertEquals(commonAction.getText(loc_lblPopupTitle), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.popupTitle"));
        Assert.assertEquals(commonAction.getAttribute(loc_txtSearch, "placeholder"), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.searchHint"));
        commonAction.click(loc_txtSearch);
        Assert.assertEquals(commonAction.getText(loc_lst_lblSearchSuggestion), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.searchSuggestion"));
        Assert.assertEquals(commonAction.getText(loc_lblNoSelectedProduct), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.noSelectedProduct"));
        Assert.assertEquals(commonAction.getText(loc_btnCancel), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.cancelBtn"));
        Assert.assertEquals(commonAction.getText(loc_btnNext), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.nextBtn"));
        inputToSearchProduct("a");
        waitTillLoadingDotsDisappear();
        commonAction.sleepInMiliSecond(1000);
        commonAction.click(loc_lst_lblProductNameSuggestion,0);
        Assert.assertEquals(commonAction.getText(loc_lblProductNameCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.productTable.productNameCol"));
        Assert.assertEquals(commonAction.getText(loc_lblQuantityCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.productTable.quantityCol"));
        Assert.assertEquals(commonAction.getText(loc_lblUnitCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.productTable.unitCol"));
        Assert.assertEquals(commonAction.getText(loc_lblSelectedProductNumber), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectedNumber").formatted("1"));
        clickOnNextBtn();
        Assert.assertEquals(commonAction.getText(loc_lblPopupTitle), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectCoupon.popupTitle"));
        Assert.assertEquals(commonAction.getAttribute(loc_txtSearch, "placeholder"), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectCoupon.searchHint"));
        Assert.assertEquals(commonAction.getText(loc_btnBack), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectCoupon.backBtn"));
        Assert.assertEquals(commonAction.getText(loc_btnFinish), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectCoupon.finishBtn"));
        return this;
    }

    public CreateBuyLink deleteAllSelectedProduct() {
        commonAction.sleepInMiliSecond(1000);
        List<WebElement> deleteProductElements = commonAction.getElements(loc_lst_btnDeleteProduct);
        for (WebElement el : deleteProductElements) {
            commonAction.clickElement(el);
        }
        logger.info("Delete all selected product.");
        return this;
    }
    public boolean isProductShowWhenSearch(String productName){
        commonAction.inputText(loc_txtSearch,productName);
        commonAction.sleepInMiliSecond(1000);
        new HomePage(driver).waitTillLoadingDotsDisappear();
        List<WebElement> productNames = new ArrayList<>();
        for (int j=0;j<5;j++){
            productNames = commonAction.getElements(loc_lst_lblProductNameSuggestion);
            if(!productNames.isEmpty()) {
                commonAction.sleepInMiliSecond(500);
                break;
            }
        }
        if (productNames.isEmpty()) return false;
        for (int i=0; i<productNames.size();i++) {
            if(commonAction.getText(loc_lst_lblProductNameSuggestion,i).equalsIgnoreCase(productName))
                return true;
        }
        return false;
    }
    public CreateBuyLink createASimpleBuyLink(String productName){
        searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN();
        return this;
    }
}
