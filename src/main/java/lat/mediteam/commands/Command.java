package lat.mediteam.commands;

import java.util.List;

import lat.mediteam.core.AppContext;
import lat.mediteam.core.Session;

public interface Command {
    public CommandResponse execute(AppContext ctx, Session session, List<String> args);
    public String getHelp();
}
