package web.GoMua.logingomua;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class LoginGoMua {
    final static Logger logger = LogManager.getLogger(LoginGoMua.class);

    WebDriver driver;
    UICommonAction common;
    LoginGoMuaElement loginUI;

    public LoginGoMua (WebDriver driver) {
        this.driver = driver;
        common = new UICommonAction(driver);
        loginUI = new LoginGoMuaElement();
    }
    public LoginGoMua inputUsername(String userName){
        common.inputText(loginUI.loc_txtUsername,userName);
        logger.info("Input username: %s".formatted(userName));
        return this;
    }
    public LoginGoMua inputPassWord(String password){
        common.inputText(loginUI.loc_txtPassword,password);
        logger.info("Input password: %s".formatted(password));
        return this;
    }
    public LoginGoMua clickOnLoginButton(){
        common.click(loginUI.loc_btnLogin);
        logger.info("Click on Login button on popup");
        return this;
    }
    public LoginGoMua loginWithUserName(String userName,String password){
        inputUsername(userName);
        inputPassWord(password);
        clickOnLoginButton();
        common.sleepInMiliSecond(500);
        return this;
    }
    
    public LoginGoMua clickForgotPassword(){
        common.click(loginUI.loc_lnkForgotPassword);
        logger.info("Click on 'Forgot Password' link text.");
        return this;
    }
    
}
