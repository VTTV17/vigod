package pages.sellerapp.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.sellerapp.SellerGeneral;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;

import java.time.Duration;

public class SellerProductCollection {
    final static Logger logger = LogManager.getLogger(SellerProductCollection.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;

    public SellerProductCollection (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }

    By SEARCH_COLLECTION_INPUT = By.xpath("//*[ends-with(@resource-id,'edtCollectionSearch')]");
    By QUANTITY_LIST = By.xpath("//*[ends-with(@resource-id,'tvItemQuantity')]");
    By COLLECTION_TYPE = By.xpath("//*[ends-with(@resource-id,'tvCollectionType')]");
    By COLLECTION_NAME_LIST = By.xpath("//*[ends-with(@resource-id,'tvCollectionName')]");
    public void verifyText() throws Exception {
        String titlePage = new SellerGeneral(driver).getHeaderTitle();
        Assert.assertEquals(titlePage, PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.pageTitle"));
        Assert.assertEquals(common.getText(SEARCH_COLLECTION_INPUT),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.searchHint"));
    }
    public SellerProductCollection tapCreateCollectionIcon(){
        new SellerGeneral(driver).tapHeaderRightIcon();
        return this;
    }
}
