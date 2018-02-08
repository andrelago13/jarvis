package jarvis.listeners;

import slack.SlackUtil;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.ejb.Singleton;

@Singleton
@Startup
public class ServletContextListener {
    @PostConstruct
    public void init() {
        SlackUtil.maybeSendDeployedMessage();
    }
}