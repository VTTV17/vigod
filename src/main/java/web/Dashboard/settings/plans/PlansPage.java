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
	
	public List<String> getDurationOptions() {
		commons.getElement(elements.loc_tabDurationOptions); //This implicitly means the tabs are present ready for further actions
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
	public PlansPage subscribeToPackage(String packageName) {
		commons.click(By.xpath(elements.loc_btnSubscribePackageByName.formatted(packageName)));
		logger.info("Subscribed to: {}", packageName);
		return this;
	}	
	public PlansPage subscribeToPackage(NewPackage newPackage) {
		return subscribeToPackage(NewPackage.getValue(newPackage));
	}
	
}
