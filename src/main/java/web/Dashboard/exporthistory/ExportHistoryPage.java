package web.Dashboard.exporthistory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.commons.UICommonAction;


public class ExportHistoryPage {
    final static Logger logger = LogManager.getLogger(ExportHistoryPage.class);
    UICommonAction common;
    WebDriver driver;
    public ExportHistoryPage(WebDriver driver){
        this.driver = driver;
        common = new UICommonAction(driver);
    }
    public By loc_lst_iconDownloadDropshipPartner  = By.xpath("//div[contains(@class,'body-desktop')]//div[contains(text(),'EXPORT_DROPSHIP_PARTNERS')]/following-sibling::div[contains(@class,'action')]/div");
    By loc_lstDropshipPartnerFileName = By.xpath("//div[contains(@class,'body-desktop')]//div[contains(text(),'EXPORT_DROPSHIP_PARTNERS')]");
    public void clickOnDownloadDropshipPartner(){
        common.click(loc_lst_iconDownloadDropshipPartner,0);
        logger.info("Click on Download export dropship partner file.");
    }
    public String getExportDropshipPartnerFileNewest(){
        String fileName = common.getText(loc_lstDropshipPartnerFileName,0);
        logger.info("Export dropship partner file name: "+fileName);
        return fileName;
    }
}
