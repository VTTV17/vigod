package pages.storefront.productcollection;

import api.dashboard.products.APIAllProducts;
import api.dashboard.products.APIProductCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.products.productcollection.createproductcollection.CreateProductCollectionElement;
import pages.dashboard.service.CreateServicePage;
import pages.storefront.services.ServiceDetailPage;
import utilities.UICommonAction;
import utilities.data.DataGenerator;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ProductCollectionSF {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction common;
    ProductCollectionSFElement productCollectionSFUI;
    final static Logger logger = LogManager.getLogger(ProductCollectionSF.class);

    public ProductCollectionSF(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        productCollectionSFUI = new ProductCollectionSFElement(driver);
        PageFactory.initElements(driver, this);
    }
    public List<String> getProductNameList(){
        List<String> productNames = new ArrayList<>();
        for (int i=0;i< productCollectionSFUI.PRODUCT_NAMES.size();i++) {
            productNames.add(productCollectionSFUI.PRODUCT_NAMES.get(i).getText().toLowerCase());
        }
        logger.info("Get product names: "+productNames);
        return productNames;
    }
    public ProductCollectionSF verifyProductNameList(List<String> expected){
        Assert.assertEquals(getProductNameList(),expected);
        logger.info("Verify product name list display and sort by latest");
        return this;
    }
    public ProductCollectionSF verifyProductCollectionName(String expected){
        Assert.assertEquals(common.getText(productCollectionSFUI.PRODUCT_COLLECTION_NAME),expected);
        logger.info("Verif product collection name show correctly.");
        return this;
    }
    public ProductCollectionSF verifyNavigateToServiceDetailBySEOUrl(String domain, String SEOPath, String collectionName) {
        common.openNewTab();
        common.switchToWindow(1);
        common.navigateToURL(domain+SEOPath);
        verifyProductCollectionName(collectionName);
        common.closeTab();
        common.switchToWindow(0);
        return this;
    }
    public ProductCollectionSF verifySEOInfo(String SEOTitle, String SEODescription, String SEOKeyword, String collectionName){
        String titleActual = common.getElementAttribute(productCollectionSFUI.META_TITLE, "content");
        if (SEOTitle==""){
            Assert.assertEquals(titleActual,collectionName);
        }else {
            Assert.assertEquals(titleActual,SEOTitle);
        }
        String SEODescActual = common.getElementAttribute(productCollectionSFUI.META_DESCRIPTION, "content");
        if (SEODescription == "") {
            Assert.assertEquals(SEODescActual,"");
        }else {
            Assert.assertEquals(SEODescActual,SEODescription);
        }
        String SEOKeywordActual = common.getElementAttribute(productCollectionSFUI.META_KEYWORD, "content");
        if (SEOKeyword==""){
            Assert.assertEquals(SEOKeywordActual,collectionName);
        }else {
            Assert.assertEquals(SEOKeywordActual,SEOKeyword);
        }
        logger.info("Verify SEO info");
        return this;
    }
}

