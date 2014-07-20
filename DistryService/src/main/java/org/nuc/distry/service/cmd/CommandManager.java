package org.nuc.distry.service.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nuc.distry.service.Publisher;

public class CommandManager {
    private static final Logger LOGGER = Logger.getLogger(CommandManager.class);
    private final Map<Class<? extends Command>, CommandAction> supportedCommands = new HashMap<>();
    private final Publisher publisher;

    public CommandManager(Publisher publisher) {
        addGenericCommandsListeners();
        this.publisher = publisher;
    }

    public void addSupportedCommand(Class<? extends Command> commandClass, CommandAction action) {
        supportedCommands.put(commandClass, action);
    }

    public void onCommand(Command command, boolean targeted) {
        final Class<? extends Command> commandClass = command.getClass();
        if (supportedCommands.containsKey(commandClass)) {
            final CommandAction commandAction = supportedCommands.get(commandClass);
            commandAction.onCommand(command);

        } else {
            if (targeted) {
                LOGGER.warn("Unsupported targeted command received " + commandClass);

            } else {
                LOGGER.info("Ignored unsupported broadcast command" + commandClass);
            }
        }
    }

    public Set<Class<? extends Command>> getSupportedCommands() {
        return supportedCommands.keySet();
    }

    private void addGenericCommandsListeners() {
        supportedCommands.put(StopCommand.class, new CommandAction() {
            @Override
            public void onCommand(Command command) {
                LOGGER.info("Received stop command");
                publisher.shutdownGracefully();
            }
        });

        supportedCommands.put(PublishSupportedCmdsCommand.class, new CommandAction() {
            @Override
            public void onCommand(Command command) {
                LOGGER.info("Received publish supported commands command");
                publisher.publishSupportedCommands();
            }
        });
    }
}
