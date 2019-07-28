package chat.server.command;

import chat.client.Client;
import chat.server.ChatServer;
import chat.server.ServerWorker;

/**
 * Models the server un-ban command; the command that allows
 * the previously banned client to connect to the server once
 * again.
 * <br>
 * Un-ban operation can be performed by clients whose privilege
 * level is equal to or higher than {@link Client#ADMIN}.
 * <br>
 * Un-ban operation will not be performed if:
 * <ul>
 *     <li>The client tries to un-ban himself/herself</li>
 *     <li>The subject of the un-ban is not a registered user</li>
 *     <li>The subject of the un-ban has equal or higher privilege than the banner</li>
 *     <li>The client is already un-banned</li>
 * </ul>
 */
public class UnbanCommand extends AbstractCommand {

    public UnbanCommand() {
        usageList.add("/unban <username> - Un-bans the client with the given username.");
    }

    @Override
    public void execute(String[] args, ChatServer server, ServerWorker caller) {
        if(args.length != 1) {
            server.sendPrivateServerMessage(caller.getClientUsername(), getInvalidUsageMessage());
            return;
        }

        String unbanner = caller.getClientUsername();
        String unbanned = args[0];

        if(unbanned.equals(unbanner)) {
            server.sendPrivateServerMessage(unbanner, "You cannot un-ban yourself.");
            return;
        }

        if(!server.isClientRegistered(unbanned)) {
            server.sendPrivateServerMessage(unbanner, "Client '" + unbanned + "' is not registered.");
            return;
        }

        if(server.getClientPrivilegeLevel(unbanner) <= server.getClientPrivilegeLevel(unbanned)) {
            server.sendPrivateServerMessage(unbanner, "You do not have the permission to un-ban client '" + unbanned + "'.");
            return;
        }

        if(!server.isClientBanned(unbanned)) {
            server.sendPrivateServerMessage(unbanner, "Client '" + unbanned + "' is already un-banned.");
            return;
        }

        boolean didUnban = server.unbanClient(unbanned);

        if(didUnban) {
            server.sendPrivateServerMessage(unbanner, "Successfully un-banned '" + unbanned + "'.");
        } else {
            server.sendPrivateServerMessage(unbanner, "IO error while banning '" + unbanned + "'.");
        }
    }

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String getDescription() {
        return "Un-bans the specified client from the server.";
    }

    @Override
    public int getRequiredPrivilegeLevel() {
        return Client.ADMIN;
    }
}