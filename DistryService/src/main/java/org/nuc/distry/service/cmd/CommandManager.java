package org.nuc.distry.service.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CommandManager {
    private static final Logger LOGGER = Logger.getLogger(CommandManager.class);
    private final Map<Class<? extends Command>, CommandAction> supportedCommands = new HashMap<>();

    public CommandManager() {
        addGenericCommandsListeners();
    }

    public void addSupportedCommand(Class<? extends Command> commandClass, CommandAction action) {
        supportedCommands.put(commandClass, action);
    }

    public void onCommand(Command command) {
        final Class<? extends Command> commandClass = command.getClass();
        if (supportedCommands.containsKey(commandClass)) {
            final CommandAction commandAction = supportedCommands.get(commandClass);
            commandAction.onCommand(command);
            
        } else {
            LOGGER.warn("Unsupported command received " + commandClass);
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
                LogManager.shutdown();
                System.exit(0);
            }
        });
    }
}
