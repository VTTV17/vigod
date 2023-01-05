package pages.dashboard.orders.orderlist;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import pages.dashboard.products.all_products.ProductPage;
import utilities.UICommonAction;

public class OrderList {

	final static Logger logger = LogManager.getLogger(OrderList.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public OrderList(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".order-page button.gs-button__green")
	WebElement EXPORT_BTN;

	@FindBy(xpath = "(//div[contains(@class,'uik-menuDrop__list')]//button)[1]")
	WebElement EXPORT_ORDER_BTN;

	@FindBy(xpath = "(//div[contains(@class,'uik-menuDrop__list')]//button)[2]")
	WebElement EXPORT_ORDER__BY_PRODUCT_BTN;

	@FindBy(xpath = "(//div[contains(@class,'uik-menuDrop__list')]//button)[3]")
	WebElement EXPORT_HISTORY_BTN;

	public OrderList clickExport() {
		commonAction.clickElement(EXPORT_BTN);
		logger.info("Clicked on 'Export' button.");
		return this;
	}

	public OrderList clickExportOrder() {
		if (commonAction.isElementVisiblyDisabled(EXPORT_ORDER_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(EXPORT_ORDER_BTN);
			return this;
		}
		commonAction.clickElement(EXPORT_ORDER_BTN);
		logger.info("Clicked on 'Export Order' button.");
		return this;
	}

	public OrderList clickExportOrderByProduct() {
		if (commonAction.isElementVisiblyDisabled(EXPORT_ORDER__BY_PRODUCT_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(EXPORT_ORDER__BY_PRODUCT_BTN);
			return this;
		}
		commonAction.clickElement(EXPORT_ORDER__BY_PRODUCT_BTN);
		logger.info("Clicked on 'Export Order By Product' button.");
		new HomePage(driver).waitTillLoadingDotsDisappear();
		return this;
	}

	public OrderList clickExportHistory() {
		if (commonAction.isElementVisiblyDisabled(EXPORT_HISTORY_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(EXPORT_HISTORY_BTN);
			return this;
		}
		commonAction.clickElement(EXPORT_HISTORY_BTN);
		logger.info("Clicked on 'Export History' button.");
		return this;
	}

}
