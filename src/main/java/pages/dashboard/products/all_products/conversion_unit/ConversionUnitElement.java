package pages.dashboard.products.all_products.conversion_unit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ConversionUnitElement {
    WebDriver driver;
    public ConversionUnitElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".conversion-unit-page-variation .gs-button__green--outline")
    WebElement SELECT_VARIATION_BTN;

    @FindBy(css = ".conversion-unit-wrapper .gs-button__green--outline")
    WebElement SELECT_UNIT_BTN;

    @FindBy(css = ".conversion-unit-wrapper .gs-button__green")
    WebElement SAVE_BTN;

    @FindBy(css = ".variation-name input")
    List<WebElement> VARIATION_LIST_IN_SELECT_VARIATION_POPUP;

    @FindBy(css = ".modal-footer > button.gs-button__green")
    WebElement OK_BTN_IN_SELECT_VARIATION_POPUP;

    @FindBy(css = ".conversion-configure > button")
    List<WebElement> CONFIGURE_BY_VARIATION_BTN;

    @FindBy(css = "#unit-0")
    WebElement CONVERSION_UNIT_NAME;

    @FindBy(css = ".icon_add_unit")
    WebElement ADD_CONVERSION_UNIT_BTN;

    @FindBy(css = ".expanded > div > div")
    List<WebElement> MATCH_CONVERSION_RESULT;

    @FindBy(css = "[name=quantity-0]")
    WebElement CONVERSION_UNIT_QUANTITY;

    @FindBy(css = "div.Toastify__toast-body")
    WebElement TOAST_MESSAGE;
}
