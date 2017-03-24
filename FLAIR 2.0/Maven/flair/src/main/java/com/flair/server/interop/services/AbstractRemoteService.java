package com.flair.server.interop.services;

import javax.servlet.http.HttpServletRequest;

import com.flair.server.interop.session.SessionManager;
import com.flair.shared.interop.AuthToken;
import com.flair.shared.interop.ServerAuthenticationToken;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/*
 * Implements basic validation helper methods
 */
public abstract class AbstractRemoteService extends RemoteServiceServlet
{
	protected ServerAuthenticationToken validateToken(AuthToken token)
	{
		HttpServletRequest request = this.getThreadLocalRequest();
		ServerAuthenticationToken authTok = (ServerAuthenticationToken)token;
		
		SessionManager.get().validateToken(authTok, request.getSession(true));
		return authTok;
	}
}