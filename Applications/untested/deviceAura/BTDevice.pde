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

class BTDevice {
	
	String id, name;
	float f1, f2, f3, s1, s2, s3;
	color firstColor, secondColor;
	float x, y, radius;
	int nameMax;
 	float angle, active, spread, thisalpha;
 
	BTDevice (String iid, String iname) {  
    	id = iid; 
    	name = iname;

		// max length of display name (the device name)
		nameMax = 14; 

		int fr = 0;
		int fg = 0;
		int fb = 0;

		int sr = 0;
		int sg = 0;
		int sb = 0;
		
		/* 
		* transform each BT hex number to it's decimal equivalent
		*/
		
		// the first three make up color number 1
		f1 = fromHex(id.substring(0,2));
		f2 = fromHex(id.substring(2,4));
		f3 = fromHex(id.substring(4,6));

		// the second three make up color number 2
		s1 = fromHex(id.substring(6,8));
		s2 = fromHex(id.substring(8,10));
		s3 = fromHex(id.substring(10,12));

		// pick an initial random size
		radius = random(60, 100);
		
		// pick an initial random location (somewhere reasonably within the frame)
		x = random(radius, widthCanvas-radius);
		y = random(radius, heightCanvas-radius);
		
		// set initial random values for the pulsating effect
		active = random(20, 40);
		spread = random(100, 140);
		
		// all devices start out invisible (and then fade in)
		thisalpha = 0;
		
 	} 

	void draw() {
		
		// 80% saturation and brightness make sure the colors won't be garish	
		saturation(80);
		brightness(80);
		
		// fade in the device
		if (thisalpha < 255) {
			thisalpha += 6;
		}
		
		// set the first and second color for this device
		firstColor = color(f1, f2, f3, thisalpha);
		secondColor = color(s1, s2, s3, thisalpha);
		
		fill(firstColor);
		stroke(secondColor);

		// draw the device 'Aura'
		ellipse(x, y, radius, radius);
		
		// pulsate it
		radius = active * sin(angle) + spread;
		angle += 0.02;
		if (angle > TWO_PI) { angle = 0; }
		
		/* 
		* draw the name of the Bluetooth device in the middle
		*/
		fill(255);
		textFont(font);
		textAlign(CENTER);
		
		float txtYAdjust = 3;
		
		String displayName = (name.length() > nameMax) ? name.substring(0,nameMax) : name;
		
		text(displayName, x, y+txtYAdjust);
		
			
	}
		
	/*
	* helper function used to calculate the integer representation of a hex value
	*/
	int fromHex(String hex) {
		return Integer.parseInt(hex, 16);
	}
	

}