package pmb.my.starter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
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

    /**
     * Configuration file path.
     */
    private static String configPath;

    private MyProperties() {
        throw new AssertionError("Must not be used");
    }

    /**
     * Loads properties from configuration file specified by {@link MyProperties#configPath}. If not found or not defined uses the configuration file
     * in resources.
     */
    private static void load() {
        LOG.debug("Start loadProperties");
        Optional<File> config = Optional.ofNullable(configPath).map(Path::of).map(Path::toFile).filter(File::exists);
        try (InputStream in = config.isPresent() ? new FileInputStream(config.get())
                                                 : MyConstant.getResourceAsStream(MyConstant.CONFIGURATION_FILENAME)) {
            prop = new Properties();
            prop.load(in);
            get(LEVEL_KEY).ifPresent(VariousUtils::setLogLevel);
        } catch (IOException e) {
            throw new MinorException("Error when importing properties", e);
        }
        LOG.debug("End loadProperties");
    }

    /**
     * Refreshes the property file, useful if it changes.
     *
     * @return true if succeed, false otherwise
     * @see #load()
     */
    public static boolean reload() {
        load();
        return prop != null;
    }

    /**
     * Recover a specific property from properties file.
     *
     * @param key id of the wanted property
     * @return the property sought
     */
    public static Optional<String> get(String key) {
        checkIfLoaded();
        return Optional.ofNullable(prop.getProperty(key)).filter(StringUtils::isNotBlank);
    }

    /**
     * Recover a specific property from property file, if not found returns default given value.
     *
     * @param key id of the wanted property
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
     * @param key id of the property
     * @param value to set
     */
    public static void set(String key, String value) {
        checkIfLoaded();
        prop.setProperty(key, value);
        if (StringUtils.equals(key, LEVEL_KEY)) {
            VariousUtils.setLogLevel(value);
        }
    }

    /**
     * Saves properties in configuration file.
     */
    public static void save() {
        try (FileOutputStream out = new FileOutputStream(Optional.ofNullable(configPath).orElse(MyConstant.getConfigPath()), false)) {
            prop.store(out, null);
        } catch (IOException e) {
            throw new MinorException("Error when saving properties", e);
        }
    }

    /**
     * Defines a configuration file overwriting the default one in resources directory and reloads properties.
     *
     * @param configPath a path
     */
    public static void setConfigPath(String configPath) {
        MyProperties.configPath = configPath;
        reload();
    }

    /**
     * Loads properties if not loaded yet.
     */
    private static void checkIfLoaded() {
        if (prop == null) {
            load();
        }
        if (prop == null) {
            throw new MinorException("Unable to load properties");
        }
    }

}
