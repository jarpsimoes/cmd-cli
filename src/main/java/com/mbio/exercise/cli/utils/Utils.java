package com.mbio.exercise.cli.utils;

import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.operations.Wrapper;

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
}
