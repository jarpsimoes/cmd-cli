package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.utils.CLIException;
import picocli.CommandLine;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@CommandLine.Command(name = "history", description = "History mode")
public class History implements Runnable {

    @Inject Datastore datastore;

    static Logger logger = Logger.getLogger(History.class.getName());

    @CommandLine.Option(names = {"-c", "--with-content"},
            description = "History with content", defaultValue = "false")
    boolean withContent;

    @CommandLine.Option(names = {"-u", "--url"},
            description = "History by URL")
    String[] url;

    @CommandLine.Option(names = {"-g", "--group"},
            description = "History by group")
    String[] group;

    @CommandLine.Option(names = {"-a", "--all"},
            description = "All history", defaultValue = "false")
    boolean all;

    @Override public void run() {

        try {
            if (url != null && url.length > 0) {
                urlHistory();
            } else if (group != null && group.length > 0) {
                groupHistory();
            } else if (all) {
                allHistory();
            } else{
                System.out.println("No options selected");
            }
        } catch (IOException e) {
            throw new CLIException(e);
        }

    }

    private void allHistory() throws IOException {

        List<HttpResponseData> allHistory = datastore.getAllHistory();
        allHistory.forEach(System.out::println);

    }

    private void groupHistory() {
        List<HttpResponseData> groupHistory = new ArrayList<>();

        for (String group : group) {
            try {
                groupHistory.addAll(datastore.getHistoryByGroup(group));
            } catch (IOException e) {
                throw new CLIException(e);
            }
        }

        groupHistory.forEach(System.out::println);
    }

    private void urlHistory() {
        List<HttpResponseData> urlHistory = new ArrayList<>();

        for (String url : url) {
            try {
                urlHistory.addAll(datastore.getHistoryByURL(url));
            } catch (IOException e) {
                throw new CLIException(e);
            }
        }

        urlHistory.forEach(System.out::println);
    }
}

