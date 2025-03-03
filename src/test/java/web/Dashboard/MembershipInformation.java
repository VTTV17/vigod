package web.Dashboard;

import static utilities.links.Links.SF_URL_TIEN;

import java.io.IOException;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import api.Seller.customers.APISegment;
import api.Seller.login.Login;

import web.Dashboard.customers.segments.Segments;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.marketing.loyaltyprogram.LoyaltyProgram;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.userprofile.MembershipInfo;
import utilities.commons.UICommonAction;
import utilities.utils.jsonFileUtility;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;

public class MembershipInformation extends BaseTest {

	LoginPage loginDB;
	web.StoreFront.login.LoginPage loginSF;
	HomePage homePage;
	HeaderSF headerPage;
	
	api.Seller.marketing.membership.LoyaltyProgram loyaltyProgramAPI;
	APISegment segmentAPI;

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
	LoginInformation loginInformation;
	
	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginDB = new LoginPage(driver);
		loginSF = new web.StoreFront.login.LoginPage(driver);
		homePage = new HomePage(driver);
		headerPage = new HeaderSF(driver);
		commonAction = new UICommonAction(driver);
		
		segmentAPI = new APISegment(loginInformation);
		loyaltyProgramAPI = new api.Seller.marketing.membership.LoyaltyProgram(loginInformation);
	}	
	
	/**
	 * Logs into Dashboard and changes user language.
	 */
	public void loginDashboard() {
		loginDB.navigate()
        .performLogin(SELLER_COUNTRY, SELLER_MAIL, SELLER_PASSWORD);
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}	
	
	/**
	 * Logs into Storefront and changes user language.
	 */
	public void loginSF() {
		loginSF.navigate(SF_URL_TIEN).performLogin(BUYER_COUNTRY, BUYER_MAIL, BUYER_PASSWORD);
		headerPage.waitTillLoaderDisappear();
		headerPage.clickUserInfoIcon().changeLanguage(language);
	}	

	@BeforeClass
	public void loginDashboardByAPI() {
		loginInformation = new Login().setLoginInformation(SELLER_MAIL, SELLER_PASSWORD).getLoginInformation();
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
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	
	
}
