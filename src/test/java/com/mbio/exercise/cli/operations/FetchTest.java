package com.mbio.exercise.cli.operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.utils.FileTestUtils;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

@QuarkusMainTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FetchTest {

    ObjectMapper mapper = new ObjectMapper();
    static Logger logger = LoggerFactory.getLogger(FetchTest.class);
    @Inject Datastore datastore;
    static String[] urls_second = {
            "https://www.google.com",
            "https://www.yahoo.com",
            "https://www.bing.com"
    };
    static String[] urls_first = {
            "https://www.noticiasaominuto.com/",
            "https://www.microsoft.com/",
            "https://www.mercedes-benz.io/"
    };
    @BeforeEach
    public void setup() {
        File file = new File("./.mbio_data");

        if(file.exists()) {
            FileTestUtils.deleteDirectory(file);
        }
    }
    @AfterEach
    public void shutdown() {
        File file = new File("./.mbio_data");

        if(file.exists()) {
            FileTestUtils.deleteDirectory(file);
        }
    }
    @Test
    @Order(1)
    public void testFetchCommandWithParameters(QuarkusMainLauncher launcher) {
        File file = new File("./.mbio_data");

        LaunchResult result = launcher.launch("fetch", "-u", "https://www.google.com", "-u", "https://www.yahoo.com");
        assert result.exitCode() == 0;

        result = launcher.launch("fetch", "-u", "https://www.bing.com");
        assert result.exitCode() == 0;

        result = launcher.launch("fetch", "-u", "htt//www.google.com");
        assert result.exitCode() != 0;

        assert file.exists();

        result = launcher.launch("fetch", "-u", "https://www.google.com", "-u", "https://www.yahoo.com", "-o", "output.out");

        assert result.exitCode() == 0;

        File outputFile0 = new File("0_output.out");
        assert outputFile0.exists();

        File outputFile1 = new File("1_output.out");
        assert outputFile1.exists();

        assert outputFile0.delete() && outputFile1.delete();

    }

    @Test
    @Order(2)
    public void testFetchCommandWithFile(QuarkusMainLauncher launcher) throws IOException {

        File file = new File("./.mbio_data");
        File fileList1 = new File("urls.txt");
        if(fileList1.exists()) {
            fileList1.delete();
        }
        File fileList2 = new File("urls_1.txt");
        if(fileList2.exists()) {
            fileList2.delete();
        }

        LaunchResult result = launcher.launch("fetch", "-U", "urls.txt");
        assert result.exitCode() != 0;

        FileTestUtils.createFileUrlList("urls.txt", urls_first);
        result = launcher.launch("fetch", "-U", "urls.txt");
        assert result.exitCode() == 0;

        FileTestUtils.createFileUrlList("urls_1.txt", urls_second);
        result = launcher.launch("fetch", "-U", "urls_1.txt", "-U", "urls.txt");
        assert result.exitCode() == 0;

        assert file.exists();


        if(fileList1.exists()) {
            fileList1.delete();
        }
        if(fileList2.exists()) {
            fileList2.delete();
        }

    }

}
