package utilities;

import org.testng.internal.Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

import static utilities.file.FileNameAndPath.*;

public class PropertiesUtil {
    private static Properties initProperties(String propertyFile) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propertyFile);
            properties.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
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
        String lang = language.length == 0 ? getLanguageFromConfig("Dashboard") : language[0];
        if (lang.equalsIgnoreCase("ENG")) {
            return getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_DASHBOARD_EN_TEXT), propertyName);
        } else if (lang.equalsIgnoreCase("VIE")) {
            return getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_DASHBOARD_VI_TEXT), propertyName);
        } else throw new Exception("Can't detect language.");
    }

    public static String getPropertiesValueBySFLang(String propertyName, String... language) throws Exception {
        String lang = language.length == 0 ? getLanguageFromConfig("Storefront") : language[0];
        if (lang.equalsIgnoreCase("ENG")) {
            return getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_STOREFRONT_EN_TEXT), propertyName);
        } else if (lang.equalsIgnoreCase("VIE")) {
            return getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_STOREFRONT_VI_TEXT), propertyName);
        } else throw new Exception("Can't detect language.");
    }
}
