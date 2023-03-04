package com.mbio.exercise.cli.operations;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true,
        subcommands = { Fetch.class, Live.class })
public class Wrapper {
    @CommandLine.Option(names = {"-d", "--datastore"}, description = "Datastore to use", defaultValue = ".mbio_data")
    public static String datastore = ".mbio_data";

    @CommandLine.Option(names = {"-t", "--timeout"}, description = "Datastore to use", defaultValue = "5000")
    public static int timeout = 5000;
}
