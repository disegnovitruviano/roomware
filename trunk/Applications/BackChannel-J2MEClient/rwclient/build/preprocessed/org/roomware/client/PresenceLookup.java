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


public class PresenceLookup implements Action {
    
    private String url;
    private Roomware app;
    public PresenceLookup(String url, Roomware app) {
        this.url = url;
        this.app = app;
    }
    
    public void execute() {
        
         HttpConnection httpConnection = null;
         KXmlParser parser = null;
        try {
            
           
            try {
                httpConnection = (HttpConnection) Connector.open(url);
            } catch(Exception e) {
                app.alertError(app.NO_INTERNET_CONNECTION);
                try {
                    Thread.sleep(100);
                } catch(Exception ex){}
                //app.exit();
            }
            parser = new KXmlParser();
            
           // parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed",true);
            InputStream is = httpConnection.openInputStream();
            InputStreamReader isr;
            ByteArrayOutputStream os;
            if(app.preprocessXML) {
            os = new ByteArrayOutputStream();
            boolean foundBeginning = false;
            int c;
            int c2;
            while((c = is.read()) >= 0) {
               if(!foundBeginning) { 
                  if(((char)c) == '<') {
                    foundBeginning = true;
                    os.write(c);
                  }
               } else {
                   c2 = is.read();
                   if(!(((char)c) == '&' && ((char)c2) == ' ')) {
                       os.write(c);
                       if(c2>=0) {
                           os.write(c2);
                       }
                   }  else {
                       
                       os.write('&');
                       os.write('a');
                       os.write('m');
                       os.write('p');
                       os.write(';');
                       os.write(c2);
                   }  
               }
            }
            
                isr = new InputStreamReader(new ByteArrayInputStream(os.toByteArray()));
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
            Vector names = new Vector();
            do
            {
                type = parser.nextToken();
                if (type == XmlPullParser.START_TAG && "name".equals(parser.getName())) {
                     names.addElement(parser.nextText());
                } 
                  
            } while (parser.getEventType() != XmlPullParser.END_DOCUMENT);
            app.presenceResults(names);
            httpConnection.close();
            httpConnection = null;
            parser = null;
            System.gc();
        } catch (IOException e) {
            try {
            httpConnection.close();
            httpConnection = null;
            parser = null;
            System.gc();
            } catch(Exception f){}
           app.alertError(app.NO_INTERNET_CONNECTION);
        }  catch (Exception e) {
            try {
            httpConnection.close();
            httpConnection = null;
            parser = null;
            System.gc();
            } catch(Exception f){}
            app.alertError("Exception: " + " Class: XMLParser: " + e + e.getMessage());
            
        }
        
    }
}

