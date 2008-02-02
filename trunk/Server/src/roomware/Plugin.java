package roomware;

import java.util.Properties;

interface Plugin extends Runnable {

	public void stop();

	public Properties getProperties();

}
