package web.StoreFront.userprofile.userprofileinfo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class UserProfileElement {
    WebDriver driver;
    public UserProfileElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By loc_btnMyAccount = By.cssSelector(".icon-my-account");
    By loc_btnMyAddress = By.cssSelector(".icon-my-address");
    By loc_btnMyOrders = By.cssSelector(".icon-my-order");
    By loc_btnMembership = By.cssSelector(".icon-membership");
    By loc_lblDisplayName = By.cssSelector(".box_name .user-left-info__user-name p");
    By loc_lblMembershipLevel = By.cssSelector(".box_name .user-left-info__user-level p");
    By loc_lblBarcode = By.cssSelector(".user-left-info__user-barcode text");
    By loc_imgAvatar = By.cssSelector(".user-left-info__avatar");
//    @FindBy(css = ".icon-my-account")
//    WebElement MYACCOUNT_SECTION;
//    @FindBy(css = ".icon-my-address")
//    WebElement MYADDRESS_SECTION;
//    @FindBy(css = ".icon-my-order")
//    WebElement MYORDERS_SECTION;
//	@FindBy(css = ".icon-membership")
//	WebElement MEMBERSHIP_SECTION;
//    @FindBy(css = ".box_name .user-left-info__user-name p")
//    WebElement DISPLAY_NAME;
//    @FindBy(css = ".box_name .user-left-info__user-level p")
//    WebElement MEMBERSHIP_LEVEL;
//    @FindBy(css = ".user-left-info__user-barcode text")
//    WebElement BARCODE_NUMBER;
//    @FindBy(css = ".user-left-info__avatar")
//    WebElement AVATAR;
}
