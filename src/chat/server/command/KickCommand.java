package chat.server.command;

import chat.client.Client;
import chat.server.ChatServer;
import chat.server.Server;
import chat.server.ServerWorker;

/**
 * Models the server kick command; the command that closes the
 * connection between some client and the server. The kicked
 * client can rejoin the server.
 * <br>
 * Kick operation can be performed by clients whose privilege
 * level is equal to or higher than {@link Client#MODERATOR}.
 * <br>
 * Kick operation will not be performed if:
 * <ul>
 *     <li>The subject of the kick is not a registered user</li>
 *     <li>The client tries to kick himself/herself</li>
 *     <li>The subject of the kick has equal or higher privilege than the kicker</li>
 * </ul>
 */
public class KickCommand extends AbstractCommand {

    public KickCommand() {
        usageList.add("/kick <username> - Kicks the specified client from the server.");
    }

    @Override
    public void execute(String[] args, ChatServer server, ServerWorker caller) {
        if(args.length != 1) {
            server.sendPrivateServerMessage(caller.getClientUsername(), getInvalidUsageMessage());
            return;
        }

        String kicker = caller.getClientUsername();
        String kicked = args[0];

        if(kicked.equals(kicker)) {
            server.sendPrivateServerMessage(kicker, "You cannot kick yourself.");
            return;
        }

        if(!server.isClientRegistered(kicked)) {
            server.sendPrivateServerMessage(kicker, "Client '" + kicked + "' is not registered.");
            return;
        }

        if(server.getClientPrivilegeLevel(kicker) <= server.getClientPrivilegeLevel(kicked)) {
            server.sendPrivateServerMessage(kicker, "You do not have the permission to kick client '" + kicked + "'.");
            return;
        }

        ServerWorker toBeKicked = server.getOnlineClient(kicked);
        if(toBeKicked != null) {
            toBeKicked.getServerWriter().write(Server.KICKED);
            toBeKicked.getServerWriter().println(kicker);
            server.broadcastClientKicked(kicker, kicked);
        } else {
            server.sendPrivateServerMessage(kicker, "Client '" + kicked + "' is not online.");
        }
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Kicks the specified user from the server.";
    }

    @Override
    public int getRequiredPrivilegeLevel() {
        return Client.MODERATOR;
    }
}
