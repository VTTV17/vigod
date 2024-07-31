package mobile.buyer.iOS.home;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.iOSNsPredicateString;

public class HomeElement {
    public static By loc_btnFooterSearch = iOSNsPredicateString("name == \"ic_main_tab_search\"");
    By loc_btnFooterAccount = iOSNsPredicateString("name == \"ic_main_tab_account\"");
}
