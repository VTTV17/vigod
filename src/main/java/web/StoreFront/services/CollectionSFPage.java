package web.StoreFront.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import utilities.enums.Domain;

import java.time.Duration;
import java.util.List;

public class CollectionSFPage {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    final static Logger logger = LogManager.getLogger(ServiceDetailPage.class);
    Domain domain;
    public CollectionSFPage(WebDriver driver){
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
    }
    public CollectionSFPage(WebDriver driver, Domain domain){
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        this.domain = domain;
    }
    By loc_lblNewestServiceName = By.xpath("(//div[@class='product-infomation']/*)[1]");
    By loc_lblNewestServiceSellingPrice = By.xpath("(//span[contains(@class,'price')])[1]");
    By loc_lblNewestServiceListingPrice = By.xpath("(//span[contains(@class,'old-price')])[1]");
    String PRICES_DYNAMIC="//h3[@class='product-name' and text()='%serviceName%']/following-sibling::div";

    public void verifyNewServiceDisplayInList(String serviceName, String sellingPrice, String listingPrice){
        Assert.assertEquals(commons.getText(loc_lblNewestServiceName),serviceName);
        logger.info("Service name show correct");

        String sellingPriceActual = commons.getText(loc_lblNewestServiceSellingPrice);
        sellingPriceActual = sellingPriceActual.replaceAll("[^\\d.]", "");
        sellingPrice = sellingPrice.replaceAll("[^\\d.]", "");
        if(domain.equals(Domain.BIZ)) sellingPrice = sellingPrice + ".00";
        Assert.assertEquals(sellingPriceActual , sellingPrice);
        logger.info("Selling price show correct");
        String listingPriceActual = commons.getText(loc_lblNewestServiceListingPrice);
        listingPriceActual = listingPriceActual.replaceAll("[^\\d.]", "");
        listingPrice = listingPrice.replaceAll("[^\\d.]", "");
        if(domain.equals(Domain.BIZ)) listingPrice = listingPrice + ".00";
        Assert.assertEquals(listingPriceActual , listingPrice);
        logger.info("Listing Price show correct");
    }
    public void verifyListingServiceDisplayInList(String serviceName){
        Assert.assertEquals(commons.getText(loc_lblNewestServiceName),serviceName);
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
