package pages.dashboard.marketing.buylink;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.PropertiesUtil;
import utilities.UICommonAction;

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

    @FindBy(css = ".buylink-intro .gs-button__green")
    WebElement EXPLORE_NOW_BTN;
    @FindBy(css = ".buylink-header button div")
    WebElement CREATE_BUYLINK_BTN;	
	@FindBy(css = ".gs-page-title")
	WebElement BUY_LINK_MANAGEMENT_TITLE;
	@FindBy(xpath = "//section[@class='gs-table-header-item'][2]/span" )
	WebElement URL_LBL;
	@FindBy(xpath = "//section[@class='gs-table-header-item'][3]/span" )
	WebElement COUPON_LBL;
	@FindBy(xpath = "//section[@class='gs-table-header-item'][4]/span" )
	WebElement CREATE_DATE_LBL;
	@FindBy(xpath = "//section[@class='gs-table-header-item'][5]/span" )
	WebElement ACTIONS_LBL;
	@FindBy(css = ".buylink-content-body .empty span")
	WebElement NO_BUY_LINK_YET;
	@FindBy(xpath = "(//i[contains(@class,'gs-action-button')])[1]")
	WebElement COPY_LINK_ICO;
	@FindBy(xpath = "(//i[contains(@class,'gs-action-button')])[2]")
	WebElement EDIT_LINK_ICO;
	@FindBy(xpath = "(//i[contains(@class,'gs-action-button')])[3]")
	WebElement DELETE_LINK_ICO;
	@FindBy(css = ".text-truncate")
	List<WebElement> URL_LIST;
	@FindBy(xpath = "(//div[contains(@class,'gs-table-body-item action')])[1]/div[1]")
	WebElement COPY_LINK_TOOLTIP;
	@FindBy(xpath = "(//div[contains(@class,'gs-table-body-item action')])[1]/div[2]")
	WebElement EDIT_LINK_TOOLTIP;
	@FindBy(xpath = "(//div[contains(@class,'gs-table-body-item action')])[1]/div[3]")
	WebElement DELETE_LINK_TOOLTIP;
	@FindBy(xpath = "//div[@class='modal-footer']/button[2]")
	WebElement DELETE_BTN_ON_MODAL;

	public BuyLinkManagement clickExploreNow() {
    	commonAction.clickElement(EXPLORE_NOW_BTN);
    	logger.info("Clicked on 'Explore Now' button.");
    	return this;
    }    
    
    public CreateBuyLink clickCreateBuyLink() {
    	commonAction.clickElement(CREATE_BUYLINK_BTN);
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
		Assert.assertEquals(commonAction.getText(BUY_LINK_MANAGEMENT_TITLE), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.pagetitle"));
		Assert.assertEquals(commonAction.getText(CREATE_BUYLINK_BTN), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.createBuyLinkBtn"));
		Assert.assertEquals(commonAction.getText(URL_LBL), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.urlCol"));
		Assert.assertEquals(commonAction.getText(COUPON_LBL), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.couponCol"));
		Assert.assertEquals(commonAction.getText(CREATE_DATE_LBL), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.createDate"));
		Assert.assertEquals(commonAction.getText(ACTIONS_LBL), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.actions"));
		commonAction.sleepInMiliSecond(500);
		Assert.assertEquals(commonAction.getElementAttribute(COPY_LINK_TOOLTIP,"data-original-title"),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.copyLinkTooltip"));
		Assert.assertEquals(commonAction.getElementAttribute(EDIT_LINK_TOOLTIP,"data-original-title"),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.editLinkTooltip"));
		Assert.assertEquals(commonAction.getElementAttribute(DELETE_LINK_TOOLTIP,"data-original-title"),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.deleteLinkTooltip"));
		return this;
	}
	public BuyLinkManagement clickOnCopyLink(){
		commonAction.clickElement(COPY_LINK_ICO);
		logger.info("Click on Copy link of the newest link (on the top)");
		return this;
	}
	public String getNewestBuyLinkURL(){
		commonAction.sleepInMiliSecond(1500);
		String URL = commonAction.getText(URL_LIST.get(0));
		return URL;
	}
	public BuyLinkManagement verifyCreateBuyLinkSuccessfulMessage() throws Exception {
		waitTillLoadingDotsDisappear();
		Assert.assertEquals(getToastMessage(),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.createSuccessfullyMessage"));
		return this;
	}
	public BuyLinkManagement verifyCopiedLink(String expectedLink) throws IOException, UnsupportedFlavorException {
		String copiedLink = commonAction.getCopiedText(COPY_LINK_ICO);
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
		commonAction.clickElement(EDIT_LINK_ICO);
		logger.info("Click on edit newest buy link");
		return new CreateBuyLink(driver);
	}
	public BuyLinkManagement verifyUpdateBuyLinkSuccessfulMessage() throws Exception {
		waitTillLoadingDotsDisappear();
		Assert.assertEquals(getToastMessage(),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.updateSuccessfullyMessage"));
		return this;
	}
	public BuyLinkManagement clickDeleteNewestBuyLink(){
		commonAction.clickElement(DELETE_LINK_ICO);
		logger.info("Click on delete newest buy link.");
		return this;
	}
	public BuyLinkManagement clickDeleteBtnOnModal(){
		commonAction.clickElement(DELETE_BTN_ON_MODAL);
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
