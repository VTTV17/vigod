package pages.dashboard.customers.allcustomers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

import java.time.Duration;

public class AllCustomers {
	
	final static Logger logger = LogManager.getLogger(AllCustomers.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public AllCustomers (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }


    @FindBy (css = ".branch-item-list")
    WebElement BRANCH_LIST;
    
    @FindBy (css = "branch-item-list")
    WebElement BRANCH;

    @FindBy(id = "phone")
    WebElement PHONE; 
    

    @FindBy (css = "div.modal-content")
    WebElement WARNING_POPUP;
    

    public AllCustomers clickBranchList() {
    	commonAction.clickElement(BRANCH_LIST);
    	logger.info("Clicked on Branch list.");
        return this;
    }
    
    public AllCustomers selectBranch(String branch) {
    	commonAction.sleepInMiliSecond(1000);
    	clickBranchList();
    	commonAction.clickElement(driver.findElement(By.xpath("//div[@class='uik-select__label' and text()='%s']".formatted(branch))));
    	logger.info("Selected branch: " + branch);
    	new HomePage(driver).waitTillSpinnerDisappear();
    	return this;
    }
    
    public AllCustomers clickUser(String user) {
    	commonAction.sleepInMiliSecond(1000);
    	commonAction.clickElement(driver.findElement(By.xpath("//div[@class='full-name' and text()='%s']".formatted(user))));
    	logger.info("Clicked on user: " + user);
    	new HomePage(driver).waitTillSpinnerDisappear();
    	return this;
    }    

    public String getPhoneNumber(String user) {
    	WebElement element = driver.findElement(By.xpath("//div[@class='full-name' and text()='%s']/parent::*/following-sibling::td[2]".formatted(user)));
    	String value = commonAction.getText(element);
    	logger.info("Retrieved phone number: " + value);
    	return value;
    }    
    
}
