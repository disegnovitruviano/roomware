/*
* Copyright (C) Tijs Teulings (tijs AT automatique.nl).
* 
* This software is licensed as per the Creative Commons Attribution License 3.0
* http://creativecommons.org/licenses/by/3.0/
*
* The above copyright notice and this permission notice shall be included in all copies or 
* substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
* PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
* ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

class RoomwareReader implements Runnable {
	
	PApplet applet;
	String server;
	int totalDevices = 0;
	int runs;
	float refresh;
	
	RoomwareReader(PApplet app, String url, float milli) {
		
		applet = app;
		server = url;
		refresh = milli;
		
		// the reading is done in a seperate thread
		Thread thread = new Thread(this);
		thread.start();
		
	}
	
	public void run() {
		
		// first update of the list
		update();

		// after that we keep calling update every x milliseconds
		while (totalDevices > 0) {
			float current = refresh * runs;
		 	if (millis() > current) {
				update();
				runs++;
			}
		}
		
	}
	
	/*
	* update() reads out the XML file from the roomware server
	* removes devices when they are no longer present and
	* adds new devices as they are found
	*/
	private void update() {
		
		try {

			XMLElement xml = new XMLElement(applet, server);
			XMLElement devicesXML[] = xml.getChildren();

			totalDevices = xml.getChildCount();
			expand(devices, totalDevices);
			
			// remove any devices no longer present
			for (int i=0; i < devices.length; i++) {
				if (devices[i] != null) {
					
					String currentID = devices[i].id;
					boolean match = false;

					for (int y=0; y < totalDevices; y++) {

						XMLElement id = devicesXML[y].getChild("id");
						String idStr = id.getContent();
						
						if (idStr.equals(currentID)) {
							match = true;
							continue;
						}
					}

					if (!match) {
						
						// print a lost message to the console
						println("Lost device: " + i);
						
						// remove the device from the stack
						devices[i] = null;
					}
				}
			}
			
			// add each newly found device
			for (int i=0; i < totalDevices; i++) {

				XMLElement id = devicesXML[i].getChild("id");
				XMLElement name = devicesXML[i].getChild("name");

				String idStr = id.getContent();
				String nameStr = name.getContent();

				if (!idStr.equals("") && !nameStr.equals("") && idStr.length() == 12) {
					if (!inList(idStr)) {
						
						// print a found message to console
						println("Found device: " + i + " - "+ idStr + ", " + nameStr);
						
						// add a new Bluetooth device object to the stack
						devices[i] = new BTDevice(idStr, nameStr);
					}
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	* helper function to check whether a certain deviceID
	* is in the list of deviceID's
	*/
	private boolean inList(String deviceID) {
		boolean exists = false;
		for(int i=0;i<devices.length;i++) {
			if (devices[i] != null) {
				if (devices[i].id.equals(deviceID)) {
					exists = true;
				}
			}
		}
		
		return exists;
		
	}
	
	
}