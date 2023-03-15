package pages.storefront.userprofile.userprofileinfo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.userprofile.MyAccount.MyAccountElement;
import utilities.UICommonAction;

import java.time.Duration;

public class UserProfileElement {
    WebDriver driver;
    public UserProfileElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".icon-my-account")
    WebElement MYACCOUNT_SECTION;

    @FindBy(css = ".icon-my-address")
    WebElement MYADDRESS_SECTION;
    
    @FindBy(css = ".icon-my-order")
    WebElement MYORDERS_SECTION;
    
	@FindBy(css = ".icon-membership")
	WebElement MEMBERSHIP_SECTION;    
    
    @FindBy(css = ".box_name .user-left-info__user-name p")
    WebElement DISPLAY_NAME;
    @FindBy(css = ".box_name .user-left-info__user-level p")
    WebElement MEMBERSHIP_LEVEL;
    @FindBy(css = ".user-left-info__user-barcode text")
    WebElement BARCODE_NUMBER;
    @FindBy(css = ".user-left-info__avatar")
    WebElement AVATAR;
}
