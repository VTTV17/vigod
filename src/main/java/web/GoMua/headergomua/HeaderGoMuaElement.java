package web.GoMua.headergomua;

import org.openqa.selenium.By;

public class HeaderGoMuaElement {

	By loc_btnLogin = By.xpath("//a[@class = 'login']");
	By loc_btnSingup = By.cssSelector("[beetranslate='beecow.action.signup'");
	By loc_lblDisplayName = By.xpath("//div[@class='name']/span[1]");
	By loc_lblMyProfile = By.xpath("//ul[@class='dropdown-menu']//span[@class='name']");
	By loc_lblChangeLanguage = By.xpath("//ul[@class='dropdown-menu']//a[@beetranslate='beecow.action.changelanguage']");
	By loc_lblVietnamese = By.xpath("//div[contains(@class,'language vi')]//span[@class='lang-name']");
	By loc_lblEnglish = By.xpath("//div[contains(@class,'language en')]//span[@class='lang-name']");
	By loc_btnSavePopup = By.xpath("//button[@beetranslate='beecow.common.save']");
	By loc_lnkCreateShop = By.cssSelector("span[beetranslate='beecow.link.createShop']");
	By loc_btnCreateAccount = By.cssSelector("button[beetranslate='beecow.signup.create']");
	By loc_lblChangePassword = By.cssSelector(".profile-setting [beetranslate='beecow.action.changepwd']");

}
