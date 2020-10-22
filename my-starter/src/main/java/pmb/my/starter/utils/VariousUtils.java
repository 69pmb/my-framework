package pmb.my.starter.utils;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import pmb.my.starter.exception.MinorException;

/**
 * Utility class of various methods.
 *
 */
public final class VariousUtils {

    public static final Comparator<String> comparePercentage = (String s1, String s2) -> Double
            .valueOf(Double.parseDouble(RegExUtils.replaceAll(StringUtils.substringBefore(s1, "%"), ",", ".")))
            .compareTo(Double.valueOf(
                    Double.parseDouble(RegExUtils.replaceAll(StringUtils.substringBefore(s2, "%"), ",", "."))));

    public static final Comparator<String> compareDouble = (String s1, String s2) -> Double
            .valueOf(Double.parseDouble(RegExUtils.replaceAll(s1, ",", ".")))
            .compareTo(Double.valueOf(Double.parseDouble(RegExUtils.replaceAll(s2, ",", "."))));

    public static final Comparator<String> compareInteger = (String s1, String s2) -> {
        s1 = StringUtils.isBlank(s1) ? "0" : s1;
        s2 = StringUtils.isBlank(s2) ? "0" : s2;
        return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
    };

    private static ObjectMapper objectMapper;
    private static final Logger LOG = LogManager.getLogger(VariousUtils.class);

    private VariousUtils() {
        throw new AssertionError("Must not be used");
    }

    /**
     * Gets the object mapper parser initialized with appropriated modules.
     *
     * @return the object mapper
     */
    public static synchronized ObjectMapper getObjectMapper() {
        if (null == objectMapper) {
            objectMapper = new ObjectMapper().registerModule(new ParameterNamesModule())
                    .registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
        }
        return objectMapper;
    }

