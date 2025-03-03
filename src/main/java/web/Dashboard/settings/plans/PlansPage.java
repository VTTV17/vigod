package web.Dashboard.settings.plans;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.enums.newpackage.NewPackage;
import utilities.model.dashboard.setting.packageinfo.PackageInfo;

public class PlansPage {
	final static Logger logger = LogManager.getLogger(PlansPage.class);
	
	WebDriver driver;
	UICommonAction commons;
	PlansPageElement elements;

	public PlansPage(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
		elements = new PlansPageElement();
	}

    public boolean isFreeTrialBtnDisplayed() {
    	commons.getElement(elements.loc_tabDurationOptions); //This implicitly means the tabs are present ready for further actions
		boolean isDisplayed = !commons.getElements(elements.loc_btnFreeTrial).isEmpty();
		logger.info("Is Free Trial button displayed: {}", isDisplayed);
		return isDisplayed;
    }	
    public PlansPage clickFreeTrialBtn() {
    	commons.click(elements.loc_btnFreeTrial);
    	logger.info("Clicked Free Trial button");
    	return this;
    }	
	public List<String> getPackagePeriodOptions() {
		commons.getElement(elements.loc_lblPackagePerMonth); //This implicitly means the tabs are displayed in correct language ready for further actions
		commons.sleepInMiliSecond(1000, "Wait for the tab to be rendered in correct language"); //Don't know why at times the tabs are displayed in mix languages
		List<String> durationOptions = new ArrayList<String>();
		for(int i=0; i<commons.getElements(elements.loc_tabDurationOptions).size(); i++) {
			durationOptions.add(commons.getText(elements.loc_tabDurationOptions, i));
		}
		logger.info("Retrieved Year options available: {}", durationOptions);
		return durationOptions;
	}
	public PlansPage selectDuration(int numberOfYears) {
		commons.click(By.xpath(elements.loc_tabDurationByName.replaceAll("noys", String.valueOf(numberOfYears))));
		logger.info("Selected Years tab: {}", numberOfYears);
		return this;
	}
	public List<PackageInfo> getPackageInfo() {
		commons.getElement(elements.loc_lblPackageTitle); //This implicitly means the packages are present ready for further actions
		boolean isDescriptionShown = !commons.getElements(elements.loc_lblPackageDescription).isEmpty();
		
		List<PackageInfo> packageOptions = new ArrayList<PackageInfo>();
		
		for(int i=0; i<commons.getElements(elements.loc_lblPackageTitle).size(); i++) {
			PackageInfo info = new PackageInfo();
			info.setName(commons.getText(elements.loc_lblPackageTitle, i));
			if (isDescriptionShown) info.setDescription(commons.getText(elements.loc_lblPackageDescription, i)); //Package description is only shown for foreign shops
			info.setTotalPrice(commons.getText(elements.loc_lblPackageTotal, i));
			info.setPricePerMonth(commons.getText(elements.loc_lblPackagePerMonth, i));
			packageOptions.add(info);
		}
		logger.info("Retrieved Package Info: {}", packageOptions);
		return packageOptions;
	}
	
	public List<List<String>> getPackageBenefits(String country) {
		commons.getElement(elements.loc_lblPackageTitle); //This implicitly means the packages are present ready for further actions
		
		int packageCount = country.contentEquals("Vietnam") ? NewPackage.forVNStore().size() : NewPackage.forForeignStore().size();
		
		List<List<String>> benefits = new ArrayList<List<String>>();
		
		for (int i=1; i<= commons.getElements(By.xpath(elements.loc_lblBenefitTitle)).size(); i++) {
			
			List<String> tray = new ArrayList<>();
			
			tray.add(commons.getText(By.xpath(elements.loc_lblBenefitTitle), i-1)); //Benefit title
			
			//Benefit content
			for (int j=1; j<=packageCount; j++) {
				String tempText = commons.getText(By.xpath(elements.loc_lblBenefitContent.formatted(i, j)));
				if (tempText.isEmpty()) {
					tempText = commons.getElements(By.xpath(elements.loc_icnBenefitChecked.formatted(i, j))).isEmpty() ? "false" : "true";
				}
				
				tray.add(tempText);
			}
			benefits.add(tray);
		}
		
		logger.info("Retrieved Package Benefits: {}", benefits);
		return benefits;
	}
	
	public List<List<String>> getPackageBenefitComparision(String country) {
		String loc_Title = "//*[starts-with(@class,'line')]/*[@class='title']";
		String loc_NewContent = "((//*[starts-with(@class,'line')]/*[@class='title'])[%s]/following-sibling::div)[%s]";
		
		commons.getElement(By.xpath(loc_Title)); //This implicitly means the packages are present ready for further actions
		
		int packageCount = 2;
		
		List<List<String>> benefits = new ArrayList<List<String>>();
		
		for (int i=1; i<= commons.getElements(By.xpath(loc_Title)).size(); i++) {
			
			List<String> tray = new ArrayList<>();
			
			tray.add(commons.getText(By.xpath(loc_Title), i-1)); //Benefit title
			
			//Benefit content
			for (int j=1; j<=packageCount; j++) {
				String tempText = commons.getText(By.xpath(loc_NewContent.formatted(i, j)));
				if (tempText.isEmpty()) {
					tempText = commons.getElements(By.xpath(loc_NewContent.formatted(i, j))).isEmpty() ? "false" : "true";
				}
				
				tray.add(tempText);
			}
			benefits.add(tray);
		}
		
		logger.info("Retrieved Benefit Comparison: {}", benefits);
		return benefits;
	}
	
	public PlansPage subscribeToPackage(String packageName) {
		commons.click(By.xpath(elements.loc_btnSubscribePackageByName.formatted(packageName)));
		logger.info("Subscribed to: {}", packageName);
		return this;
	}	
	public PlansPage subscribeToPackage(NewPackage newPackage) {
		return subscribeToPackage(NewPackage.getValue(newPackage));
	}
	public PlansPage clickContinueOnFeatureComparisionDialog() {
	  commons.click(elements.loc_btnContinue);
	  logger.info("Click Continue on Feature Comparision dialog");
	  return this;
	}
	public boolean isComparisionDialogDisplayed() {
		boolean isDisplayed = !commons.getElements(elements.loc_btnContinue).isEmpty();
		logger.info("Is Comparision dialog displayed: {}", isDisplayed);
		return isDisplayed;
	}
}
