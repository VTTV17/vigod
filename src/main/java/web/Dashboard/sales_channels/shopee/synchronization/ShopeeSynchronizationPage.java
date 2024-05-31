package web.Dashboard.sales_channels.shopee.synchronization;

import static utilities.account.AccountTest.SHOPEE_COUNTRY;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;
import web.Dashboard.sales_channels.shopee.account_information.AccountInformationPage;
import web.Dashboard.sales_channels.shopee.account_management.AccountManagementPage;

import static utilities.account.AccountTest.*;

public class ShopeeSynchronizationPage extends ShopeeSynchronizationElement {

	final static Logger logger = LogManager.getLogger(ShopeeSynchronizationPage.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public ShopeeSynchronizationPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }
 
	public void waitTillPageFinishLoading() {
    	for (int i=0; i<30; i++) {
    		if (!commonAction.getElements(loc_imgShopeeIntroBackground).isEmpty()) break;
    		commonAction.sleepInMiliSecond(500);
    	}
	}
	
	public boolean isConnectShopeeBtnDisplayed() {
		commonAction.sleepInMiliSecond(500);
		return commonAction.isElementDisplay(loc_btnConnectShopee);
	}     
    
    public void clickConnectShopee() {
    	commonAction.click(loc_btnConnectShopee);
    	logger.info("Clicked on 'Connect Shopee' button.");
	}

    public void verifyPermissionToConnectShopee(String permission) {
    	if (permission.contentEquals("A")) {
    		if (isConnectShopeeBtnDisplayed()) {
        		clickConnectShopee();
        		new utilities.thirdparty.Shopee(driver).performLogin(SHOPEE_COUNTRY, SHOPEE_USERNAME, SHOPEE_PASSWORD);
        		new HomePage(driver).navigateToPage("Account Information");
        		new AccountInformationPage(driver).clickDownloadShopeeProduct();
        		new HomePage(driver).navigateToPage("Account Management");
        		new AccountManagementPage(driver).deleteAccount();
    		}
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
}
