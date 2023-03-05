package com.mbio.exercise.cli.utils;

import com.mbio.exercise.cli.datastore.obj.FetchUrl;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.operations.Wrapper;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.UnescapedQuoteHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Utils {
    static Logger logger = LoggerFactory.getLogger(Utils.class);
    public static List<FetchUrl> getUrlsFromFile(final String filePath)
            throws IOException {

        File file = new File(filePath);
        List<FetchUrl> urls = new ArrayList<>();

        if(file.exists() && file.isFile()) {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                urls.add(new FetchUrl(new URL(line), file.getName()));
            }

            return urls;

        } else {
            throw new FileNotFoundException(String.format("File not found or not a file [%s]", filePath));
        }
    }
    public static HttpResponseData getContent(final URL url)
            throws IOException {

        HttpResponseData httpResponseData = new HttpResponseData();

        InputStream inputStream;


        long startTime = System.currentTimeMillis();
        if(url != null && url.getProtocol().equals("http")) {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(Wrapper.timeout);
            httpResponseData.setUrl(url.toString());
            inputStream = connection.getInputStream();
            httpResponseData.setResultCode(connection.getResponseCode());
            httpResponseData.setContentType(connection.getContentType());
            httpResponseData.setResponseTime(System.currentTimeMillis() - startTime);

        }else if(url != null && url.getProtocol().equals("https")){

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(Wrapper.timeout);
            httpResponseData.setUrl(url.toString());
            inputStream = connection.getInputStream();
            httpResponseData.setResultCode(connection.getResponseCode());
            httpResponseData.setContentType(connection.getContentType());
            httpResponseData.setResponseTime(System.currentTimeMillis() - startTime);

        }else {
            throw new MalformedURLException(String.format("URL is null or not HTTP protocol [%s]", url));
        }

        if(inputStream == null) {
            throw new IOException(String.format("Input stream is null [%s]", url));
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        httpResponseData.setContent(stringBuilder.toString());

        return httpResponseData;
    }
    public static List<HttpResponseData> parseCSV(final String fileName)
            throws FileNotFoundException {
        File fileCSV = new File(fileName);

        if(!fileCSV.exists() || !fileCSV.isFile()) {
            throw new FileNotFoundException(String.format("File not found or not a file [%s]", fileName));
        }

        Reader reader = new InputStreamReader(new FileInputStream(fileCSV));

        CsvParserSettings parserSettings = new CsvParserSettings();

        parserSettings.setUnescapedQuoteHandling(UnescapedQuoteHandling.BACK_TO_DELIMITER);

        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setHeaderExtractionEnabled(false);
        parserSettings.setDelimiterDetectionEnabled(true);
        parserSettings.setQuoteDetectionEnabled(true);
        parserSettings.setMaxCharsPerColumn(1000000);

        CsvParser parser = new CsvParser(parserSettings);

        List<String[]> allRows = parser.parseAll(reader);
        List<HttpResponseData> allData = new ArrayList<>();
        allRows.forEach(row -> {
            String contentDecoded = Arrays.toString(
                    Base64.getDecoder().decode(row[4]));

            HttpResponseData httpResponseData = new HttpResponseData();
            httpResponseData.setUrl(row[0]);
            httpResponseData.setResultCode(Integer.parseInt(row[1]));
            httpResponseData.setResponseTime(Long.parseLong(row[2]));
            httpResponseData.setContentType(row[3]);
            httpResponseData.setContent(contentDecoded);
            allData.add(httpResponseData);
        });

        return allData;
    }
    public static List<HttpResponseData> parseTXT(final String fileName)
            throws IOException {
        File file = new File(fileName);

        if(!file.exists() || !file.isFile()) {
            throw new FileNotFoundException(String.format("File not found or not a file [%s]", fileName));
        }

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;

        List<HttpResponseData> result = new ArrayList<>();

        boolean loadingEntity = false;
        HttpResponseData entity = null;
        boolean startLongText = false;

        while ((line = bufferedReader.readLine()) != null) {

            if(!loadingEntity) {
                entity = new HttpResponseData();
                loadingEntity = true;
            }

            if(line.startsWith("URL:")){
                entity.setUrl(line.substring(4).trim());
            } else
            if(line.startsWith("Result code:")){
                entity.setResultCode(Integer.parseInt(line.substring(12).trim()));
            } else
            if(line.startsWith("Response time:")){
                entity.setResponseTime(Long.parseLong(line.substring(14).trim()));
            } else
            if(line.startsWith("Content type:")){
                entity.setContentType(line.substring(13).trim());
            } else
            if(line.startsWith("Content:")){
                String contentDecoded = Arrays.toString(
                        Base64.getDecoder().decode(line.substring(8).trim()));

                entity.setContent(contentDecoded);
                startLongText = true;
            } else {
                if(startLongText && line.equals("--------------------------------------------------")) {
                    startLongText = false;
                    loadingEntity = false;

                    result.add(entity);

                }
                if(startLongText) {
                    entity.setContent(entity.getContent() + line);
                }
            }


        }

        return result;
    }
    public static List<FetchUrl> mergeUrlsAndFileUrls(final String[] urls,
            final String[] filenames) throws IOException {
        List<FetchUrl> result = new ArrayList<>();

        AtomicBoolean fail = new AtomicBoolean(false);

        if(urls != null && urls.length > 0) {

            Arrays.stream(urls).forEach(u -> {
                try {
                    result.add(new FetchUrl(new URL(u), "parameter"));
                } catch (MalformedURLException e) {
                    logger.error("Error while parsing URL", e);
                    fail.set(true);
                }
            });

        }

        if(filenames != null && filenames.length > 0) {
            Arrays.stream(filenames).toList().forEach(f -> {
                try {
                    result.addAll(Utils.getUrlsFromFile(f));
                } catch (IOException e) {
                    logger.error("Error while reading file", e);
                    fail.set(true);
                }
            });
        }

        if(fail.get()) {
            throw new IOException("Error while reading file");
        }

        return result;
    }
    public static void writeOutput(String content, String filePath)
            throws IOException {
        File file = new File(filePath);

        if(file.exists()) {
            logger.error("File already exists: {}", filePath);
            throw new IOException("File already exists: " + filePath);
        }

        if(!file.createNewFile()) {
            logger.error("Error while creating file: {}", filePath);
            throw new IOException("Error while creating file: " + filePath);
        }

        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.flush();
        writer.close();
    }
}
