package pages.storefront.productcollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.storefront.GeneralSF;
import utilities.PropertiesUtil;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ProductCollectionSF extends GeneralSF {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction common;
    ProductCollectionSFElement productCollectionSFUI;
    final static Logger logger = LogManager.getLogger(ProductCollectionSF.class);

    public ProductCollectionSF(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        productCollectionSFUI = new ProductCollectionSFElement(driver);
        PageFactory.initElements(driver, this);
    }

    public List<String> getProductNameList() {
        List<String> productNames = new ArrayList<>();
        int pageNumber = 1;
        if (productCollectionSFUI.PAGING_PAGE_LIST.size() > 0) {
            pageNumber = productCollectionSFUI.PAGING_PAGE_LIST.size() - 1;
        }
        for (int i = 1; i <= pageNumber; i++) {
            for (int j = 0; j < productCollectionSFUI.PRODUCT_NAMES.size(); j++) {
                productNames.add(productCollectionSFUI.PRODUCT_NAMES.get(j).getText().toLowerCase());
            }
            if (i < pageNumber - 1) {
//                String pageActive = productCollectionSFUI.PAGE_ACTIVE.getText();
                common.clickElement(common.getElementByXpath(productCollectionSFUI.PAGE_IN_PAGINATION_DYNAMIC_XP.formatted(String.valueOf(i + 1))));
                waitTillLoaderDisappear();
            }
            pageNumber = productCollectionSFUI.PAGING_PAGE_LIST.size() - 1;
        }
        logger.info("Get product names: " + productNames);
        return productNames;
    }

    public List<String> getProductNameListWithLazyLoad(int scrollNumber) {
        List<String> productNames = new ArrayList<>();
        for (int i = 0; i < scrollNumber; i++) {
            common.scrollBottomPage();
            common.sleepInMiliSecond(500);
            waitTillLoaderDisappear();
        }
        for (int j = 0; j < productCollectionSFUI.PRODUCT_NAMES.size(); j++) {
            productNames.add(productCollectionSFUI.PRODUCT_NAMES.get(j).getText().toLowerCase());
        }
        logger.info("Get product names: " + productNames);
        return productNames;
    }

    public ProductCollectionSF verifyProductNameList(List<String>actual,List<String> expected) {
        Assert.assertEquals(actual, expected);
        logger.info("Verify product name list display and sort by latest");
        return this;
    }

    public ProductCollectionSF verifyProductCollectionName(String expected) {
        Assert.assertEquals(common.getText(productCollectionSFUI.PRODUCT_COLLECTION_NAME), expected);
        logger.info("Verif product collection name show correctly.");
        return this;
    }

    public ProductCollectionSF verifySEOInfo(String SEOTitle, String SEODescription, String SEOKeyword, String collectionName) {
        String titleActual = common.getElementAttribute(productCollectionSFUI.META_TITLE, "content");
        if (SEOTitle == "") {
            Assert.assertEquals(titleActual, collectionName);
        } else {
            Assert.assertEquals(titleActual, SEOTitle);
        }
        String SEODescActual = common.getElementAttribute(productCollectionSFUI.META_DESCRIPTION, "content");
        if (SEODescription == "") {
            Assert.assertEquals(SEODescActual, "");
        } else {
            Assert.assertEquals(SEODescActual, SEODescription);
        }
        String SEOKeywordActual = common.getElementAttribute(productCollectionSFUI.META_KEYWORD, "content");
        if (SEOKeyword == "") {
            Assert.assertEquals(SEOKeywordActual, collectionName);
        } else {
            Assert.assertEquals(SEOKeywordActual, SEOKeyword);
        }
        logger.info("Verify SEO info");
        return this;
    }
    public ProductCollectionSF verifyCollectionEmpty(){
        try {
            Assert.assertEquals(common.getText(productCollectionSFUI.PRODUCT_NOT_FOUND_LBL), PropertiesUtil.getPropertiesValueBySFLang("collection.noItem"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}

