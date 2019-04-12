package com.flair.server.interop.services;

import com.flair.server.interop.session.SessionManager;
import com.flair.shared.interop.AuthToken;
import com.flair.shared.interop.InvalidAuthTokenException;
import com.flair.shared.interop.ServerAuthenticationToken;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*
 * Implements basic validation helper methods
 */
public abstract class AbstractRemoteService extends RemoteServiceServlet {
	protected ServerAuthenticationToken validateToken(AuthToken token) throws InvalidAuthTokenException {
		HttpServletRequest request = this.getThreadLocalRequest();
		ServerAuthenticationToken authTok = (ServerAuthenticationToken) token;

		HttpSession session = request.getSession(false);
		SessionManager.get().validateToken(authTok, session);
		return authTok;
	}
}
