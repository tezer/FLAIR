package com.flair.server.interop.messaging;

import com.flair.shared.interop.ClientIdToken;
import com.flair.shared.interop.messaging.Message;
import com.flair.shared.interop.messaging.MessageReceivedHandler;

public interface ServerMessageChannel {
	ClientIdToken clientId();

	<P extends Message.Payload> void addReceiveHandler(Class<P> payloadClass, MessageReceivedHandler<P> handler);
	void send(Message.Payload payload);
	void clearPendingMessages();
}
