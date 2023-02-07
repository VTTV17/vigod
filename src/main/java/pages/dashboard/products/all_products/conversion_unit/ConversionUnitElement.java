package pages.dashboard.products.all_products.conversion_unit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

class ConversionUnitElement {
    WebDriver driver;

    ConversionUnitElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /* Conversion unit config */
    @FindBy(css = ".uik-checkbox__wrapper > [name='conversionUnitCheckbox']")
    WebElement ADD_CONVERSION_UNIT_CHECKBOX;

    @FindBy(css = "div:nth-child(5)  .bg-light-white > div > button")
    WebElement CONFIGURE_BTN;

    /* Without variation config */
    @FindBy(css = ".btn-header-wrapper > .gs-button__green--outline")
    WebElement WITHOUT_VARIATION_HEADER_SELECT_UNIT_BTN;

    @FindBy(css = ".btn-header-wrapper > .gs-button__green")
    WebElement WITHOUT_VARIATION_HEADER_SAVE_BTN;

    @FindBy(css = "#unit-0")
    WebElement WITHOUT_VARIATION_UNIT;

    By WITHOUT_VARIATION_LIST_AVAILABLE_UNIT = By.cssSelector(".expanded > div > div");

    @FindBy(css = ".icon_add_unit")
    WebElement WITHOUT_VARIATION_ADD_BTN;

    @FindBy(css = "[name *= quantity]")
    WebElement WITHOUT_VARIATION_QUANTITY;

    /* Variation config */
    @FindBy(css = ".gs-button__green--outline")
    WebElement VARIATION_HEADER_SELECT_VARIATION_BTN;

    @FindBy(css = ".gs-button__green")
    WebElement VARIATION_HEADER_SAVE_BTN;

    @FindBy(css = ".modal-content")
    WebElement SELECT_VARIATION_POPUP;

    By VARIATION_SELECT_VARIATION_POPUP_LIST_VARIATION_CHECKBOX = By.cssSelector("input[name = variationUnit]");

    @FindBy(css = ".modal-footer > .gs-button__green")
    WebElement VARIATION_SELECT_VARIATION_POPUP_SAVE_BTN;

    @FindBy(css = ".conversion-configure > .gs-button__blue--outline")
    List<WebElement> VARIATION_CONFIGURE_BTN;

    @FindBy(css = ".btn-header-wrapper > .gs-button__green--outline")
    WebElement CONFIGURE_FOR_EACH_VARIATION_HEADER_SELECT_UNIT_BTN;

    @FindBy(css = ".btn-header-wrapper > .gs-button__green")
    WebElement CONFIGURE_FOR_EACH_VARIATION_HEADER_SAVE_BTN;

    @FindBy(css = "#unit-0")
    WebElement CONFIGURE_FOR_EACH_VARIATION_UNIT;

    By CONFIGURE_FOR_EACH_VARIATION_LIST_AVAILABLE_UNIT = By.cssSelector(".expanded > div > div");

    @FindBy(css = ".icon_add_unit")
    WebElement CONFIGURE_FOR_EACH_VARIATION_ADD_BTN;

    @FindBy(css = "[name *= quantity]")
    WebElement CONFIGURE_FOR_EACH_VARIATION_QUANTITY;

    /* UI element */
    @FindBy(css = ".gs-widget__content .bg-light-white p")
    WebElement UI_CONVERSION_UNIT_INFORMATION;

    @FindBy(css = ".gs-widget__content .bg-light-white buton")
    WebElement UI_CONVERSION_UNIT_CONFIGURE_BTN;

    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(5) small")
    WebElement UI_CONVERSION_UNIT_FOR_IMEI_NOTICE;
}
