package pages.gomua.headergomua;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HeaderGoMuaElement {
	WebDriver driver;

	public HeaderGoMuaElement(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//a[@class = 'login']")
	WebElement LOGIN_BTN;
	@FindBy(xpath = "//div[@class='name']/span[1]")
	WebElement DISPLAY_NAME;
	@FindBy(xpath = "//ul[@class='dropdown-menu']//span[@class='name']")
	WebElement MY_PROFILE;
	@FindBy(xpath = "//ul[@class='dropdown-menu']//a[@beetranslate='beecow.action.changelanguage']")
	WebElement CHANGE_LANGUAGE;
	@FindBy(xpath = "//div[contains(@class,'language vi')]//span[@class='lang-name']")
	WebElement VIETNAMESE;
	@FindBy(xpath = "//div[contains(@class,'language en')]//span[@class='lang-name']")
	WebElement ENGLISH;
	@FindBy(xpath = "//button[@beetranslate='beecow.common.save']")
	WebElement POPUP_SAVE_BTN;
	
	@FindBy(css = "span[beetranslate='beecow.link.createShop']")
	WebElement CREATE_SHOP_LINKTEXT;
	
	@FindBy(css = "button[beetranslate='beecow.signup.create']")
	WebElement CREATE_ACCOUNT_BTN;

}
