package pages.gomua.myprofile;

import org.openqa.selenium.By;

public class MyProfileGoMuaElement {

	By loc_lnkEditProfile = By.xpath("//a[@beetranslate='beecow.user.editProfile.edProfile']");
	By loc_lblDisplayName = By.xpath("//span[@class='icon-about']/following-sibling::span[1]");
	By loc_btnPhone = By.xpath("//span[@class='icon-call-solid']/following-sibling::span[1]");
	By loc_lblGender = By.xpath("//span[@class='icon-gender']/following-sibling::span[1]/span");
	By loc_lblEmail = By.xpath("//span[@class='icon-email-solid']/following-sibling::span[1]");
	By loc_lblBirthday = By.xpath("//span[@class='icon-birthday']/following-sibling::span[1]");

}
