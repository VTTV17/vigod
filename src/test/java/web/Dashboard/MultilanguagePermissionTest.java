package web.Dashboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.StoreLanguageAPI;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.setting.languages.AdditionalLanguages;
import utilities.model.dashboard.setting.languages.CreatedLanguage;
import utilities.model.dashboard.setting.languages.DefaultLanguage;
import utilities.model.dashboard.setting.languages.LanguageCatalog;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.storelanguages.StoreLanguages;

/**
 * <p>Ticket: https://mediastep.atlassian.net/browse/BH-25480</p>
 * <p>Preconditions: Update later</p>
 */

public class MultilanguagePermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	StoreLanguageAPI storeLanguageAPI;
	
	LanguageCatalog[] languageCatalog;
	CreatedLanguage originalPublishedLang, originalUnpublishedLang, originalDeletedLanguage;
	DefaultLanguage defaultLang;
	
	LoginPage loginPage;
	HomePage homePage;
	StoreLanguages languagePage;
	
	int permissionGroupId;
	boolean isMultiLangPurchased;
	int latestLanguageId;
	
	@BeforeClass
	void precondition() {
		ownerCredentials = new Login().setLoginInformation("+84", "automation0-shop74053@mailnesia.com", "fortesting!1").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff74053@mailnesia.com", "fortesting!1").getLoginInformation();
//		ownerCredentials = new Login().setLoginInformation("+84", "automation0-shop36938@mailnesia.com", "fortesting!1").getLoginInformation();
//		staffCredentials = new Login().setLoginInformation("+84", "staff36938@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		storeLanguageAPI = new StoreLanguageAPI(ownerCredentials);
		
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    	
    	isMultiLangPurchased = storeLanguageAPI.getPackageId() >0;
    	
    	latestLanguageId = Collections.max(getExistingLanguageIds());
    	
    	languageCatalog = storeLanguageAPI.getLanguageCatalog();
    	
    	defaultLang = storeLanguageAPI.getDefaultLanguage();
    	
    	if (isMultiLangPurchased) {
        	originalPublishedLang = addLanguage();
        	originalUnpublishedLang = addLanguage();
        	storeLanguageAPI.publishLanguage(originalUnpublishedLang.getId());
        	originalDeletedLanguage = addLanguage();
    	}
    	
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		languagePage = new StoreLanguages(driver);
		
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}	

	@AfterClass
	void rollback() {
		permissionAPI.deleteGroupPermission(permissionGroupId);
		storeLanguageAPI.selectDefaultLanguage(defaultLang.getId());
		removeLanguages(latestLanguageId);
		driver.quit();
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
    }	

    AdditionalLanguages[] getExistingLanguages() {
    	return storeLanguageAPI.getAdditionalLanguages();
    }
    
    List<Integer> getExistingLanguageIds() {
    	return Arrays.asList(getExistingLanguages()).stream().map(e -> e.getId()).collect(Collectors.toList());
    }

    LanguageCatalog getUniqueLanguage() {
    	List<LanguageCatalog> catalogList = Arrays.asList(languageCatalog);
    	List<AdditionalLanguages> additionalLanguageList = Arrays.asList(getExistingLanguages());
    	return catalogList.stream().filter(e -> !additionalLanguageList.stream().map(e1 -> e1.getLangName().toString()).collect(Collectors.toList()).contains(e.getDisplayValue())).findFirst().orElse(null);
    }
    
    CreatedLanguage addLanguage() {
    	LanguageCatalog uniqueLanguage = getUniqueLanguage();
    	return storeLanguageAPI.addLanguageThenReturnClass(uniqueLanguage.getDisplayValue(), uniqueLanguage.getLangCode(), uniqueLanguage.getLangIcon(), "vi");
    }
    
    void removeLanguages(int cutoffId) {
    	getExistingLanguageIds().stream().filter(id-> id > cutoffId).forEach(id-> storeLanguageAPI.removeLanguage(id));
    }
    
	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_storeLanguage(permissionBinary);
		return model;
	}
	
	@Test(dataProvider = "languagePermission", dataProviderClass = PermissionDataProvider.class)
	public void CheckLanguagePermission(String permissionBinary) {
		int latestLanguageId = Collections.max(getExistingLanguageIds());
		
		String staffOldPermissionToken = new Login().getInfo(staffCredentials).getStaffPermissionToken();
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));		
		
		String staffNewPermissionToken = new CheckPermission(driver).waitUntilPermissionUpdated(staffOldPermissionToken, staffCredentials);
		
		AllPermissions allPermissionDTO = new AllPermissions(staffNewPermissionToken);
		
		System.out.println(allPermissionDTO.getSetting().getStoreLanguage());
		
		commonAction.refreshPage();
		commonAction.sleepInMiliSecond(2000, "OMG");
		
		String publishedLang = "";
		String unpublishedLang = "";
		String addedLang = "";
		String deletedLang = "";
		if (isMultiLangPurchased) {
			publishedLang = (allPermissionDTO.getSetting().getStoreLanguage().isPublishLanguage()) ? addLanguage().getLangName() : originalPublishedLang.getLangName();
			if (allPermissionDTO.getSetting().getStoreLanguage().isUnpublishLanguage()) {
				CreatedLanguage te = addLanguage();
				unpublishedLang = te.getLangName();
				storeLanguageAPI.publishLanguage(te.getId());
			} else {
				unpublishedLang = originalUnpublishedLang.getLangName();
			}
			deletedLang = (allPermissionDTO.getSetting().getStoreLanguage().isRemoveLanguage()) ? addLanguage().getLangName() : originalDeletedLanguage.getLangName();
		}

		if (allPermissionDTO.getSetting().getStoreLanguage().isChangeDefaultLanguage()) storeLanguageAPI.selectDefaultLanguage(defaultLang.getId());
		
		if (allPermissionDTO.getSetting().getStoreLanguage().isAddLanguage()) addedLang = getUniqueLanguage().getDisplayValue();
		
		languagePage.checkStoreLanguagePermission(allPermissionDTO, isMultiLangPurchased, publishedLang, unpublishedLang, addedLang, deletedLang);
		
		removeLanguages(latestLanguageId);
	}		
}
