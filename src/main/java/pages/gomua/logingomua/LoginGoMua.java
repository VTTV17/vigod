package pages.gomua.logingomua;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;

public class LoginGoMua {
    final static Logger logger = LogManager.getLogger(LoginGoMua.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction common;
    LoginGoMuaElement loginUI;

    public LoginGoMua (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        loginUI = new LoginGoMuaElement(driver);
        PageFactory.initElements(driver, this);
    }
    public LoginGoMua inputUsername(String userName){
        common.inputText(loginUI.USERNAME_INPUT,userName);
        logger.info("Input username: %s".formatted(userName));
        return this;
    }
    public LoginGoMua inputPassWord(String password){
        common.inputText(loginUI.PASSWORD_INPUT,password);
        logger.info("Input password: %s".formatted(password));
        return this;
    }
    public LoginGoMua clickOnLoginButton(){
        common.clickElement(loginUI.LOGIN_BTN);
        logger.info("Click on Login button on popup");
        return this;
    }
    public LoginGoMua loginWithUserName(String userName,String password){
        inputUsername(userName);
        inputPassWord(password);
        clickOnLoginButton();
        return this;
    }
    
    public LoginGoMua clickForgotPassword(){
        common.clickElement(loginUI.FORGOT_PASSWORD_LINKTEXT);
        logger.info("Click on 'Forgot Password' link text.");
        return this;
    }
    
}
