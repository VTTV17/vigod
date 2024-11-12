package web.Dashboard.products.all_products.crud.shopeesync;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import web.Dashboard.home.HomePage;

public class ShopeeSyncPage {
	
	final static Logger logger = LogManager.getLogger(ShopeeSyncPage.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    ShopeeSyncElement elements;

    public ShopeeSyncPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        elements = new ShopeeSyncElement();
    }
    
    public ShopeeSyncPage clickPlusIcon(){
        commonAction.click(elements.loc_icnPlus);
        logger.info("Clicked Plus icon to show product name and description");
        return this;
    }
    
    public ShopeeSyncPage inputDescription(String productDescription){
    	commonAction.inputText(elements.loc_txtDescription, productDescription);
    	logger.info("Input product description: {}", productDescription);
    	return this;
    }
    
    /**
     * This is a temporary function that helps select a category for a product before pushing it to Shopee.
     * The function assumes there are 3 category levels and the first option of each level will be selected
     */
    public ShopeeSyncPage selectCategory(){
    	
    	commonAction.click(elements.loc_ddlCategory);
    	
    	for (int i=1; i<=3; i++) {
    		commonAction.click(elements.loc_ddvFirstOptionOfCategoryLevel(i));
    		logger.info("Category level {} is defined", i);
    	}
    	
    	return this;
    }
    
    /**
     * Selects 'No brand' as the brand of a product
     */
    public ShopeeSyncPage selectBrand(){
    	commonAction.click(elements.loc_ddlBrand);
    	commonAction.click(elements.loc_ddvNoBrand);
    	logger.info("Selected brand: 'No brand'");
    	return this;
    }
    
    /**
     * Selects a random logistics option that is not disabled 
     */
    public ShopeeSyncPage selectLogistics(){
    	commonAction.click(elements.loc_chkLogicticsOption);
    	logger.info("Selected a logistics option");
    	return this;
    }
    
    public ShopeeSyncPage clickSaveBtn(){
    	commonAction.click(elements.loc_btnCreate);
    	logger.info("Clicked Create button");
    	new HomePage(driver).waitTillLoadingDotsDisappear().getToastMessage();
    	//TODO Use toast message to verify the creation is successful
    	return this;
    }
    
}
