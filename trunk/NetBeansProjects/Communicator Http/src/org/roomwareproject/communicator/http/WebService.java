package org.roomwareproject.communicator.http;


import org.roomwareproject.server.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;




public class WebService implements Runnable {

	private Socket client;
	private Logger logger;

	private PrintStream out;
	private Scanner in;
        
        private RoomWareServer server;

    WebService(RoomWareServer roomwareServer, Socket client, Logger logger) {
        this.client = client;
        this.logger = logger;
        this.server = roomwareServer;
    }

	public void run() {
		try {
			out = new PrintStream(client.getOutputStream());
			in = new Scanner(client.getInputStream());

			String line = in.nextLine();
			if(line.contains("crossdomain")) handleCrossDomain();
			else handleRequest();
			out.close();
			client.close();

		}
        catch (URISyntaxException ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
        }		catch(IOException cause) {
			logger.warning(cause.getMessage());
		}
	}

	
        private String xmlEscape(String raw) {
            String escaped;
            escaped = raw.replace("&", "&amp;");
            escaped = escaped.replace("<", "&lt;");
            return escaped;
        }
        
        
	private void handleRequest() throws IOException {
		out.println("HTTP/0.9 200 OK");
		out.println("Server: RoomWare Admin Communicator");
		out.println("Content-Type: text/xml");
		out.println("");

                Set<Device> devices = server.getDevices();
		String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<devicelist>\n";
		for(Device dev: devices) {
                        String deviceAddressRaw = dev.getDeviceAddress().toString();
                        String friendlyNameRaw = dev.getFriendlyName();
                        if(friendlyNameRaw == null) friendlyNameRaw = "";
			response += "<device><id>" + xmlEscape(deviceAddressRaw) + "</id><name>" + xmlEscape(friendlyNameRaw) + "</name><type>bluetooth</type><event>discover</event><data></data><time></time></device>\n";
		}
		response += "</devicelist>";

		out.println(response);

	}


	private void handleCrossDomain() throws IOException, URISyntaxException  {
		out.println("HTTP/0.9 200 OK");
		out.println("Server: RoomWare Admin Communicator");
		out.println("Content-Type: text/html");
                out.println("Connection: close");
		out.println("");

                Scanner ins = new Scanner(new File(WebService.class.getResource("/crossdomain.xml").toURI()));
                while(ins.hasNextLine()) {
                    out.println(ins.nextLine());
                }
        }
        
}

