package web.Dashboard.saleschannels.shopee;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.commons.UICommonAction;

public class AccountInformation {

	final static Logger logger = LogManager.getLogger(AccountInformation.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public AccountInformation(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "(//div[@class='sp-connected__content'])[2]//button")
    WebElement DOWNLOAD_SHOPEE_PRODUCT_BTN;	

    
    public AccountInformation clickDownloadShopeeProduct() {
    	commonAction.clickElement(DOWNLOAD_SHOPEE_PRODUCT_BTN);
    	logger.info("Clicked on 'Download Shopee Product' button.");
    	return this;
    }      
}
