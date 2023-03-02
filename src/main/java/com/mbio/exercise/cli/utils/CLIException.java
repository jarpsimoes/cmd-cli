package com.mbio.exercise.cli.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLIException extends RuntimeException{
    Logger logger = LoggerFactory.getLogger(CLIException.class);
    public CLIException(Throwable cause) {
        super(cause);
        logger.error(cause.getMessage());
    }
}
