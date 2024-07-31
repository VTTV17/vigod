package utilities.account;

import utilities.utils.PropertiesUtil;

public class AccountTest {
    public final static String ADMIN_ACCOUNT_THANG = PropertiesUtil.getEnvironmentData("adminShopThangAccount");
    public final static String ADMIN_PASSWORD_THANG = PropertiesUtil.getEnvironmentData("adminShopThangPassword");
    public final static String BUYER_ACCOUNT_THANG = PropertiesUtil.getEnvironmentData("buyerShopThangAccount");
    public final static String BUYER_PASSWORD_THANG = PropertiesUtil.getEnvironmentData("buyerShopThangPassword");
    // 10.10.0.15
    // beecow-staging.clvpjm611njd.ap-southeast-1.rds.amazonaws.com
//    public static String DB_HOST = "beecow-staging.clvpjm611njd.ap-southeast-1.rds.amazonaws.com";
    public static String DB_HOST = "172.16.113.55";
    public static String DB_HOST_ITEM2 = "beecow-staging-item.clvpjm611njd.ap-southeast-1.rds.amazonaws.com";
    public static String DB_HOST_CATALOG = "beecow-staging.clvpjm611njd.ap-southeast-1.rds.amazonaws.com";
    public static String DB_PORT = "5432";

    // postgres
    // beecow
    public static String DB_DATABASE = "beecow";

    // postgres
    // readonly
    public static String DB_USER = "readonly";
    public static String DB_ITEM2_USER = "dbaitem";
    public static String DB_USER_CATALOG = "dbacatalog";
    // postgres
    // R7LHffcgeEh2tpQ0qU2y
    public static String DB_PASS = "R7LHffcgeEh2tpQ0qU2y";
    public static String DB_ITEM2_PASS = "ShogdBiPHcC67RDQ6H1u";
    public static String DB_PASS_CATALOG = "Nw4kYW3G9eribICWw0dP";

    public static String USERNAME_INTERNALTOOL = PropertiesUtil.getEnvironmentData("internalUserName");
    public static String PASSWORD_INTERNALTOOL = PropertiesUtil.getEnvironmentData("internalPass");
    public static String ADMIN_SHOP_VI_USERNAME = PropertiesUtil.getEnvironmentData("adminShopViUserName");
    public static String SF_SHOP_VI_PASSWORD = PropertiesUtil.getEnvironmentData("sfShopViPass");
    public static String ADMIN_SHOP_VI_PASSWORD = PropertiesUtil.getEnvironmentData("adminShopViPass");

