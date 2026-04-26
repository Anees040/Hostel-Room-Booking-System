package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility helper for text file read and write operations.
 */
public class FileManager {

    /**
     * Writes all lines to a text file.
     *
     * @param filePath target file path
     * @param lines lines to write
     */
    public static void writeToFile(String filePath, List<String> lines) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) {
            ensureDirectoryExists(parent.getPath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line == null ? "" : line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Reads all lines from a text file.
     *
     * @param filePath source file path
     * @return list of lines, empty if missing or unreadable
     */
    public static List<String> readFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }

        return lines;
    }

    /**
     * Ensures a directory exists by creating it if required.
     *
     * @param dirPath directory path
     */
    public static void ensureDirectoryExists(String dirPath) {
        if (dirPath == null || dirPath.trim().isEmpty()) {
            return;
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Could not create directory: " + dirPath);
            }
        }
    }
}
