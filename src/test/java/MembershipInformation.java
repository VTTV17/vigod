import java.io.IOException;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import api.dashboard.customers.SegmentAPI;
import api.dashboard.login.Login;
import pages.dashboard.customers.segments.Segments;
import pages.dashboard.home.HomePage;
import pages.dashboard.marketing.loyaltyprogram.LoyaltyProgram;
import pages.storefront.header.HeaderSF;
import pages.storefront.userprofile.MembershipInfo;
import utilities.UICommonAction;
import utilities.jsonFileUtility;
import utilities.driver.InitWebdriver;

public class MembershipInformation extends BaseTest {

	pages.dashboard.login.LoginPage loginDB;
	pages.storefront.login.LoginPage loginSF;
	HomePage homePage;
	HeaderSF headerPage;
	
	api.dashboard.marketing.LoyaltyProgram loyaltyProgramAPI;
	SegmentAPI segmentAPI;

	JsonNode sellerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	JsonNode buyerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
	String SELLER_MAIL = sellerData.findValue("seller").findValue("mail").findValue("username").asText();
	String SELLER_PASSWORD = sellerData.findValue("seller").findValue("mail").findValue("password").asText();
	String SELLER_COUNTRY = sellerData.findValue("seller").findValue("mail").findValue("country").asText();
	String BUYER_MAIL = buyerData.findValue("buyer").findValue("spareAccount").findValue("username").asText();
	String BUYER_PASSWORD = buyerData.findValue("buyer").findValue("spareAccount").findValue("password").asText();
	String BUYER_COUNTRY = buyerData.findValue("buyer").findValue("spareAccount").findValue("country").asText();
	
	// Customer segment info
	String customerSegment = "BH_4602 and BH_4603";
	String dataGroup = "Customers data";
	String data = "Registration date";
	String operator = "is before";
	String comparedValue = "09/29/2022";
	
	// Loyalty program info
	String tierName = "Rich Customers";
	String avatar = "membership.jpg";
	String description = "Test BH_4602 and BH_4603";
	String discountPercent = "50";
	String maximunDiscount = "100000";
	
	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginDB = new pages.dashboard.login.LoginPage(driver);
		loginSF = new pages.storefront.login.LoginPage(driver);
		homePage = new HomePage(driver);
		headerPage = new HeaderSF(driver);
		commonAction = new UICommonAction(driver);
		
		segmentAPI = new SegmentAPI();
		loyaltyProgramAPI = new api.dashboard.marketing.LoyaltyProgram();
	}	
	
	/**
	 * Logs into Dashboard and changes user language.
	 */
	public void loginDashboard() {
		loginDB.navigate()
        .performLogin(SELLER_COUNTRY, SELLER_MAIL, SELLER_PASSWORD);
		homePage.waitTillSpinnerDisappear().selectLanguage(language).hideFacebookBubble();
	}	
	
	/**
	 * Logs into Storefront and changes user language.
	 */
	public void loginSF() {
		loginSF.navigate().performLogin(BUYER_COUNTRY, BUYER_MAIL, BUYER_PASSWORD);
		headerPage.waitTillLoaderDisappear();
		headerPage.clickUserInfoIcon().changeLanguage(language);
	}	

	@BeforeClass
	public void loginDashboardByAPI() throws InterruptedException {
		new Login().loginToDashboardByMail(SELLER_MAIL, SELLER_PASSWORD);
	}	
	
	@BeforeMethod
	public void setup() throws InterruptedException {
		instantiatePageObjects();
	}

	@Test
	public void BH_4602_LoginWithNonMembershipAccount() throws Exception {
		
		String operator = "is after";
		
		// Log into Dashboard
		loginDashboard();
		
		// Create customer segment
		new Segments(driver).navigate()
		.clickCreateSegmentBtn()
		.inputSegmentName(customerSegment)
		.selectDataGroupCondition(dataGroup)
		.selectDataCondition(data)
		.selectComparisonOperatorCondition(operator)
		.inputComparedValueCondition(comparedValue)
		.clickSaveBtn();
		
		// Create loyalty program
		new LoyaltyProgram(driver).navigate().clickCreateMembershipBtn()
		.createMembershipLevel(tierName, avatar, customerSegment, description, discountPercent, maximunDiscount);
		
		// Log into SF
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		loginSF();
		
		// Verify membership info
		headerPage.clickUserInfoIcon().clickUserProfile().clickMembershipInfoSection().verifyMembershipTitle(false, tierName);
		
		String actualDescription = new MembershipInfo(driver)
		.clickMembershipTitle(tierName)
		.getMembershipContentInHTMLFormat();
		
		Assert.assertEquals(actualDescription, description);
		
		// Delete loyalty program
		loyaltyProgramAPI.deleteMembership(loyaltyProgramAPI.getMembershipIdByName(tierName));
		
		// Delete customer segment
		segmentAPI.deleteSegment(segmentAPI.getSegmentIdByName(customerSegment));
	}
	
	@Test
	public void BH_4603_LoginWithMembershipAccount() throws Exception {
		
		// Log into Dashboard
		loginDashboard();
		
		// Create customer segment
		new Segments(driver).navigate()
		.clickCreateSegmentBtn()
		.inputSegmentName(customerSegment)
		.selectDataGroupCondition(dataGroup)
		.selectDataCondition(data)
		.selectComparisonOperatorCondition(operator)
		.inputComparedValueCondition(comparedValue)
		.clickSaveBtn();
		
		// Create loyalty program
		new LoyaltyProgram(driver).navigate()
		.clickCreateMembershipBtn()
		.createMembershipLevel(tierName, avatar, customerSegment, description, discountPercent, maximunDiscount);
		
		// Log into SF
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		loginSF();
		
		// Verify membership info
		headerPage.clickUserInfoIcon().clickUserProfile().clickMembershipInfoSection().verifyMembershipTitle(true, tierName);
		
		String actualDescription = new MembershipInfo(driver)
		.clickMembershipTitle(tierName)
		.getMembershipContentInHTMLFormat();
		
		Assert.assertEquals(actualDescription, description);
		
		// Delete loyalty program
		loyaltyProgramAPI.deleteMembership(loyaltyProgramAPI.getMembershipIdByName(tierName));
		
		// Delete customer segment
		segmentAPI.deleteSegment(segmentAPI.getSegmentIdByName(customerSegment));
	}

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }	
	
}
