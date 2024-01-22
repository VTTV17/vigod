package web.Dashboard.marketing.buylink;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import web.Dashboard.home.HomePage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

public class BuyLinkManagement extends HomePage{

	final static Logger logger = LogManager.getLogger(BuyLinkManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public BuyLinkManagement(WebDriver driver) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);

		PageFactory.initElements(driver, this);
	}
	By loc_btnExploreNow = By.cssSelector(".buylink-intro .gs-button__green");
	By loc_btnCreateBuyLink = By.cssSelector(".buylink-header button div");
	By loc_lblBuyLinkManagementTitle = By.cssSelector(".gs-page-title");
	By loc_lblUrlCol = By.xpath("//section[contains(@class,'gs-table-header-item')][2]/span");
	By loc_lblCouponCol = By.xpath("//section[contains(@class,'gs-table-header-item')][3]/span");
	By loc_lblCreateDateCol = By.xpath("//section[contains(@class,'gs-table-header-item')][4]/span");
	By loc_lblActionCol = By.xpath("//section[contains(@class,'gs-table-header-item')][5]/span");
	By loc_btnCopyLink = By.xpath("(//i[contains(@class,'gs-action-button')])[1]");
	By loc_btnEditLink = By.xpath("(//i[contains(@class,'gs-action-button')])[2]");
	By loc_btnDelete = By.xpath("(//i[contains(@class,'gs-action-button')])[3]");
	By loc_lst_lblUrl = By.xpath("//div[@class='gs-table-body-item text-truncate']");
	By loc_tltCopyLink = By.xpath("(//div[contains(@class,'gs-table-body-item action')])[1]/div[1]");
	By loc_tltEditLink = By.xpath("(//div[contains(@class,'gs-table-body-item action')])[1]/div[2]");
	By loc_tltDeleteLink = By.xpath("(//div[contains(@class,'gs-table-body-item action')])[1]/div[3]");
	By loc_dlgConfirmation_btnDelete = By.xpath("//div[@class='modal-footer']/button[2]");
	public BuyLinkManagement clickExploreNow() {
    	commonAction.click(loc_btnExploreNow);
    	logger.info("Clicked on 'Explore Now' button.");
    	return this;
    }    
    
    public CreateBuyLink clickCreateBuyLink() {
    	commonAction.click(loc_btnCreateBuyLink);
    	logger.info("Clicked on 'Create Buy Link' button.");
    	return new CreateBuyLink(driver);
    }    	

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateBuyLink(String permission) {
		if (permission.contentEquals("A")) {
			clickExploreNow().clickCreateBuyLink();
			boolean flag =  new CreateBuyLink(driver).isProductSelectionDialogDisplayed();
			commonAction.navigateBack();
			Assert.assertTrue(flag);
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }

    /*-------------------------------------*/
	public BuyLinkManagement VerifyText() throws Exception {
		Assert.assertEquals(commonAction.getText(loc_lblBuyLinkManagementTitle), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.pagetitle"));
		Assert.assertEquals(commonAction.getText(loc_btnCreateBuyLink), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.createBuyLinkBtn"));
		Assert.assertEquals(commonAction.getText(loc_lblUrlCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.urlCol"));
		Assert.assertEquals(commonAction.getText(loc_lblCouponCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.couponCol"));
		Assert.assertEquals(commonAction.getText(loc_lblCreateDateCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.createDate"));
		Assert.assertEquals(commonAction.getText(loc_lblActionCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.actions"));
		commonAction.sleepInMiliSecond(500);
		Assert.assertEquals(commonAction.getAttribute(loc_tltCopyLink,"data-original-title"),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.copyLinkTooltip"));
		Assert.assertEquals(commonAction.getAttribute(loc_tltEditLink,"data-original-title"),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.editLinkTooltip"));
		Assert.assertEquals(commonAction.getAttribute(loc_tltDeleteLink,"data-original-title"),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.deleteLinkTooltip"));
		return this;
	}
	public BuyLinkManagement clickOnCopyLink(){
		commonAction.click(loc_btnCopyLink);
		logger.info("Click on Copy link of the newest link (on the top)");
		return this;
	}
	public String getNewestBuyLinkURL(){
		commonAction.sleepInMiliSecond(1500);
		String URL = commonAction.getText(loc_lst_lblUrl,0);
		return URL;
	}
	public BuyLinkManagement verifyCreateBuyLinkSuccessfulMessage() throws Exception {
		waitTillLoadingDotsDisappear();
		Assert.assertEquals(getToastMessage(),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.createSuccessfullyMessage"));
		return this;
	}
	public BuyLinkManagement verifyCopiedLink(String expectedLink) throws IOException, UnsupportedFlavorException {
		String copiedLink = commonAction.getCopiedText(loc_btnCopyLink);
		Assert.assertEquals(copiedLink,expectedLink);
		logger.info("Verify copied link");
		return this;
	}
	public BuyLinkManagement verifyCopiedMessage() throws Exception {
		Assert.assertEquals(getToastMessage(),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.copySuccessfullyMessage"));
		logger.info("Verify toast message Copy successfully.");
		return this;
	}
	public CreateBuyLink clickEditNewestBuyLink(){
		commonAction.click(loc_btnEditLink);
		logger.info("Click on edit newest buy link");
		return new CreateBuyLink(driver);
	}
	public BuyLinkManagement verifyUpdateBuyLinkSuccessfulMessage() throws Exception {
		waitTillLoadingDotsDisappear();
		Assert.assertEquals(getToastMessage(),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.updateSuccessfullyMessage"));
		return this;
	}
	public BuyLinkManagement clickDeleteNewestBuyLink(){
		commonAction.click(loc_btnDelete);
		logger.info("Click on delete newest buy link.");
		return this;
	}
	public BuyLinkManagement clickDeleteBtnOnModal(){
		commonAction.click(loc_dlgConfirmation_btnDelete);
		logger.info("Click on delete button on Delete Confirmation modal.");
		return this;
	}
	public BuyLinkManagement verifyAfterDeleteBuyLink(String linkBefore){

		waitTillLoadingDotsDisappear();
		Assert.assertNotEquals(getNewestBuyLinkURL(),linkBefore);
		logger.info("Verify newest buy link after deleted.");
		return this;
	}

}
