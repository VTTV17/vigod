package web.Dashboard.marketing.landingpage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LandingPageElement {
	WebDriver driver;

	public LandingPageElement(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".planding-header button")
	WebElement CREATE_PAGE_LANDING_BTN;

	@FindBy(css = ".modal-title")
	WebElement PERMISSION_MODAL;
	@FindBy(css = ".modal-title button")
	WebElement CLOSE_MODAL_BTN;
	By loc_lst_lblName = By.cssSelector(".gs-table-body-items .name");
	By loc_icnEdit = By.xpath("(//i[contains(@class,'gs-action-button')])[2]");
	By loc_ddlStatus = By.cssSelector(".btn-secondary");
	By loc_ddvStatus = By.cssSelector(".top-search button[role='menuitem']");
	By loc_btnCreateLandingPage = By.cssSelector(".planding-header button");
	By loc_icnShowMoreAction = By.xpath("(//i[contains(@class,'gs-action-button')])[3]");
	/*
	0: Publish
	1: Unpublish
	2: Clone
	3: Delete
	 */
	By loc_ddvAction = By.xpath("(//section[@class='gs-table-body']//div[@role='menu'])[1]//button");
}
