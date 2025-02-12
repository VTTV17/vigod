package web.Dashboard;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import utilities.account.AccountTest;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.data.testdatagenerator.CreateCustomerTDG;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.dashboard.customer.create.UICreateCustomerData;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;

public class CreateCustomer extends BaseTest {

	String sellerCountry, sellerUsername, sellerPassword, sellerSFURL, sfDomain;
	
	LoginPage loginPage;
	HomePage homePage;
	AllCustomers customerPage;
	
	LoginInformation sellerCredentials;

	@BeforeClass
	void loadData() {
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			sellerCountry = AccountTest.ADMIN_COUNTRY_TIEN;
			sellerUsername = AccountTest.ADMIN_USERNAME_TIEN;
			sellerPassword = AccountTest.ADMIN_PASSWORD_TIEN;
		} else {
			sellerCountry = AccountTest.ADMIN_MAIL_BIZ_COUNTRY;
			sellerUsername = AccountTest.ADMIN_MAIL_BIZ_USERNAME;
			sellerPassword = AccountTest.ADMIN_MAIL_BIZ_PASSWORD;
		}
		
		sellerCredentials = new Login().setLoginInformation(DataGenerator.getPhoneCode(sellerCountry), sellerUsername, sellerPassword).getLoginInformation();
		
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver, Domain.valueOf(domain));
		homePage = new HomePage(driver);
		customerPage = new AllCustomers(driver, Domain.valueOf(domain));
		commonAction = new UICommonAction(driver);
		
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language)).performValidLogin(sellerCountry, sellerUsername, sellerPassword);
	}	
	
	@DataProvider
	public Object[][] customerData() {
		return new Object[][] { 
			{CreateCustomerTDG.buildVNCustomerUIData(DisplayLanguage.valueOf(language))},
			{CreateCustomerTDG.buildForeignCustomerUIData(DisplayLanguage.valueOf(language))},
		};
	}

	@Test(dataProvider = "customerData", invocationCount = 3)
	public void TC_CreateCustomer(UICreateCustomerData data) {
		
		//Create a customer
		customerPage.navigateUsingURL()
			.clickCreateNewCustomerBtn()
			.createCustomer(data);
		
		homePage.getToastMessage();
		
		commonAction.refreshPage();
		
		//See his details
		var customerDetail =  customerPage.clickUser(data.getName());
		Assert.assertEquals(customerDetail.getPhoneNumber().split("\\:")[1], data.getPhone()); //We won't check the phoneCode for now
		Assert.assertEquals(customerDetail.getCountry(), data.getCountry());
		Assert.assertEquals(customerDetail.getAddress(), data.getAddress());
		
		//Delete the customer
		customerPage.navigateUsingURL()
			.tickCustomerByName(data.getName())
			.clickSelectAction()
			.clickDeleteBtn()
			.clickDeleteCustomerConfirmBtn();
	}
	
    @AfterClass
    public void afterClass() {
        tearDownWeb();
    }    
	
}
