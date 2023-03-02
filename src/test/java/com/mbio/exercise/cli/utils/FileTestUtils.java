package com.mbio.exercise.cli.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

public class FileTestUtils {
    static Logger logger = LoggerFactory.getLogger(FileTestUtils.class);
    public static boolean deleteDirectory(final File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
    public static void createFileUrlList(final String filePath, final String[] urls) throws IOException {
        File file = new File(filePath);

        if(!file.exists() && file.createNewFile()) {
            FileWriter fileWriter = new FileWriter(file);

            Arrays.stream(urls).allMatch(url -> {

                try {
                    fileWriter.write(url);
                    fileWriter.write(System.lineSeparator());
                    return true;
                } catch (IOException e) {
                    logger.error("Error while writing to file: {}", filePath);
                    logger.error(e.getMessage());
                    return false;
                }
            });

            fileWriter.flush();
            fileWriter.close();
        }
    }
    public static String readFile(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder content = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }

            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
