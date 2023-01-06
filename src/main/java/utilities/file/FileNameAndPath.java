package utilities.file;

public class FileNameAndPath {
    public static String projectLocation= System.getProperty("user.dir");
    static String osName= System.getProperty("os.name");
    public static final String FILE_CONFIG = "config.properties";
    public static final String FILE_DASHBOARD_EN_TEXT = "dashboard_en.properties";
    public static final String FILE_STOREFRONT_EN_TEXT = "storefront_en.properties";

    public static final String FILE_DASHBOARD_VI_TEXT = "dashboard_vi.properties";
    public static final String FILE_STOREFRONT_VI_TEXT = "storefront_vi.properties";


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
