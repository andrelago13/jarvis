import jarvis.routes.Index;
import jarvis.util.ClassUtils;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

  @Override
  public Set<Class<?>> getClasses() {
    HashSet h = new HashSet<Class<?>>();
    h.addAll(ClassUtils.getClassesInPackage(Index.class.getPackage().getName()));
    return h;
  }
}