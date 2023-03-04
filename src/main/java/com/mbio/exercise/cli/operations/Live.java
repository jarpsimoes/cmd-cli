package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.datastore.obj.FetchUrl;
import com.mbio.exercise.cli.utils.CLIException;
import com.mbio.exercise.cli.utils.UrlsTimerTask;
import com.mbio.exercise.cli.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

@CommandLine.Command(name = "live", description = "Live mode")
public class Live implements Runnable {

    Logger logger = LoggerFactory.getLogger(Live.class);
    @Inject Datastore datastore;

    @CommandLine.Option(names = {"-i", "--interval"}, description = "Interval in seconds", defaultValue = "5")
    int interval;

    @CommandLine.Option(names = {"-u", "--url"}, description = "Urls to fetch")
    String[] url;

    @CommandLine.Option(names = {"-U", "--file"}, description = "Files with urls to fetch")
    String[] file;

    @CommandLine.Option(names = {"-l", "--limit"}, description = "Limit of urls to fetch", defaultValue = "0")
    int limit;

    @Override public void run() {

        logger.info("Live mode");
        logger.info("Press q to stop");
        Scanner scanner = new Scanner(System.in);

        try {


            List<FetchUrl> urls = Utils.mergeUrlsAndFileUrls(url, file);
            Timer test = new Timer();

            UrlsTimerTask urlsTimerTask;

            if(limit > 0) {
                urlsTimerTask = new UrlsTimerTask(urls, datastore, limit);
            } else {
                urlsTimerTask = new UrlsTimerTask(urls, datastore);
            }

            test.scheduleAtFixedRate(urlsTimerTask, 0, interval * 1000L);

            if(limit > 0) {
                Thread.sleep(limit * (interval * 1000L));
            }else{
                while(true) {
                    if(scanner.nextLine().equals("q")) {
                        logger.info("Stopping");
                        test.cancel();
                        break;
                    }
                }
            }


        } catch (IOException e) {
            logger.error("Error while fetching urls");
            throw new CLIException(e);
        } catch (InterruptedException e) {
            throw new CLIException(e);
        }

    }
}
