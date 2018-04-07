package jarvis.listeners;

import jarvis.engine.JarvisEngine;
import jarvis.util.AdminAlertUtil;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import mongodb.MongoDB;

@Singleton
@Startup
public class ServletContextListener {

  @PostConstruct
  public void init() {
    AdminAlertUtil.alertJarvisDeployed();
    if (!MongoDB.hasConnection()) {
      AdminAlertUtil.alertNoDbConnection();
    }

    JarvisEngine.getInstance();
  }

  @PreDestroy
  public void terminate() {
    JarvisEngine.getInstance().terminate();
  }
}