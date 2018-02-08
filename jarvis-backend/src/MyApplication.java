import jarvis.routes.Index;
import jarvis.routes.Ping;
import jarvis.util.ClassUtils;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class MyApplication extends Application{
    @Override
    public Set<Class<?>> getClasses() {
        HashSet h = new HashSet<Class<?>>();
        h.addAll(ClassUtils.getClassesInPackage(Index.class.getPackage().getName()));
        return h;
    }
}