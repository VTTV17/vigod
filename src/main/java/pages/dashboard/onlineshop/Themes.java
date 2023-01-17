package pages.dashboard.onlineshop;

import java.time.Duration;
import java.util.ArrayList;

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

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Themes {

	final static Logger logger = LogManager.getLogger(Themes.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public Themes(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = ".btn-create.ml-auto")
    WebElement COMPLETE_BTN;
    
    @FindBy (css = ".theme-library-template__item__actions .gs-button.gs-button__green.gs-button--undefined")
    WebElement EDIT_THEME_BTN;
    
    By MODAL_CONTENT = By.cssSelector(".modal-content");
    
    public Themes clickVisitThemeStore() {
    	commonAction.clickElement(COMPLETE_BTN);
    	logger.info("Clicked on 'Visit Theme Store' button.");
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	return this;
    }
    
    public Themes clickEditTheme() {
    	commonAction.hoverOverElement(EDIT_THEME_BTN);
    	if (commonAction.isElementVisiblyDisabled(EDIT_THEME_BTN)) {
    		new HomePage(driver).isMenuClicked(EDIT_THEME_BTN);
    		return this;
    	}
    	commonAction.clickElement(EDIT_THEME_BTN);
    	logger.info("Clicked on 'Edit' button.");
    	return this;
    }
    
    public boolean isModalContentDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return !commonAction.isElementNotDisplay(driver.findElements(MODAL_CONTENT));
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
