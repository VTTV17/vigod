package pages.gomua.logingomua;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginGoMuaElement {
    WebDriver driver;
    public LoginGoMuaElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(id = "usr")
    WebElement USERNAME_INPUT;
    @FindBy(xpath = "//section[@class='signin-box']//input[@type='password'and @id='pwd']")
    WebElement PASSWORD_INPUT;
    @FindBy(xpath = "//button[@beetranslate='beecow.login.login']")
    WebElement LOGIN_BTN;
}
