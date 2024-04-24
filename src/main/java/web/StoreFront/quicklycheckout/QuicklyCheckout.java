package web.StoreFront.quicklycheckout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import java.time.Duration;

public class QuicklyCheckout extends QuicklyCheckoutElement{
    final static Logger logger = LogManager.getLogger(QuicklyCheckout.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    public QuicklyCheckout (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }
    public QuicklyCheckout verifyShopCartHeader() throws Exception {
        Assert.assertEquals(commons.getText(CARD_HEADER), PropertiesUtil.getPropertiesValueBySFLang("quicklyCheckout.cardHeader"));
        logger.info("Verify shop card header.");
        return this;
    }
    public QuicklyCheckout verifyDiscountInvalidError() throws Exception {
        Assert.assertEquals(commons.getText(ERROR_MESSAGE), PropertiesUtil.getPropertiesValueBySFLang("quicklyCheckout.invalidError"));
        logger.info("Verify discount invalid message.");
        return this;
    }
    public QuicklyCheckout verifyMaximumAllowUsageError() throws Exception {
        Assert.assertEquals(commons.getText(ERROR_MESSAGE), PropertiesUtil.getPropertiesValueBySFLang("quicklyCheckout.limitUsageError"));
        logger.info("Verify error message.");
        return this;
    }
    public QuicklyCheckout verifyMaximumAllowUsagePerUserError() throws Exception {
        Assert.assertEquals(commons.getText(ERROR_MESSAGE), PropertiesUtil.getPropertiesValueBySFLang("quicklyCheckout.limitUsagePerUserError"));
        logger.info("Verify error message.");
        return this;
    }
    public QuicklyCheckout verifyNoProductShow() {
        Assert.assertTrue(PRODUCT_NAME_LIST.size()>0);
        logger.info("Verify product list not show.");
        return this;
    }
    public QuicklyCheckout checkTextByLanguage(String lang) throws Exception {
        Assert.assertEquals(commons.getText(PRODUCT_LBL), PropertiesUtil.getPropertiesValueBySFLang("quicklyCheckout.productLbl",lang));
        Assert.assertEquals(commons.getText(QUANTITY_LBL), PropertiesUtil.getPropertiesValueBySFLang("quicklyCheckout.quantityLbl",lang));
        Assert.assertEquals(commons.getText(UNIT_PRICE_LBL), PropertiesUtil.getPropertiesValueBySFLang("quicklyCheckout.unitPriceLbl",lang));
        Assert.assertEquals(commons.getText(HOME_BTN), PropertiesUtil.getPropertiesValueBySFLang("quicklyCheckout.homeLbl",lang));
        return this;
    }
    public QuicklyCheckout checkProductNames(String...expected){
        for (int i=0;i<expected.length;i++) {
            Assert.assertEquals(commons.getText(PRODUCT_NAME_LIST.get(i)).trim(),expected[i].trim());
        }
        return this;
    }
}
