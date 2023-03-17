package pages.dashboard.marketing.buylink;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.PropertiesUtil;
import utilities.UICommonAction;

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

    @FindBy(css = ".modal-title")
    WebElement POPUP_TITLE;
    @FindBy(css = ".buy-link-created-modal__search-wrapper input")
    WebElement SEARCH_INPUT;
    @FindBy(css = ".product-item-row")
    WebElement SEARCH_SUGGESTION;
    @FindBy(css = ".product-item-row__product-summary >h6")
    List<WebElement> PRODUCT_NAME_SUGGESTION;
    @FindBy(css = ".buy-link-created-modal__selected-product >div")
    WebElement NO_SELECTED_PRODUCT_LBL;
    @FindBy(xpath = "//div[@class='buy-link-created-modal__selected-product']//th[1]")
    WebElement PRODUCT_NAME_COL;
    @FindBy(xpath = "//div[@class='buy-link-created-modal__selected-product']//th[2]")
    WebElement QUANTITY_COL;
    @FindBy(xpath = "//div[@class='buy-link-created-modal__selected-product']//th[3]")
    WebElement UNIT_COL;
    @FindBy(css = ".customer-list-barcode-printer__selected")
    WebElement SELECTED_PRODUCT_NUMBER;
    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement NEXT_BTN;
    @FindBy(css = ".modal-footer .gs-button__gray--outline")
    WebElement CANCEL_BTN;
    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement FINISH_BTN;
    @FindBy(css = ".modal-footer .gs-button__gray--outline")
    WebElement BACK_BTN;
    @FindBy(css = ".buy-link-created-modal__coupon-row h6")
    List<WebElement> COUPON_NAME_LIST;
    @FindBy(css = ".buy-link-created-modal__selected-product .gs-action-button")
    List<WebElement> DELETE_PRODUCT_ICONS;
    By PRODUCT_SELECTION_MODAL = By.cssSelector(".buy-link-created-modal");

    public boolean isProductSelectionDialogDisplayed() {
        commonAction.sleepInMiliSecond(1000);
        return !commonAction.isElementNotDisplay(driver.findElements(PRODUCT_SELECTION_MODAL));
    }

    public CreateBuyLink inputToSearchProduct(String keyword) {
        commonAction.inputText(SEARCH_INPUT, keyword);
        return this;
    }

    public CreateBuyLink searchAndSelectProduct(String... productNames) throws Exception {
        for (String productName : productNames) {
            commonAction.inputText(SEARCH_INPUT, productName);
            commonAction.sleepInMiliSecond(1000);
            waitTillLoadingDotsDisappear();
            commonAction.sleepInMiliSecond(2000);
            if (commonAction.getText(PRODUCT_NAME_SUGGESTION.get(0)).equalsIgnoreCase(productName)) {
                commonAction.clickElement(PRODUCT_NAME_SUGGESTION.get(0));
            } else {
                throw new Exception(productName + ": Product Not Found");
            }
        }
        return this;
    }

    public CreateBuyLink searchAndSelectPromotion(String promotionName) throws Exception {
        commonAction.inputText(SEARCH_INPUT, promotionName);
        boolean isSelected = false;
        for (WebElement el : COUPON_NAME_LIST) {
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
        commonAction.clickElement(NEXT_BTN);
        logger.info("Click on Next button");
        return this;
    }

    public BuyLinkManagement clickOnFinishBTN() {
        commonAction.clickElement(FINISH_BTN);
        logger.info("Click on Finish button");
        return new BuyLinkManagement(driver);
    }

    public CreateBuyLink VerifyText() throws Exception {
        Assert.assertEquals(commonAction.getText(POPUP_TITLE), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.popupTitle"));
        Assert.assertEquals(commonAction.getElementAttribute(SEARCH_INPUT, "placeholder"), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.searchHint"));
        commonAction.clickElement(SEARCH_INPUT);
        Assert.assertEquals(commonAction.getText(SEARCH_SUGGESTION), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.searchSuggestion"));
        Assert.assertEquals(commonAction.getText(NO_SELECTED_PRODUCT_LBL), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.noSelectedProduct"));
        Assert.assertEquals(commonAction.getText(CANCEL_BTN), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.cancelBtn"));
        Assert.assertEquals(commonAction.getText(NEXT_BTN), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.nextBtn"));
        inputToSearchProduct("a");
        waitTillLoadingDotsDisappear();
        commonAction.sleepInMiliSecond(1000);
        commonAction.clickElement(PRODUCT_NAME_SUGGESTION.get(0));
        Assert.assertEquals(commonAction.getText(PRODUCT_NAME_COL), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.productTable.productNameCol"));
        Assert.assertEquals(commonAction.getText(QUANTITY_COL), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.productTable.quantityCol"));
        Assert.assertEquals(commonAction.getText(UNIT_COL), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.productTable.unitCol"));
        Assert.assertEquals(commonAction.getText(SELECTED_PRODUCT_NUMBER), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectedNumber").formatted("1"));
        clickOnNextBtn();
        Assert.assertEquals(commonAction.getText(POPUP_TITLE), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectCoupon.popupTitle"));
        Assert.assertEquals(commonAction.getElementAttribute(SEARCH_INPUT, "placeholder"), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectCoupon.searchHint"));
//        Assert.assertEquals(commonAction.getText(NO_SELECTED_PRODUCT_LBL), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectCoupon.noSelectedCoupon"));
        Assert.assertEquals(commonAction.getText(BACK_BTN), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectCoupon.backBtn"));
        Assert.assertEquals(commonAction.getText(FINISH_BTN), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.create.selectCoupon.finishBtn"));
        return this;
    }

    public CreateBuyLink deleteAllSelectedProduct() {
        commonAction.sleepInMiliSecond(1000);
        for (WebElement el : DELETE_PRODUCT_ICONS) {
            commonAction.clickElement(el);
        }
        logger.info("Delete all selected product.");
        return this;
    }
}
