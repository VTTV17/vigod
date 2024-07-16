package web.Dashboard.onlineshop.menus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.enums.MenuItemType;
import utilities.links.Links;
import web.Dashboard.confirmationdialog.ConfirmationDialog;

public class AddMenu {

	final static Logger logger = LogManager.getLogger(AddMenu.class);

	WebDriver driver;
	UICommonAction commonAction;

	public AddMenu(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtMenuName = By.id("name");
	By loc_btnAddMenuItem = By.cssSelector(".add-menu-item");
	By loc_btnSave = By.cssSelector(".gs-button__green, .btn-save");
	By loc_dlgAddMenuItem_txtName = By.cssSelector(".menu-item-modal #name");
	By loc_ddlUrlLinkType = By.cssSelector(".links .btn-secondary");
	By loc_ddvUrlLinkType = By.cssSelector(".links .dropdown-menu button");
	By loc_ddlUrlLinkValue = By.cssSelector(".collections .btn-secondary");
	By loc_ddvUrlLinkValue = By.cssSelector(".collections .dropdown-menu button");
	By loc_btnEditTranslation = By.xpath("//button[contains(@class,'gs-button__green')]/preceding-sibling::button");
	By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
	public AddMenu inputMenuTitle(String menuTitle) {
		commonAction.sendKeys(loc_txtMenuName, menuTitle);
		logger.info("Input '" + menuTitle + "' into Menu Title field.");
		return this;
	}
	public void inputMenuItemName(String menuItem){
		commonAction.inputText(loc_dlgAddMenuItem_txtName,menuItem);
		logger.info("Input %s into name file on Add menu item popup.".formatted(menuItem));
	}
	public void clickOnAddMenuItem(){
		commonAction.click(loc_btnAddMenuItem);
		logger.info("Click on add menu item button.");
	}
	public void clickOnSaveBtn(){
		commonAction.click(loc_btnSave);
		logger.info("Click on Save button.");
	}
	public void selectUrlLinkType(MenuItemType type){
		commonAction.click(loc_ddlUrlLinkType);
		switch (type){
			case COLLECTION_PRODUCT -> commonAction.click(loc_ddvUrlLinkType,0);
			case COLLECTION_SERVICE -> commonAction.click(loc_ddvUrlLinkType,1);
			case PAGE -> commonAction.click(loc_ddvUrlLinkType,2);
			case BLOG -> commonAction.click(loc_ddvUrlLinkType,4);
			case ARTICLE -> commonAction.click(loc_ddvUrlLinkType,5);
		}
		logger.info("Select url link type: "+type);
	}

	/**
	 * Don't use this function to input link value.
	 */
	public void selectUrlLinkValue(){
		commonAction.click(loc_ddlUrlLinkValue);
		commonAction.click(loc_ddvUrlLinkValue,0);
	}
	public void createSimpleAMenu(){
		inputMenuTitle("menu "+new DataGenerator().randomNumberGeneratedFromEpochTime(5));
		clickOnAddMenuItem();
		inputMenuItemName("menu item "+new DataGenerator().randomNumberGeneratedFromEpochTime(5));
		selectUrlLinkType(MenuItemType.COLLECTION_PRODUCT);
		selectUrlLinkValue();
		new ConfirmationDialog(driver).clickGreenBtn();
		commonAction.sleepInMiliSecond(300);
		new ConfirmationDialog(driver).clickGreenBtnOnSecondModal();
		commonAction.sleepInMiliSecond(500);
		clickOnSaveBtn();
	}
	public AddMenu clickOnEditTranslation(){
		commonAction.click(loc_btnEditTranslation);
		logger.info("Click on Edit Translation button");
		commonAction.sleepInMiliSecond(500,"Waiting modal show.");
		return this;
	}
	public AddMenu clickSaveOnEditTranslationModal(){
		commonAction.click(loc_dlgEditTranslation_btnSave);
		logger.info("Click on Save button on Edit Translation modal.");
		return this;
	}
}
