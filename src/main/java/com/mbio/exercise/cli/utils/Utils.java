package com.mbio.exercise.cli.utils;

import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.operations.Wrapper;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.UnescapedQuoteHandling;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<String> getUrlsFromFile(final String filePath) throws IOException {

        File file = new File(filePath);
        List<String> urls = new ArrayList<>();

        if(file.exists() && file.isFile()) {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                urls.add(line);
            }

            return urls;

        } else {
            throw new FileNotFoundException(String.format("File not found or not a file [%s]", filePath));
        }
    }
    public static HttpResponseData getContent(final URL url) throws IOException {

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
    public static List<HttpResponseData> parseCSV(final String fileName) throws FileNotFoundException {
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


        CsvParser parser = new CsvParser(parserSettings);

        List<String[]> allRows = parser.parseAll(reader);
        List<HttpResponseData> allData = new ArrayList<>();
        allRows.forEach(row -> {
            HttpResponseData httpResponseData = new HttpResponseData();
            httpResponseData.setUrl(row[0]);
            httpResponseData.setResultCode(Integer.parseInt(row[1]));
            httpResponseData.setResponseTime(Long.parseLong(row[2]));
            httpResponseData.setContentType(row[3]);
            httpResponseData.setContent(row[4]);
            allData.add(httpResponseData);
        });

        return allData;
    }
    public static List<HttpResponseData> parseTXT(final String fileName) throws IOException {
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
                entity.setContent(line.substring(8).trim());
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
}
