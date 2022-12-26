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
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class ExportOrderByProductDialog {

	final static Logger logger = LogManager.getLogger(ExportOrderByProductDialog.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public ExportOrderByProductDialog(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".select-product-modal")
	List<WebElement> EXPORT_ORDER_BY_PRODUCT_DIALOG;
	
	@FindBy(css = ".modal-body button.gs-button__gray--outline")
	WebElement CANCEL_BTN;
	
	@FindBy(css = ".modal-body button.gs-button__green")
	WebElement EXPORT_BY_PRODUCT_BTN;

	public ExportOrderByProductDialog clickCancel() {
		commonAction.clickElementByJS(CANCEL_BTN);
		logger.info("Clicked on 'Cancel' button.");
		return this;
	}
	
	public ExportOrderByProductDialog clickExportByProduct() {
		commonAction.clickElement(EXPORT_BY_PRODUCT_BTN);
		logger.info("Clicked on 'Export By Product' button.");
		return this;
	}

	public boolean isSelectProductDialogDisplayed() {
		commonAction.sleepInMiliSecond(1000);
		return !commonAction.isElementNotDisplay(EXPORT_ORDER_BY_PRODUCT_DIALOG);
	}	
	
}
