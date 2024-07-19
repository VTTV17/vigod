package utilities.environment;

import utilities.utils.PropertiesUtil;

public class goBUYEREnvironment {
    public final static String goBUYERBundleId = PropertiesUtil.getEnvironmentData("goBUYERBundleId_shopThang");
    public final static String goBUYERSplashActivity = "%s.ui.modules.splash.SplashScreenActivity".formatted(goBUYERBundleId);

}
