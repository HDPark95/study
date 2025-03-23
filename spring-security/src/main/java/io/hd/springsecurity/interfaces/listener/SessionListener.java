package io.hd.springsecurity.interfaces.listener;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SessionListener implements HttpSessionListener {

    private static final Map<String, HttpSession> activeSessions = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSessionListener.super.sessionCreated(se);
        HttpSession session = se.getSession();
        activeSessions.put(session.getId(), session);
        log.info("Session created: {}", se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSessionListener.super.sessionDestroyed(se);
        activeSessions.remove(se.getSession().getId());
        log.info("Session destroyed: {}", se.getSession().getId());
    }
}
