package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.utils.CLIException;
import com.mbio.exercise.cli.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@CommandLine.Command(name = "fetch", description = "Fetches Urls", mixinStandardHelpOptions = true)
public class Fetch implements Runnable {

    @Inject Datastore datastore;
    Logger logger = LoggerFactory.getLogger(Fetch.class);
    @CommandLine.Option(names = {"-u", "--url"}, description = "Urls to fetch")
    String[] url;

    @CommandLine.Option(names = {"-U", "--file"}, description = "Files with urls to fetch")
    String[] file;
    @CommandLine.Option(names = {"-o", "--output"}, description = "Output file path")
    String output;
    @Override
    public void run() {

        List<String> urls = new ArrayList<>();

        if(url != null && url.length > 0) {
            logger.info("Fetching {} urls", url.length);
            urls.addAll(Arrays.stream(url).toList());
        }

        if(file != null && file.length > 0) {
            logger.info("Fetching {} files", file.length);

            Arrays.stream(file).toList().forEach(f -> {
                try {
                    urls.addAll(Utils.getUrlsFromFile(f));
                } catch (Exception e) {
                    logger.error("Error while reading file: {}", f);
                    throw new CLIException(e);
                }
            });

        }
        try {
            loadUrls(urls);
        } catch (IOException e) {
            logger.error("Error while fetching urls");
            throw new CLIException(e);
        }

    }

    private void loadUrls(List<String> urls) throws IOException {

        AtomicBoolean error = new AtomicBoolean(false);
        urls.forEach(u -> {

            URL url = null;
            try {
                url = new URL(u);
                HttpResponseData data = Utils.getContent(url);

                if(output != null && !output.isEmpty()) {
                    writeOutput(data.getContent(), output);
                }
                datastore.addNew(data);

            } catch (IOException e) {
                logger.error("Error while fetching url: {}", url);
                error.set(true);
            }

        });

        if(error.get()) {
            throw new IOException("Error while fetching urls");
        }
    }
    private void writeOutput(String content, String filePath) throws IOException {
        File file = new File(output);

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
