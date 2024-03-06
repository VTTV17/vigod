package utilities.utils;

import org.testng.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static utilities.file.FileNameAndPath.*;

public class PropertiesUtil {
    public static String environment = "STAG";
    public static String sfLanguage = "vi";
    public static String dbLanguage = "vi";
    private static Properties initProperties(String propertyFile) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propertyFile);
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    return properties;
    }

    private static String getPropertyValue(Properties pro, String propertyName) {
        if (pro == null || propertyName == null) {
            return null;
        }
        return pro.getProperty(propertyName);
    }

    public static String getLanguageFromConfig(String webType) throws Exception {
        String language = "";
        switch (webType) {
            case "Dashboard":
                language = getPropertyValue(initProperties(projectLocation+ getDirectorySlash("src") +getDirectorySlash("main") + getDirectorySlash("resources") + FILE_CONFIG), "languageDB");
                break;
            case "Storefront":
                language = getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") +getDirectorySlash("main") + getDirectorySlash("resources") + FILE_CONFIG), "languageSF");
                break;
            default: throw new Exception("Can't detect web type to get language from properties file.");
        }
        return language;
    }

    public static String getPropertiesValueByDBLang(String propertyName, String... language) throws Exception {
        String lang = ((language.length == 0) || (language[0] == null)) ? PropertiesUtil.dbLanguage : language[0];
        if (lang.equalsIgnoreCase("ENG") || lang.equalsIgnoreCase("en")) {
            return getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_DASHBOARD_EN_TEXT), propertyName);
        } else if (lang.equalsIgnoreCase("VIE") || lang.equalsIgnoreCase("vi")) {
            return getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_DASHBOARD_VI_TEXT), propertyName);
        } else throw new Exception("Can't detect language.");
    }

    public static String getPropertiesValueBySFLang(String propertyName, String... language) throws Exception {
        String lang = language.length == 0 ? PropertiesUtil.sfLanguage : language[0];
        if (lang.equalsIgnoreCase("ENG") || lang.equalsIgnoreCase("en")) {
            return getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_STOREFRONT_EN_TEXT), propertyName);
        } else if (lang.equalsIgnoreCase("VIE") || lang.equalsIgnoreCase("vi")) {
            return getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_STOREFRONT_VI_TEXT), propertyName);
        } else throw new Exception("Can't detect language.");
    }
    public static void setEnvironment(String environment) {
        PropertiesUtil.environment = environment;
    }

    public static void setSFLanguage(String language) {
        PropertiesUtil.sfLanguage = language;
    }

    public static void setDBLanguage(String language) {
        PropertiesUtil.dbLanguage = language;
    }

    public static String getEnvironmentData(String propertyName)  {
        String value = "";
        switch (environment) {
            case "PROD" -> {
                try {
                    value = getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("environment") + FILE_DATA_PROD), propertyName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "STAG" -> {
                try {
                    value = getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("environment") + FILE_DATA_STAG), propertyName);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "CA" -> {
                try {
                    value = getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("environment") + FILE_DATA_CA), propertyName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> Assert.fail("Environment not match: " + environment);
        }
        return value;
    }
}
