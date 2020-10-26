package pmb.my.starter.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import pmb.my.starter.exception.MajorException;
import pmb.my.starter.exception.MinorException;

/**
 * Utility class for handling files.
 */
public final class MyFileUtils {

    private static final Logger LOG = LogManager.getLogger(MyFileUtils.class);

    private MyFileUtils() {
        throw new AssertionError("Must not be used");
    }

    /**
     * Crée le dossier si il n'existe pas.
     *
     * @param nomDir le chemin du dossier
     */
    public static void createFolderIfNotExists(String nomDir) {
        File dir = new File(nomDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * Recovers the list of files contained in a folder.
     *
     * @param folder directory containing files
     * @param extension extension of files to research
     * @param recursive if the search is recursive or not
     * @return a list of files
     */
    public static List<File> listFilesInFolder(final File folder, String extension, boolean recursive) {
        List<File> result = new ArrayList<>();
        listFilesForFolder(folder, result, List.of(extension), recursive);
        return result;
    }

    /**
     * Recovers the list of files contained in a folder.
     *
     * @param folder directory containing files
     * @param extensions list of extension of files to research
     * @param recursive if the search is recursive or not
     * @return a list of files
     */
    public static List<File> listFilesInFolder(final File folder, List<String> extensions, boolean recursive) {
        List<File> result = new ArrayList<>();
        listFilesForFolder(folder, result, extensions, recursive);
        return result;
    }

    private static void listFilesForFolder(final File folder, List<File> files, List<String> extensions, boolean recursive) {
        if (!folder.isDirectory()) {
            files.add(folder);
            return;
        }
        for (final File fileEntry : folder.listFiles()) {
            if (recursive && fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, files, extensions, recursive);
            } else if (extensions.stream().anyMatch(extension -> StringUtils.endsWithIgnoreCase(fileEntry.getName(), extension))) {
                files.add(fileEntry);
            }
        }
    }

    /**
     * Export an object to json in a file.
     *
     * @param o        the object to export
     * @param filePath the absolute path of the file
     */
    public static void exportJsonInFile(Object o, String filePath) {
        LOG.debug("Start exportJsonInFile");
        String json = "";
        try {
            json = VariousUtils.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            LOG.error("Error when converting object to json", e);
        }
        writeFile(filePath, List.of(json));
        LOG.debug("End exportJsonInFile");
    }

    /**
     * Retourne la première ligne du fichier donné.
     *
     * @param filePath le chemin absolu du fichier
     * @return la 1ère ligne
     */
    public static String readFirstLine(String filePath) {
        return MyFileUtils.readFile(filePath).stream().limit(1).reduce("", (a, b) -> b);
    }

    /**
     * Recovers the creation date of the given file.
     *
     * @param file the file of which the creation date is wanted
     * @return the creation date, now if error
     */
    public static LocalDateTime getCreationDate(File file) {
        LOG.debug("Start getCreationDate");
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            LOG.error("Impossible de récupérer la date de création de {}", file.getAbsolutePath(), e);
        }
        if (attr == null) {
            return LocalDateTime.now();
        }
        LocalDateTime creationDate = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
        LOG.debug("End getCreationDate");
        return creationDate;
    }

    /**
     * Zip the given file.
     *
     * @param file to zip
     * @return {@link ByteArrayOutputStream} the files zipped
     * @throws MajorException if something went wrong
     */
    public static File zipFile(File file) throws MajorException {
        LOG.debug("Start zipFiles");
        String zipName = file.getParent() + MyConstant.FS
                + StringUtils.substringBeforeLast(file.getName(), MyConstant.DOT) + ".zip";
        try (FileOutputStream fos = new FileOutputStream(zipName);
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        } catch (IOException e) {
            throw new MajorException("Exception thrown when zipping file: " + file.getName(), e);
        }
        LOG.debug("End zipFiles");
        return new File(zipName);
    }

    /**
     * Reads completely the given file.
     *
     * @param file        to read
     * @param charsetName encoding
     * @return a list of String
     */
    public static List<String> readFile(File file, String charsetName) {
        try (Stream<String> lines = Files.lines(file.toPath(), Charset.forName(charsetName))) {
            return lines.collect(Collectors.toList());
        } catch (IOException e) {
            throw new MinorException("Error when reading file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Reads completely the given file with {@code ANSI} default encoding.
     *
     * @param file path of the file to read
     * @return a list of String
     */
    public static List<String> readFile(String file) {
        return readFile(new File(file), MyConstant.ANSI_ENCODING);
    }

    /**
     * Reads completely the given file with {@code ANSI} default encoding.
     *
     * @param file to read
     * @return a list of String
     */
    public static List<String> readFile(File file) {
        return readFile(file, MyConstant.ANSI_ENCODING);
    }

    /**
     * Writes in given file the given content.
     *
     * @param file        to write into
     * @param lines       content to write
     * @param charsetName encoding
     */
    public static void writeFile(File file, List<String> lines, String charsetName) {
        try {
            Files.write(file.toPath(), lines, Charset.forName(charsetName));
        } catch (IOException e) {
            throw new MinorException("Error when writing in file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Writes in given file the given content with {@code ANSI} default encoding.
     *
     * @param file  path of the file to write into
     * @param lines content to write
     */
    public static void writeFile(String file, List<String> lines) {
        writeFile(new File(file), lines, MyConstant.ANSI_ENCODING);
    }

    /**
     * Writes in given file the given content with {@code ANSI} default encoding.
     *
     * @param file  to write into
     * @param lines content to write
     */
    public static void writeFile(File file, List<String> lines) {
        writeFile(file, lines, MyConstant.ANSI_ENCODING);
    }
}
