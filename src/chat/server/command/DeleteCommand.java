package chat.server.command;

import chat.client.Client;
import chat.server.ChatServer;
import chat.server.Server;
import chat.server.ServerWorker;

/**
 * Models the account deletion command. This command can be only
 * used by the highest level clients since this operation can
 * be dangerous in the wrong hands.
 */
public class DeleteCommand extends AbstractCommand {

    public DeleteCommand() {
        usageList.add("/delete <username> - Deletes the client account with specified username.");
    }

    @Override
    public void execute(String[] args, ChatServer server, ServerWorker caller) {
        if(args.length != 1) {
            server.sendPrivateServerMessage(caller.getClientUsername(), getInvalidUsageMessage());
            return;
        }

        String deleter = caller.getClientUsername();
        String deleted = args[0];

        if(deleted.equals(deleter)) {
            server.sendPrivateServerMessage(deleter, "You cannot delete your own account.");
            return;
        }

        if(!server.isClientRegistered(deleted)) {
            server.sendPrivateServerMessage(deleter, "Client '" + deleted + "' is not registered.");
            return;
        }

        boolean didDelete = server.deleteClient(deleted);

        if(didDelete) {
            ServerWorker justDeletedClient = server.getOnlineClient(deleted);
            if(justDeletedClient != null) {
                justDeletedClient.getServerWriter().write(Server.DELETED);
                justDeletedClient.getServerWriter().println(deleter);
            }

            server.broadcastClientDeleted(deleter, deleted);
        } else {
            server.sendPrivateServerMessage(deleter, "IO error while deleting '" + deleted + "'.");
        }
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Deletes the specified client account from the server.";
    }

    @Override
    public int getRequiredPrivilegeLevel() {
        return Client.OWNER;
    }
}
