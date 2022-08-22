package pages.dashboard.settings.plans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

import java.time.Duration;

public class PlansPage extends HomePage {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;

    public PlansPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//button[contains(@class,'btn-pay')]")
    WebElement PAY_BTN;
    @FindBy(xpath = "//div[contains(@class,'loading-wrapper')]")
    WebElement LOADING;
    @FindBy(xpath = "//table[@class='d-tablet-none d-desktop-exclude-tablet-block']//tr[1]//td[@class='value']")
    WebElement ORDER_ID;
    @FindBy(xpath = "//button[@class='btn btn-outline-secondary']")
    WebElement BANK_TRANSFER_BTN;
    @FindBy(css = ".wizard-layout__content a[href='/logout']")
    WebElement LOGOUT_BTN;
    String PLAN_PRICE_12M = "//tr[contains(@class,'plan-price')]//td[count(//div[text()='%planName%']//ancestor::th/preceding-sibling::*)+1]//button[not(contains(@class,'price-btn--disable'))]";
    final static Logger logger = LogManager.getLogger(PlansPage.class);

    public PlansPage selectPlan(String planName) {
        commons.waitTillElementDisappear(LOADING, 20);
        String newXpath = PLAN_PRICE_12M.replace("%planName%", planName);
        commons.clickElement(driver.findElement(By.xpath(newXpath)));
        logger.info("Select plan: "+planName);
        return this;
    }

    public PlansPage selectPayment() {
        commons.clickElement(BANK_TRANSFER_BTN);
        logger.info("Click bank transfer");
        commons.clickElement(PAY_BTN);
        logger.info("Click Pay button");
        return this;
    }

    public String getOrderId() {
        logger.info("Get orderID: "+commons.getText(ORDER_ID));
        return commons.getText(ORDER_ID);
    }
    public PlansPage clickOnLogOut(){
        commons.clickElement(LOGOUT_BTN);
        logger.info("Clicked on Logout link");
        return this;
    }
}
