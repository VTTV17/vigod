package web.Dashboard.home;

import org.openqa.selenium.By;

public class HomePageElement {
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
    By loc_lblWhatToDoNextTitle = By.xpath("//div[@class='what-to-do-next']//h3[@class='title']");
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
    By loc_btnLogOut = By.id("logout");
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
    By loc_btnLanguage = By.xpath("(//div[@id='language-selector']//img[@alt='down arrow']/preceding-sibling::span)[2]");
    By loc_btnLanguageInList (String lang){
        return By.xpath("//span[text()='%s']".formatted(lang));
    }
    By loc_dlgSalePitch = By.cssSelector(".gs-sale-pitch_content");
    public By loc_lblToastMessage = By.cssSelector(".Toastify__toast-body");
    By loc_toastMessage_btnClose = By.cssSelector(".Toastify__close-button");
    By loc_imgFacebookBubble = By.id("fb-root");
    By loc_imgFacebookActionBubble = By.id("fb-open-app-action");
    By loc_stnStatistics = By.cssSelector(".statistic");
    By loc_icnBell = By.cssSelector(".header-right__ele-right #recommendationBtn");
    By loc_tllNotification = By.cssSelector(".header-right__ele-right .modal-header .title");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
}
