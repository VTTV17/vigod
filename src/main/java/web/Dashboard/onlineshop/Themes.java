package web.Dashboard.onlineshop;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class Themes {

	final static Logger logger = LogManager.getLogger(Themes.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    
    public Themes(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_btnVisitThemeLibrary = By.cssSelector(".btn-create.ml-auto");
    By loc_btnEditTheme = By.cssSelector(".theme-library-template__item__actions .gs-button.gs-button__green.gs-button--undefined");
    By loc_dlgModal = By.cssSelector(".modal-content");
    
    public Themes clickVisitThemeStore() {
    	commonAction.click(loc_btnVisitThemeLibrary);
    	logger.info("Clicked on 'Visit Theme Store' button.");
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	return this;
    }
    
    public Themes clickEditTheme() {
    	commonAction.hoverActions(loc_btnEditTheme);
    	WebElement el = commonAction.getElement(loc_btnEditTheme);
    	if (commonAction.isElementVisiblyDisabled(el)) {
    		new HomePage(driver).isMenuClicked(el);
    		return this;
    	}
    	commonAction.click(loc_btnEditTheme);
    	logger.info("Clicked on 'Edit' button.");
    	return this;
    }
    
    public boolean isModalContentDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return commonAction.getElements(loc_dlgModal).size() >0;
    } 
    
    public void verifyPermissionToCustomizeAppearance(String permission) {
    	String originalWindowHandle = commonAction.getCurrentWindowHandle();
    	ArrayList<String> list = commonAction.getAllWindowHandles();
    	int originalSize = list.size();
		clickVisitThemeStore().clickEditTheme();
		ArrayList<String> list1 = commonAction.getAllWindowHandles();
		int laterSize = list1.size();
		new HomePage(driver).waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(originalSize < laterSize);
    		for(String winHandle : list1){
    			if (!winHandle.contentEquals(originalWindowHandle)) {
    				commonAction.switchToWindow(winHandle);
    			}
    		}
    		commonAction.closeTab();
    		commonAction.switchToWindow(originalWindowHandle);
    		commonAction.navigateBack();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertTrue(originalSize == laterSize);
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
}
