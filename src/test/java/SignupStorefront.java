import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pages.storefront.SignupPage;
import utilities.database.InitConnection;
import pages.Mailnesia;

import java.sql.SQLException;

public class SignupStorefront extends BaseTest{

	SignupPage signupPage;
	
    @BeforeMethod
    public void setup() throws InterruptedException {
    	super.setup();
    	signupPage = new SignupPage(driver);
    }		
	
    @Test
    public void SignupWithPhone() throws SQLException, InterruptedException {
    	String phone = "1122334455";
    	
    	signupPage.navigate()
    			.fillOutSignupForm("Andorra", phone, "Abc@12345", "Luke Thames", "02/02/1990")
                .inputVerificationCode(new InitConnection().getActivationKey("+376:" + phone));
//                .clickConfirmBtn();
        Thread.sleep(2000);
    }
    
//    @Test
    public void SignupWithEmail() throws SQLException, InterruptedException {
    	String username = "tienvan345";
    	
    	signupPage.navigate()
    	.fillOutSignupForm("Andorra", username + "@mailnesia.com", "Abc@12345", "Luke Thames", "02/02/1990");
    	Thread.sleep(7000);

    	// Get verification code from Mailnesia
    	commonAction.openNewTab(); // Open a new tab
    	commonAction.switchToWindow(1); // Switch to the newly opened tab
    	String verificationCode = new Mailnesia(driver).navigate(username).getVerificationCode(); // Get verification code
    	commonAction.closeTab(); // Close the newly opened tab
    	commonAction.switchToWindow(0); // Switch back to the original tab
    	
    	signupPage.inputVerificationCode(verificationCode);
//    	.clickConfirmBtn();
        Thread.sleep(2000);
    }
    
}
