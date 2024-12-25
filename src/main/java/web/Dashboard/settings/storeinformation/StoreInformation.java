package web.Dashboard.settings.storeinformation;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.DOMAIN_BIZ;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.enums.Domain;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;

public class StoreInformation {

	final static Logger logger = LogManager.getLogger(StoreInformation.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	StoreInfoPageElement elements;
	
	Domain domain;

	public StoreInformation (WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new StoreInfoPageElement();
	}
	public StoreInformation (WebDriver driver, Domain domain) {
		this(driver);
		this.domain = domain;
	}

	/**
	 * A temporary function that helps get rid of the annoying try catch block when reading text from property file
	 * @param propertyKey
	 */
	public String translateText(String propertyKey) {
		String translatedText = null;
		try {
			translatedText = PropertiesUtil.getPropertiesValueByDBLang(propertyKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translatedText;
	}	
	
	public StoreInformation navigate() {
		clickStoreInformationTab();
		homePage.waitTillSpinnerDisappear1();

		//Sometimes the element is not present even after the loading icon has disappeared. The code below fixes this intermittent issue
		for (int i=0; i<30; i++) {
			if (!commonAction.getElements(elements.loc_btnRegisteredLogo).isEmpty()) break;
			UICommonAction.sleepInMiliSecond(500);
		}
		return this;
	}
	
	StoreInformation navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}	
	
	//Will be removed
	public StoreInformation navigateToStoreInfoTabByURL() {
		navigateByURL(DOMAIN + "/setting?tabId=5");
		return this;
	}	
	
	public StoreInformation navigateByURL() {
		if (domain.equals(Domain.VN)) {
			navigateByURL(DOMAIN + "/setting/store-information");
		} else {
			navigateByURL(DOMAIN_BIZ + "/setting/store-information");
		}
		
    	UICommonAction.sleepInMiliSecond(500, "Wait a little after navigation");
		return this;
	}		
	
	public StoreInformation clickStoreInformationTab() {
		commonAction.click(elements.loc_tabStoreInfo);
		logger.info("Clicked Store Information tab.");
		return this;
	}

	//Currently returns timezone only. Will update this function so that location is returned as well 
	public String getTimezone() {
		String timezone = commonAction.getText(elements.loc_ddlTimezone);
		logger.info("Retrieved timezone: {}", timezone);
		return timezone;
	}
	
	public StoreInformation inputShopName(String name) {
		commonAction.inputText(elements.loc_txtShopName, name);
		logger.info("Input Shop Name: {}", name);
		return this;
	}
	public String getShopName() {
		String name = commonAction.getAttribute(elements.loc_txtShopName, "value");
		logger.info("Retrieved shop name: {}", name);
		return name;
	}

	public StoreInformation inputAppName(String name) {
		if (commonAction.isElementVisiblyDisabled(elements.loc_txtAppNameAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_txtAppName));
			return this;
		}
		commonAction.inputText(elements.loc_txtAppName, name);
		logger.info("Input App Name: {}", name);
		return this;
	}
	public String getAppName() {
		String name = commonAction.getAttribute(elements.loc_txtAppName, "value");
		logger.info("Retrieved app name: {}", name);
		return name;
	}    

	public StoreInformation inputHotline(String phone) {
		commonAction.inputText(elements.loc_txtHotline, phone);
		logger.info("Input Hotline: {}", phone);
		return this;
	}    
	public String getHoline() {
		String value = commonAction.getAttribute(elements.loc_txtHotline, "value");
		logger.info("Retrieved Hotline: {}", value);
		return value;
	}      

	public StoreInformation inputEmail(String email) {
		commonAction.inputText(elements.loc_txtEmail, email);
		logger.info("Input Email: {}", email);
		return this;
	}
	public String getEmail() {
		String value = commonAction.getAttribute(elements.loc_txtEmail, "value");
		logger.info("Retrieved Email: {}", value);
		return value;
	}     

	public StoreInformation inputStoreAdress(String address) {
		commonAction.inputText(elements.loc_txtAddress, address);
		logger.info("Input Store Address: {}", address);
		return this;
	}

	public StoreInformation inputFacebookLink(String link) {
		commonAction.inputText(elements.loc_txtFacebook, link);
		logger.info("Input Facebook Link: {}", link);
		return this;
	}
	public StoreInformation inputInstagramLink(String link) {
		commonAction.inputText(elements.loc_txtInstagram, link);
		logger.info("Input Instagram Link: {}", link);
		return this;
	}
	public StoreInformation inputYoutubeLink(String link) {
		commonAction.inputText(elements.loc_txtYoutube, link);
		logger.info("Input Youtube Link: {}", link);
		return this;
	}

	public StoreInformation inputSEOTitle(String seoTitle) {
		if (commonAction.isElementVisiblyDisabled(elements.loc_txtSEOTitleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_txtSEOTitle));
			return this;
		}
		commonAction.inputText(elements.loc_txtSEOTitle, seoTitle);
		logger.info("Input SEO Title: {}", seoTitle);
		return this;
	}  
	public String getSEOTitle() {
		String title = commonAction.getAttribute(elements.loc_txtSEOTitle, "value");
		logger.info("Retrieved SEO Title: {}", title);
		return title;
	}     

