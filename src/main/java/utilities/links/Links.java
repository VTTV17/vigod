package utilities.links;

import utilities.utils.PropertiesUtil;

public class Links {
    public static String LOGIN_PAGE_TITLE = PropertiesUtil.getEnvironmentData("loginPageTitle");
    public static String SIGNUP_PAGE_TITLE = PropertiesUtil.getEnvironmentData("signupPageTitle");

    public static String SETTING_PAGE_TITLE = "Admin Staging - Settings";
    public static String DOMAIN = PropertiesUtil.getEnvironmentData("dashboarUrl");
    public static String DOMAIN_BIZ = PropertiesUtil.getEnvironmentData("dashboardUrlBiz");
    public static String SETTING_PATH = "/setting";
    public static String SF_URL_TIEN = PropertiesUtil.getEnvironmentData("sfShopTien");
    public static String SF_ShopVi = PropertiesUtil.getEnvironmentData("sfShopVi");

    public static String SF_COFFEE = PropertiesUtil.getEnvironmentData("sfShopCoffee");
    public static String LOGIN_PATH = "/login";
    public static String SIGNUP_PATH = "/signup";
    public static String SIGNUP_PATH1 = "/wizard/1";
    public static String INTERNAL_TOOL = PropertiesUtil.getEnvironmentData("internalUrl");

    public static String PRODUCT_DETAIL_PATH = "/product/edit/";
    public static String GOMUA_URL = PropertiesUtil.getEnvironmentData("gomuaUrl");
    public static String STORE_CURRENCY = "đ";
    public static String ALL_PRODUCT_PATH = "product";
    public static String AFFILIATE_PAYOUT_INFORMATION_PATH = "/affiliate/payout/information";
    public static String AFFILIATE_TRANSFER_GOODS_PATH = "/affiliate/transfer/partner/list";
    public static String AFFILIATE_CREATE_TRANSFER_PATH = "/affiliate/transfer/partner/create";
    public static String AFFILIATE_TRACKING_STOCK_PATH = "/affiliate/inventory";
    public static String AFFILIATE_TRANSFER_DETAIL_PATH = "/affiliate/transfer/partner/wizard/%s";
    public static String AFFILIATE_TRANSFER_EDIT_PATH = "/affiliate/transfer/partner/edit/%s";
    public static String AFFILIATE_CUSTOMER_PATH = "/affiliate/customers";
    public static String AFFILIATE_PAYOUT_HISTORY_PATH = "/affiliate/payout/history";
    public static String GO_WALLET_PATH = "/go-wallet/transaction-history/list";
    public static String THEMES_PATH = "/theme/management";
    public static String THEMES_LIBRARY_PATH = "/theme/library";
    public static String BLOG_MANAGEMENT_PATH = "/channel/storefront/blog/article/list";

    // PROD config
//    public final static String URI = "https://api.beecow.com";
//    public final static String SF_DOMAIN = ".gosell.vn";

    // STG config
	public static String URI = PropertiesUtil.getEnvironmentData("urlApi");
    public final static String KIBANA_URI = PropertiesUtil.getEnvironmentData("kibanaAPIURI");
    public final static String SF_DOMAIN = PropertiesUtil.getEnvironmentData("sfDomain");
    public final static String PAGE_404_PATH = "404-page.html";
}
