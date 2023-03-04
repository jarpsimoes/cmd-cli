package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;
import com.mbio.exercise.cli.utils.CLIException;
import com.mbio.exercise.cli.utils.Utils;
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

    @CommandLine.Option(names = {"-o", "--output"},
            description = "Output file")
    String output;

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

        output(allHistory);


    }

    private void groupHistory() throws IOException {
        List<HttpResponseData> groupHistory = new ArrayList<>();

        for (String group : group) {
            groupHistory.addAll(datastore.getHistoryByGroup(group));
        }

        output(groupHistory);
    }

    private void urlHistory() throws IOException {
        List<HttpResponseData> urlHistory = new ArrayList<>();


        for (String url : url) {
            urlHistory.addAll(datastore.getHistoryByURL(url));
        }

        output(urlHistory);

    }

    private void output(List<HttpResponseData> urlHistory)
            throws IOException {
        if(output != null && !output.isEmpty()){
            outputToFile(urlHistory);
        } else {
            if(!withContent){
                urlHistory.forEach(System.out::print);
            } else {
                urlHistory.forEach(h -> {
                    System.out.print(h.toStringWithContent());
                });
            }
        }
    }

    private void outputToFile(List<HttpResponseData> history)
            throws IOException {

        StringBuilder sb = new StringBuilder();

        history.forEach(h -> {
            if(!withContent){
                sb.append(h.toString());
            } else {
                sb.append(h.toStringWithContent());
            }
        });

        Utils.writeOutput(sb.toString(), output);
    }
}

