package web.Dashboard.promotion.discount.discountcode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import web.Dashboard.home.HomePage;

/**
 * Shared Page Object for Product/Service Discount Page Objects
 * Acts as a Base Object for Product/Service Discount Page Objects
 * Includes properties and functions that its child can inherit from
 */
public class DiscountCodePage {
	final static Logger logger = LogManager.getLogger(DiscountCodePage.class);

	public WebDriver driver;

	UICommonAction commons;
	DiscountCodeElement elements;
	HomePage homePage;

	public DiscountCodePage(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new DiscountCodeElement();
	}

	public DiscountCodePage navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commons.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		
	
	public DiscountCodePage inputDiscountCodeName(String name) {
		commons.inputText(elements.loc_txtDiscountCodeName, name);
		logger.info("Input discount code name: " + name);
		return this;
	}

	public DiscountCodePage selectApplyToOption(int option) {
		commons.click(elements.loc_rdoApplyToOptions, option);
		logger.info("Selected Apply To option: " + option);
		return this;
	}	

	public DiscountCodePage clickAddCollectionLink() {
		commons.click(elements.loc_lnkAddCollectionOrSpecificProduct);
		logger.info("Clicked on Add Collection link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(elements.loc_dlgSelectCollection).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Collection dialog to appear");
		}
		return this;
	}		
	
	public DiscountCodePage clickSpecificAddProductOrServiceLink() {
		commons.click(elements.loc_lnkAddCollectionOrSpecificProduct);
		logger.info("Clicked on Add Product/Service link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(elements.loc_dlgSelectProduct).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Product/Service dialog to appear");
		}
		return this;
	}

	public DiscountCodePage inputSearchTermInDialog(String searchTerm) {
		commons.inputText(elements.loc_txtSearchInDialog, searchTerm);
		logger.info("Input search term: " + searchTerm);
		commons.sleepInMiliSecond(1000, "Wait a little inputSearchTermInDialog"); //Will find a better way to remove this sleep
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public boolean isProductOrServicePresentInDialog() {
		commons.sleepInMiliSecond(1000, "Wait a little for products/services to appear in the Add Product/Service dialog");
		return !commons.getElements(elements.loc_tblProductNames).isEmpty();
	}

	public boolean isCollectionPresentInDialog() {
		commons.sleepInMiliSecond(1000, "Wait a little for collections to appear in the Add Collection dialog");
		return !commons.getElements(elements.loc_tblProductNames).isEmpty();
	}	    

	public DiscountCodePage clickSaveBtn() {
		commons.click(elements.loc_btnSave);
		logger.info("Clicked on Save button");
		return this;
	}		
	
	public String getPageTitle() {
		String title = commons.getText(elements.loc_lblPageTitle);
		logger.info("Retrieved page title: " + title);
		return title;
	}	

}
