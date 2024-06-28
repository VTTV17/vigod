package web.Dashboard.onlineshop.blog.categorymanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import web.Dashboard.confirmationdialog.ConfirmationDialog;

public class CategoryManagement {

    final static Logger logger = LogManager.getLogger(CategoryManagement.class);

    WebDriver driver;
    UICommonAction commonAction;


    public CategoryManagement(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    public By loc_btnCreateCategory = By.cssSelector(".gss-content-header--undefined .gs-button__green");
    public By loc_lst_icnEdit = By.cssSelector(".blog-category-table tbody tr svg:nth-child(2)");
    public By loc_lst_icnDelete = By.cssSelector(".blog-category-table tbody tr svg:nth-child(3)");

    public CategoryManagement clickCreateCategory() {
        commonAction.click(loc_btnCreateCategory);
        logger.info("Clicked on 'Create Article' button.");
        return this;
    }

    public CategoryManagement deleteACatogory() {
        commonAction.click(loc_lst_icnDelete, 0);
        new ConfirmationDialog(driver).clickOnRedBtn();
        logger.info("Delete catogory");
        return this;
    }
}
