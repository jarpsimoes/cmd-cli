package com.mbio.exercise.cli.operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.utils.FileTestUtils;
import com.mbio.exercise.cli.utils.Utils;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@QuarkusMainTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BackupTest {

    @AfterAll
    @BeforeAll
    public static void setup() {
        File file = new File("./.mbio_data");

        if(file.exists()) {
            FileTestUtils.deleteDirectory(file);
        }
    }

    @Test
    public void testBackupCommandWithParameters(QuarkusMainLauncher launcher)
            throws IOException {

        LaunchResult result = launcher.launch("live", "-u", "https://www.google.com", "-u",
                "https://www.yahoo.com", "-l", "2");

        assert result.exitCode() == 0;

        result = launcher.launch("backup", "-o", "test_backup.json", "-t", "JSON");

        assert result.exitCode() == 0;

        ObjectMapper mapper = new ObjectMapper();

        File fileJson = new File("test_backup.json");
        assert fileJson.exists();

        HttpResponseData[] data = mapper.readValue(fileJson, HttpResponseData[].class);

        assert data.length != 0;

        Arrays.stream(data).toList().forEach(r -> {
            assert r.getUrl().contains("https://www.google.com")
                    || r.getUrl().contains("https://www.yahoo.com");

        });

        assert fileJson.delete();

        result = launcher.launch("backup", "-o", "test_backup.csv", "-t", "CSV");

        assert result.exitCode() == 0;

        File fileCsv = new File("test_backup.csv");

        assert fileCsv.exists();

        List<HttpResponseData> dataCsv = Utils.parseCSV("test_backup.csv");

        assert dataCsv.size() != 0;

        dataCsv.forEach(r -> {
            assert r.getUrl().contains("https://www.google.com")
                    || r.getUrl().contains("https://www.yahoo.com");
        });

        assert fileCsv.delete();

        result = launcher.launch("backup", "-o", "test_backup.txt", "-t", "TXT");

        assert result.exitCode() == 0;

        File fileTxt = new File("test_backup.txt");

        assert fileTxt.exists();

        List<HttpResponseData> dataTxt = Utils.parseTXT("test_backup.txt");

        assert dataTxt.size() != 0;

        dataTxt.forEach(r -> {
            assert r.getUrl().contains("https://www.google.com")
                    || r.getUrl().contains("https://www.yahoo.com");
        });

        assert fileTxt.delete();
    }
}
