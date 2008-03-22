package org.roomwareproject.server;

import java.util.Properties;

public interface Plugin extends Runnable {

	public void stop();

	public Properties getProperties();

}
