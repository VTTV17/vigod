package pages.storefront.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import pages.dashboard.service.CreateServicePage;
import utilities.UICommonAction;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.List;

public class CollectionSFPage {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    final static Logger logger = LogManager.getLogger(ServiceDetailPage.class);

    public CollectionSFPage(WebDriver driver){
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver,this);
    }
    @FindBy(xpath = "(//div[@class='product-infomation']//p)[1]")
    WebElement NEWEST_SERVICE_NAME;
    @FindBy(xpath = "(//span[contains(@class,'price')])[1]")
    WebElement NEWEST_SERVICE_SELLING_PRICE;
    @FindBy(xpath = "(//span[contains(@class,'old-price')])[1]")
    WebElement NEWEST_SERVICE_LISTING_PRICE;
    String PRICES_DYNAMIC="//h3[@class='product-name' and text()='%serviceName%']/following-sibling::div";
    public void verifyNewServiceDisplayInList(String serviceName, String sellingPrice, String listingPrice){
        Assert.assertEquals(commons.getText(NEWEST_SERVICE_NAME),serviceName);
        logger.info("Service name show correct");
        String sellingPriceActual = String.join("",commons.getText(NEWEST_SERVICE_SELLING_PRICE).split(","));
        Assert.assertEquals(sellingPriceActual.subSequence(0,sellingPriceActual.length()-1),sellingPrice);
        logger.info("Selling price show correct");
        String listingPriceActual = String.join("",commons.getText(NEWEST_SERVICE_LISTING_PRICE).split(","));
        Assert.assertEquals(listingPriceActual.subSequence(0,listingPriceActual.length()-1),listingPrice);
        logger.info("Listing Price show correct");
    }
    public void verifyListingServiceDisplayInList(String serviceName){
        Assert.assertEquals(commons.getText(NEWEST_SERVICE_NAME),serviceName);
        logger.info("Service name show correct");
        String newXpath = PRICES_DYNAMIC.replace("%serviceName%",serviceName);
        List<WebElement> pricElements= driver.findElements(By.xpath(newXpath));
        Assert.assertTrue(commons.isElementNotDisplay(pricElements));
        logger.info("Price of listing service not show");
    }
    public CollectionSFPage verifyCollectionPageTitle(String pageName){
        String titleActual = driver.getTitle();
        if(pageName.equals("All Services")){
            Assert.assertEquals(titleActual,"Service List");
        }else {
            Assert.assertEquals(titleActual,pageName);
        }
        return this;
    }

}
