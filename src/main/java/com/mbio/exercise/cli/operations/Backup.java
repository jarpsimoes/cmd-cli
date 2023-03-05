package com.mbio.exercise.cli.operations;

import com.mbio.exercise.cli.datastore.Datastore;
import com.mbio.exercise.cli.utils.CLIException;
import picocli.CommandLine;

import javax.inject.Inject;
import java.io.IOException;

@CommandLine.Command(name = "backup", description = "Backup mode")
public class Backup implements Runnable {

    @Inject Datastore datastore;

    @CommandLine.Option(names = {"-o", "--output"},
            description = "Output file", required = true)
    String output;

    @CommandLine.Option(names = {"-t", "--type"},
            description = "Backup ", defaultValue = "json")
    Datastore.BackupType type;

    @Override public void run() {

        try {
            datastore.backup(output, type);
        } catch (IOException e) {
            throw new CLIException(e);
        }

    }
}
