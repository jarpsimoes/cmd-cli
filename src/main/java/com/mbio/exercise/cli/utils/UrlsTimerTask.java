package com.mbio.exercise.cli.utils;

import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.datastore.obj.FetchUrl;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.TimerTask;

public class UrlsTimerTask extends TimerTask {

    List<FetchUrl> urls;

    Logger logger = LoggerFactory.getLogger(UrlsTimerTask.class);

    long limit = 0;

    long count = 0;

    Datastore datastore;

    public UrlsTimerTask(List<FetchUrl> urls, Datastore datastore) {
        this.urls = urls;
        this.datastore = datastore;
    }
    public UrlsTimerTask(List<FetchUrl> urls, Datastore datastore, long limit) {
        this.urls = urls;
        this.datastore = datastore;
        this.limit = limit;
    }

    @Override public void run() {

        urls.forEach(url -> {
            try {

                HttpResponseData result = Utils.getContent(url.getUrl());
                result.setGroup(url.getOrigin());
                logger.info(
                        "Fetched url: {} [Result Code: {} | Content Type: {}]",
                        url.getUrl(),
                        result.getResultCode(), result.getContentType());
                datastore.addNew(result);


            } catch (IOException e) {
                logger.error("Error while fetching url: {}", url.getUrl());
            }

        });

        if(limit > 0) {
            count++;
            if(count >= limit) {
                logger.info("Limit reached. Stopping timer");
                this.cancel();
            }
        }

    }
}
