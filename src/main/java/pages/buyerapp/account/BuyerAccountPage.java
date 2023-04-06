package pages.buyerapp.account;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonMobile;

public class BuyerAccountPage {
    final static Logger logger = LogManager.getLogger(BuyerAccountPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;

    int defaultTimeout = 5;
    
    public BuyerAccountPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }
    
    By NAVIGATE_LOGIN_BTN = By.xpath("//*[contains(@resource-id,'id/fragment_tab_account_user_profile_tv_sign_in')]");
    By LOGOUT_BTN = By.xpath("//*[contains(@resource-id,'id/rlLogout')]");
    By LANGUAGE_BTN = By.xpath("//android.widget.RelativeLayout[contains(@resource-id,'fragment_tab_account_user_profile_rl_language_container')]/android.widget.TextView");
    public BuyerAccountPage clickLoginBtn() {
    	commonAction.getElement(NAVIGATE_LOGIN_BTN, defaultTimeout).click();
    	logger.info("Clicked on Login button.");
        return this;
    }    
    
    public BuyerAccountPage clickLogoutBtn() {
    	commonAction.getElement(LOGOUT_BTN, defaultTimeout).click();
    	logger.info("Clicked on Log out button.");
    	return this;
    }    
    public BuyerChangeLanguage clickLanguageBtn(){
        commonAction.clickElement(LANGUAGE_BTN);
        return new BuyerChangeLanguage(driver);
    }
}
