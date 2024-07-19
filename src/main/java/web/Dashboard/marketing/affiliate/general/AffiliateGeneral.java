package web.Dashboard.marketing.affiliate.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.commons.UICommonAction;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;
import web.Dashboard.marketing.affiliate.order.PartnerOrdersPage;

import java.util.List;

public class AffiliateGeneral {
    final static Logger logger = LogManager.getLogger(AffiliateGeneral.class);
    WebDriver driver;
    UICommonAction common;

    public AffiliateGeneral(WebDriver driver) {
        this.driver = driver;
        common = new UICommonAction(driver);
    }

    public By loc_tabAffiliateActive = By.cssSelector(".affiliate-tab__tab__button__active");
    public By loc_tab_dropshipReseller = By.xpath("//div[contains(@class,'affiliate-tab__tab__button')]");
    By loc_lst_tabCommissionByProductAndRevenue = By.xpath("//div[contains(@class,'drop-ship')]/span");
    By loc_blkRestricted = By.cssSelector(".no-permission-wrapper");
    public void selectAffiliateTab(boolean isDropship){
        String activeTab = common.getText(loc_tabAffiliateActive);
        String selectTab = "";
        try {
            selectTab = isDropship? PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.dropshipTab"): PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.resellerTab");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(!activeTab.equals(selectTab)){
            common.sleepInMiliSecond(500);
            if (isDropship) {
                common.click(loc_tab_dropshipReseller, 0);
                logger.info("Switch to tab 1 (Dropship)");
            } else {
                common.click(loc_tab_dropshipReseller, 1);
                logger.info("Switch to tab 2 (Reseller)");
            }
        }else logger.info("Active tab is '%s', Expected tab is '%s', so no need switch tab".formatted(activeTab,selectTab));
    }
    public void selectTabCommissionByRevenue(){
        common.click(loc_lst_tabCommissionByProductAndRevenue,1);
        logger.info("Select Commission by Revenue tab.");
        common.sleepInMiliSecond(500);
    }
    public boolean isRestrictedComponentShow(){
        return common.getElements(loc_blkRestricted,2).size()>0;
    }
}
