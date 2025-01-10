package utilities.environment;

import utilities.utils.PropertiesUtil;

public class goBUYEREnvironment {
    public final static String goBUYERBundleId = PropertiesUtil.getEnvironmentData("goBUYERBundleId_shopThang");
    public final static String goBUYERSplashActivity = "%s.ui.modules.splash.SplashScreenActivity".formatted(goBUYERBundleId);
    public final static String goBUYERHomeScreenActivity = "com.mediastep.gosell.ui.MainActivity";
    public final static String goBUYERBundleId_ShopVi = PropertiesUtil.getEnvironmentData("goBUYERBundleId_shopVi");


}
