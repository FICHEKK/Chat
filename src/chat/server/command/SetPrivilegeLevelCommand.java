package chat.server.command;

import chat.client.Client;
import chat.server.ChatServer;
import chat.server.ServerWorker;

/**
 * Models the general command for setting the privilege levels.
 * Privilege level of X can be given only by clients of
 * privilege levels X+1 or above.
 */
public class SetPrivilegeLevelCommand extends AbstractCommand {

    public SetPrivilegeLevelCommand() {
        usageList.add("/set <username> <privilege_level>");
    }

    @Override
    public void execute(String[] args, ChatServer server, ServerWorker caller) {
        if(args.length != 2) {
            server.sendPrivateServerMessage(caller.getClientUsername(), getInvalidUsageMessage());
            return;
        }

        String setter = caller.getClientUsername();
        String subject = args[0];

        if(setter.equals(subject)) {
            String msg = "You cannot change your own privilege level.";
            server.sendPrivateServerMessage(setter, msg);
            return;
        }

        int level;
        int setterLevel = server.getClientPrivilegeLevel(setter);
        int subjectLevel = server.getClientPrivilegeLevel(subject);

        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            String msg = "Privilege level must be an integer in range from 0 to " + (Client.RANK.length - 1) + ".";
            server.sendPrivateServerMessage(setter, msg);
            return;
        }

        if(setterLevel <= subjectLevel) {
            String msg = "Cannot change privilege level of a client with privilege level equal or higher to yours.";
            server.sendPrivateServerMessage(setter, msg);
            return;
        }

        if(level >= setterLevel) {
            String msg = "You do not have the permission to grant privilege level '" + Client.RANK[level] + "'.";
            server.sendPrivateServerMessage(setter, msg);
            return;
        }

        if(level == subjectLevel) {
            String msg = "Subject already has the privilege level '" + Client.RANK[level] + "'.";
            server.sendPrivateServerMessage(setter, msg);
            return;
        }

        boolean didSet = server.setClientPrivilegeLevel(subject, level);

        if(didSet) {
            server.sendGlobalServerMessage("'" + setter + "' has " + (level > subjectLevel ? "promoted '" : "demoted '") +
                                           subject + "' to " + Client.RANK[level] + ".");
        } else {
            server.sendPrivateServerMessage(setter, "Error setting the privilege. Is the client '" +
                                            subject + "' registered?");
        }
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Promotes the specified client with the specified level privilege.";
    }

    @Override
    public int getRequiredPrivilegeLevel() {
        return Client.USER;
    }
}
