package pages.gomua.myprofile;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MyProfileGoMuaElement {
	WebDriver driver;

	public MyProfileGoMuaElement(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//a[@beetranslate='beecow.user.editProfile.edProfile']")
	WebElement EDIT_PROFILE_LINK;
	@FindBy(xpath = "//span[@class='icon-about']/following-sibling::span[1]")
	WebElement PROFILE_DISPLAY_NAME;

	@FindBy(xpath = "//span[@class='icon-call-solid']/following-sibling::span[1]")
	List<WebElement> PROFILE_PHONE_NUMBER;

	@FindBy(xpath = "//span[@class='icon-gender']/following-sibling::span[1]/span")
	List<WebElement> PROFILE_GENDER;

	@FindBy(xpath = "//span[@class='icon-email-solid']/following-sibling::span[1]")
	List<WebElement> PROFILE_EMAIL;

	@FindBy(xpath = "//span[@class='icon-birthday']/following-sibling::span[1]")
	List<WebElement> PROFILE_BIRTHDAY;
}
