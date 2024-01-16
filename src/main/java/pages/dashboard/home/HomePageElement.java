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


//    @FindBy(css = ".header-right .store-detail__name")
//    WebElement SHOP_NAME;
//    @FindBy(xpath = "//div[@class='title']")
//    WebElement HOME_PAGE_TITLE;
//    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[1]")
//    WebElement GOPOS_LBL;
//    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[2]")
//    WebElement GOWEB_LBL;
//    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[3]")
//    WebElement GOAPP_LBL;
//    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[2]/following-sibling::span")
//    WebElement GOWEB_BUILDING_Txt;
//    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[3]/following-sibling::span")
//    WebElement GOAPP_BUILDING_Txt;
//    @FindBy(xpath = "//div[@class='card-row']/div[last()]/div/span")
//    WebElement SALE_CHANNELS_LBL;
//    @FindBy(xpath = "(//div[@class='statistic-title'])[1]")
//    WebElement TO_CONFIRM_ORDERS_TXT;
//    @FindBy(xpath = "(//div[@class='statistic-title'])[2]")
//    WebElement DELIVERED_ORDERS_TXT;
//    @FindBy(xpath = "(//div[@class='statistic-title'])[3]")
//    WebElement TO_CONFIRM_RESERVATIONS_TXT;
//    @FindBy(xpath = "(//div[@class='statistic-title'])[4]")
//    WebElement COMPLETED_RESERVATIONS_TXT;
//    @FindBy(xpath = "//h3[@class='title']")
//    WebElement WHAT_TO_DO_NEXT_TITLE;
//    @FindBy(xpath = "//span[@class='subTitle']")
//    WebElement WHAT_TO_DO_NEXT_DESCRIPTION;
//    @FindBy(xpath = "(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[1]")
//    WebElement ADD_OR_IMPORT_PRODUCTS_TITLE;
//    @FindBy(xpath = "(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[2]")
//    WebElement CUSTOMIZE_APPEARANCE_TITLE;
//    @FindBy(xpath = "(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[3]")
//    WebElement ADD_YOUR_DOMAIN_TITLE;
//    @FindBy(xpath = "(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[4]")
//    WebElement ADD_BANK_ACCOUNT_TITLE;
//    @FindBy(xpath = "(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[1]")
//    WebElement ADD_OR_IMPORT_PRODUCT_DESCRIPTION;
//    @FindBy(xpath = "(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[2]")
//    WebElement CUSTOMIZE_APPEARANCE_DESCRIPTION;
//    @FindBy(xpath = "(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[3]")
//    WebElement ADD_YOUR_DOMAIN_DESCRIPTION;
//    @FindBy(xpath = "(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[4]")
//    WebElement ADD_BANK_ACCOUNT_DESCRIPTION;
//    @FindBy(xpath = "//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='step-hint']")
//    WebElement ADD_OR_IMPORT_PRODUCT_HINT_TXT;
//    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[1]//button[1]")
//    WebElement CREATE_PRODUCT_BTN;
//    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[1]//button[2]")
//    WebElement IMPORT_FROM_SHOPEE_BTN;
//    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[1]//button[3]")
//    WebElement IMPORT_FROM_LAZADA_BTN;
//    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[2]//button")
//    WebElement CHANGE_DESIGN_BTN;
//    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[3]//button")
//    WebElement ADD_DOMAIN_BTN;
//    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[4]//button")
//    WebElement BANK_INFORMATION_BTN;
//    @FindBy(css = ".header-right__ele-right a[href='/logout']")
//    WebElement LOGOUT_BTN;
//    @FindBy(css = ".loading .lds-dual-ring-grey")
//    WebElement SPINNER;
//    @FindBy(css = ".loading-screen")
//    WebElement LOADING_DOTS;
//    @FindBy(css = "a[name $=settings]")
//    WebElement SETTINGS_MENU;
//    @FindBy(css = "a[name='component.navigation.products'] > span > span")
//    WebElement PRODUCTS_MENU;
//    @FindBy(css = "a[name='component.navigation.promotion'] > span > span")
//    WebElement PROMOTION_MENU;
//    @FindBy(css = "a[name='component.navigation.promotion.flashsale'] > span > span")
//    WebElement PROMOTION_FLASH_SALE_MENU;

//    @FindBy(css = "a[name='component.navigation.promotion.discount'] > span > span")
//    WebElement PROMOTION_DISCOUNT_MENU;

//    @FindBy(css = ".alert-modal .modal-content .gs-button__green")
//    WebElement UPGRADENOW_BTN;

//    @FindBy(css = ".modal-success.modal-header img")
//    List<WebElement> CLOSE_UPGRADENOW_BTN;

//    @FindBy(css = "button[aria-label='skip-product-tour']")
//    List<WebElement> SKIP_INTRODUCTION_BTN;
//    @FindBy(css = ".modal-content")
//    List<WebElement> UPGRADENOW_MESSAGE;
//    @FindBy(css = "div.language-selector > button")
//    WebElement LANGUAGE;
//    @FindBy(css = "button.uik-select__option")
//    List<WebElement> LANGUAGE_LIST;
//    @FindBy(css = ".gs-sale-pitch_content")
//    WebElement SALE_PITCH_POPUP;
//    @FindBy(css = ".Toastify__toast-body")
//    WebElement TOAST_MESSAGE;
//    @FindBy(css = ".Toastify__close-button")
//    WebElement TOAST_MESSAGE_CLOSE_BTN;
//    @FindBy(id = "fb-root")
//    WebElement FACEBOOK_BUBBLE;

//    @FindBy(xpath = "//img[contains(@src,'/icon-AddProduct.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[1]")
//    WebElement CREATE_PRODUCT_BTN;

//    @FindBy(xpath = "//img[contains(@src,'/icon-AddProduct.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[2]")
//    WebElement IMPORT_FROM_SHOPEE_BTN;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-AddProduct.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[3]")
//    WebElement IMPORT_FROM_LAZADA_BTN;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-CustomizeTheme.svg')]")
//    WebElement CUSTOMIZE_APPEARANCE_ICON;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-CustomizeTheme.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[1]")
//    WebElement CHANGE_DESIGN_BTN;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-CustomizeURL.svg')]")
//    WebElement ADD_YOUR_DOMAIN_ICON;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-CustomizeURL.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[1]")
//    WebElement ADD_DOMAIN_BTN;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-AddBank.svg')]")
//    WebElement ADD_BANK_ACCOUNT_ICON;
//
//    @FindBy(xpath = "//img[contains(@src,'/icon-AddBank.svg')]/ancestor::div[contains(@class,'shortcut-card')]//button[1]")
//    WebElement BANK_INFORMATION_BTN;

//    By STATISTICS = By.cssSelector(".statistic");

//    @FindBy(xpath = ".what-to-do-next")
//    WebElement WHAT_TO_DO_NEXT;
}
