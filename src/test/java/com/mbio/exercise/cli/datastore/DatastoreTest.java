package com.mbio.exercise.cli.datastore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbio.exercise.cli.datastore.impls.DatastoreImpl;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.utils.FileTestUtils;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatastoreTest {

    static Datastore datastore;
    static ObjectMapper objectMapper = new ObjectMapper();
    @BeforeAll
    public static void setup() throws IOException {
        File file = new File("./.mbio_data");

        if(file.exists()) {
            FileTestUtils.deleteDirectory(file);
        }

        datastore = new DatastoreImpl();
    }
    @AfterAll
    public static void shutdown() {
        File file = new File("./.mbio_data");

        if(file.exists()) {
            FileTestUtils.deleteDirectory(file);
        }

        File fileBackup = new File("./backup.json");

        if(fileBackup.exists()) {
            fileBackup.delete();
        }

        File fileBackupCSV = new File("./backup.csv");

        if(fileBackupCSV.exists()) {
            fileBackupCSV.delete();
        }
    }


    @Test
    @Order(1)
    public void testAddNew() throws IOException {

        assert datastore.getAllHistory().size() == 0;

        HttpResponseData testData = new HttpResponseData();

        testData.setUrl("https://www.google.com");
        testData.setResultCode(200);
        testData.setContent("OK");
        testData.setResponseTime(100);
        testData.setContentType("text/html");

        HttpResponseData result = datastore.addNew(testData);

        assert datastore.getAllHistory().size() == 1;

        File file = new File("./.mbio_data/contents/" + result.getContentFile());

        assert file.exists();

        String fileContent = FileTestUtils.readFile(file);

        assert fileContent.contains("OK");

    }
    @Test
    @Order(2)
    public void testGetAllHistory() throws IOException {

        assert datastore.getAllHistory().size() == 1;

        HttpResponseData testData = new HttpResponseData();

        testData.setUrl("https://www.adadada.com");
        testData.setResultCode(200);
        testData.setContent("OK");
        testData.setResponseTime(100);
        testData.setContentType("text/html");

        HttpResponseData testData1 = new HttpResponseData();

        testData1.setUrl("https://www.adadada.com");
        testData1.setResultCode(200);
        testData1.setContent("OK");
        testData1.setResponseTime(100);
        testData1.setContentType("text/html");

        HttpResponseData result = datastore.addNew(testData);
        HttpResponseData result1 = datastore.addNew(testData1);


        List<HttpResponseData> history = datastore.getAllHistory();
        assert history.size() == 3;

        File file = new File("./.mbio_data/contents/" + result.getContentFile());
        File file1 = new File("./.mbio_data/contents/" + result1.getContentFile());

        assert file.exists();
        assert file1.exists();

        String fileContent = FileTestUtils.readFile(file);

        assert fileContent.contains("OK");

    }
    @Test
    @Order(3)
    public void testGetHistoryByUrl() {
        List<HttpResponseData> listOfResults = datastore.getHistoryByURL("https://www.adadada.com");

        assert listOfResults.size() == 2;

        listOfResults.forEach(result -> {
            assert result.getUrl().equals("https://www.adadada.com");
        });
    }
    @Test
    @Order(4)
    public void testBackup() throws IOException {
        datastore.backup("./backup.json", DatastoreImpl.BackupType.JSON);

        File file = new File("./backup.json");

        assert file.exists();

        String fileContent = FileTestUtils.readFile(file);

        assert fileContent.contains("https://www.google.com");
        assert fileContent.contains("https://www.adadada.com");

        HttpResponseData[] data = objectMapper.readValue(fileContent, HttpResponseData[].class);

        assert data.length == 3;

        datastore.backup("./backup.csv", DatastoreImpl.BackupType.CSV);

        File fileCSV = new File("./backup.csv");

        assert fileCSV.exists();


    }
}
