package pages.dashboard.orders.orderlist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class OrderList {

	final static Logger logger = LogManager.getLogger(OrderList.class);

	WebDriver driver;
	UICommonAction commonAction;

	public OrderList(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}
	
	By loc_tmpRecords = By.cssSelector(".transaction-row");
	By loc_btnExport = By.cssSelector(".order-page button.gs-button__green");
	By loc_btnExportOrder = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[1]");
	By loc_btnExportOrderByProduct = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[2]");
	By loc_btnExportHistory = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[last()]");
	By loc_btnConfirmOrder = By.id("btn-readyToShip");
	By loc_btnShipmentOK = By.cssSelector(".ready-to-ship-confirm__btn-wrapper .gs-button__green");
	By loc_btnDelivered = By.cssSelector(".gs-button__green");
	
	public OrderList navigate() {
		new HomePage(driver).navigateToPage("Orders");
		new HomePage(driver).hideFacebookBubble();
		return this;
	}	
	
	public OrderList clickExport() {
		commonAction.click(loc_btnExport);
		logger.info("Clicked on 'Export' button.");
		return this;
	}

	public OrderList clickExportOrder() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportOrder).findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportOrder));
			return this;
		}
		commonAction.click(loc_btnExportOrder);
		logger.info("Clicked on 'Export Order' button.");
		return this;
	}

	public OrderList clickExportOrderByProduct() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportOrderByProduct).findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportOrderByProduct));
			return this;
		}
		commonAction.click(loc_btnExportOrderByProduct);
		logger.info("Clicked on 'Export Order By Product' button.");
		new HomePage(driver).waitTillLoadingDotsDisappear();
		return this;
	}

	public OrderList clickExportHistory() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportHistory).findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportHistory));
			return this;
		}
		commonAction.click(loc_btnExportHistory);
		new HomePage(driver).waitTillLoadingDotsDisappear();
		logger.info("Clicked on 'Export History' button.");
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon.
	 * @return 
	 */
	public OrderList clickFirstOrder() {
		commonAction.click(loc_tmpRecords, 0);
		logger.info("Clicked on the first order in order list.");
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon.
	 * @return 
	 */
	public OrderList clickConfirmOrder() {
		commonAction.click(loc_btnConfirmOrder);
		logger.info("Clicked on 'Confirm Order' button.");
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon.
	 * @return 
	 */
	public OrderList clickShipmentOKBtn() {
		commonAction.click(loc_btnShipmentOK);
		logger.info("Clicked on 'OK' button to confirm shipment.");
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon.
	 * @return 
	 */
	public OrderList clickDeliveredBtn() {
		commonAction.click(loc_btnDelivered);
		logger.info("Clicked on 'Delivered' button.");
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon.
	 * @return 
	 */
	public OrderList clickConfirmDeliveredOKBtn() {
		new ConfirmationDialog(driver).clickOKBtn();
		logger.info("Clicked on 'OK' button to confirm the order is delivered.");
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToExportOrder(String permission) {
		if (permission.contentEquals("A")) {
			clickExport().clickExportOrder();
			new ConfirmationDialog(driver).clickCancelBtn();
		} else if (permission.contentEquals("D")) {
			clickExport().clickExportOrder();
			boolean flag = new ConfirmationDialog(driver).isConfirmationDialogDisplayed();
			new OrderList(driver).clickExport();
			Assert.assertFalse(flag);
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    public void verifyPermissionToExportOrderByProduct(String permission) {
    	if (permission.contentEquals("A")) {
			clickExport().clickExportOrderByProduct();
			try {
				new ExportOrderByProductDialog(driver).clickCancel();
			} catch (Exception e) {
				commonAction.navigateBack();
			}
			
    	} else if (permission.contentEquals("D")) {
			clickExport().clickExportOrderByProduct();
			boolean flag = new ExportOrderByProductDialog(driver).isSelectProductDialogDisplayed();
			clickExport();
			Assert.assertFalse(flag);
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToExportHistory(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		clickExport().clickExportHistory();
			Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }

    /*-------------------------------------*/   	
	
}
