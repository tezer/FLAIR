/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flair.server;

import com.flair.utilities.FLAIRLogger;
import java.util.HashMap;
import javax.websocket.Session;

/**
 * Tracks active sessions and its attributes
 * @author shadeMe
 */
public class SessionManager
{
    private static SessionManager	    SINGLETON = null;
    
    public static SessionManager get()
    {
	if (SINGLETON == null)
	{
	    synchronized(SessionManager.class)
	    {
		if (SINGLETON == null)
		    SINGLETON = new SessionManager();
	    }
	}
	
	return SINGLETON;
    }
        
    private final HashMap<Session, SessionState>	    activeSessions;
    
    private SessionManager()
    {
	activeSessions = new HashMap<>();
    }
    
    public synchronized void addSession(Session newSession)
    {
	if (activeSessions.containsKey(newSession))
	    throw new IllegalArgumentException("Session already added");
	else if (newSession.isOpen() == false)
	    throw new IllegalArgumentException("Session is not open");
	
	activeSessions.put(newSession, new SessionState(newSession));
	FLAIRLogger.get().info("New WebSocket session opened. ID: "+ newSession.getId());
    }
    
    public synchronized void removeSession(Session oldSession)
    {
	if (activeSessions.containsKey(oldSession) == false)
	    throw new IllegalArgumentException("Session is not tracked");
	
	SessionState state = activeSessions.get(oldSession);
	state.release();
	activeSessions.remove(oldSession);
	FLAIRLogger.get().info("WebSocket session " + oldSession.getId() + " closed");
    }
    
    public synchronized void routeMessage(String message, Session session)
    {
	if (activeSessions.containsKey(session) == false)
	    throw new IllegalArgumentException("Session is not tracked");
	
	activeSessions.get(session).handleMessage(message);
    }
}