package web.Dashboard.onlineshop.themes;

import com.github.dockerjava.api.model.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.commons.UICommonAction;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import web.Dashboard.home.HomePage;

public class ThemesLibrary {
    final static Logger logger = LogManager.getLogger(ThemesLibrary.class);

    WebDriver driver;
    UICommonAction commonAction;

    public ThemesLibrary (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
    By loc_lstTemplate = By.cssSelector(".theme-library-template__item__actions");
    By loc_btnEditTheme = By.cssSelector(".theme-library-template__item__actions .gs-button.gs-button__green.gs-button--undefined");
    By loc_dlgTransferResources_btnEdit = By.cssSelector(".modal-footer .gs-button__green--outline");
    By loc_dlgTransferResources_btnTransferAndEdit = By.cssSelector(".modal-footer .gs-button__green");

    public ThemesLibrary navigateByUrl(){
        String url = Links.DOMAIN + Links.THEMES_LIBRARY_PATH;
        commonAction.navigateToURL(url);
        logger.info("Navigate to url: "+url);
        commonAction.sleepInMiliSecond(500);
        return this;
    }
    public ThemesLibrary clickEditTheme() {
        commonAction.hoverActions(loc_btnEditTheme);
        WebElement el = commonAction.getElement(loc_btnEditTheme);
        if (commonAction.isElementVisiblyDisabled(el)) {
            new HomePage(driver).isMenuClicked(el);
            return this;
        }
        commonAction.click(loc_btnEditTheme);
        logger.info("Clicked on 'Edit' button.");
        return this;
    }
    public ThemeDetail clickEditBtnOnTransferModal(){
        commonAction.click(loc_dlgTransferResources_btnEdit);
        logger.info("Click on edit button on Transfer resource modal.");
        return new ThemeDetail(driver);
    }
    public ThemeDetail clickTransferAndEditOnTransferModal(){
        commonAction.click(loc_dlgTransferResources_btnTransferAndEdit);
        logger.info("Click on Transfer and Edit button on Transfer modal.");
        return new ThemeDetail(driver);
    }
}
