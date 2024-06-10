package web.Dashboard.marketing.affiliate.partnerinventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.commons.UICommonAction;
import utilities.links.Links;

public class TransferDetailPage extends TransferDetailElement{
    final static Logger logger = LogManager.getLogger(TransferDetailPage.class);
    UICommonAction common;
    WebDriver driver;

    public TransferDetailPage(WebDriver driver) {
        this.driver = driver;
        common = new UICommonAction(driver);
    }
    public void navigateByUrl(int id){
        String url = Links.DOMAIN + Links.AFFILIATE_TRANSFER_DETAIL_PATH.formatted(id);
        common.navigateToURL(url);
        logger.info("Navigate to url: "+url);
    }
    public void clickSelectAction(){
        common.click(loc_lnkSelectAction);
        logger.info("Click Select Action link");

    }
    public AddEditTransferPage clickEditTransfer(){
        common.click(loc_lst_actions,0);
        logger.info("Click on Edit transfer");
        return new AddEditTransferPage(driver);
    }
    public void clickCancelTransfer(){
        common.click(loc_lst_actions,1);
        logger.info("Click on Cancel transfer");
    }
    public void clickTransferShipGoods_ReceiveGood(){
        common.click(loc_btnShipGoods_ReceiveGood);
        logger.info("Click on Ship Goods button");
    }
}
