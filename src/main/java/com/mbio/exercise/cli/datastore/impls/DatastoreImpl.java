package com.mbio.exercise.cli.datastore.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.operations.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class DatastoreImpl implements Datastore {
    Logger logger = LoggerFactory.getLogger(DatastoreImpl.class);
    List<HttpResponseData> history = new ArrayList<>();
    private static final String DATASTORE_FOLDER = Wrapper.datastore;
    private static final String DATASTORE_FILE = DATASTORE_FOLDER + File.separator + "index.json";
    private static final String DATASTORE_CONTENT = DATASTORE_FOLDER + File.separator + "contents";
    public enum BackupType {
        TXT,
        JSON,
        CSV
    }
    public DatastoreImpl() throws IOException {
        connectDatastore();
    }

    @Override public HttpResponseData addNew(final HttpResponseData data) throws IOException {

        String uuid = UUID.randomUUID().toString();

        data.setContentFile(uuid);
        addContent(data);
        data.setContent(null);

        history.add(data);
        flushDatastore();

        return data;
    }

    @Override public List<HttpResponseData> getAllHistory() throws IOException {

        AtomicBoolean fail = new AtomicBoolean(false);

        history.forEach(h -> {
            try {
                h.setContent(loadContent(h.getContentFile()));
            } catch (IOException e) {
                logger.error("Error while loading content file: {}", h.getContentFile());
                fail.set(true);
            }
        });

        if(fail.get()) throw new IOException("Error while loading content file(s)");

        return history;
    }

    @Override public List<HttpResponseData> getHistoryByURL(String url) throws IOException {

        List<HttpResponseData> result = new ArrayList<>();
        AtomicBoolean fail = new AtomicBoolean(false);
        history.forEach(h -> {
            if(h.getUrl().equals(url)) {

                try {
                    h.setContent(loadContent(h.getContentFile()));
                    result.add(h);
                } catch (IOException e) {
                    logger.error("Error while loading content file: {}", h.getContentFile());
                    fail.set(true);
                }

            }
        });

        if(fail.get()) throw new IOException("Error while loading content file(s)");
        return result;
    }

    @Override public void backup(String filePath, BackupType backupType) throws IOException {
        List<HttpResponseData> allData = getAllHistory();

        writeBackUp(filePath, allData, backupType);
    }

    @Override public void restore(List<HttpResponseData> data) {

    }
    private void addContent(HttpResponseData data) throws IOException {
        File contentFile = new File(DATASTORE_CONTENT + File.separator + data.getContentFile());
        if(!contentFile.exists()) {
            logger.info("Content file not found: {}", contentFile.getAbsolutePath());
            logger.info("Creating content file: {}", contentFile.getAbsolutePath());

            if(!contentFile.createNewFile()) throw new IOException("Error while creating content file: " + contentFile.getAbsolutePath());
        } else {
            logger.info("Content file found: {}", contentFile.getAbsolutePath());
        }

        FileWriter writer = new FileWriter(contentFile);
        writer.write(data.getContent());
        writer.flush();
        writer.close();
    }
    private void connectDatastore() throws IOException {

        File mainDirectory = new File(DATASTORE_FOLDER);

        if(!mainDirectory.exists()) {
            logger.info("Main directory not found: {}", mainDirectory.getAbsolutePath());
            logger.info("Creating main directory: {}", mainDirectory.getAbsolutePath());

            if(!mainDirectory.mkdir()) throw new IOException("Error while creating main directory: " + mainDirectory.getAbsolutePath());
        }

        File indexJson = new File(DATASTORE_FILE);

        if(!indexJson.exists()) {
            logger.info("Index file not found: {}", indexJson.getAbsolutePath());
            logger.info("Creating index file: {}", indexJson.getAbsolutePath());

            if(!indexJson.createNewFile()) throw new IOException("Error while creating index file: " + indexJson.getAbsolutePath());

            FileWriter writer = new FileWriter(indexJson);
            writer.write("[]");
            writer.flush();
            writer.close();

        } else {
            logger.info("Index file found: {}", indexJson.getAbsolutePath());

            loadDatastore(indexJson);
        }

        File contentsDirectory = new File(DATASTORE_CONTENT);

        if(!contentsDirectory.exists()) {
            logger.info("Contents directory not found: {}", contentsDirectory.getAbsolutePath());
            logger.info("Creating contents directory: {}", contentsDirectory.getAbsolutePath());

            if(!contentsDirectory.mkdir()) throw new IOException("Error while creating contents directory: " + contentsDirectory.getAbsolutePath());
        }

    }
    private void loadDatastore(File file) throws IOException {

        if(!file.exists()) throw new IOException("File not found: " + file.getAbsolutePath());

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        StringBuilder builder = new StringBuilder();

        while((line = reader.readLine()) != null) {
            builder.append(line);
        }

        ObjectMapper mapper = new ObjectMapper();
        HttpResponseData[] data = mapper.readValue(builder.toString(), HttpResponseData[].class);
        history.addAll(Arrays.stream(data).toList());

    }
    private void flushDatastore() throws IOException {

        File mainDirectory = new File(".mbio_data");

        if(!mainDirectory.exists()) throw new IOException("Main directory not found: " + mainDirectory.getAbsolutePath());

        File indexJson = new File(mainDirectory.getAbsolutePath() + File.separator + "index.json");

        if(!indexJson.exists()) throw new IOException("Index file not found: " + indexJson.getAbsolutePath());

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(history);

        FileWriter writer = new FileWriter(indexJson);
        writer.write(json);
        writer.flush();
        writer.close();

    }
    private String loadContent(String filePath) throws IOException {

        File file = new File(DATASTORE_CONTENT + File.separator + filePath);

        if(!file.exists()) throw new IOException("File not found: " + filePath);

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        StringBuilder builder = new StringBuilder();

        while((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();

    }

    private void writeBackUp(String filePath, List<HttpResponseData> data, BackupType type) throws IOException {

        File file = new File(filePath);

        if(file.exists()) throw new IOException("File already exist: " + filePath);

        if(!file.createNewFile()) throw new IOException("Error while creating file: " + filePath);


        BufferedWriter writer = new BufferedWriter(new FileWriter(file));


        AtomicBoolean fail = new AtomicBoolean(false);

        switch (type) {
            case JSON -> {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(data);
                writer.write(json);
                writer.newLine();
            }
            case CSV -> {
                data.forEach(row -> {
                    try {
                        writer.write(String.format("\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"", row.getUrl(), row.getResultCode(), row.getContentFile(), row.getContentType(),
                                row.getContent()));
                        writer.newLine();
                    } catch (IOException e) {
                        logger.error("Error while writing CSV file: {}", e.getMessage());
                        fail.set(true);
                    }

                });
            }
            case TXT -> {
                data.forEach(row -> {
                    try {
                        writer.write(String.format("URL: %s", row.getUrl()));
                        writer.newLine();
                        writer.write(String.format("Result Code: %s", row.getResultCode()));
                        writer.newLine();
                        writer.write(String.format("Content Type: %s", row.getContentType()));
                        writer.newLine();
                        writer.write(String.format("Content: %s", row.getContent()));
                        writer.newLine();

                        writer.write("--------------------------------------------------");
                        writer.newLine();
                    } catch (IOException e) {
                        logger.error("Error while writing TXT file: {}", e.getMessage());
                        fail.set(true);
                    }

                });
            }
        }

        writer.flush();
        writer.close();

        if(fail.get()) throw new IOException("Error while writing file: " + filePath);
    }
}
