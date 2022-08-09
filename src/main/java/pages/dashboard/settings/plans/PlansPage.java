package pages.dashboard.settings.plans;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;

public class PlansPage {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    public  PlansPage(WebDriver driver){
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver,this);
    }
    @FindBy(xpath = "//button[contains(@class,'btn-pay')]")
    WebElement PAY_BTN;
    String PLAN_PRICE_12M ="//tr[contains(@class,'plan-price')]//td[count(//div[text()='%planName%']//ancestor::th/preceding-sibling::*)+1]//button[not(contains(@class,'price-btn--disable'))]";
    public PlansPage selectPlan(String planName){
        String newXpath = PLAN_PRICE_12M.replace("%planName%",planName);
        commons.clickElement(driver.findElement(By.xpath(newXpath)));
        return this;
    }

}
