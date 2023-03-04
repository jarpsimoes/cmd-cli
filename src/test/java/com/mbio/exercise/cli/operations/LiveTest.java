package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.datastore.impls.DatastoreImpl;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.utils.FileTestUtils;
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
public class LiveTest {

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
    static Datastore datastore;

    @BeforeEach
    public void setup() throws IOException {
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
    public void testLiveCommandWithParameters(QuarkusMainLauncher launcher)
            throws IOException, InterruptedException {

        LaunchResult result = launcher.launch("live", "-u", "https://www.google.com", "-u",
                "https://www.yahoo.com", "-l", "2");
        assert result.exitCode() == 0;
        datastore = new DatastoreImpl();
        List<HttpResponseData> results = datastore.getAllHistory();

        assert results.size() == 4;

        assert datastore.getHistoryByURL("https://www.google.com").size() == 2;
        assert datastore.getHistoryByURL("https://www.yahoo.com").size() == 2;
    }

    @Test
    @Order(2)
    public void testLiveCommandWithFile(QuarkusMainLauncher launcher)
            throws IOException, InterruptedException {
        File fileList1 = new File("urls.txt");

        if(fileList1.exists()) {
            fileList1.delete();
        }

        File fileList2 = new File("urls_1.txt");
        if(fileList2.exists()) {
            fileList2.delete();
        }

        FileTestUtils.createFileUrlList("urls.txt", urls_first);
        FileTestUtils.createFileUrlList("urls_1.txt", urls_second);

        LaunchResult result = launcher.launch("live", "-U", "urls.txt", "-U",
                "urls_1.txt", "-l", "2");

        assert result.exitCode() == 0;
        datastore = new DatastoreImpl();
        List<HttpResponseData> results = datastore.getAllHistory();

        assert results.size() == 12;

        Arrays.stream(urls_first).toList().forEach(url -> {

            try {
                assert datastore.getHistoryByURL(url).size() == 2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Arrays.stream(urls_second).toList().forEach(url -> {

            try {
                assert datastore.getHistoryByURL(url).size() == 2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if(fileList1.exists()) {
            fileList1.delete();
        }
        if(fileList2.exists()) {
            fileList2.delete();
        }


    }

    @Test
    @Order(3)
    public void testLiveCommandWithBoth(QuarkusMainLauncher launcher)
            throws IOException, InterruptedException {
        File fileList1 = new File("urls.txt");

        if(fileList1.exists()) {
            fileList1.delete();
        }

        File fileList2 = new File("urls_1.txt");
        if(fileList2.exists()) {
            fileList2.delete();
        }

        FileTestUtils.createFileUrlList("urls.txt", urls_first);
        FileTestUtils.createFileUrlList("urls_1.txt", urls_second);

        LaunchResult result =
                launcher.launch("live", "-U", "urls.txt", "-U",
                "urls_1.txt", "-u", "https://www.facebook.com/",
                        "-u", "https://youtube.com", "-l", "2");

        assert result.exitCode() == 0;


        datastore = new DatastoreImpl();
        List<HttpResponseData> results = datastore.getAllHistory();

        assert results.size() > 12;

        Arrays.stream(urls_first).toList().forEach(url -> {

            try {
                assert datastore.getHistoryByURL(url).size() == 2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Arrays.stream(urls_second).toList().forEach(url -> {

            try {
                assert datastore.getHistoryByURL(url).size() == 2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if(fileList1.exists()) {
            fileList1.delete();
        }
        if(fileList2.exists()) {
            fileList2.delete();
        }
    }
}
