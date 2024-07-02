package web.Dashboard.settings.storeinformation;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

public class StoreInfoPageElement {

    By loc_tabStoreInfo = By.cssSelector("li:nth-child(2) > a.nav-link");
    By loc_ddlTimezone = By.cssSelector(".time-zone--selection-option .right-content");
    By loc_txtShopName = By.id("shopName");
    By loc_txtAppName = By.id("appName");
    By loc_txtAppNameAncestor = new ByChained(loc_txtAppName, By.xpath("./parent::*/parent::*"));
    By loc_txtHotline = By.id("contactNumber");
    By loc_txtEmail = By.cssSelector(".info-container #email");
    By loc_txtAddress = By.id("addressList");
    By loc_txtFacebook = By.id("FACEBOOK");
    By loc_txtInstagram = By.id("INSTAGRAM");
    By loc_txtYoutube = By.id("YOUTUBE_VIDEO");
    By loc_txtSEOTitle = By.cssSelector("input#seoTitle");
    By loc_txtSEOTitleAncestor = new ByChained(loc_txtSEOTitle, By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*"));
    By loc_btnNoticeLogo = By.id("noticeEnabled");
    By loc_btnNoticeLogoAncestor = new ByChained(loc_btnNoticeLogo, By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*/parent::*/parent::*"));
    By loc_btnNoticeLogoSibling = new ByChained(loc_btnNoticeLogo, By.xpath("../preceding-sibling::*"));
    By loc_btnRegisteredLogo = By.id("registeredEnabled");
    By loc_btnRegisteredLogoAncestor = new ByChained(loc_btnRegisteredLogo, By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*/parent::*/parent::*"));
    By loc_btnRegisteredLogoSibling = new ByChained(loc_btnRegisteredLogo, By.xpath("../preceding-sibling::*"));
    By loc_btnSave = By.cssSelector(".info-container .setting_btn_save");
}
