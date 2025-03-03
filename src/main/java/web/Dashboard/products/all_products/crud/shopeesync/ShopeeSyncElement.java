package web.Dashboard.products.all_products.crud.shopeesync;

import org.openqa.selenium.By;

public class ShopeeSyncElement {
	
	/**
	 * When clicked, it shows the product's name and description
	 */
    By loc_icnPlus = By.cssSelector("span.btn-more-info svg.collapse-expand");
    By loc_txtDescription = By.id("productDescription");
    
    By loc_txtPriceOfVariations = By.xpath("//table[@class='shopee-edit-product-variants-table']//td[starts-with(@class,'price-product')]//input[starts-with(@id,'productPrice')]");
    
    
    By loc_ddlCategory = By.cssSelector("button.btn.btn-secondary");
    /**
     * Gets the first drop-down element of a category
     * @param categoryLevel Eg. 1,2,3
     * @return a By object representing the element
     */
    By loc_ddvFirstOptionOfCategoryLevel(int categoryLevel) {
    	return By.xpath("//div[@class='category-menu']/div[@tabindex='%s']/div[1]/button".formatted(categoryLevel));
    }
    By loc_ddlBrand = By.id("react-select-2-input");
    By loc_ddvNoBrand = By.id("react-select-2-option-0");
    
    By loc_txtPackageWeight = By.xpath("//label[@for='productWeight']/following-sibling::div//input");
    By loc_txtPackageLength = By.xpath("//label[@for='productLength']/following-sibling::div//input");
    By loc_txtPackageWidth = By.xpath("//label[@for='productWidth']/following-sibling::div//input");
    By loc_txtPackageHeight = By.xpath("//label[@for='productHeight']/following-sibling::div//input");
    
    By loc_chkLogicticsOption = By.xpath("//*[@class='logistics-container']//input[not(@disabled)]//following-sibling::div");
    By loc_btnCreate = By.cssSelector(".btn-save");
    
    
}
