import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.dashboard.customers.segments.Segments;
import pages.dashboard.home.HomePage;
import pages.dashboard.marketing.loyaltyprogram.LoyaltyProgram;
import pages.storefront.header.HeaderSF;
import pages.storefront.userprofile.MembershipInfo;
import utilities.jsonFileUtility;

public class MembershipInformation extends BaseTest {

	pages.dashboard.login.LoginPage loginDB;
	pages.storefront.login.LoginPage loginSF;
	HomePage homePage;
	HeaderSF headerPage;

	String SELLER_MAIL;
	String SELLER_PASSWORD;
	String SELLER_COUNTRY;	
	
	String BUYER_MAIL;
	String BUYER_PASSWORD;
	String BUYER_COUNTRY;
	
	String EXPECTED_NON_MEMBERSHIP_VI = "Bạn chưa thỏa điều kiện chương trình Hội Viên";
	String EXPECTED_NON_MEMBERSHIP_EN = "You have not met conditions of Membership program";
	
	String EXPECTED_MEMBERSHIP_VI = "Bạn đang là thành viên %s";
	String EXPECTED_MEMBERSHIP_EN = "You are in %s tier";
	
	// Language of Storefront.
	String language = "English";
	
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
	
	
	@BeforeClass
	public void readData() {
		JsonNode sellerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
		JsonNode buyerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");

		SELLER_MAIL = sellerData.findValue("seller").findValue("mail").findValue("username").asText();
		SELLER_PASSWORD = sellerData.findValue("seller").findValue("mail").findValue("password").asText();
		SELLER_COUNTRY = sellerData.findValue("seller").findValue("mail").findValue("country").asText();
		
		BUYER_MAIL = buyerData.findValue("buyer").findValue("spareAccount").findValue("username").asText();
		BUYER_PASSWORD = buyerData.findValue("buyer").findValue("spareAccount").findValue("password").asText();
		BUYER_COUNTRY = buyerData.findValue("buyer").findValue("spareAccount").findValue("country").asText();
	}

	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		loginDB = new pages.dashboard.login.LoginPage(driver);
		loginSF = new pages.storefront.login.LoginPage(driver);
		homePage = new HomePage(driver);
		headerPage = new HeaderSF(driver);
	}

	@Test
	public void BH_4602_LoginWithNonMembershipAccount() throws InterruptedException {
		
		String operator = "is after";
		
		// Log into Dashboard
		loginDB.navigate()
        .performLogin(SELLER_COUNTRY, SELLER_MAIL, SELLER_PASSWORD);
		homePage.waitTillSpinnerDisappear()
		.hideFacebookBubble();
		
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
		loginSF.navigate().
		performLogin(BUYER_COUNTRY, BUYER_MAIL, BUYER_PASSWORD);
		headerPage.waitTillLoaderDisappear();
		
		// Verify membership info
		new HeaderSF(driver)
		.clickUserInfoIcon()
		.changeLanguage(language)
		.clickUserInfoIcon()
		.clickUserProfile()
		.clickMembershipInfoSection();
		String actualTitle = new MembershipInfo(driver).getMembershipTitle().trim();

		new MembershipInfo(driver).clickMembershipTitle(tierName);
		String actualDescription = new MembershipInfo(driver).getMembershipContentInHTMLFormat();

		String temp = language.contentEquals("English") ? EXPECTED_NON_MEMBERSHIP_EN:EXPECTED_NON_MEMBERSHIP_VI;
		Assert.assertEquals(actualTitle, temp.formatted(tierName));
		Assert.assertEquals(actualDescription, description);
		
		// Delete loyalty program
		commonAction.switchToWindow(0);
		new LoyaltyProgram(driver)
		.deleteMembership(tierName)
		.clickOKBtn();
		
		// Delete customer segment
		new Segments(driver)
		.navigate()
		.inputSearchTerm(customerSegment)
		.deleteSegment(customerSegment)
		.clickOKBtn();
		new HomePage(driver).getToastMessage();
	}
	
	@Test
	public void BH_4603_LoginWithMembershipAccount() throws InterruptedException {
		// Log into Dashboard
		loginDB.navigate()
		.performLogin(SELLER_COUNTRY, SELLER_MAIL, SELLER_PASSWORD);
		homePage.waitTillSpinnerDisappear()
		.hideFacebookBubble();
		
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
		loginSF.navigate().
		performLogin(BUYER_COUNTRY, BUYER_MAIL, BUYER_PASSWORD);
		headerPage.waitTillLoaderDisappear();
		
		// Verify membership info
		new HeaderSF(driver)
		.clickUserInfoIcon()
		.changeLanguage(language)
		.clickUserInfoIcon()
		.clickUserProfile()
		.clickMembershipInfoSection();
		String actualTitle = new MembershipInfo(driver).getMembershipTitle().trim();
		
		new MembershipInfo(driver).clickMembershipTitle(tierName);
		String actualDescription = new MembershipInfo(driver).getMembershipContentInHTMLFormat();
		
		String temp = language.contentEquals("English") ? EXPECTED_MEMBERSHIP_EN:EXPECTED_MEMBERSHIP_VI;
		Assert.assertEquals(actualTitle, temp.formatted(tierName));
		Assert.assertEquals(actualDescription, description);
		
		// Delete loyalty program
		commonAction.switchToWindow(0);
		new LoyaltyProgram(driver)
		.deleteMembership(tierName)
		.clickOKBtn();
		
		// Delete customer segment
		new Segments(driver)
		.navigate()
		.inputSearchTerm(customerSegment)
		.deleteSegment(customerSegment)
		.clickOKBtn();
		new HomePage(driver).getToastMessage();
	}
	
}
