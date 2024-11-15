package utilities.utils;

import org.testng.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static utilities.file.FileNameAndPath.*;

public class PropertiesUtil {
    private static final String ENV_PROPERTIES_FILE = "config.properties";
    private static final Properties envProperties = new Properties();

    static {
        try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream(ENV_PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + ENV_PROPERTIES_FILE);
            }
            // Load the properties file from the class path
            envProperties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load properties file: " + ENV_PROPERTIES_FILE, ex);
        }
    }

    /**
     * Retrieves the property value for the given key.
     *
     * @param key The property key.
     * @return The property value, or null if the key does not exist.
     */
    private static String getEnvProperty(String key) {
        return envProperties.getProperty(key);
    }

    // Environment setting (e.g., "PROD", "STAG")
    public static String environment = getEnvProperty("environment");

    // Storefront language (language used in the Storefront)
    public static String sfLanguage = getEnvProperty("languageDB");

    // Database language (language used in the Dashboard)
    public static String dbLanguage = getEnvProperty("languageSF");

    // Browser to be used for the application
    public static String browser = getEnvProperty("browser");

    // Headless mode flag for browser automation
    public static String headless = getEnvProperty("headless");

    // Flag indicating whether proxy is enabled for network requests
    public static boolean enableProxy = Boolean.parseBoolean(getEnvProperty("enableProxy"));

    public static String domain = getEnvProperty("domain");


    /**
     * Sets the environment for the application.
     *
     * @param environment the environment to set (e.g., "PROD", "STAG")
     */
    public static void setEnvironment(String environment) {
        PropertiesUtil.environment = environment;
    }

    /**
     * Sets the storefront language.
     *
     * @param language the language to set (e.g., "ENG", "VIE")
     */
    public static void setSFLanguage(String language) {
        sfLanguage = language;
    }

    /**
     * Sets the database language.
     *
     * @param language the language to set (e.g., "ENG", "VIE")
     */
    public static void setDBLanguage(String language) {
        dbLanguage = language;
    }


    /**
     * Initializes and loads properties from the specified file.
     *
     * @param propertyFile the path to the property file
     * @return the loaded Properties object
     * @throws IOException if an error occurs while reading the file
     */
    private static Properties initProperties(String propertyFile) throws IOException {
        Properties properties = new Properties();

        // Using try-with-resources to ensure the input stream is closed automatically
        try (FileInputStream inputStream = new FileInputStream(propertyFile);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IOException("Failed to load properties from file: " + propertyFile, e);
        }

        return properties;
    }

    /**
     * Retrieves the value of the specified property from the provided Properties object.
     *
     * @param properties   the Properties object from which the property value will be fetched
     * @param propertyName the name of the property whose value is to be retrieved
     * @return the value of the property, or null if the property is not found or if either parameter is null
     */
    private static String getPropertyValue(Properties properties, String propertyName) {
        // Validate that neither of the parameters are null
        if (properties == null || propertyName == null) {
            return null;  // Return null if properties or propertyName are null
        }

        // Return the property value
        return properties.getProperty(propertyName);
    }

    /**
     * Retrieves the language setting based on the web type.
     *
     * @param webType the type of the web application (e.g., "Dashboard", "Storefront")
     * @return the language setting for the given web type
     * @throws Exception if the web type is unrecognized
     */
    public static String getLanguageFromConfig(String webType) throws Exception {
        return switch (webType) {
            case "Dashboard" -> dbLanguage;
            case "Storefront" -> sfLanguage;
            default -> throw new Exception("Can't detect web type to get language from properties file.");
        };
    }

    /**
     * Retrieves the property value based on the database language setting.
     * This method checks the provided language or defaults to the database language if not provided.
     *
     * @param propertyName the name of the property to retrieve from the language file
     * @param language optional parameter to specify the language (defaults to dbLanguage if not provided)
     * @return the value of the specified property from the corresponding language file (English or Vietnamese)
     * @throws Exception if the language is not recognized or an error occurs while loading the properties file
     */
    public static String getPropertiesValueByDBLang(String propertyName, String... language) throws Exception {
        String langCode = language.length > 0 ? language[0] : dbLanguage;

        // Return the property value for English language
        if (langCode.equalsIgnoreCase("ENG") || langCode.equalsIgnoreCase("en")) {
            return getPropertyValue(
                    initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") +
                                   getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_DASHBOARD_EN_TEXT),
                    propertyName);
        }

        // Return the property value for Vietnamese language
        if (langCode.equalsIgnoreCase("VIE") || langCode.equalsIgnoreCase("vi")) {
            return getPropertyValue(
                    initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") +
                                   getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_DASHBOARD_VI_TEXT),
                    propertyName);
        }

        // Throw exception if language is not recognized
        throw new Exception("Can't detect language: " + langCode);
    }

    /**
     * Retrieves the property value based on the Storefront language setting.
     * This method checks the provided language or defaults to the storefront language if not provided.
     *
     * @param propertyName the name of the property to retrieve from the language file
     * @param language optional parameter to specify the language (defaults to sfLanguage if not provided)
     * @return the value of the specified property from the corresponding language file (English or Vietnamese)
     * @throws Exception if the language is not recognized or an error occurs while loading the properties file
     */
    public static String getPropertiesValueBySFLang(String propertyName, String... language) throws Exception {
        String langCode = language.length > 0 ? language[0] : sfLanguage;

        // Return the property value for English language
        if (langCode.equalsIgnoreCase("ENG") || langCode.equalsIgnoreCase("en")) {
            return getPropertyValue(
                    initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") +
                                   getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_STOREFRONT_EN_TEXT),
                    propertyName);
        }

        // Return the property value for Vietnamese language
        if (langCode.equalsIgnoreCase("VIE") || langCode.equalsIgnoreCase("vi")) {
            return getPropertyValue(
                    initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") +
                                   getDirectorySlash("resources") + getDirectorySlash("i18n") + FILE_STOREFRONT_VI_TEXT),
                    propertyName);
        }

        // Throw exception if language is not recognized
        throw new Exception("Can't detect language: " + langCode);
    }

    /**
     * Retrieves environment-specific data based on the provided property name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the value of the specified property for the current environment
     * @throws RuntimeException if the environment is not recognized or an error occurs while loading the property file
     */
    public static String getEnvironmentData(String propertyName) {
        String fileName = getFileNameForEnvironment();
        if (fileName == null) {
            Assert.fail("Environment not match: " + environment);
            return null;
        }

        try {
            return getPropertyValue(initProperties(projectLocation + getDirectorySlash("src") + getDirectorySlash("main") +
                                                   getDirectorySlash("resources") + getDirectorySlash("environment") + fileName), propertyName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load property file for environment: " + environment, e);
        }
    }

    /**
     * Determines the appropriate file name for the current environment.
     *
     * @return the file name corresponding to the environment, or null if the environment is not recognized
     */
    private static String getFileNameForEnvironment() {
        return switch (environment) {
            case "PROD" -> FILE_DATA_PROD;
            case "STAG" -> FILE_DATA_STAG;
            case "CA" -> FILE_DATA_CA;
            case "PREPROD" -> FILE_DATA_PREPROD;
            default -> null;
        };
    }
}
