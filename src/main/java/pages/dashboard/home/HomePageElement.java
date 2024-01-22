package pages.dashboard.home;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class HomePageElement {
    WebDriver driver;
    public HomePageElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By loc_lblShopName = By.cssSelector(".header-right .store-detail__name");
    By loc_lblPageTitle = By.xpath("//div[@class='title']");
    By loc_lblGoPOS = By.xpath("(//div[@class='card-row']//span[@class='capitalize-none'])[1]");
    By loc_lblGoWeb = By.xpath("(//div[@class='card-row']//span[@class='capitalize-none'])[2]");
    By loc_lblGoApp = By.xpath("(//div[@class='card-row']//span[@class='capitalize-none'])[3]");
    By loc_lblGoWebBuilding = By.xpath("(//div[@class='card-row']//span[@class='capitalize-none'])[2]/following-sibling::span");
    By loc_lblGoAppBuilding = By.xpath("(//div[@class='card-row']//span[@class='capitalize-none'])[3]/following-sibling::span");
    By loc_lblSaleChannels = By.xpath("//div[@class='card-row']/div[last()]/div/span");
    By loc_lblToConfirmOrders = By.xpath("(//div[@class='statistic-title'])[1]");
    By loc_lblDeliveredOrders = By.xpath("(//div[@class='statistic-title'])[2]");
    By loc_lblToConfirmReservations = By.xpath("(//div[@class='statistic-title'])[3]");
    By loc_lblCompletedReservations = By.xpath("(//div[@class='statistic-title'])[4]");
    By loc_lblWhatToDoNextTitle = By.xpath("//h3[@class='title']");
    By loc_lblWhatToDoNextDescription = By.xpath("//span[@class='subTitle']");
    By loc_lblAddOrImportProduct = By.xpath("(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[1]");
    By loc_lblCustomizeAppearance = By.xpath("(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[2]");
    By loc_lblAddYourDomain = By.xpath("(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[3]");
    By loc_lblAddBankAccount = By.xpath("(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[4]");
    By loc_lblAddOrImportProductDescription = By.xpath("(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[1]");
    By loc_lblCustomizeAppearanceDescription = By.xpath("(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[2]");
    By loc_lblAddYourDomainDescription = By.xpath("(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[3]");
    By loc_lblAddBankAccountDescription = By.xpath("(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[4]");
    By loc_lblAddOrImportProductHint = By.xpath("//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='step-hint']");
    By loc_btnCreateProduct = By.xpath("(//div[contains(@class,'card-btn-group')])[1]//button[1]");
    By loc_btnImportFromShopee = By.xpath("(//div[contains(@class,'card-btn-group')])[1]//button[2]");
    By loc_btnImportFromLazada = By.xpath("(//div[contains(@class,'card-btn-group')])[1]//button[3]");
    By loc_btnChangeDesign = By.xpath("(//div[contains(@class,'card-btn-group')])[2]//button");
    By loc_btnAddDomain = By.xpath("(//div[contains(@class,'card-btn-group')])[3]//button");
    By loc_btnBankInformation = By.xpath("(//div[contains(@class,'card-btn-group')])[4]//button");
    By loc_btnLogOut = By.cssSelector(".header-right__ele-right a[href='/logout']");
    By loc_imgSpinner = By.cssSelector(".loading .lds-dual-ring-grey");
    By loc_imgLoadingDots = By.cssSelector(".loading-screen");
    By loc_mnuSettings = By.cssSelector("a[name $=settings]");
    By loc_mnuProducts = By.cssSelector("a[name='component.navigation.products'] > span > span");
    By loc_mnuPromotion = By.cssSelector("a[name='component.navigation.promotion'] > span > span");
    By loc_mnuPromotion_mnuFlashsale = By.cssSelector("a[name='component.navigation.promotion.flashsale'] > span > span");
    By loc_mnuPromotion_Discount = By.cssSelector("a[name='component.navigation.promotion.discount'] > span > span");
    By loc_btnUpgradeNow = By.cssSelector(".alert-modal .modal-content .gs-button__green");
    By loc_dlgUpgradeNow_btnClose = By.cssSelector(".modal-success.modal-header img");
    By loc_btnSkipIntroduction = By.cssSelector("button[aria-label='skip-product-tour']");
    By loc_dlgUpgradeNow_lblMessage = By.cssSelector(".modal-content");
    By loc_btnLanguage = By.cssSelector("div.language-selector > button");
    By loc_lst_btnLanguages = By.cssSelector("button.uik-select__option");
    By loc_dlgSalePitch = By.cssSelector(".gs-sale-pitch_content");
    By loc_lblToastMessage = By.cssSelector(".Toastify__toast-body");
    By loc_toastMessage_btnClose = By.cssSelector(".Toastify__close-button");
    By loc_imgFacebookBubble = By.id("fb-root");
    By loc_stnStatistics = By.cssSelector(".statistic");
}
