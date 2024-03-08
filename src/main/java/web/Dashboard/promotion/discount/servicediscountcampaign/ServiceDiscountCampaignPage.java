package web.Dashboard.promotion.discount.servicediscountcampaign;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.commons.UICommonAction;
import utilities.links.Links;
import web.Dashboard.promotion.discount.product_discount_campaign.ProductDiscountCampaignPage;

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
	public By loc_detailPage_lblDiscountCampaignName = By.xpath("(//div[@class = 'row'])[3]/div[2]");
	public By loc_txtCampaignName = By.cssSelector("input#name");
	public By loc_btnAddCollection = By.cssSelector("fieldset[name ='conditionAppliesTo'] .gs-fake-link");
	public By loc_btnAddService = By.cssSelector("fieldset[name ='conditionAppliesTo'] .gs-fake-link");
	public By loc_lst_lblCollectionName = By.cssSelector(".product-name");
	public By loc_lst_lblServiceName = By.cssSelector(".product-name");
	public By loc_txtSearch = By.cssSelector(".search-input");
	public By loc_btnSave  = By.cssSelector(".gs-button__green");
	public By loc_btnEndEarly = By.cssSelector(".discount-campaign__detail .btn-save div");


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
	public ServiceDiscountCampaignPage navigateToCreateServiceCampaign(){
		String url = Links.DOMAIN + "/discounts/create/WHOLE_SALE_SERVICE";
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
		return this;
	}
	public ServiceDiscountCampaignPage clickOnAddService(){
		commonAction.click(loc_btnAddService);
		logger.info("Click on Add service button.");
		return this;
	}
	public ServiceDiscountCampaignPage clickOnAddServiceCollection(){
		commonAction.click(loc_btnAddCollection);
		logger.info("Click on Add service collection button.");
		return this;
	}
	public boolean isServiceShowOnSelectServiceList(String productName){
		commonAction.inputText(loc_txtSearch,productName);
		List<WebElement> productNames = commonAction.getElements(loc_lst_lblServiceName);
		if (productNames.isEmpty()) return false;
		for (int i=0; i<productNames.size();i++) {
			if(commonAction.getText(loc_lst_lblServiceName,i).equalsIgnoreCase(productName))
				return true;
		}
		return false;
	}
}
