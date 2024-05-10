package web.Dashboard.orders.orderlist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class ExportOrderByProductDialog {

	final static Logger logger = LogManager.getLogger(ExportOrderByProductDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ExportOrderByProductDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_btnExportOrderByProduct = By.cssSelector(".select-product-modal");
	By loc_btnCancel = By.cssSelector(".modal-body button.gs-button__gray--outline");
	By loc_btnExportByProduct = By.cssSelector(".modal-body button.gs-button__green");

	public ExportOrderByProductDialog clickCancel() {
		commonAction.clickJS(loc_btnCancel);
		logger.info("Clicked on 'Cancel' button.");
		return this;
	}
	
	public ExportOrderByProductDialog clickExportByProduct() {
		commonAction.click(loc_btnExportByProduct);
		logger.info("Clicked on 'Export By Product' button.");
		return this;
	}

	public boolean isSelectProductDialogDisplayed() {
		commonAction.sleepInMiliSecond(1000);
		return !commonAction.getElements(loc_btnExportOrderByProduct).isEmpty();
	}	
	
}