    /**
     * Converts to json the given object.
     *
     * @param o the object to format
     * @return a string representing the given object in json format
     * @throws JsonProcessingException if the process fails
     */
    public static String writeValueAsString(Object o) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(o);
    }

    /**
     * Parse a string representing a {@code Map<String, T>}.
     *
     * @param <T> the type of map values
     * @param content the string to parse
     * @return the map parsed
     * @throws IOException if parser fails
     */
    public static <T> Map<String, T> readValueAsMap(String content) throws IOException {
        return getObjectMapper().readValue(content, new TypeReference<Map<String, T>>() {});
    }

    /**
     * Copy in the clipboard the given text.
     *
     * @param text the text to put in the clipboard
     */
    public static void clipBoardAction(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    /**
     * Clean given line with given rules.
     *
     * @param line the line to clean
     * @param entrySet key: character to replace, value: character to replace with
     * @return the line cleaned
     */
    public static String cleanLine(String line, Set<Entry<String, String>> entrySet) {
        for (Entry<String, String> entry : entrySet) {
            if (StringUtils.containsIgnoreCase(line, entry.getKey())) {
                line = StringUtils.replaceIgnoreCase(line, entry.getKey(), entry.getValue());
            }
        }
        return line;
    }

    /**
     * Retourne la date à l'instant de l'appel.
     *
     * @return la date au format dd-MM-yyyy HH-mm-ss
     */
    public static String dateNow() {
        Calendar greg = new GregorianCalendar();
        Date date = greg.getTime();
        return new SimpleDateFormat("yyyy-MM-dd HH-mm").format(date);
    }

    /**
     * Returns the current time, only.
     *
     * @return format: {@code hour:minute:second}
     */
    public static String getCurrentTime() {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now());
    }

    /**
     * Sets global log level.
     * @param level the wanted level
     * @see Level
     */
    public static void setLogLevel(String level) {
        if (StringUtils.isNotBlank(level)) {
            Configurator.setLevel(MyConstant.PACKAGE_NAME, Level.valueOf(level));
        }
    }

    /**
     * Distinct and collect to String array a given stream of String.
     *
     * @param stream a stream of String
     * @return an array of String
     */
    public static String[] distinctStreamToArray(Stream<String> stream) {
        return stream.distinct().toArray(String[]::new);
    }

    /**
     * When given values of a enum, project and transform to array of String.
     *
     * @param <T> type of the enum
     * @param values all values of the enum
     * @param projection to get enum value
     * @return array of String
     */
    public static <T> String[] getEnumValues(T[] values, Function<T, String> projection) {
        return Arrays.stream(values).map(projection).toArray(String[]::new);
    }

    /**
     * Calculates the median.
     *
     * @param numArray a list of number
     * @return the median calculated
     */
    public static Double median(List<BigDecimal> numArray) {
        if (numArray.isEmpty()) {
            return 0D;
        }
        Collections.sort(numArray);
        double median;
        int halfSize = numArray.size() / 2;
        if (numArray.size() % 2 == 0) {
            median = numArray.get(halfSize).add(numArray.get(halfSize - 1)).divide(BigDecimal.valueOf(2D)).doubleValue();
        } else {
            median = numArray.get(halfSize).doubleValue();
        }
        return median;
    }

    /**
     * Calculates the standard deviation.
     *
     * @param numArray a list of number
     * @param average mean
     * @param count size of the list
     * @return the standard deviation calculated
     */
    public static Double calculateSD(List<Double> numArray, Double average, long count) {
        double standardDeviation = 0.0;
        for (Double num : numArray) {
            standardDeviation += Math.pow(num - average, 2);
        }

        return Math.sqrt(standardDeviation / count);
    }

    /**
     * Gets a random uuid without hyphens.
     * @return an uuid
     * @see UUID
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Joins a list of uuids to a single {@link String}.
     * @param uuids list of uuids
     * @return a string comma separated
     */
    public static String uuidsToString(List<String> uuids) {
        return Optional.ofNullable(uuids).map(list -> StringUtils.join(list, ",")).orElse("");
    }

    /**
     * Splits a {@link String} containing uuids comma separated to a list.
     * @param uuids the string to split
     * @return a list of uuid
     */
    public static List<String> stringToUuids(String uuids) {
        return Optional.ofNullable(uuids).map(list -> new LinkedList<>(Arrays.asList(StringUtils.split(list, ","))))
                .orElse(new LinkedList<>());
    }

    /**
     * Remove all punctuation and lower the case of the given text. The string is return if it's only made of punctuation.
     *
     * @param text The String to compress
     * @return a string with no punctuation
     */
    public static String removePunctuation(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        String trim = StringUtils.trim(text);
        String res = MyConstant.PATTERN_PUNCTUATION.matcher(trim).replaceAll("").toLowerCase();
        return StringUtils.isBlank(res) ? trim : res;
    }

    /**
     * Encodes given url.
     * @param url url to encode
     * @return encoded url
     */
    public static String urlEncode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    /**
     * Opens given url in default browser.
     * @param url url to open
     */
    public static void openUrl(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                throw new MinorException("Error when opening url: " + url, e);
            }
        } else {
            LOG.warn("Desktop not supported");
        }
    }

    /**
     * Converts a string to a number.
     * @param <T> extending {@link Number}
     * @param string to parse
     * @param type wanted return type
     * @return value converted, 0 if a {@link NumberFormatException} is thrown
     */
    public static <T extends Number> T parseStringToNumber(String string, Class<T> type) {
        Map<Class<? extends Number>, Function<String, Number>> map = Map.of(Long.class, Long::valueOf, Integer.class,
                Integer::valueOf, Float.class, Float::valueOf, Double.class, Double::valueOf);
        Function<String, Number> converter = map.entrySet().stream().filter(e -> type.isNestmateOf(e.getKey()))
                .findFirst().map(Entry::getValue)
                .orElseThrow(() -> new MinorException("Incorrect given type: " + type));
        try {
            return type.cast(converter.apply(string));
        } catch (NumberFormatException e) {
            LOG.warn("Can't parse string {}", string);
            return type.cast(converter.apply("0"));
        }
    }
}
