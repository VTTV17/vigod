package web.Dashboard.promotion.discount.servicediscountcampaign;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.commons.UICommonAction;

public class ServiceDiscountCampaignPage {

	final static Logger logger = LogManager.getLogger(ServiceDiscountCampaignPage.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public ServiceDiscountCampaignPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "fieldset[name ='conditionAppliesTo'] label")
    List<WebElement> APPLIES_TO_LABEL;
    
	public ServiceDiscountCampaignPage tickAppliesTo(int optionIndex) {
		commonAction.waitElementList(APPLIES_TO_LABEL);
		if (optionIndex ==0) {
			commonAction.checkTheCheckBoxOrRadio(APPLIES_TO_LABEL.get(optionIndex));
			logger.info("Ticked 'All Services' radio button.");
		} else if (optionIndex ==1) {
			commonAction.checkTheCheckBoxOrRadio(APPLIES_TO_LABEL.get(optionIndex));
			logger.info("Ticked 'Specific Service Collections' radio button.");
		} else if (optionIndex ==2) {
			commonAction.checkTheCheckBoxOrRadio(APPLIES_TO_LABEL.get(optionIndex));
			logger.info("Ticked 'Specific Services' radio button.");
		} else {
			logger.info("Input value is not in range (0:2). By default, 'All Products' radio button is ticked.");
			commonAction.checkTheCheckBoxOrRadio(APPLIES_TO_LABEL.get(0));
		}
		return this;
	} 
    
}
