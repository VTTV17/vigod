package android;

import org.testng.annotations.Test;
import pages.buyerapp.BuyerGeneral;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.search.BuyerSearchDetailPage;
import pages.buyerapp.search.BuyerSearchPage;

public class ServiceDetailTest extends AppiumTest{
    NavigationBar navigationBar;
    BuyerSearchPage searchPage;
    BuyerSearchDetailPage searchDetailPage;
    @Test
    public void demo(){
        BuyerGeneral general = new BuyerGeneral(driver);
        general.waitLoadingDisappear();
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch("Automation Service SVnSmnnuyefP");
    }
}