	public boolean getNoticeLogoToggleStatus() {
		String status = commonAction.getAttribute(elements.loc_btnNoticeLogo, "value");
		logger.info("Retrieved status of Notice Logo Toggle: {}", status);
		return Boolean.parseBoolean(status);
	}      
	public boolean getRegisteredLogoToggleStatus() {
		String status = commonAction.getAttribute(elements.loc_btnRegisteredLogo, "value");
		logger.info("Retrieved status of Registered Logo Toggle: {}", status);
		return Boolean.parseBoolean(status);
	}      

	public StoreInformation clickNoticeLogoToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnNoticeLogoAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnNoticeLogoSibling));
			return this;
		}
		commonAction.click(elements.loc_btnNoticeLogoSibling);
		logger.info("Clicked on Notice Logo toggle button.");
		return this;
	}   

	public StoreInformation clickRegisteredLogoToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnRegisteredLogoAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnRegisteredLogo));
			return this;
		}
		commonAction.click(elements.loc_btnNoticeLogoSibling);
		logger.info("Clicked on Registered Logo toggle button.");
		return this;
	}    

	public StoreInformation clickSaveBtn() {
		commonAction.click(elements.loc_btnSave);
		logger.info("Clicked on Save button.");
		return this;
	}       

	/*Verify permission for certain feature*/
	public void verifyPermissionToSetStoreName(String permission) {
		navigate();
		if (permission.contentEquals("A")) {
			inputShopName("Test Permission");
			Assert.assertEquals(getShopName(), "Test Permission"); 
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
	}
	public void verifyPermissionToSetAppName(String permission) {
		navigate();
		if (permission.contentEquals("A")) {
			inputAppName("Test Permission");
			Assert.assertEquals(getAppName(), "Test Permission"); 
		} else if (permission.contentEquals("D")) {
			inputAppName("Test Permission");
			Assert.assertNotEquals(getAppName(), "Test Permission");
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
	}
	public void verifyPermissionToSetHotlineAndEmail(String permission) {
		navigate();
		if (permission.contentEquals("A")) {
			inputHotline("0123000100");
			inputEmail("test@gmail.com");
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
	}
	public void verifyPermissionToSetStoreAddress(String permission) {
		navigate();
		if (permission.contentEquals("A")) {
			inputStoreAdress("100 Wall Street");
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
	}
	public void verifyPermissionToSetSocialMedia(String permission) {
		navigate();
		if (permission.contentEquals("A")) {
			inputFacebookLink("https://www.facebook.com/Shopping-Heaven-107830291950514/");
			inputInstagramLink("https://www.instagram.com/samsung_vietnam/");
			inputYoutubeLink("https://www.youtube.com/watch?v=NqDVlK4rohc");
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
	}
	public void verifyPermissionToSetSEO(String permission) {
		navigate();
		if (permission.contentEquals("A")) {
			inputSEOTitle("Test Permission");
			Assert.assertEquals(getSEOTitle(), "Test Permission"); 
		} else if (permission.contentEquals("D")) {
			inputSEOTitle("Test Permission");
			Assert.assertNotEquals(getSEOTitle(), "Test Permission"); 
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
	}
	public void verifyPermissionToEnableTradeLogo(String permission) {
		navigate();
		clickNoticeLogoToggle();
		clickRegisteredLogoToggle();
		if (permission.contentEquals("A")) {
			Assert.assertTrue(getNoticeLogoToggleStatus());
			Assert.assertTrue(getRegisteredLogoToggleStatus());
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(getNoticeLogoToggleStatus());
			Assert.assertFalse(getRegisteredLogoToggleStatus());
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
	}

	void checkPermissionToViewStoreInfo(AllPermissions staffPermission) {
		navigateToStoreInfoTabByURL();

		if (staffPermission.getSetting().getStoreInformation().isViewStoreInformation()) {
			String retrievedAppName = getAppName();
			Assert.assertFalse(retrievedAppName.isEmpty(), retrievedAppName);
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToViewStoreInfo");
	}
	void checkPermissionToUpdateStoreInfo(AllPermissions staffPermission) {
		
		if (!staffPermission.getSetting().getStoreInformation().isViewStoreInformation()) {
			logger.info("Permission to update store info is not granted. Skipping this check");
			return;
		}
		
		navigateToStoreInfoTabByURL(); 
		
		clickSaveBtn();
		
		if (staffPermission.getSetting().getStoreInformation().isUpdateInformation()) {
			Assert.assertEquals(homePage.getToastMessage(), translateText("settings.storeInfo.saveSuccess"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToUpdateStoreInfo");
	}
	public void checkStoreInfoSettingPermission(AllPermissions staffPermission) {
		checkPermissionToViewStoreInfo(staffPermission);
		checkPermissionToUpdateStoreInfo(staffPermission);
	}	
	
}
