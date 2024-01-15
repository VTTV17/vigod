package pages.dashboard.orders.returnorders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class ReturnOrders {

	final static Logger logger = LogManager.getLogger(ReturnOrders.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ReturnOrders(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_btnCreateReturnOrder = By.xpath("(//div[contains(@class,'gs-page-container-max return-order-list')]//button[contains(@class,'gs-button__green')])[1]");
	By loc_btnExport = By.xpath("(//div[contains(@class,'gs-page-container-max return-order-list')]//button[contains(@class,'gs-button__green')])[2]");
	By loc_btnExportReturnOrder = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[1]");
	By loc_btnExportHistory = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[2]");

	public ReturnOrders clickCreateReturnOrder() {
		commonAction.click(loc_btnCreateReturnOrder);
		logger.info("Clicked on 'Export Order' button.");
		return this;
	}	

	public ReturnOrders clickExport() {
		commonAction.click(loc_btnExport);
		logger.info("Clicked on 'Export' button.");
		return this;
	}	
	
	public ReturnOrders clickExportReturnOrder() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportReturnOrder).findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportReturnOrder));
			return this;
		}
		commonAction.click(loc_btnExportReturnOrder);
		logger.info("Clicked on 'Export Return Order' button.");
		return this;
	}

	public ReturnOrders clickExportHistory() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportHistory).findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportHistory));
			return this;
		}
		commonAction.click(loc_btnExportHistory);
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
