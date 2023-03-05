package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.utils.CLIException;
import picocli.CommandLine;

import javax.inject.Inject;
import java.io.IOException;

@CommandLine.Command(name = "restore", description = "Restore mode")
public class Restore implements Runnable {


    @Inject Datastore datastore;
    @CommandLine.Option(names = {"-i", "--input"}, description = "Input file", required = true)
    String input;

    @CommandLine.Option(names = {"-t", "--type"}, description = "Backup ", defaultValue = "json")
    Datastore.BackupType type;

    @Override public void run() {

        try {
            datastore.restore(input, type);
        } catch (IOException e) {
            throw new CLIException(e);
        }

    }
}
