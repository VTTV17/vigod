package pages.dashboard.orders.returnorders;

import java.time.Duration;

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

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class ReturnOrders {

	final static Logger logger = LogManager.getLogger(ReturnOrders.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public ReturnOrders(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "(//div[contains(@class,'gs-page-container-max return-order-list')]//button[contains(@class,'gs-button__green')])[1]")
	WebElement CREATE_RETURN_ORDER_BTN;
	
	@FindBy(xpath = "(//div[contains(@class,'gs-page-container-max return-order-list')]//button[contains(@class,'gs-button__green')])[2]")
	WebElement EXPORT_BTN;

	@FindBy(xpath = "(//div[contains(@class,'uik-menuDrop__list')]//button)[1]")
	WebElement EXPORT_RETURN_ORDER_BTN;

	@FindBy(xpath = "(//div[contains(@class,'uik-menuDrop__list')]//button)[2]")
	WebElement EXPORT_HISTORY_BTN;


	public ReturnOrders clickCreateReturnOrder() {
		commonAction.clickElement(CREATE_RETURN_ORDER_BTN);
		logger.info("Clicked on 'Export Order' button.");
		return this;
	}	

	public ReturnOrders clickExport() {
		commonAction.clickElement(EXPORT_BTN);
		logger.info("Clicked on 'Export' button.");
		return this;
	}	
	
	public ReturnOrders clickExportReturnOrder() {
		if (commonAction.isElementVisiblyDisabled(EXPORT_RETURN_ORDER_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(EXPORT_RETURN_ORDER_BTN);
			return this;
		}
		commonAction.clickElement(EXPORT_RETURN_ORDER_BTN);
		logger.info("Clicked on 'Export Return Order' button.");
		return this;
	}

	public ReturnOrders clickExportHistory() {
		if (commonAction.isElementVisiblyDisabled(EXPORT_HISTORY_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(EXPORT_HISTORY_BTN);
			return this;
		}
		commonAction.clickElement(EXPORT_HISTORY_BTN);
		logger.info("Clicked on 'Export History' button.");
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateReturnedOrder(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickExport().clickCreateReturnOrder();
			boolean flag = new SelectOrderToReturnDialog(driver).isDialogDisplayed();
			new SelectOrderToReturnDialog(driver).closeDialog();
			Assert.assertTrue(flag);
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    public void verifyPermissionToExportReturnedOrder(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		clickExport().clickExportReturnOrder();
    		new ConfirmationDialog(driver).clickCancelBtn();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(commonAction.getCurrentURL().contains(url));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToExportHistory(String permission, String url) {
    	if (permission.contentEquals("A")) {
			clickExport().clickExportHistory();
			Assert.assertTrue(commonAction.getCurrentURL().contains(url));
			commonAction.navigateBack();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(commonAction.getCurrentURL().contains(url));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    /*-------------------------------------*/    	
	
}
