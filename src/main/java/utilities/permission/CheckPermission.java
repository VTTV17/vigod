package utilities.permission;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import api.Seller.login.Login;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import web.Dashboard.home.HomePage;

public class CheckPermission {
	final static Logger logger = LogManager.getLogger(CheckPermission.class);
	
    WebDriver driver;
    UICommonAction commonAction;

    public CheckPermission(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
    By loc_dlgRestricted_btnOK = By.cssSelector(".modal.fade.show .gs-button__deep-blue");

    By loc_lblNoPermissionNotice = By.xpath("//*[@class='no-permission-wrapper__content' or @class='access-restricted modal-header']/ancestor::div[2]/self::*[(not(@hidden) and (@class='tab-pane active' or not(@class='tab-pane')))]/div/div[1]");

    public boolean isAccessRestrictedPresent() {
    	try {
    		commonAction.getElement(loc_lblNoPermissionNotice);
            if (commonAction.getAttribute(loc_lblNoPermissionNotice, "class").contains("access-restricted")) {
                commonAction.closePopup(loc_dlgRestricted_btnOK, loc_lblNoPermissionNotice);
            }
            logger.info("Restriction Notice appears");
    		return true;
    	} catch (TimeoutException ex) {
    		logger.info("Restriction Notice do not appear");
    		return false;
    	}
    }

    public boolean checkAccessRestricted(By locator) {
        commonAction.clickJS(locator);
        return isAccessRestrictedPresent();
    }

    public boolean checkAccessRestricted(By locator, int index) {
        commonAction.clickJS(locator, index);
        return isAccessRestrictedPresent();
    }
    public boolean checkAccessRestricted(String url) {
        driver.get(url);
        try {
            commonAction.waitURLShouldBeContains("/restricted", 2000);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(By locator, By destinationLocator) {
        commonAction.clickJS(locator);
        try {
            commonAction.getElement(destinationLocator);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(By locator, int index, By destinationLocator) {
        commonAction.clickJS(locator, index);
        try {
            commonAction.getElement(destinationLocator);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(By locator, String url) {
        commonAction.clickJS(locator);
        try {
            commonAction.waitURLShouldBeContains(url, 5000);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(By locator, int index, String url) {
        commonAction.click(locator, index);
        try {
            commonAction.waitURLShouldBeContains(url, 5000);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(String url, String destinationURL) {
        driver.get(url);
        try {
            commonAction.waitURLShouldBeContains(destinationURL, 5000);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(String url, By destinationLocator) {
        driver.get(url);
        new HomePage(driver).waitTillSpinnerDisappear1();
        try {
            commonAction.getElement(destinationLocator);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }
    /**
     * Use: check input has value or not.
     * @param url: use url to navigate to page
     * @param locator: input element
     * @return true if show data / false if show no data
     */
    public boolean checkValueShow(String url, By locator){
        commonAction.navigateToURL(url);
        new HomePage(driver).waitTillSpinnerDisappear1();
        commonAction.sleepInMiliSecond(2000);
        String value = commonAction.getValue(locator);
        return !value.isEmpty();
    }

    /**
     * Use: check input has value or not.
     * @param locatorClick: use click to navigate to page
     * @param locatorInput: check value of this element
     * @return true if show data / false if show no data
     */
    public boolean checkValueShow(By locatorClick, By locatorInput){
        commonAction.click(locatorClick);
        new HomePage(driver).waitTillSpinnerDisappear1();
        commonAction.sleepInMiliSecond(2000);
        String value = commonAction.getValue(locatorInput);
        return !value.isEmpty();
    }
    public boolean checkValueShow(By locatorClick,int index, By locatorInput){
        commonAction.click(locatorClick,index);
        new HomePage(driver).waitTillSpinnerDisappear1();
        commonAction.sleepInMiliSecond(2000);
        String value = commonAction.getValue(locatorInput);
        return !value.isEmpty();
    }
    public String waitUntilPermissionUpdated(String oldPermissionToken, LoginInformation loginCredentials){
    	LinkedHashMap<String, Integer> oldPermissions = new AllPermissions().getStaffPermissionFromToken(oldPermissionToken);
    	LinkedHashMap<String, Integer> newPermissions = null;
    	
    	String newPermissionToken = "";
    	
    	for (int i=0; i<5; i++) {
    		
    		newPermissionToken = new Login().getInfo(loginCredentials).getStaffPermissionToken();
    		
    		newPermissions = new AllPermissions().getStaffPermissionFromToken(new Login().getInfo(loginCredentials).getStaffPermissionToken());
    		if(!newPermissions.equals(oldPermissions)) break;
    		commonAction.sleepInMiliSecond(1000, "Wait until permissions have changed");
    	}
    	return newPermissionToken;
    }

}
