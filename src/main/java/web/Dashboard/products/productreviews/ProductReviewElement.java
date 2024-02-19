package web.Dashboard.products.productreviews;

import org.openqa.selenium.By;

public class ProductReviewElement {
	By loc_lblPageTitle = By.cssSelector(".gs-page-title");
	By loc_btnEnableReviewToggle = By.cssSelector(".gss-content-header .uik-checkbox__wrapper");
	By loc_btnToggleStatus = By.xpath("./input");
	By loc_tltEnableReviewToggle = By.cssSelector(".tippy-tooltip-content");
	By loc_txtSearchProduct = By.cssSelector(".d-desktop-flex .gs-search-box__wrapper input");
	By loc_btnFilter = By.cssSelector(".n-filter-container .uik-select__wrapper");
	By loc_lblTableHeader = By.cssSelector(".product-review-list-widget .d-mobile-none .gs-table-header");
	By loc_lblEnableProductReview = By.xpath("./ancestor::div[@class=' gs-content-header-right-el']");
	By loc_tmpRecords = By.cssSelector(".d-desktop-block .gs-table-body .shortest-row");
	By loc_tblProductNameColumn = By.xpath("//div[contains(@class,'product-name')]");
	By loc_tblProductRatingColumn = By.xpath("//div[contains(@class,'product-rating')]");
	By loc_tblReviewTitleColumn = By.xpath("//div[contains(@class,'product-review')]/span[@class='title']");
	By loc_tblReviewDescriptionColumn = By.xpath("//div[contains(@class,'product-review')]/span[contains(@class,'description')]");
	By loc_tblCustomerNameColumn = By.xpath("//div[contains(@class,'customer-name')]");
	By loc_tblCreatedDateColumn = By.xpath("//div[contains(@class,'created-date')]");
	By loc_tblStatus = By.xpath("//label[contains(@class,'lastest-button')]/input");
	By loc_btnNavigation = By.xpath("//*[contains(@class,'first-button')]");
	By loc_btnEnableSpecificReviewToggle = By.xpath("//*[contains(@class,'lastest-button')]");
	
	String loc_ddlSortCondition = "//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']";
}
