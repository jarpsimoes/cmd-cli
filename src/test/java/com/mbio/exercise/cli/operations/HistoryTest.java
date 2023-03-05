package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.utils.FileTestUtils;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;

@QuarkusMainTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HistoryTest {

    static String[] urls_first = {
            "https://www.noticiasaominuto.com/",
            "https://www.microsoft.com/",
            "https://www.mercedes-benz.io/"
    };

    @BeforeAll
    public static void setup() {
        File file = new File("./.mbio_data");

        if(file.exists()) {
            FileTestUtils.deleteDirectory(file);
        }
    }
    @BeforeEach
    @AfterEach
    public void setupTest() {
        File fileList1 = new File("urls.txt");
        if(fileList1.exists()) {
            fileList1.delete();
        }
    }
    @AfterAll
    public static void shutdown() {
        File file = new File("./.mbio_data");

        if(file.exists()) {
            FileTestUtils.deleteDirectory(file);
        }
    }
    @Test
    @Order(1)
    public void testHistoryOutput(QuarkusMainLauncher launcher)
            throws IOException {
        FileTestUtils.createFileUrlList("urls.txt", urls_first);

        LaunchResult result = launcher.launch("fetch", "-U", "urls.txt",
                "-u" , "https://google.pt", "-u", "https://www.abola.pt");

        assert result.exitCode() == 0;

        result = launcher.launch("history", "--all");

        assert result.exitCode() == 0;
        assert result.getOutput().contains("https://google.pt");
        assert result.getOutput().contains("https://www.abola.pt");
        assert result.getOutput().contains("https://www.microsoft.com");

        result = launcher.launch("history", "--url",
                "https://google.pt");

        assert result.exitCode() == 0;
        assert result.getOutput().contains("https://google.pt");

        result = launcher.launch("history", "-g", "urls.txt");

        assert result.exitCode() == 0;
        assert result.getOutput().contains("https://www.noticiasaominuto.com/");
        assert result.getOutput().contains("https://www.microsoft.com/");
        assert result.getOutput().contains("https://www.mercedes-benz.io/");

    }
    @Test
    @Order(1)
    public void testHistoryOutputFile(QuarkusMainLauncher launcher)
            throws IOException {
        FileTestUtils.createFileUrlList("urls.txt", urls_first);

        LaunchResult result = launcher.launch("fetch", "-U", "urls.txt",
                "-u" , "https://google.pt", "-u", "https://www.abola.pt");

        assert result.exitCode() == 0;

        result = launcher.launch("history", "--all", "-c", "-o",
                "output.txt");

        assert result.exitCode() == 0;


        File file = new File("output.txt");
        assert file.exists();

        String content = FileTestUtils.readFile(file);


        assert content.contains("https://google.pt");
        assert content.contains("https://www.abola.pt");
        assert content.contains("https://www.microsoft.com");

        if(file.exists()) {
            file.delete();
        }



    }


}
