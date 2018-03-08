package jarvis.actions;

import jarvis.actions.definitions.Command;
import jarvis.util.AdminAlertUtil;
import jarvis.util.JarvisException;
import org.json.JSONObject;

import java.util.List;

import static jarvis.actions.definitions.Command.KEY_TYPE;

public abstract class CommandBuilder {
    public static final String KEY_COMMAND = "command";

    public static Command buildFromJSON(JSONObject json) {
        //{
        //  "_id": "5aa13c75cd4e021a9826b5ba",
        //  "undo": false,
        //  "success": true,
        //  "commandText": "[DelayedAction] Scheduled action for 10 SECONDS : [OnOffAction] Executed device on (light)",
        //  "command": {
        //    "id": "7510243007421604922",
        //    "type": "delayedCommand",
        //    "timeValue": 10,
        //    "command": {
        //      "type": "onOffAction",
        //      "thing": "light",
        //      "status": true
        //    },
        //    "timeUnit": "SECONDS"
        //  },
        //  "timestamp": "2018-03-08 13:36:53.132"
        //}
        if(!json.has(KEY_COMMAND)) {
            return null;
        }

        JSONObject command = json.getJSONObject(KEY_COMMAND);
        String cmdType = command.getString(KEY_TYPE);
        try {
            if(OnOffAction.TAG.equals(cmdType)) {
                return new OnOffAction(command);
            } else if (DelayedCommand.TAG.equals(cmdType)) {
                return new DelayedCommand(command);
            }
        } catch (JarvisException e) {
            AdminAlertUtil.alertJarvisException(e);
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
        }

        return null;
    }
}
