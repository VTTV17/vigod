package utilities.links;

import utilities.PropertiesUtil;

public class Links {
    public static String LOGIN_PAGE_TITLE = PropertiesUtil.getEnvironmentData("loginPageTitle");
    public static String SIGNUP_PAGE_TITLE = PropertiesUtil.getEnvironmentData("signupPageTitle");

    public static String SETTING_PAGE_TITLE = "Admin Staging - Settings";
    public static String DOMAIN = PropertiesUtil.getEnvironmentData("dashboarUrl");
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

    // PROD config
//    public final static String URI = "https://api.beecow.com";
//    public final static String SF_DOMAIN = ".gosell.vn";

    // STG config
    public static String URI = PropertiesUtil.getEnvironmentData("urlApi");
    public final static String SF_DOMAIN = PropertiesUtil.getEnvironmentData("sfDomain");
    public final static String PAGE_404_PATH = "404-page.html";
}
