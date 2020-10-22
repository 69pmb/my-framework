package pmb.my.starter.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pmb.my.starter.exception.MinorException;

/**
 * Handles configuration properties file.
 *
 * @see MyConstant#CONFIGURATION_FILENAME
 */
public final class MyProperties {

    private static final Logger LOG = LogManager.getLogger(MyProperties.class);
    private static final String LEVEL_KEY = "level";

    /**
     * {@link Properties} contains application properties.
     */
    private static Properties prop;

    private MyProperties() {
        throw new AssertionError("Must not be used");
    }

    /**
     * Loads properties from configuration file.
     */
    private static void load() {
        LOG.debug("Start loadProperties");
        try (InputStream input = new FileInputStream(MyConstant.getConfigPath())) {
            prop = new Properties();
            prop.load(input);
            get(LEVEL_KEY).ifPresent(VariousUtils::setLogLevel);
        } catch (IOException e) {
            throw new MinorException("Error when importing properties", e);
        }
        LOG.debug("End loadProperties");
    }

    /**
     * Refreshs the property file, useful if it changes.
     *
     * @return true if succeed, false otherwise
     * @see #load()
     */
    public static boolean reload() {
        load();
        return prop != null;
    }

    /**
     * Recover a specific property from property file.
     *
     * @param key id of the wanted property
     * @return the property sought
     */
    public static Optional<String> get(String key) {
        checkIfLoaded();
        return Optional.ofNullable(prop.getProperty(key)).filter(StringUtils::isNotBlank);
    }

    /**
     * Recover a specific property from property file, if not found returns default
     * given value.
     *
     * @param key          id of the wanted property
     * @param defaultValue value to return if not found
     * @return the property's value or the default one
     * @see #get(String)
     */
    public static String getOrDefault(String key, String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    /**
     * Sets a specific property's value.
     *
     * @param key   id of the property
     * @param value to set
     */
    public static void set(String key, String value) {
        checkIfLoaded();
        prop.setProperty(key, value);
    }

    /**
     * Saves properties in configuration file.
     */
    public static void save() {
        try (FileOutputStream configFile = new FileOutputStream(MyConstant.getConfigPath())) {
            prop.store(configFile, null);
        } catch (IOException e) {
            throw new MinorException("Error when saving properties", e);
        }
    }

    private static void checkIfLoaded() {
        if (prop == null) {
            load();
        }
        if (prop == null) {
            throw new MinorException("Unable to load properties");
        }
    }
}
