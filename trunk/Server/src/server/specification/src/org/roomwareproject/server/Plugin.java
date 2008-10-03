package org.roomwareproject.server;

import java.util.*;

public interface Plugin extends Runnable {

	public void stop();

	public Properties getProperties();

}
