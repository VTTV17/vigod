package mobile.buyer.home.account;

import app.Buyer.account.BuyerChangeLanguage;
import app.Buyer.account.BuyerMyProfile;
import app.Buyer.account.membershipinfo.MembershipInfo;
import app.Buyer.account.myorders.MyOrders;
import app.Buyer.login.LoginPage;
import app.Buyer.signup.SignupPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class BuyerAccountPage {
    final static Logger logger = LogManager.getLogger(BuyerAccountPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;
    BuyerAccountElement accountEl;

    int defaultTimeout = 5;
    
    public BuyerAccountPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
        accountEl = new BuyerAccountElement();
    }
    
    By NAVIGATE_LOGIN_BTN = By.xpath("//*[ends-with(@resource-id,'sign_in')]");
    By NAVIGATE_SIGNUP_BTN = By.xpath("//*[ends-with(@resource-id,'sign_up')]");
    By LOGOUT_BTN = By.xpath("//*[contains(@resource-id,'id/rlLogout')]");
    By CONFIRM_LOGOUT_BTN = By.xpath("//*[contains(@resource-id,'button1')]");
    By ABORT_LOGOUT_BTN = By.xpath("//*[contains(@resource-id,'button2')]");
    By LANGUAGE_BTN = By.xpath("//android.widget.RelativeLayout[contains(@resource-id,'fragment_tab_account_user_profile_rl_language_container')]/android.widget.TextView");
    By MEMBERSHIP_SECTION = By.xpath("//*[ends-with(@resource-id,'tab_account_user_profile_rl_membership_information_container')]");
    By MYORDER_SECTION = By.xpath("//*[ends-with(@resource-id,'tab_account_user_profile_rl_products_ordered_container')]");
    
    public LoginPage clickLoginBtn() {
    	commonAction.getElement(NAVIGATE_LOGIN_BTN, defaultTimeout).click();
    	logger.info("Clicked on Login button.");
        return new LoginPage(driver);
    }    
    
    public SignupPage clickSignupBtn() {
    	commonAction.getElement(NAVIGATE_SIGNUP_BTN, defaultTimeout).click();
    	logger.info("Clicked on Signup button.");
    	return new SignupPage(driver);
    }    
    
    public BuyerAccountPage clickLogoutBtn() {
        commonAction.getElement(LOGOUT_BTN, defaultTimeout).click();
    	logger.info("Clicked on Log out button.");
    	return this;
    }    
    
    public BuyerAccountPage clickConfirmLogoutBtn() {
    	commonAction.clickElement(CONFIRM_LOGOUT_BTN);
    	logger.info("Clicked on Confirm Log out button.");
    	return this;
    }    
    
    public BuyerAccountPage clickAbortLogoutBtn() {
    	commonAction.clickElement(ABORT_LOGOUT_BTN);
    	logger.info("Clicked on Abort Confirm Log out button.");
    	return this;
    }  

    public BuyerAccountPage logOutOfApp() {
    	commonAction.swipeByCoordinatesInPercent(0.5, 0.8, 0.5, 0.2);
    	clickLogoutBtn().clickConfirmLogoutBtn();
    	return this;
    }

    public MembershipInfo clickMembershipInfoSection() {
    	commonAction.clickElement(MEMBERSHIP_SECTION);
    	logger.info("Clicked on Membership Infomation section.");
    	return new MembershipInfo(driver);
	}    
    
    public MyOrders clickMyOrdersSection() {
    	commonAction.clickElement(MYORDER_SECTION);
    	logger.info("Clicked on My Order section.");
    	return new MyOrders(driver);
    }    
    
    public BuyerChangeLanguage clickLanguageBtn(){
        commonAction.sleepInMiliSecond(1000);
        commonAction.clickElement(LANGUAGE_BTN);
        return new BuyerChangeLanguage(driver);
    }
    public BuyerMyProfile clickProfile(){
        commonAction.sleepInMiliSecond(1000);
        commonAction.clickElement(accountEl.DISPLAY_NAME);
        commonAction.sleepInMiliSecond(2000);
        logger.info("Click on profile.");
        return new BuyerMyProfile(driver);
    }
    public BuyerAccountPage verifyAvatarDisplay(){
        commonAction.sleepInMiliSecond(1000);
        Assert.assertTrue(commonAction.isElementDisplay(commonAction.getElement(accountEl.AVATAR)));
        logger.info("Verify avatar display");
        return this;
    }
    public BuyerAccountPage verifyDisplayName(String displayNameExpected){
        commonAction.sleepInMiliSecond(1000);
        Assert.assertEquals(commonAction.getText(accountEl.DISPLAY_NAME),displayNameExpected);
        logger.info("Verify display name show correctly");
        return this;
    }
    public BuyerAccountPage verifyMemberShipLevel(String expected){
        Assert.assertEquals(commonAction.getText(accountEl.MEMBERSHIP_LEVEL),expected);
        logger.info("Verify member ship level show correctly");
        return this;
    }
    public BuyerAccountPage tapOnBarcodeIcon(){
        commonAction.clickElement(accountEl.BARCODE);
        logger.info("Tap on barcode icon.");
        return this;
    }
    public BuyerAccountPage verifyBarcode(String barcodeExpected){
        Assert.assertEquals(commonAction.getText(accountEl.MYBARCODE_BARCODE),barcodeExpected);
        logger.info("Verify barcode show correctly");
        return this;
    }
    public BuyerAccountPage scrollDown(){
        commonAction.swipeByCoordinatesInPercent(0.75,0.75,0.75,0.25);
        logger.info("Scroll down");
        return new BuyerAccountPage(driver);
    }
    public BuyerAccountPage logOut(){
        clickLogoutBtn();
        commonAction.clickElement(accountEl.LOGOUT_POPUP_LOGOUT_BTN);
        return this;
    }
    public BuyerAccountPage verifyLoginButtonShow(){
        Assert.assertTrue(commonAction.isElementDisplay(commonAction.getElement(NAVIGATE_LOGIN_BTN)),"Login button not show.");
        logger.info("Login button show.");
        return this;
    }
    public BuyerAccountPage changeLanguage(String lang){
        new BuyerAccountPage(driver).clickLanguageBtn()
                .changeLanguage(lang);
        return new BuyerAccountPage(driver);
    }
}
