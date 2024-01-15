package pages.dashboard.onlineshop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Domains {

	final static Logger logger = LogManager.getLogger(Domains.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    
    public Domains(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_txtSubDomain = By.id("subDomain");
    By loc_txtNewDomain = By.id("newDomain");
    By loc_txtSampleDomain = By.cssSelector(".sub-domain__sample");

    public Domains inputSubDomain(String domain) {
    	commonAction.sendKeys(loc_txtSubDomain, domain);
    	logger.info("Input '" + domain + "' into Sub Domain field.");
    	return this;
    }
    
    public String getGeneratedSampleDomain() {
    	logger.info("Getting sample domain...");
    	return commonAction.getText(loc_txtSampleDomain);
    }

    public Domains inputNewDomain(String domain) {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtNewDomain).findElement(By.xpath("./parent::*/parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtNewDomain));
    		return this;
    	}
    	commonAction.sendKeys(loc_txtNewDomain, domain);
    	logger.info("Input '" + domain + "' into New Domain field.");
    	return this;
    }

    public String getNewDomainValue() {
    	logger.info("Getting new domain value...");
    	return commonAction.getValue(loc_txtNewDomain);
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
