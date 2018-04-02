package jarvis.listeners;

import jarvis.engine.JarvisEngine;
import jarvis.util.AdminAlertUtil;
import mongodb.MongoDB;
import slack.SlackUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.ejb.Singleton;

@Singleton
@Startup
public class ServletContextListener {
    @PostConstruct
    public void init() {
        AdminAlertUtil.alertJarvisDeployed();
        if(!MongoDB.hasConnection()) {
            AdminAlertUtil.alertNoDbConnection();
        }

        JarvisEngine.getInstance();
    }

    @PreDestroy
    public void terminate() {
        JarvisEngine.getInstance().terminate();
    }
}