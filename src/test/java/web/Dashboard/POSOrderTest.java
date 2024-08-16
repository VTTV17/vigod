package web.Dashboard;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import utilities.data.testdatagenerator.CreateCustomerTDG;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.customer.create.UICreateCustomerData;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;

public class POSOrderTest extends BaseTest{

	LoginInformation credentials;

	@BeforeMethod
	public void setup() {
		credentials = new Login().setLoginInformation("tham1babe@mailnesia.com", "fortesting!1").getLoginInformation();
	}

	@Test(invocationCount = 2)
	public void createVNCustomer() {

		driver = new InitWebdriver().getDriver(browser, headless);
		
		LoginPage loginPage = new LoginPage(driver);
		
		loginPage.navigate().performLogin("tham1babe@mailnesia.com", "fortesting!1");
		
		HomePage homePage = new HomePage(driver);
		
		homePage.navigateToPage("All Customers");
		
		UICreateCustomerData data = CreateCustomerTDG.buildVNCustomerUIData("VIE");
		
		new AllCustomers(driver).clickCreateNewCustomerBtn().createCustomer(data).clickAddBtn();
		homePage.getToastMessage();
	}
	
	@Test(invocationCount = 2)
	public void createForeignCustomer() {
		
		driver = new InitWebdriver().getDriver(browser, headless);
		
		LoginPage loginPage = new LoginPage(driver);
		
		loginPage.navigate().performLogin("tham1babe@mailnesia.com", "fortesting!1");
		
		HomePage homePage = new HomePage(driver);
		
		homePage.navigateToPage("All Customers");
		
		UICreateCustomerData data = CreateCustomerTDG.buildForeignCustomerUIData("VIE");
		
		new AllCustomers(driver).clickCreateNewCustomerBtn().createCustomer(data).clickAddBtn();
		homePage.getToastMessage();
	}

	@AfterMethod
	public void writeResult(ITestResult result) throws Exception {
		super.writeResult(result);
		driver.quit();
	}	
	
}
