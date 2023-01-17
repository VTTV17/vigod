package pages.dashboard.onlineshop;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Domains {

	final static Logger logger = LogManager.getLogger(Domains.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public Domains(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (id = "subDomain")
    WebElement SUB_DOMAIN;
    
    @FindBy (id = "newDomain")
    WebElement NEW_DOMAIN;
    
    @FindBy (css = ".sub-domain__sample")
    WebElement SAMPLE_DOMAIN;
    
    public Domains inputSubDomain(String domain) {
    	commonAction.inputText(SUB_DOMAIN, domain);
    	logger.info("Input '" + domain + "' into Sub Domain field.");
    	return this;
    }
    
    public String getGeneratedSampleDomain() {
    	logger.info("Getting sample domain...");
    	return commonAction.getText(SAMPLE_DOMAIN);
    }

    public Domains inputNewDomain(String domain) {
    	if (commonAction.isElementVisiblyDisabled(NEW_DOMAIN.findElement(By.xpath("./parent::*/parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(NEW_DOMAIN);
    		return this;
    	}
    	commonAction.inputText(NEW_DOMAIN, domain);
    	logger.info("Input '" + domain + "' into New Domain field.");
    	return this;
    }

    public String getNewDomainValue() {
    	logger.info("Getting new domain value...");
    	return commonAction.getElementAttribute(NEW_DOMAIN, "value");
    }
    
    public void verifyPermissionToEditSubDomain(String permission) {
    	if (permission.contentEquals("A")) {
    		inputSubDomain("testpermission");
    		Assert.assertTrue(getGeneratedSampleDomain().contains("testpermission"));
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
    public void verifyPermissionToEditNewDomain(String permission) {
    	inputNewDomain("testpermission.com");
    	String text = getNewDomainValue();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(text.contentEquals("testpermission.com"));
    	} else if (permission.contentEquals("D")) {
    		Assert.assertTrue(text.contentEquals(""));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
}
