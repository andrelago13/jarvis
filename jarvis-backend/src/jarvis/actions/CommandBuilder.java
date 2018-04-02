package jarvis.actions;

import jarvis.actions.command.*;
import jarvis.actions.command.definitions.Command;
import jarvis.util.AdminAlertUtil;
import jarvis.util.JarvisException;
import org.json.JSONObject;

import static jarvis.actions.command.definitions.Command.KEY_TYPE;

public abstract class CommandBuilder {
    public static final String KEY_COMMAND = "command";

    public static Command buildFromJSON(JSONObject json) {
        if(!json.has(KEY_TYPE)) {
            return null;
        }

        String cmdType = json.getString(KEY_TYPE);
        try {
            if(OnOffCommand.TAG.equals(cmdType)) {
                return new OnOffCommand(json);
            } else if (DelayedCommand.TAG.equals(cmdType)) {
                return new DelayedCommand(json);
            } else if (PeriodCommand.TAG.equals(cmdType)) {
                return new PeriodCommand(json);
            } else if (RuleCommand.TAG.equals(cmdType)) {
                return new RuleCommand(json);
            } else if (PeriodRuleCommand.TAG.equals(cmdType)) {
                return new PeriodRuleCommand(json);
            } else if (EventCommand.TAG.equals(cmdType)) {
                return new EventCommand(json);
            }
        } catch (JarvisException e) {
            AdminAlertUtil.alertJarvisException(e);
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
        }

        return null;
    }
}
