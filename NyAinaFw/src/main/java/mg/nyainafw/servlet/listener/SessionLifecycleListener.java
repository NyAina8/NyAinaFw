package mg.nyainafw.servlet.listener;

import java.util.Map;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;
import mg.nyainafw.model.MySession;

public class SessionLifecycleListener implements HttpSessionListener, HttpSessionIdListener {
    @SuppressWarnings("unchecked")
    private Map<String, MySession> sessions(HttpSessionEvent event) {
        return (Map<String, MySession>) event.getSession().getServletContext()
                .getAttribute(FrontServletContextListener.SESSIONS_ATTR);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        Map<String, MySession> sessions = sessions(event);
        if (sessions != null) {
            sessions.remove(event.getSession().getId());
        }
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        Map<String, MySession> sessions = sessions(event);
        if (sessions != null) {
            MySession data = sessions.remove(oldSessionId);
            if (data != null) {
                sessions.put(event.getSession().getId(), data);
            }
        }
    }
}
