package pages.sellerapp.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.sellerapp.SellerGeneral;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;

import java.time.Duration;

public class SellerCreateCollection {
    final static Logger logger = LogManager.getLogger(SellerCreateCollection.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;

    public SellerCreateCollection (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    By COLLECTION_NAME_INPUT = By.xpath("//*[ends-with(@resource-id,'edtCollectionName')]");
    By COLLECTION_NAME_LBL = By.xpath("//*[ends-with(@resource-id,'edtCollectionName')]/parent::android.widget.FrameLayout/preceding-sibling::android.widget.TextView");
    By COLLECTION_NAME_ERROR = By.xpath("//*[ends-with(@resource-id,'tvErrorCollectionName')]");
    By MANUALLY_TAB = By.xpath("(//*[ends-with(@resource-id,'inputTypeTabLayout')]//android.widget.TextView)[1]");
    By AUTOMATED_TAB = By.xpath("(//*[ends-with(@resource-id,'inputTypeTabLayout')]//android.widget.TextView)[2]");
    By GUIDE_MANUALLY = By.xpath("//*[ends-with(@resource-id,'tvGuideInputTypeManual')]");
    By ADD_PRODUCT_BTN = By.xpath("//*[ends-with(@resource-id,'btnAddProductManual')]");
    By PRODUCT_LIST_LBL_MANUALLY = By.xpath("//*[ends-with(@resource-id,'btnAddProductManual')]/preceding-sibling::android.widget.TextView");
    By EMPTY_PRODUCT_LBL = By.xpath("//*[ends-with(@resource-id,'tvEmptyProduct')]");
    By SEARCH_PRODUCT_NAME_INPUT = By.xpath("//*[ends-with(@resource-id,'search_src_text')]");
    By GUIDE_AUTOMATED = By.xpath("//*[ends-with(@resource-id,'tvGuideInputTypeAutomated')]");
    By SEE_DETAIL_BTN = By.xpath("//*[ends-with(@resource-id,'btnSeeProductListAutomated')]");
    By PRODUCT_LIST_LBL_AUTOMATED = By.xpath("//*[ends-with(@resource-id,'btnSeeProductListAutomated')]/preceding-sibling::android.widget.TextView");
    By PRODUTC_MUST_MATCH_LBL = By.xpath("//*[ends-with(@resource-id,'rgConditionMatchType')]/preceding-sibling::android.widget.TextView");
    By ALL_CONDITIONS_OPTION = By.xpath("//*[ends-with(@resource-id,'rbtAllConditions')]");
    By ANY_CONDITION_OPTION = By.xpath("//*[ends-with(@resource-id,'rbtAnyCondition')]");
    By CONDITION_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'btnConditionField')]");
    By OPERATE_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'btnConditionOperand')]");
    By INPUT_A_STRING = By.xpath("//*[ends-with(@resource-id,'edtConditionValue')]");
    By INPUT_STRING_ERROR = By.xpath("//*[ends-with(@resource-id,'edtConditionValueError')]");
    By ADD_MORE_CONDITION_BTN = By.xpath("//*[ends-with(@resource-id,'tvAddCondition')]");
    By DELETE_CONDITION_ICON = By.xpath("//*[ends-with(@resource-id,'btnDelete')]");
    By PRODUCT_NAME_LIST_AUTOMATED = By.xpath("//*[ends-with(@resource-id,'tvProductName')]");
    By NO_PRODUCT_MESSAGE_AUTOMATED = By.xpath("//*[ends-with(@resource-id,'llActionBarContainer')]/parent::android.widget.RelativeLayout/following-sibling::android.widget.RelativeLayout/android.widget.TextView");

    public SellerCreateCollection verifyText() throws Exception {
        String pageTitle = new SellerGeneral(driver).getHeaderTitle();
        Assert.assertEquals(pageTitle, PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.pageTitle"));
        return this;
    }
}
