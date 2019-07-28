package chat.server.command;

import chat.server.ChatServer;
import chat.server.ServerWorker;

import java.util.List;

/**
 * Models objects that can perform the required
 * command operations on the server.
 */
public interface Command {

    /**
     * Executes the command on the given server.
     * @param args command arguments
     * @param server the server on which the command is being executed on
     * @param caller the server worker indicating the caller of the command
     */
    void execute(String[] args, ChatServer server, ServerWorker caller);

    /**
     * @return the name of the command
     */
    String getName();

    /**
     * @return the description of the command
     */
    String getDescription();

    /**
     * Returns the list of all the possible command usages, with explanations. For example,
     * command {@code help} can be used as {@code /help} and as {@code /help [command]}, so
     * this method would return a list with those 2 usages, both explained in detail.
     * @return the list of all the possible command usages
     */
    List<String> getUsageList();

    /**
     * @return the required privilege level to execute this command
     */
    int getRequiredPrivilegeLevel();

    /**
     * Returns the message displayed to the caller once the invalid command
     * usage has been made.
     * @return the invalid arguments message
     */
    default String getInvalidUsageMessage() {
        return "Invalid usage. Type \"/help " + getName() + "\" for proper command usage.";
    }
}
