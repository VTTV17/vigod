package pages.dashboard.orders.orderlist;

import java.time.Duration;
import java.util.List;

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

public class OrderList {

	final static Logger logger = LogManager.getLogger(OrderList.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public OrderList(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(css = ".order-list-table-custom tbody tr")
	List<WebElement> TABLE_ROWS;

	@FindBy(css = ".order-page button.gs-button__green")
	WebElement EXPORT_BTN;

	@FindBy(xpath = "(//div[contains(@class,'uik-menuDrop__list')]//button)[1]")
	WebElement EXPORT_ORDER_BTN;

	@FindBy(xpath = "(//div[contains(@class,'uik-menuDrop__list')]//button)[2]")
	WebElement EXPORT_ORDER__BY_PRODUCT_BTN;

	@FindBy(xpath = "(//div[contains(@class,'uik-menuDrop__list')]//button)[3]")
	WebElement EXPORT_HISTORY_BTN;
	
	@FindBy(id = "btn-readyToShip")
	WebElement CONFIRM_ORDER_BTN;
	
	@FindBy(css = ".ready-to-ship-confirm__btn-wrapper .gs-button__green")
	WebElement SHIPMENT_OK_BTN;
	
	@FindBy(css = ".gs-button__green")
	WebElement DELIVERED_BTN;
	
	public OrderList navigate() {
		new HomePage(driver).navigateToPage("Orders");
		new HomePage(driver).hideFacebookBubble();
		return this;
	}	
	
	public OrderList clickExport() {
		commonAction.clickElement(EXPORT_BTN);
		logger.info("Clicked on 'Export' button.");
		return this;
	}

	public OrderList clickExportOrder() {
		if (commonAction.isElementVisiblyDisabled(EXPORT_ORDER_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(EXPORT_ORDER_BTN);
			return this;
		}
		commonAction.clickElement(EXPORT_ORDER_BTN);
		logger.info("Clicked on 'Export Order' button.");
		return this;
	}

	public OrderList clickExportOrderByProduct() {
		if (commonAction.isElementVisiblyDisabled(EXPORT_ORDER__BY_PRODUCT_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(EXPORT_ORDER__BY_PRODUCT_BTN);
			return this;
		}
		commonAction.clickElement(EXPORT_ORDER__BY_PRODUCT_BTN);
		logger.info("Clicked on 'Export Order By Product' button.");
		new HomePage(driver).waitTillLoadingDotsDisappear();
		return this;
	}

	public OrderList clickExportHistory() {
		if (commonAction.isElementVisiblyDisabled(EXPORT_HISTORY_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(EXPORT_HISTORY_BTN);
			return this;
		}
		commonAction.clickElement(EXPORT_HISTORY_BTN);
		logger.info("Clicked on 'Export History' button.");
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon.
	 * @return 
	 */
	public OrderList clickFirstOrder() {
		commonAction.clickElement(TABLE_ROWS.get(0));
		logger.info("Clicked on the first order in order list.");
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon.
	 * @return 
	 */
	public OrderList clickConfirmOrder() {
		commonAction.clickElement(CONFIRM_ORDER_BTN);
		logger.info("Clicked on 'Confirm Order' button.");
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon.
	 * @return 
	 */
	public OrderList clickShipmentOKBtn() {
		commonAction.clickElement(SHIPMENT_OK_BTN);
		logger.info("Clicked on 'OK' button to confirm shipment.");
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon.
	 * @return 
	 */
	public OrderList clickDeliveredBtn() {
		commonAction.clickElement(DELIVERED_BTN);
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
