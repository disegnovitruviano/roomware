package org.roomwareproject.server;

import java.util.*;

public interface MessageListener extends EventListener {

	public void messageReceived(MessageEvent event);

}
