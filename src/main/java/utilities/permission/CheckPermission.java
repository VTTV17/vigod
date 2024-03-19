package utilities.permission;

import api.Seller.login.Login;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.home.HomePage;

public class CheckPermission {
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
    		return true;
    	} catch (TimeoutException ex) {
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
    public void waitUntilPermissionUpdated(String staffPermissionTokenOld, LoginInformation  staffCredentials){
        String newToken;
        int i=0;
        do {
            LoginDashboardInfo info = new Login().getInfo(staffCredentials);
            newToken = info.getStaffPermissionToken();
            System.out.println("info: "+info);
            System.out.println("newToken: "+newToken);


            System.out.println("Wait to update staff permission...");
            i++;
        }while (newToken.equals(staffPermissionTokenOld) && i<20);
        if(newToken.equals(staffPermissionTokenOld))
            try {
                throw new Exception("Staff permission token is not updated.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

}
