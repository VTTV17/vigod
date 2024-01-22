package web.Dashboard.marketing.landingpage;

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
}
