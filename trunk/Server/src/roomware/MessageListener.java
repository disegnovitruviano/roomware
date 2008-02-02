package roomware;

import java.util.*;

public interface MessageListener extends EventListener {

	public void messageReceived(MessageEvent event);

}
