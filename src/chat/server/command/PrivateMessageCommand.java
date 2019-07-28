package chat.server.command;

import chat.client.Client;
import chat.server.ChatServer;
import chat.server.ServerWorker;

/**
 * Models the command for sending private message to the specified
 * client. Private message is a message that only the sending and
 * receiving client can see.
 */
public class PrivateMessageCommand extends AbstractCommand {

    public PrivateMessageCommand() {
        usageList.add("/pm <username> <message> - Sends the private message to the client with the given username.");
    }

    @Override
    public void execute(String[] args, ChatServer server, ServerWorker caller) {
        if(args.length != 2) {
            server.sendPrivateServerMessage(caller.getClientUsername(), getInvalidUsageMessage());
            return;
        }

        String sender = caller.getClientUsername();
        String receiver = args[0];

        if(receiver.equals(sender)) {
            server.sendPrivateServerMessage(sender, "You cannot send a private message to yourself.");
            return;
        }

        if(!server.isClientOnline(receiver)) {
            server.sendPrivateServerMessage(sender, "Invalid user '" + receiver + "'.");
            return;
        }

        String message = args[1];
        server.sendPrivateClientMessage(sender, receiver, message);
    }

    @Override
    public String getName() {
        return "pm";
    }

    @Override
    public String getDescription() {
        return "Sends the private message to the specified client.";
    }

    @Override
    public int getRequiredPrivilegeLevel() {
        return Client.USER;
    }
}
