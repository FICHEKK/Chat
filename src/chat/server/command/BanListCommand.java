package chat.server.command;

import chat.client.Client;
import chat.server.ChatServer;
import chat.server.ServerWorker;

import java.util.Iterator;
import java.util.List;

public class BanListCommand extends AbstractCommand {

    public BanListCommand() {
        usageList.add("/banlist - Displays the list of all the banned usernames.");
    }

    @Override
    public void execute(String[] args, ChatServer server, ServerWorker caller) {
        if(args.length != 0) {
            server.sendPrivateServerMessage(caller.getClientUsername(), getInvalidUsageMessage());
            return;
        }

        List<String> banList = server.getBanList();
        Iterator<String> iterator = banList.iterator();

        if(!iterator.hasNext()) {
            server.sendPrivateServerMessage(caller.getClientUsername(), "There are no banned clients.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        while (true) {
            String username = iterator.next();
            sb.append(username);

            if(iterator.hasNext()) {
                sb.append(", ");
            } else {
                break;
            }
        }

        server.sendPrivateServerMessage(caller.getClientUsername(), "Banned clients: " + sb.toString());
    }

    @Override
    public String getName() {
        return "banlist";
    }

    @Override
    public String getDescription() {
        return "Displays the list of all the banned usernames.";
    }

    @Override
    public int getRequiredPrivilegeLevel() {
        return Client.ADMIN;
    }
}
