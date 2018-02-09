import jarvis.routes.Index;
import jarvis.util.ClassUtils;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet h = new HashSet<Class<?>>();
        h.addAll(ClassUtils.getClassesInPackage(Index.class.getPackage().getName()));
        return h;
    }
}