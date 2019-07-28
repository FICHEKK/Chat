package chat.server.command;

import chat.client.Client;
import chat.server.ChatServer;
import chat.server.Server;
import chat.server.ServerWorker;

/**
 * Models the server ban command; the command that closes the
 * connection between some client and the server. The banned
 * client cannot join to the server anymore.
 * <br>
 * Ban operation can be performed by clients whose privilege
 * level is equal to or higher than {@link Client#ADMIN}.
 * <br>
 * Ban operation will not be performed if:
 * <ul>
 *     <li>The subject of the ban is not a registered user</li>
 *     <li>The client tries to ban himself/herself</li>
 *     <li>The subject of the ban has equal or higher privilege than the banner</li>
 *     <li>The client is already banned</li>
 * </ul>
 */
public class BanCommand extends AbstractCommand {

    public BanCommand() {
        usageList.add("/ban <username> - Bans the client with the given username.");
    }

    @Override
    public void execute(String[] args, ChatServer server, ServerWorker caller) {
        if(args.length != 1) {
            server.sendPrivateServerMessage(caller.getClientUsername(), getInvalidUsageMessage());
            return;
        }

        String banner = caller.getClientUsername();
        String banned = args[0];

        if(banned.equals(banner)) {
            server.sendPrivateServerMessage(banner, "You cannot ban yourself.");
            return;
        }

        if(!server.isClientRegistered(banned)) {
            server.sendPrivateServerMessage(banner, "Client '" + banned + "' is not registered.");
            return;
        }

        if(server.getClientPrivilegeLevel(banner) <= server.getClientPrivilegeLevel(banned)) {
            server.sendPrivateServerMessage(banner, "You do not have the permission to ban client '" + banned + "'.");
            return;
        }

        if(server.isClientBanned(banned)) {
            server.sendPrivateServerMessage(banner, "Client '" + banned + "' is already banned.");
            return;
        }

        boolean didBan = server.banClient(banned);

        if(didBan) {
            ServerWorker justBannedClient = server.getOnlineClient(banned);
            if(justBannedClient != null) {
                justBannedClient.getServerWriter().write(Server.BANNED);
                justBannedClient.getServerWriter().println(banner);
            }

            server.broadcastClientBanned(banner, banned);
        } else {
            server.sendPrivateServerMessage(banner, "IO error while banning '" + banned + "'.");
        }
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Bans the specified client from the server.";
    }

    @Override
    public int getRequiredPrivilegeLevel() {
        return Client.ADMIN;
    }
}