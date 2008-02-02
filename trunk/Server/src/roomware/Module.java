package roomware;

import java.beans.*;
import java.util.*;

public interface Module extends Plugin {

	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void removePropertyChangeListener(PropertyChangeListener listener);
	public void addMessageListener(MessageListener listener);
	public void removeMessageListener(MessageListener listener);
	public void sendMessage(Device device, Message message)
		throws RoomWareException;
	public Set<Device> getDevices();

}
