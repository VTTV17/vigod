package web.InternalTool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import web.StoreFront.services.ServiceDetailPage;
import utilities.commons.UICommonAction;

import java.time.Duration;

import static utilities.account.AccountTest.*;
import static utilities.links.Links.INTERNAL_TOOL;

public class InternalTool {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    final static Logger logger = LogManager.getLogger(ServiceDetailPage.class);

    public InternalTool(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }
    String PAGE_LINKS = "//li[text()='%s']//following-sibling::li[1]//a[text()='%s']";
    String APPROVE_BTN_BY_ORDERID = "//tbody//td[text()='%s']//parent::tr//td[count(//th[text()='Approved']//preceding-sibling::*)+1]/span";
    String APPROVE_BTN_BY_SHOPNAME = "(//tbody//td[text()='%s']//parent::tr//td[count(//th[text()='Approved']//preceding-sibling::*)+1]/span)[1]";

    By loc_icnLoading = By.xpath("//div[contains(@class,'ant-spin-spinning')]");
    By loc_txtContactID = By.xpath("//input[@placeholder='Contract ID']");
    By loc_dlgConfirmation_btnApprove = By.xpath("//span[text()='Approve']/parent::button");
    By loc_lblSuccessfullMessage = By.xpath("//span[text()='Successful!']");
    By loc_txtUserName = By.cssSelector("#email");
    By loc_txtPassword = By.cssSelector("#email");
    By loc_btnLogin = By.xpath("//button[@type='submit']");

    /**
     * @param packageType Input value: GoSell, VipDeal, Call center, Go F&B, Branch Purchase,Shopee Purchase, Language Purchase,Affiliate Purchase,Reports
     * @param menuItems Input value: Packages, Orders list
     */
    public InternalTool navigateToPage(String packageType,String...menuItems) {
        commons.waitVisibilityOfElementLocated(loc_icnLoading);
        commons.waitInvisibilityOfElementLocated(loc_icnLoading);
        for (String menuItem:menuItems) {
            String NEW_XPATH_ORDER_LIST = PAGE_LINKS.formatted(packageType,menuItem);
            commons.clickElement(driver.findElement(By.xpath(NEW_XPATH_ORDER_LIST)));
            logger.info("Click on menu item to navigate to: "+menuItem);
        }
        return this;
    }

    public InternalTool approveOrder(String orderId) {
        commons.waitVisibilityOfElementLocated(loc_icnLoading);
        commons.waitInvisibilityOfElementLocated(loc_icnLoading);
        String NEW_XPATH_APPROVE_BTN = APPROVE_BTN_BY_ORDERID.formatted(orderId);
        commons.clickElement(driver.findElement(By.xpath(NEW_XPATH_APPROVE_BTN)));
        commons.inputText(loc_txtContactID, "aaaa");
        commons.click(loc_dlgConfirmation_btnApprove);
        commons.waitVisibilityOfElementLocated(loc_lblSuccessfullMessage);
        logger.info("Approved order");
        return this;
    }

    public String processApprovalStatus(String statusIcon) {
		if (statusIcon.contentEquals("✔")) {
			return "Approved";
		}
		else if (statusIcon.contentEquals("❗")) {
			return "Cancelled";
		}
		else if (statusIcon.contentEquals("👍")) {
			return "Pending Approval";
		}
        return "Undefined";
    } 
    
    /**
     * <p> Get approval status of an order by its ID <p>
     * @param orderId ID of the order to get status
     * @return one of these values: Undefined/Approved/Cancelled/Pending Approval
     */
    public String getOrderApprovalStatus(String orderId) {
        commons.waitVisibilityOfElementLocated(loc_icnLoading);
        commons.waitInvisibilityOfElementLocated(loc_icnLoading);
        String statusIcon = commons.getText(driver.findElement(By.xpath(APPROVE_BTN_BY_ORDERID.formatted(orderId))));
		String statusMessage = processApprovalStatus(statusIcon);
        logger.info("Approval status of order '%s': '%s'".formatted(orderId, statusMessage));
        return statusMessage;
    } 
    
    /**
     * <p> Get approval status of an order by shop name <p>
     * @param shopName
     * @return one of these values: Undefined/Approved/Cancelled/Pending Approval
     */
    public String getOrderApprovalStatusByShopName(String shopName) {
        commons.waitVisibilityOfElementLocated(loc_icnLoading);
        commons.waitInvisibilityOfElementLocated(loc_icnLoading);
    	String statusIcon = commons.getText(driver.findElement(By.xpath(APPROVE_BTN_BY_SHOPNAME.formatted(shopName))));
    	String statusMessage = processApprovalStatus(statusIcon);
    	logger.info("Approval status of order registered by shop '%s': '%s'".formatted(shopName, statusMessage));
    	return statusMessage;
    }    
    
    public InternalTool login() {
        commons.inputText(loc_txtUserName,USERNAME_INTERNALTOOL);
        commons.inputText(loc_txtPassword,PASSWORD_INTERNALTOOL);
        commons.click(loc_btnLogin);
        return this;
    }
    public InternalTool openNewTabAndNavigateToInternalTool(){
        commons.openNewTab();
        commons.switchToWindow(1);
        driver.manage().window().setSize(new Dimension(1920, 1080)); //When run in headless, the browser is small so some elements are not displayed
        commons.navigateToURL(INTERNAL_TOOL);
        return this;
    }
    public InternalTool closeTab(){
        commons.closeTab();
        commons.switchToWindow(0);
        return this;
    }

}
