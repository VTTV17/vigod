package web.Dashboard.products.all_products.crud.shopeesync;

import java.util.List;

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
 
    public ShopeeSyncPage inputPackageWeight(String packageWeight){
    	commonAction.inputText(elements.loc_txtPackageWeight, packageWeight);
    	logger.info("Input Package weight: {}", packageWeight);
    	return this;
    }    
    //TODO add function description
    public ShopeeSyncPage inputPackageDimensions(List<String> length_Width_Height){
    	commonAction.inputText(elements.loc_txtPackageLength, length_Width_Height.get(0));
    	commonAction.inputText(elements.loc_txtPackageWidth, length_Width_Height.get(1));
    	commonAction.inputText(elements.loc_txtPackageHeight, length_Width_Height.get(2));
    	logger.info("Input Package length - width - height: {}", length_Width_Height);
    	return this;
    }    

    /**
     * This temporary function fills package info fields such as weight and dimensions with fixed values
     */
    public ShopeeSyncPage inputPackageWeightAndDimensions() {
    	inputPackageWeight("100");
    	inputPackageDimensions(List.of("10", "20", "35"));
    	return this;
    }
    
    
    /**
     * This temporary function fills the price of variations with a fixed value of 2000000.
     * Please create a new function if this one doesn't meet your needs.
     */
    public ShopeeSyncPage inputPriceForVariations() {
    	final String price = "2000000"; 
    	var variationPriceElementCount = commonAction.getElements(elements.loc_txtPriceOfVariations).size();
    	
    	for (int i=0; i< variationPriceElementCount; i++) {
    		commonAction.inputText(elements.loc_txtPriceOfVariations, i, price);
    		logger.info("Input price of variation indexed '{}' : {}", i, price);
    	}
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
