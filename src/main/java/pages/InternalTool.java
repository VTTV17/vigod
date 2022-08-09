package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.reporters.jq.INavigatorPanel;
import pages.storefront.ServiceDetailPage;
import utilities.UICommonAction;

import java.time.Duration;

public class InternalTool {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    final static Logger logger = LogManager.getLogger(ServiceDetailPage.class);
    public InternalTool(WebDriver driver){
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver,this);
    }
    @FindBy(xpath = "//div[contains(@class,'ant-spin-spinning')]")
    WebElement LOADING;
    @FindBy(xpath = "//input[@placeholder='Contract ID']")
    WebElement CONTACT_ID_INPUT;
    @FindBy(xpath = "//button/span[text()='Approve']")
    WebElement APPROVE_BTN_ON_POPUP;
    @FindBy(xpath = "//span[text()='Successful!']")
    WebElement SUCCESSFUL_MESSAGE;
    String ORDER_LIST_LINKS = "//li[text()='%s']//following-sibling::li[1]//a[text()='Orders list']";
    String APPROVE_BTN_BY_ORDERID = "//tbody//td[text()='%s']//parent::tr//td[count(//th[text()='Approved']//preceding-sibling::*)+1]/span";
    public InternalTool navigateToOrderList (String packageType){
        String NEW_XPATH_ORDER_LIST= ORDER_LIST_LINKS.formatted(packageType);
        commons.clickElement(driver.findElement(By.xpath(NEW_XPATH_ORDER_LIST)));
        logger.info("Click on menu item to navigate to Order list page");
        return this;
    }
    public InternalTool approveOrder(String orderId){
        commons.waitTillElementDisappear(LOADING,30);
        String NEW_XPATH_APPROVE_BTN = APPROVE_BTN_BY_ORDERID.formatted(orderId);
        commons.clickElement(driver.findElement(By.xpath(NEW_XPATH_APPROVE_BTN)));
        commons.inputText(CONTACT_ID_INPUT,"aaaa");
        commons.clickElement(APPROVE_BTN_ON_POPUP);
        commons.waitForElementVisible(SUCCESSFUL_MESSAGE);
        logger.info("Approved order");
        return this;
    }

}