    public static String USERNAME_RESELLER_SHOPVI = PropertiesUtil.getEnvironmentData("resellerUserNameShopVi");
    public static String PASSWORD_RESELLER_SHOPVI = PropertiesUtil.getEnvironmentData("resellerPassShopVi");
    public static String ADMIN_SHOP_COFFEE_PASSWORD = PropertiesUtil.getEnvironmentData("adminShopCoffeePass");
    public static String ADMIN_SHOP_COFFEE_USERNAME = PropertiesUtil.getEnvironmentData("adminShopCoffeeUserName");
    public static String ADMIN_CREATE_NEW_SHOP_PASSWORD = "fortesting!1";
    public static String ADMIN_COUNTRY_TIEN = PropertiesUtil.getEnvironmentData("adminCountryShopTien");
    public static String ADMIN_USERNAME_TIEN = PropertiesUtil.getEnvironmentData("adminUsernameShopTien");
    public static String ADMIN_PASSWORD_TIEN = PropertiesUtil.getEnvironmentData("adminPasswordShopTien");
    public static String ADMIN_FACEBOOK_USERNAME = PropertiesUtil.getEnvironmentData("adminFacebookUsername");
    public static String ADMIN_FACEBOOK_PASSWORD = PropertiesUtil.getEnvironmentData("adminFacebookPassword");
    public static String ADMIN_USERNAME_GOWEB = PropertiesUtil.getEnvironmentData("adminGoWebUserName");
    public static String ADMIN_USERNAME_GOAPP = PropertiesUtil.getEnvironmentData("adminGoAppUserName");
    public static String ADMIN_USERNAME_GOPOS = PropertiesUtil.getEnvironmentData("adminGoPOSUserName");
    public static String ADMIN_USERNAME_GOSOCIAL = PropertiesUtil.getEnvironmentData("adminGoSocialUserName");
    public static String ADMIN_USERNAME_GOLEAD = PropertiesUtil.getEnvironmentData("adminGoLeadUserName");
    public static String SF_USERNAME_VI_1 = PropertiesUtil.getEnvironmentData("buyer1");
    public static String SF_USERNAME_VI_2 = PropertiesUtil.getEnvironmentData("buyer2");
    public static String SF_USERNAME_VI_3 = PropertiesUtil.getEnvironmentData("buyer3");
    public static String SF_USERNAME_VI_4 = PropertiesUtil.getEnvironmentData("buyer4");
    public static String SF_USERNAME_PHONE_VI_1 = PropertiesUtil.getEnvironmentData("buyerPhone1");
    public static String SF_USERNAME_VI_5 = PropertiesUtil.getEnvironmentData("buyer5");
    public static String GOMUA_USERNAME_EMAIL = PropertiesUtil.getEnvironmentData("gomuaBuyer");
    public static String GOMUA_PASSWORD_EMAIL = PropertiesUtil.getEnvironmentData("gomuaBuyerPassword");
    public static String GOMUA_USERNAME_PHONE = PropertiesUtil.getEnvironmentData("gomuaBuyerPhone");
    public static String STAFF_SHOP_VI_USERNAME = PropertiesUtil.getEnvironmentData("staffShopViUserName");
    public static String STAFF_COFFEE_SHOP_USERNAME = PropertiesUtil.getEnvironmentData("staffShopCoffeeShop");
    public static String STAFF_SHOP_VI_PASSWORD = PropertiesUtil.getEnvironmentData("staffShopViPass");
    public static String STAFF_USERNAME_TIEN = PropertiesUtil.getEnvironmentData("staffUsernameShopTien");
    public static String STAFF_PASSWORD_TIEN = PropertiesUtil.getEnvironmentData("staffPasswordShopTien");
    public static String ADMIN_FORGOTPASSWORD_USERNAME_MAIL = PropertiesUtil.getEnvironmentData("adminForgotPasswordUsernameMail");
    public static String ADMIN_FORGOTPASSWORD_PASSWORD_MAIL = PropertiesUtil.getEnvironmentData("adminForgotPasswordPasswordMail");
    public static String ADMIN_FORGOTPASSWORD_COUNTRY_MAIL = PropertiesUtil.getEnvironmentData("adminForgotPasswordCountryMail");
    public static String ADMIN_FORGOTPASSWORD_USERNAME_PHONE = PropertiesUtil.getEnvironmentData("adminForgotPasswordUsernamePhone");
    public static String ADMIN_FORGOTPASSWORD_PASSWORD_PHONE = PropertiesUtil.getEnvironmentData("adminForgotPasswordPasswordPhone");
    public static String ADMIN_FORGOTPASSWORD_COUNTRY_PHONE = PropertiesUtil.getEnvironmentData("adminForgotPasswordCountryPhone");


    //ATM Credentials
    public static String ATM_BANK = "NCB";
    public static String ATM_CARDNUMBER = "9704198526191432198";
    public static String ATM_CARDHOLDER = "NGUYEN VAN A";
    public static String ATM_ISSUINGDATE = "07/15";
    public static String ATM_OTP = "123456";
    //VISA Credentials
    public static String VISA_CARDNUMBER = "4456530000001096";
    public static String VISA_EXPIRYDATE = "12/23";
    public static String VISA_CCV = "123";
    public static String VISA_CARDHOLDER = "NGUYEN VAN A";
    public static String VISA_EMAIL = "test@gmail.com";
    public static String VISA_COUNTRY = "Viet Nam";
    public static String VISA_CITY = "Ha Noi";
    public static String VISA_ADDRESS = "22 Lang Ha";
    public static String VISA_OTP = "1234";
    //PAYPAL Credentials
//    public static String PAYPAL_USERNAME = "trangthuy9662@gmail.com";
//    public static String PAYPAL_PASSWORD = "P@ssword1@";
    public static String PAYPAL_USERNAME = "tienvan-staging-vn@mailnesia.com";
    public static String PAYPAL_PASSWORD = "246357x@X";
    //Shopee Credentials
    public static String SHOPEE_COUNTRY = "VN";
    public static String SHOPEE_USERNAME = "chicoseller.01@gmail.com";
    public static String SHOPEE_PASSWORD = "Medi@step01";
}
