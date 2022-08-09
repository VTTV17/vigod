package utilities.mail;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class MailElement {
    WebDriver driver;

    public MailElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = "#messages > a")
    List<WebElement> MAIL_ELEMENT;

    @FindBy (css = "div.message_full")
    List<WebElement> PASSWORD;
}
