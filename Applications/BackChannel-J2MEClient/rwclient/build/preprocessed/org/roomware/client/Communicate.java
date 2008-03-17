package org.roomware.client;

import javax.microedition.amms.control.MIDIChannelControl;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.io.*;
import org.kxml2.io.*;
import org.xmlpull.v1.*;


public class Communicate implements Action {
    
    private String url;
    private Roomware app;
    private String message;
    private String id;
    public Communicate(String url, String id, String message,Roomware app) {
        this.url = url;
        this.app = app;
        this.message= message;
        this.id = id;
    }
    
    public void execute() {
        
         HttpConnection c = null;
         KXmlParser parser = null;
         OutputStream os = null;
        try {
            
           
            try {
                c = (HttpConnection) Connector.open(url);
                   // Set the request method and headers
            c.setRequestMethod(HttpConnection.POST);
            c.setRequestProperty("User-Agent",
                "Profile/MIDP-2.0 Configuration/CLDC-1.0");
           

            // Getting the output stream may flush the headers
            os = c.openOutputStream();
            os.write("<?xml content=\"UTF-8\" ?>\n".getBytes());
            String xmlid= "<device>" + id + "</device>\n<messagelist>\n";
            /*
            <message>
            <date></date>           // long
            <sender></sender>       // always present
            <receiver></receiver>   // none means broadcast, one or more means private
            <body></body>           // actual message
            </message>
            */
            os.write(xmlid.getBytes());
            if(message != null) {
                 String xmlMessage = "<message>" + message + "</message>\n";
                 os.write(xmlMessage.getBytes());
            }
            os.write("</messagelist/>\n".getBytes());
            os.flush();           // Optional, getResponseCode will flush

            } catch(Exception e) {
                app.alertError(app.NO_INTERNET_CONNECTION);
                try {
                    Thread.sleep(100);
                } catch(Exception ex){}
                //app.exit();
            }
            parser = new KXmlParser();
            
           // parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed",true);
            InputStream is = c.openInputStream();
            InputStreamReader isr;
            ByteArrayOutputStream bos;
            if(app.preprocessXML) {
            bos = new ByteArrayOutputStream();
            boolean foundBeginning = false;
            int c1;
            int c2;
            while((c1 = is.read()) >= 0) {
               if(!foundBeginning) { 
                  if(((char)c1) == '<') {
                    foundBeginning = true;
                    bos.write(c1);
                  }
               } else {
                   c2 = is.read();
                   if(!(((char)c1) == '&' && ((char)c2) == ' ')) {
                       bos.write(c1);
                       if(c2>=0) {
                           bos.write(c2);
                       }
                   }  else {
                       
                       bos.write('&');
                       bos.write('a');
                       bos.write('m');
                       bos.write('p');
                       bos.write(';');
                       bos.write(c2);
                   }  
               }
            }
            
                isr = new InputStreamReader(new ByteArrayInputStream(bos.toByteArray()));
            } else {
                isr = new InputStreamReader(is);
            }
            parser.setInput(isr);
            is = null;
            os = null;
            isr = null;
            System.gc();
            
            
            
            
            
            // parser.nextTag();
            // parser.require(XmlPullParser.START_TAG, null, "feed");
            // myForm.append(new StringItem("XML Start Tag: ", parser.getName()));
           
            
            int type = -1;
            int parsedFields = 0;
            Vector messages = new Vector();
            do
            {
                type = parser.nextToken();
                if (type == XmlPullParser.START_TAG && "message".equals(parser.getName())) {
                     messages.addElement(parser.nextText());
                } 
                  
            } while (parser.getEventType() != XmlPullParser.END_DOCUMENT);
            if(!messages.isEmpty()) {
            app.newMessage(messages);
            }
            c.close();
            c = null;
            parser = null;
            System.gc();
        } catch (IOException e) {
            try {
            c.close();
            c = null;
            parser = null;
            System.gc();
            } catch(Exception f){}
           app.alertError(app.NO_INTERNET_CONNECTION);
        }  catch (Exception e) {
            try {
            c.close();
            c = null;
            parser = null;
            System.gc();
            } catch(Exception f){}
            app.alertError("Exception: " + " Class: XMLParser: " + e + e.getMessage());
            
        }
        
    }
}

