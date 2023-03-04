package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.datastore.obj.FetchUrl;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

@CommandLine.Command(name = "fetch", description = "Fetches Urls",
        mixinStandardHelpOptions = true)
public class Fetch implements Runnable {

    @Inject Datastore datastore;
    Logger logger = LoggerFactory.getLogger(Fetch.class);
    @CommandLine.Option(names = {"-u", "--url"}, description = "Urls to fetch")
    String[] url;

    @CommandLine.Option(names = {"-U", "--file"},
            description = "Files with urls to fetch")
    String[] file;
    @CommandLine.Option(names = {"-o", "--output"},
            description = "Output file path")
    String output;

    @Override
    public void run() {

        try {
            List<FetchUrl> urls = Utils.mergeUrlsAndFileUrls(url, file);
            loadUrls(urls);
        } catch (IOException e) {
            logger.error("Error while fetching urls");
            throw new CLIException(e);
        }

    }

    private void loadUrls(List<FetchUrl> urls) throws IOException {

        AtomicBoolean error = new AtomicBoolean(false);

        IntStream.range(0, urls.size()).forEach(i -> {
            try {
                URL url = urls.get(i).getUrl();;
                HttpResponseData data = Utils.getContent(url);

                data.setGroup(urls.get(i).getOrigin());

                if(output != null && !output.isEmpty()) {
                    Utils.writeOutput(data.getContent(), String.format("%s_%s",
                            i, output));
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

}
