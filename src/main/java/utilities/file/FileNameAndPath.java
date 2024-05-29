package utilities.file;

import java.io.File;

public class FileNameAndPath {
    public static String projectLocation= System.getProperty("user.dir");
    public static String downloadFolder = projectLocation + File.separator + "target" + File.separator + "downloads";
    static String osName= System.getProperty("os.name");
    public static final String FILE_CONFIG = "config.properties";
    public static final String FILE_DASHBOARD_EN_TEXT = "dashboard_en.properties";
    public static final String FILE_STOREFRONT_EN_TEXT = "storefront_en.properties";
    public static final String FILE_DASHBOARD_VI_TEXT = "dashboard_vi.properties";
    public static final String FILE_STOREFRONT_VI_TEXT = "storefront_vi.properties";
    public static final String FILE_DATA_CA = "ca.properties";
    public static final String FILE_DATA_PROD = "prod.properties";
    public static final String FILE_DATA_STAG = "stag.properties";
    public static final String FILE_CREATE_SERVICE_TCS = "CreateService.xlsx";
    public static final String FILE_PERMISSION_PLAN_TCS = "PlanPermissionTcs.xlsx";
    public static final String FILE_PRODUCT_COLLECTION_TCS = "ProductCollection.xlsx";
    public static final String FILE_IMAGE_1 = "cham-soc-mat.jpg";
    public static final String FILE_IMAGE_2 = "giam-beo-bung.jpg";
    public static final String FILE_IMAGE_3 = "fusionmeso.jpg";
    public static final String FILE_IMAGE_4 = "peel.jpg";
    public static final String FILE_PLAN_PERMISSION = "PlanPermission.xlsx";
    public static final String FILE_FEATURE_PERMISSION = "Features.xlsx";
    public static final String FILE_USER_PROFILE_TCS = "UserProfileSFTcs.xlsx";
    public static final String FILE_BUY_LINK_TCS = "BuyLinkTCs.xlsx";
    public static final String FILE_PRODUCT_COLLECTION = "ProductCollection.xlsx";
    public static final String FILE_SERVICE_COLLECTION_TCS = "ServiceCollectionTcs.xlsx";
    public static final String FILE_NAME_IMAGE_SERVICE_COLLECTION_1 = "serviceCollection.jpg";
    public static final String FILE_IMPORT_PRODUCT = "import_product.xlsx";
    public static final String FOLDER_UPLOAD_FILE = "uploadfile";
    public static final String FOLDER_IMPORT_PRODUCT = "import_product";
    public static final String FILE_IMPORT_PAYOUT_PRODUCT = "importPayoutByProduct.xlsx";
    public static final String FILE_IMPORT_PAYOUT_REVENUE = "ImportPayoutByRevenue.xlsx";
    public static final String FILE_IMPORT_PAYOUT_RESELLER = "ImportPayoutReseller.xlsx";
    public static final String FOLDER_IMPORT_PAYOUT = "import_payout";
    private static boolean isWindow() {
        return (osName.toLowerCase().indexOf("win"))>=0;
    }
    private static boolean isMac() {
        return (osName.toLowerCase().indexOf("mac"))>=0;
    }
    private static boolean isUnix() {
        return (osName.toLowerCase().indexOf("nix"))>=0 || (osName.toLowerCase().indexOf("nux"))>=0;
    }
    public static String getDirectorySlash(String folderName) {
        if(isMac()||isUnix()) {
            folderName="/"+folderName+'/';
        }else if(isWindow()) {
            folderName= "\\"+folderName+"\\";
        }else {
            folderName=null;
        }
        return folderName;
    }
}
