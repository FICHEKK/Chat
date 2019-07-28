package chat.server.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Models the abstract command; that is the command that
 * encapsulates the general command behaviour.
 */
abstract class AbstractCommand implements Command {

    List<String> usageList = new ArrayList<>();

    @Override
    public List<String> getUsageList() {
        return usageList;
    }
}
