package org.nuc.distry.ms;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MessagingServerStarter {
    private static final String BIND_ADDRESS = "bindAddress";
    private static final String LOG4J = "log4j";
    private static final Logger LOGGER = Logger.getLogger(MessagingServerStarter.class);

    public static void main(String[] args) {
        try {
            final String bindAddress = parseArguments(args);
            startServer(bindAddress);
            
        } catch (Exception exception) {
            LOGGER.error("Failed to start messaging server. Reason: " + exception.getMessage());
        }
    }

    private static String parseArguments(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption(LOG4J, true, "Log4j configuration file");
        options.addOption(BIND_ADDRESS, true, "Bind address");

        final CommandLineParser parser = new BasicParser();
        final CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption(LOG4J)) {
            PropertyConfigurator.configure(commandLine.getOptionValue(LOG4J));

        } else {
            BasicConfigurator.configure();
            throw new IllegalArgumentException("log4j argument is missing");
        }

        if (commandLine.hasOption(BIND_ADDRESS)) {
            return commandLine.getOptionValue(BIND_ADDRESS);
            
        } else {
            throw new IllegalArgumentException("bindAddress argument is missing");
        }

    }

    private static void startServer(String bindAddress) throws Exception {
        final MessagingServer messagingServer = new ActiveMQWrapper(bindAddress);
        messagingServer.start();
    }
}
