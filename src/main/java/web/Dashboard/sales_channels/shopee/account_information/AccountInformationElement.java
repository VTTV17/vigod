package web.Dashboard.sales_channels.shopee.account_information;

import org.openqa.selenium.By;

public class AccountInformationElement {
    By loc_btnDownloadShopeeProduct = By.xpath("(//div[@class='sp-connected__content'])[2]//button");
    By loc_icnProductDownloading = By.cssSelector(".sp-connected__content:nth-child(2) [class *= rotate]");
    By loc_btnSyncShopeeOrder = By.cssSelector(".sp-connected__content:nth-child(3) button");
    By loc_icnOrderSyncing = By.cssSelector(".sp-connected__content:nth-child(3) [class *= rotate]");
}
