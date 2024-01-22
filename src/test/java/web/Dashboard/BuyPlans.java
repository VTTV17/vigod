package web.Dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.account.AccountPage;
import web.Dashboard.settings.plans.PlansPage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import utilities.enums.PaymentMethod;

public class BuyPlans {
	


	public Object[] paymentMethod(){
		return PaymentMethod.getAllValues();
	}
	
	@DataProvider
	public Object[] plans(){
		String[] plans = {"GoWEB", "GoAPP", "GoPOS", "GoSOCIAL", "GoLEAD"};
		return plans;
	}
	@Test(dataProvider = "plans")
	public void Test_PaymentMethods(String plan) throws Exception {
		String country = "Vietnam";
		String username = "auto0-shop0844735279@mailnesia.com";
		String password = "fortesting!1";
        
		PropertiesUtil.setEnvironment("STAG");
        PropertiesUtil.setDBLanguage("VIE");
		
		for (String payment: PaymentMethod.getAllValues()) {
			
			WebDriver driver = new InitWebdriver().getDriver("chrome", "false");
			LoginPage loginPage = new LoginPage(driver);
			HomePage homePage = new HomePage(driver);
			AccountPage accountPage = new AccountPage(driver);
			PlansPage plansPage = new PlansPage(driver);
			
			
			loginPage.navigate().performLogin(country, username, password);
			homePage.navigateToPage("Settings");
			
			List<String> originalInfo = accountPage.navigate().getPlanInfo(plan);
			System.out.println("Original Info: " + originalInfo.toString());
			accountPage.clickSeePlans();

			/* Buy Plan */
			plansPage.selectPlan(plan);
			plansPage.selectPaymentMethod(payment);
			String orderID = plansPage.completePayment(payment);
			plansPage.logoutAfterSuccessfulPurchase(payment, orderID);
			
			loginPage.navigate().performLogin(country, username, password);
			homePage.navigateToPage("Settings");
			
			List<String> laterInfo = new ArrayList<>();
			
			for (int i=0; i<5; i++) {
				laterInfo = accountPage.navigate().getPlanInfo(plan);
				if (!laterInfo.equals(originalInfo)) break;
				new UICommonAction(driver).sleepInMiliSecond(3000);
			}
			
			System.out.println("Later Info: " + laterInfo.toString());
			
	        String regex = "\\d{4}";

	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(originalInfo.get(1));
	        int orginalExpiryYear = 0;
	        if (matcher.find()) {
	        	orginalExpiryYear = Integer.valueOf(matcher.group());
	        }
	        
	        Pattern pattern1 = Pattern.compile(regex);
	        Matcher matcher1 = pattern1.matcher(laterInfo.get(1));
	        int laterExpiryYear = 0;
	        if (matcher1.find()) {
	        	laterExpiryYear = Integer.valueOf(matcher1.group());
	        }
			
	        Assert.assertEquals(laterExpiryYear, orginalExpiryYear + 1);
	        homePage.clickLogout();
	        
	        driver.quit();
		}
	}

}
