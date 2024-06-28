package web.Dashboard.gowallet.transactionhistory;

import org.openqa.selenium.By;

public class TransactionHistoryElement {
    By loc_lst_icnEye = By.xpath("//div[contains(@class,'header-right__ele-left') or contains(@class,'layout-body')]//div[@class='money_reveal']");
    By loc_lst_btnTopup = By.cssSelector(".gs-button__deep-blue");
    By loc_balance = By.xpath("//div[contains(@class,'header-right__ele-left') or contains(@class,'layout-body')]//span[contains(@class,'balance-wrapper')]");
    By loc_ttlTransactionHistory = By.cssSelector(".trasaction-history-title");
    By loc_lstTransactionId = By.cssSelector(".transaction-row a");
}
