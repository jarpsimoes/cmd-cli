package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.utils.FileTestUtils;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

@QuarkusMainTest
public class RestoreTest {

    static String[] urls_first = {
            "https://www.noticiasaominuto.com/",
            "https://www.microsoft.com/",
            "https://www.mercedes-benz.io/"
    };

    @BeforeAll
    @AfterAll
    public static void setup() {
        File file = new File("./.mbio_data");

        if(file.exists()) {
            FileTestUtils.deleteDirectory(file);
        }

    }

    @Test
    public void testRestoreCommand(QuarkusMainLauncher launcher)
            throws IOException {

        File fileList1 = new File("urls.txt");

        if(fileList1.exists()) {
            fileList1.delete();
        }

        FileTestUtils.createFileUrlList("urls.txt", urls_first);

        LaunchResult result = launcher.launch("live", "-U", "urls.txt",
                                                "-l", "2");

        assert result.exitCode() == 0;

        result = launcher.launch("backup", "-o", "test_backup.json", "-t", "JSON");

        assert result.exitCode() == 0;

        File backup = new File("test_backup.json");
        assert backup.exists();

        result = launcher.launch("restore", "-i", "test_backup.json", "-t", "JSON");

        assert result.exitCode() == 0;

        backup.delete();
        fileList1.delete();

    }

}
