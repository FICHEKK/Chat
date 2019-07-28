package chat.server.command;

import chat.client.Client;
import chat.server.ChatServer;
import chat.server.ServerWorker;

import java.util.*;

/**
 * A classic "help" command. Help command is used by the clients
 * once they wish to understand what the command does, or how
 * the command should be used.
 */
public class HelpCommand extends AbstractCommand {

    public HelpCommand() {
        usageList.add("/help - Lists all of the commands that you can use.");
        usageList.add("/help <command> - Displays the details about the specified command.");
    }

    @Override
    public void execute(String[] args, ChatServer server, ServerWorker caller) {
        if(args.length == 0) {
            listCommands(server, caller.getClientUsername());
        } else if(args.length == 1) {
            displayCommandDetails(args[0], server, caller.getClientUsername());
        } else {
            server.sendPrivateServerMessage(caller.getClientUsername(), getInvalidUsageMessage());
        }
    }

    /**
     * Lists all of the commands that the client can perform. The list
     * will be generated based on the client's privilege level, meaning
     * that some clients may have different server response, based on
     * their privilege level.
     * @param server the server used for sending the response
     * @param receiver the response receiver
     */
    private void listCommands(ChatServer server, String receiver) {
        StringBuilder sb = new StringBuilder();

        Collection<Command> commands = ServerWorker.getCommandMap().values();
        Iterator<Command> iterator = commands.iterator();
        int callerLevel = server.getClientPrivilegeLevel(receiver);

        boolean foundFirst = false;
        while (iterator.hasNext()) {
            Command command = iterator.next();
            if(callerLevel < command.getRequiredPrivilegeLevel()) continue;

            if(foundFirst) {
                sb.append(", ").append(command.getName());
            } else {
                sb.append(command.getName());
                foundFirst = true;
            }
        }

        server.sendPrivateServerMessage(receiver, "Valid commands are: " + sb.toString());
    }

    /**
     * Displays the details about the specified command, unless the given
     * command name is invalid.
     * @param commandName the name of the command
     * @param server the server used for sending the response
     * @param receiver the command information receiver
     */
    private void displayCommandDetails(String commandName, ChatServer server, String receiver) {
        Command command = ServerWorker.getCommandMap().get(commandName);

        if(command != null) {
            server.sendPrivateServerMessage(receiver, "\t" + commandName + " - " + command.getDescription());

            for(String usage : command.getUsageList()) {
                server.sendPrivateServerMessage(receiver, usage);
            }
        } else {
            server.sendPrivateServerMessage(receiver, "Unknown command '" + commandName + "'.");
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Lists all of the available server commands.";
    }

    @Override
    public int getRequiredPrivilegeLevel() {
        return Client.USER;
    }
}