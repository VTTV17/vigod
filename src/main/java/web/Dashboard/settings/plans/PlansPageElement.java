package web.Dashboard.settings.plans;

import org.openqa.selenium.By;

public class PlansPageElement {
	
	By loc_btnFreeTrial = By.cssSelector(".package-plans-layout-header .right button.button-v2");
	By loc_tabDurationOptions = By.cssSelector(".package-plans-layout-header .options div.option");
	String loc_tabDurationByName = "//div[@class='package-plans-layout-header']/*[@class='options']/*[starts-with(@class,'option') and (contains(.,'noys Year') or contains(.,'noys năm'))]";
	By loc_lblPackageTitle = By.xpath("//div[@class='package-plans-package-list']//div[starts-with(@class,'items')]//*[@class='title']");
	By loc_lblPackageDescription = By.xpath("//div[@class='package-plans-package-list']//div[starts-with(@class,'items')]//*[@class='desc']");
	By loc_lblPackageTotal = By.xpath("//div[@class='package-plans-package-list']//div[starts-with(@class,'items')]//*[@class='total']");
	By loc_lblPackagePerMonth = By.xpath("//div[@class='package-plans-package-list']//div[starts-with(@class,'items')]//*[@class='per-month']");
	String loc_blkPackageByName = "//div[@class='package-plans-package-list']//div[starts-with(@class,'items')]//*[@class='title' and .='%s']";
	String loc_btnSubscribePackageByName = loc_blkPackageByName + "//following-sibling::button[starts-with(@class,'button-v2')]";		
	By loc_btnContinue = By.cssSelector(".modal-body button:nth-child(2)");
	
	String loc_lblBenefitTitle = "//*[starts-with(@class,'package-plans-benefit')]//div[starts-with(@class,'item')]//div[@class='title']";
	String loc_lblBenefitContent = "((//*[starts-with(@class,'package-plans-benefit')]//div[starts-with(@class,'item')]//div[@class='title'])[%s]/following-sibling::div[@class='check'])[%s]";
	String loc_icnBenefitChecked = loc_lblBenefitContent + "//img";
}
