package web.Dashboard.onlineshop.themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import api.Seller.sale_channel.onlineshop.APIThemes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.dashboard.onlineshop.ThemeInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class Themes {

	final static Logger logger = LogManager.getLogger(Themes.class);
	
    WebDriver driver;
    UICommonAction commonAction;
	AllPermissions allPermissions;
    ThemesLibrary themesLibrary;
	AssertCustomize assertCustomize;
	ThemeDetail themeDetail;
	LoginInformation loginInformation;
	ThemeInfo themeActiveInfo;
    public Themes(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
		themesLibrary = new ThemesLibrary(driver);
		assertCustomize = new AssertCustomize(driver);
		themeDetail = new ThemeDetail(driver);


	}
	public Themes getLoginInfo( LoginInformation loginInformation){
		this.loginInformation = loginInformation;
		themeActiveInfo = new APIThemes(loginInformation).getActiveThemeId();
		return this;
	}
    By loc_btnVisitThemeLibrary = By.cssSelector(".btn-create.ml-auto");
    By loc_dlgModal = By.cssSelector(".modal-content");
	By loc_btnCustomize = By.cssSelector(".theme-management-section__header .gs-button__green");
	By loc_blkMyThemes_icnThreeDot = By.cssSelector(".theme-management-section__header__actions");
	By loc_blkMyThemes_btnAction = By.cssSelector(".uik-dropdown-item__wrapper");
	By loc_blkMyThemes_lstThemeName = By.cssSelector("//i[contains(@class,'theme-management-section__header__actions')]//ancestor::div[contains(@class,'align-items-center')]");

    public ThemesLibrary clickVisitThemeStore() {
    	commonAction.click(loc_btnVisitThemeLibrary);
    	logger.info("Clicked on 'Visit Theme Store' button.");
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	return new ThemesLibrary(driver);
    }

    public boolean isModalContentDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return commonAction.getElements(loc_dlgModal).size() >0;
    } 
    public void navigateByUrl(){
		String url = Links.DOMAIN + Links.THEMES_PATH;
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
		commonAction.sleepInMiliSecond(500);
	}
    public void verifyPermissionToCustomizeAppearance(String permission) {
    	String originalWindowHandle = commonAction.getCurrentWindowHandle();
    	ArrayList<String> list = commonAction.getAllWindowHandles();
    	int originalSize = list.size();
		clickVisitThemeStore().clickEditTheme();
		ArrayList<String> list1 = commonAction.getAllWindowHandles();
		int laterSize = list1.size();
		new HomePage(driver).waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(originalSize < laterSize);
    		for(String winHandle : list1){
    			if (!winHandle.contentEquals(originalWindowHandle)) {
    				commonAction.switchToWindow(winHandle);
    			}
    		}
    		commonAction.closeTab();
    		commonAction.switchToWindow(originalWindowHandle);
    		commonAction.navigateBack();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertTrue(originalSize == laterSize);
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
	public void clickOnCustomizeBtn(){
		commonAction.click(loc_btnCustomize);
		logger.info("Click on Customize button.");
	}
	public void clickThreeDotIconOnMyThemes(){
		if (commonAction.getElements(loc_blkMyThemes_icnThreeDot,1).isEmpty()){
			new APIThemes(loginInformation).addANewTheme();
			logger.info("Call api add new them");
		}
		commonAction.click(loc_blkMyThemes_icnThreeDot);
		logger.info("Click on three dot icon on My themes section.");
	}
	public void clickPublishMyThemes(){
		commonAction.click(loc_blkMyThemes_btnAction,0);
		logger.info("Click Publish button on My themes section");
	}
	public void clickCustomizeMyThemes(){
		commonAction.click(loc_blkMyThemes_btnAction,1);
		logger.info("Click Customize button on My Themes section.");
	}
	public void clickRemoveMyThemes(){
		commonAction.click(loc_blkMyThemes_btnAction,2);
		logger.info("Click Remove on My Themes section");
	}
	public Themes deleteAThemes(){
		clickThreeDotIconOnMyThemes();
		clickRemoveMyThemes();
		new ConfirmationDialog(driver).clickOnRedBtn();
		return this;
	}
	public void switchNewTab(){
		commonAction.sleepInMiliSecond(2000);
		commonAction.switchToWindow(1);
	}
    /*-------------------Staff Permission------------------*/
	public boolean hasViewThemeLibrary(){
		return allPermissions.getOnlineStore().getTheme().isViewThemeLibrary();
	}
	public boolean hasViewThemeDetail(){
		return allPermissions.getOnlineStore().getTheme().isViewThemeDetail();
	}
	public boolean hasEditTheme(){
		return allPermissions.getOnlineStore().getTheme().isEditTheme();
	}
	public boolean hasPushlishTheme(){
		return allPermissions.getOnlineStore().getTheme().isPublishTheme();
	}
	public boolean hasAddNewTheme(){
		return allPermissions.getOnlineStore().getTheme().isAddNewTheme();
	}
	public boolean hasDeleteTheme(){
		return allPermissions.getOnlineStore().getTheme().isDeleteTheme();
	}
	public void checkViewThemeLibrary(){
		navigateByUrl();
		if(hasViewThemeLibrary()){
			clickVisitThemeStore();
			List<WebElement> listTemplate = commonAction.getElements(themesLibrary.loc_lstTemplate);
			assertCustomize.assertTrue(listTemplate.size()>0,"[Failed] Template list on theme library page should be shown");
		}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnVisitThemeLibrary),
				"[Failed] Restricted page should be shown when click on Vist themes store.");
	}
	public void checkViewThemeDetail(){
		navigateByUrl();
		String currentWindow = commonAction.getCurrentWindowHandle();
		clickOnCustomizeBtn();
		switchNewTab();
		if(hasViewThemeDetail()){
			assertCustomize.assertTrue(commonAction.getCurrentURL().contains("/theme/theme-making/"),
					"[Failed] Theme detail page should be shown when click on Customize");
			commonAction.closeTab();
			commonAction.switchToWindow(currentWindow);
			//Check when click Customize on My Themes
			navigateByUrl();
			clickThreeDotIconOnMyThemes();
			clickCustomizeMyThemes();
			switchNewTab();
			assertCustomize.assertTrue(commonAction.getCurrentURL().contains("/theme/theme-making/"),
					"[Failed] Theme detail page should be shown when click on Customize on My Themes");
			commonAction.closeTab();
			commonAction.switchToWindow(currentWindow);

			if(hasViewThemeLibrary()){
				//check permission when click edit on Theme library page
				themesLibrary.navigateByUrl()
						.clickEditTheme().clickEditBtnOnTransferModal();
				switchNewTab();
				assertCustomize.assertTrue(commonAction.getCurrentURL().contains("/theme/theme-making/"),
						"[Failed] Theme detail page should be shown when click on Edit theme.");
				commonAction.closeTab();
				commonAction.switchToWindow(currentWindow);
				//Check permission when click transfer and edit on on Theme library page
				themesLibrary.navigateByUrl()
						.clickEditTheme().clickTransferAndEditOnTransferModal();
				switchNewTab();
				assertCustomize.assertTrue(commonAction.getCurrentURL().contains("/theme/theme-making/"),
						"[Failed] Theme detail page should be shown when click on Edit theme.");
				commonAction.closeTab();
				commonAction.switchToWindow(currentWindow);
			}
		}else{
			assertCustomize.assertTrue(commonAction.getCurrentURL().contains("/restricted"),
					"[Failed] Restricted page should be shown when click on customize button");
			commonAction.closeTab();
			commonAction.switchToWindow(currentWindow);
			//Check when click Customize on My Themes
			navigateByUrl();
			clickThreeDotIconOnMyThemes();
			clickCustomizeMyThemes();
			switchNewTab();
			assertCustomize.assertTrue(commonAction.getCurrentURL().contains("/restricted"),
					"[Failed] Restricted page should be shown when click on customize button on My Themes");
			commonAction.closeTab();
			commonAction.switchToWindow(currentWindow);

			if (hasViewThemeLibrary()){
				//check Restricted page when click edit on Theme library page
				themesLibrary.navigateByUrl().clickEditTheme()
						.clickEditBtnOnTransferModal();
				switchNewTab();
				assertCustomize.assertTrue(commonAction.getCurrentURL().contains("/restricted"),
						"[Failed] Restricted page should be shown when click on Edit theme.");
				commonAction.closeTab();
				commonAction.switchToWindow(currentWindow);
				//check Restricted page when click transfer and edit on Theme library page
				themesLibrary.navigateByUrl().clickEditTheme()
						.clickTransferAndEditOnTransferModal();
				switchNewTab();
				assertCustomize.assertTrue(commonAction.getCurrentURL().contains("/restricted"),
						"[Failed] Restricted page should be shown when click on Transfer and Edit theme.");
				commonAction.closeTab();
				commonAction.switchToWindow(currentWindow);
			}
		}
		logger.info("Verified View theme detail permission.");
	}
	public void editThemesAndVerifyMessage(boolean isAdd){
		switchNewTab();
		new HomePage(driver).waitTillSpinnerDisappear1();
		new HomePage(driver).waitTillLoadingDotsDisappear();
		themeDetail.waitSpinerLoadOnComponenHidden();
		themeDetail.clickOnSave();
		if(isAdd) {
			themeDetail.inputThemeName();
			new ConfirmationDialog(driver).clickGreenBtn();
		}
		String toastMessage = new HomePage(driver).getToastMessage();
		try {
			assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.themes.update.successMessage"),
					"[Failed] Update success message should be shown, but '%s' is shown".formatted(toastMessage));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		commonAction.closeTab();
	}
	public void checkEditTheme(){
		navigateByUrl();
		String currentWindow = commonAction.getCurrentWindowHandle();
		clickOnCustomizeBtn();
		switchNewTab();
		if(hasViewThemeDetail() && hasEditTheme()){
			editThemesAndVerifyMessage(false);
			commonAction.switchToWindow(currentWindow);
			//Check when click Customize on My Themes
			navigateByUrl();
			clickThreeDotIconOnMyThemes();
			clickCustomizeMyThemes();
			switchNewTab();
			editThemesAndVerifyMessage(false);
		}else if(!hasViewThemeDetail()){
			logger.info("Don't have View theme detail, so no need check edit theme.");
			commonAction.closeTab();
		}else {
			// has View theme detail permission, don't have edit permission
			new HomePage(driver).waitTillSpinnerDisappear1();
			new HomePage(driver).waitTillLoadingDotsDisappear();
			themeDetail.clickOnSave();
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(themeDetail.loc_btnSave),
					"[Failed] Restricted popup should be shown when click on Save");
			commonAction.closeTab();
			commonAction.switchToWindow(currentWindow);
			//Check when click Customize on My Themes
			navigateByUrl();
			clickThreeDotIconOnMyThemes();
			clickCustomizeMyThemes();
			switchNewTab();
			new HomePage(driver).waitTillSpinnerDisappear1();
			new HomePage(driver).waitTillLoadingDotsDisappear();
			themeDetail.clickOnSave();
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(themeDetail.loc_btnSave),
					"[Failed] Restricted popup should be shown when click on Save");
			commonAction.closeTab();
		}
		commonAction.switchToWindow(currentWindow);
		logger.info("Verified Edit theme permission.");
	}
	public void publishThemesAndVerifyMessage(boolean isNewTheme){
		switchNewTab();
		new HomePage(driver).waitTillSpinnerDisappear1();
		new HomePage(driver).waitTillLoadingDotsDisappear();
		themeDetail.waitSpinerLoadOnComponenHidden();
		themeDetail.clickOnPublish();
		commonAction.sleepInMiliSecond(500);
		new ConfirmationDialog(driver).clickOKBtn();
		commonAction.sleepInMiliSecond(500);
		if(isNewTheme){
			themeDetail.inputThemeName();
			new ConfirmationDialog(driver).clickGreenBtn();
			new HomePage(driver).waitTillSpinnerDisappear1();
			new HomePage(driver).waitTillLoadingDotsDisappear();
		}
		String toastMessage = new HomePage(driver).getToastMessage();
		if((hasEditTheme()&&!isNewTheme)||(hasAddNewTheme()&&isNewTheme)) {
			try {
				assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.themes.update.successMessage"),
						"[Failed] Update success message should be shown when click on publish on Theme detail page, but '%s' is shown".formatted(toastMessage));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			new APIThemes(loginInformation).publishATheme(themeActiveInfo.getId());//call api to republish old theme.
		}else {
			// don't have edit theme permission, Staff can't publish active theme on detail page, because staff can edit theme when they publish
			// don't have add theme permission, staff can't publish new theme, because satff also add new theme when publish new theme.
			assertCustomize.assertTrue(toastMessage.contains("Oops")||toastMessage.contains("Có lỗi"),"Error should be shown, but '%s' is shown.".formatted(toastMessage));
		}
		commonAction.closeTab();
	}
	public void checkPublishTheme(){
		navigateByUrl();
		String currentWindow = commonAction.getCurrentWindowHandle();
		clickOnCustomizeBtn();
		switchNewTab();
		if(hasViewThemeDetail() && hasPushlishTheme()){
			publishThemesAndVerifyMessage(false);
			commonAction.switchToWindow(currentWindow);
			//Check when click publish on My Themes
			navigateByUrl();
			clickThreeDotIconOnMyThemes();
			clickPublishMyThemes();
			commonAction.sleepInMiliSecond(500);
			new ConfirmationDialog(driver).clickOKBtn();
			commonAction.sleepInMiliSecond(500);
			String toastMessage = new ConfirmationDialog(driver).getPopUpContent();
			try {
				assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("onlineshop.themes.publish.successModalContent"),
						"[Failed] Publish success popup should be shown, but '%s' is shown".formatted(toastMessage));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			new APIThemes(loginInformation).publishATheme(themeActiveInfo.getId());//call api to republish old theme.
			if(hasViewThemeLibrary()){
				//check permission when click edit on Theme library page
				themesLibrary.navigateByUrl()
						.clickEditTheme().clickEditBtnOnTransferModal();
				publishThemesAndVerifyMessage(true);
				commonAction.switchToWindow(currentWindow);
				//check permission when click transfer and edit on Theme library page
				themesLibrary.navigateByUrl()
						.clickEditTheme().clickTransferAndEditOnTransferModal();
				publishThemesAndVerifyMessage(true);
				commonAction.switchToWindow(currentWindow);
			}
		}else if(!hasViewThemeDetail()){
			logger.info("Don't have View theme detail, so no need check publish theme.");
		}else {
			// has View theme detail permission, don't have Publish permision
			new HomePage(driver).waitTillSpinnerDisappear1();
			new HomePage(driver).waitTillLoadingDotsDisappear();
			themeDetail.clickOnPublish();
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(themeDetail.loc_btnPushlish),
					"[Failed] Restricted popup should be shown when click on Publish");
			commonAction.closeTab();
			commonAction.switchToWindow(currentWindow);
			//Check when click publish on My Themes
			navigateByUrl();
			clickThreeDotIconOnMyThemes();
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_blkMyThemes_btnAction,0),
					"[Failed] Restricted popup should be shown when click publish on My Themes.");

			if (hasViewThemeLibrary()){
				//check Restricted popup when click publish on Theme library page
				themesLibrary.navigateByUrl().clickEditTheme()
						.clickEditBtnOnTransferModal();
				switchNewTab();
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(themeDetail.loc_btnPushlish),
						"[Failed] Restricted popup should be shown when click Edit on Theme library then Publish theme.");
				commonAction.closeTab();
				commonAction.switchToWindow(currentWindow);
				//check Restricted popup when click transfer and edit on Theme library page then click Publish.
				themesLibrary.navigateByUrl().clickEditTheme()
						.clickTransferAndEditOnTransferModal();
				switchNewTab();
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(themeDetail.loc_btnPushlish),
						"[Failed] Restricted popup should be shown when click on Transfer and Edit theme then Publish theme.");
				commonAction.closeTab();
				commonAction.switchToWindow(currentWindow);
			}
		}
		logger.info("Verified Publish theme permission.");
	}
	public void checkAddNewTheme(){
		if(hasViewThemeDetail() && hasViewThemeLibrary()){
			String currentWindow = commonAction.getCurrentWindowHandle();
			if(hasAddNewTheme()){
				//check permission when click edit on Theme library page
				themesLibrary.navigateByUrl()
						.clickEditTheme().clickEditBtnOnTransferModal();
				editThemesAndVerifyMessage(true);
				commonAction.switchToWindow(currentWindow);
				//check permission when click transfer and edit on Theme library page
				themesLibrary.navigateByUrl()
						.clickEditTheme().clickTransferAndEditOnTransferModal();
				editThemesAndVerifyMessage(true);
			}else {
					//check Restricted popup when click edit on Theme library page
					themesLibrary.navigateByUrl().clickEditTheme()
							.clickEditBtnOnTransferModal();
					switchNewTab();
					assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(themeDetail.loc_btnSave),
							"[Failed] Restricted popup should be shown when click Edit on Theme library then Save theme.");
					commonAction.closeTab();
					commonAction.switchToWindow(currentWindow);
					//check Restricted page when click transfer and edit on Theme library page
					themesLibrary.navigateByUrl().clickEditTheme()
							.clickTransferAndEditOnTransferModal();
					switchNewTab();
					assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(themeDetail.loc_btnSave),
							"[Failed] Restricted popup should be shown when click on Transfer and Edit theme then Save theme.");
					commonAction.closeTab();
			}
			commonAction.switchToWindow(currentWindow);
		}else logger.info("Don't have View theme detail permission and View theme library permission, so no need check View Add new theme permission.");
		logger.info("Verified Add new theme permission.");
	}
	public void checkDeleteTheme(){
		navigateByUrl();
		if(hasDeleteTheme()){
			deleteAThemes();
			String message = new ConfirmationDialog(driver).getPopUpContent();
			try {
				assertCustomize.assertEquals(message,PropertiesUtil.getPropertiesValueByDBLang("onlineshop.themes.delete.successModalContent"),
						"[Failed] Delete success message should be shown, but '%s' is shown.".formatted(message));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else {
			clickThreeDotIconOnMyThemes();
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_blkMyThemes_btnAction,2),
					"[Failed] Restricted popup should be shown when click on Delete theme.");
		}
		logger.info("Verified delete theme permission.");
	}
	public Themes checkThemesPermission(AllPermissions allPermissions){
		this.allPermissions = allPermissions;
		checkViewThemeLibrary();
		checkViewThemeDetail();
		checkEditTheme();
		checkPublishTheme();
		checkAddNewTheme();
		checkDeleteTheme();
		AssertCustomize.verifyTest();
		return this;
	}
}
