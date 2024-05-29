package web.Dashboard.marketing.affiliate.payout.payoutinformation;

import org.openqa.selenium.By;

public class PayoutInfomationElement {
    By loc_lst_lblPayoutInfoAmount = By.cssSelector(".box-amount span");
    By loc_lst_lblParnetCode = By.cssSelector(".affiliate-payout__table td:nth-child(1)");
    By loc_btnExport = By.cssSelector(".btn-export");
    By loc_btnImport = By.xpath("//button[contains(@class,'btn-export')]/following-sibling::button");
    By loc_txtUploadFile = By.xpath("//div[contains(@class,'item-list-import')]/input");
    By loc_dlgImport_btnImport = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgImport_lst_lblError = By.cssSelector(".item-list-import-modal-error-list-list");
}
