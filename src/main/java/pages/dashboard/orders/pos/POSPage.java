package pages.dashboard.orders.pos;

import com.google.common.primitives.Booleans;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;
import utilities.model.dashboard.products.productInfomation.ProductInfo;

import java.time.Duration;

import static utilities.links.Links.DOMAIN;

public class POSPage extends POSElement{

	final static Logger logger = LogManager.getLogger(POSPage.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commons;

	public POSPage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
	}

	public void inputProductSearchTerm(String searchTerm) {
		commons.sendKeys(SEARCH_PRODUCT_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search Product box.");
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToUsePOS(String permission) {
		if (permission.contentEquals("A")) {
			commons.switchToWindow(1);
			new POSPage(driver).inputProductSearchTerm("Test Permission");
			commons.closeTab();
			commons.switchToWindow(0);
		} else if (permission.contentEquals("D")) {
            Assert.assertEquals(commons.getAllWindowHandles().size(), 1);
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/

	/*
		Create POS order with:
		- Normal/IMEI product with/without variation
		- With/without delivery
		- Apply some promotion
	 */
	public POSPage navigateToInStorePurchasePage() {
		driver.get(DOMAIN + "/order/instore-purchase");
		logger.info("Open POS page.");
		return this;
	}

	void changeBranch(String brName) {
		if (!commons.getText(CURRENT_BRANCH).equals(brName)) {
			// open branch dropdown
			while (driver.findElements(BRANCH_LIST).isEmpty()) {
				commons.click(BRANCH_DROPDOWN);
			}

			// find and change branch
			commons.getListElement(BRANCH_LIST).stream().filter(brElement -> brElement.getText().equals(brName)).findFirst().ifPresent(WebElement::click);

			// confirm change branch
			commons.click(CONFIRM_CHANGE_BRANCH_BTN);
		}
	}

	void selectProduct(ProductInfo productInfo) {
		// open search type dropdown
		commons.click(SEARCH_TYPE);

		// change search type to barcode
		commons.getListElement(LIST_SEARCH_TYPE).get(2).click();

		// add product to cart
		for (String barcode: productInfo.getBarcodeList()) {
			// search by barcode
			System.out.println(barcode);
			commons.sendKeys(SEARCH_PRODUCT_BOX, barcode);
			commons.click(SEARCH_PRODUCT_BOX);

			// select product
			commons.click(By.xpath(SEARCH_RESULT_BARCODE.formatted(barcode)));
//			((JavascriptExecutor) driver).executeScript("arguments[0].click()",commons.getElement(By.xpath(SEARCH_RESULT_BARCODE.formatted(barcode))));
		}
	}

	public void checkProductInformation(ProductInfo productInfo) {
		changeBranch("Paid branch 01 upd");
		selectProduct(productInfo);
	}
	
}
