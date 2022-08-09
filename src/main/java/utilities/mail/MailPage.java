package utilities.mail;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.screenshot.Screenshot;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MailPage extends MailElement {
    WebDriverWait wait;

    public MailPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public MailPage openMail(String username) throws InterruptedException {
        sleep(10000);
        ((JavascriptExecutor) driver).executeScript("window.open('%s');".formatted("https://qa.team/" + username));
        var tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1));
        return this;
    }

    public String getPassword(String subjectText) throws IOException, InterruptedException {
        String password = "";
        new Screenshot().takeScreenshot(driver);
        if (MAIL_ELEMENT.size() == 0) {
            sleep(5000);
            driver.navigate().refresh();
            new Screenshot().takeScreenshot(driver);
        }
        driver.navigate();
        for (int i = 0; i < MAIL_ELEMENT.size(); i++) {
            MAIL_ELEMENT.get(i).click();

            if (MAIL_ELEMENT.get(i).getText().contains(subjectText)) {
                wait.until(ExpectedConditions.visibilityOf(PASSWORD.get(i)));
                password = PASSWORD.get(i).getText().split("Password :")[1].split("Log In at")[0];
                if (password.length() < 10) {
                    break;
                }
            }
        }
        var tabs = new ArrayList<>(driver.getWindowHandles());
        if (tabs.size() > 1) {
            driver.switchTo().window(tabs.get(tabs.size() - 1));
            ((JavascriptExecutor) driver).executeScript("window.close();");
        }
        driver.switchTo().window(tabs.get(0));
        return password;
    }
}
