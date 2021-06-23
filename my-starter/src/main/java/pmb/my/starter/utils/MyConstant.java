package pmb.my.starter.utils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.regex.Pattern;

import pmb.my.starter.exception.MinorException;

/**
 * Constants class.
 *
 */
public final class MyConstant {

    /**
     * File separator.
     */
    public static final String FS = System.getProperty("file.separator");

    /**
     * User directory, launching location of the app.
     */
    public static final String USER_DIRECTORY = System.getProperty("user.dir") + FS;

    /**
     * Chemin des resources de l'application.
     */
    public static final String RESOURCES_DIRECTORY = USER_DIRECTORY + "src" + FS + "main" + FS + "resources" + FS;

    /**
     * Chemin abs du fichier de log.
     */
    public static final String FILE_LOG_PATH = USER_DIRECTORY + "error.log";

    public static final String CONFIGURATION_FILENAME = "config.properties";

    public static final String NEW_LINE = "\r\n";

    public static final String ANSI_ENCODING = "Cp1252";

    public static final Charset ANSI_CHARSET = Charset.forName(MyConstant.ANSI_ENCODING);

    public static final String DOT = ".";

    public static final String QUOTE = "\"";

    public static final String PACKAGE_NAME = "pmb";

    public static final String XML_EXTENSION = ".xml";

    public static final String CSV_EXTENSION = ".csv";

    public static final String TXT_EXTENSION = ".txt";

    private static final String[] FORBIDDEN_CHARACTERS_FILENAME = { "<", ">", ":", "\"", "/", "|", "*", "?" };

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    public static final String REGEX_PUNCTUATION = "\\p{Punct}|\\s";

    public static final Pattern PATTERN_PUNCTUATION = Pattern.compile(REGEX_PUNCTUATION);

    private MyConstant() {
        throw new AssertionError("Must not be used");
    }

    /**
     * Chemin abs du fichier de configuration.
     */
    public static String getConfigPath() {
        return RESOURCES_DIRECTORY + CONFIGURATION_FILENAME;
    }

    /**
     * Opens a resource file a stream.
     *
     * @param path the resource name
     * @return An input stream for reading the resource, if not found throw a {@link MinorException}
     * @see ClassLoader#getResourceAsStream(String)
     */
    public static InputStream getResourceAsStream(String path) {
        return Optional.ofNullable(MyConstant.class.getClassLoader().getResourceAsStream(path))
                .orElseThrow(() -> new MinorException("Can't find property file"));
    }

    /**
     * @return {@link MyConstant#FORBIDDEN_CHARACTERS_FILENAME}
     */
    public static String[] getForbiddenCharactersFilename() {
        return FORBIDDEN_CHARACTERS_FILENAME;
    }

    public static DecimalFormat getDecimalFormat() {
        return DECIMAL_FORMAT;
    }

}
