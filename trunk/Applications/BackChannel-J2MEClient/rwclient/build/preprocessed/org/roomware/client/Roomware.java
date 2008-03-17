/*
 * Roomware.java
 *
 * Created on February 2, 2008, 1:25 PM
 */

package org.roomware.client;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
   import javax.bluetooth.*;
/**
 *
 * @author  travis
 * @version
 */
public class Roomware extends MIDlet implements CommandListener {
    
    public final static  String NO_INTERNET_CONNECTION = "Unable to establish internet connection. Please exit the app and check the settings on your phone.";
    public final static boolean preprocessXML = false;
    public Display display;
    public Form infoScreen;
    public Form chatScreen;
    private Command exitCommand;
    private Command backCommand;
    private Command sendCommand;
    private Command chatCommand;
    private String url = "http://82.92.150.88:4040";
    private String commURL = "http://82.92.150.88:4069";
    private TextField message;
    private TextField bluetoothId;
    private Timer timer;
 
    public void startApp() {
          display = Display.getDisplay(this);
         
          infoScreen = new Form("Registered Roomware users");
          chatScreen = new Form("Chat");
          exitCommand = new Command("Exit",Command.EXIT,2);
          chatCommand = new Command("Chat",Command.SCREEN,1);
          backCommand = new Command("Back",Command.BACK,2);
          sendCommand = new Command("Send",Command.SCREEN,1);
          bluetoothId = new TextField("Bluetooth-id: ",null,100,TextField.ANY);
          message = new TextField("Message: ",null,100,TextField.ANY);
          chatScreen.append(bluetoothId);
          chatScreen.append(message);
          
          chatScreen.addCommand(sendCommand);
          chatScreen.addCommand(backCommand);
          chatScreen.setCommandListener(this);
          infoScreen.setCommandListener(this);
          infoScreen.addCommand(exitCommand);
          infoScreen.addCommand(chatCommand);
          
          display.setCurrent(infoScreen);
        
           // Retrieve the local Bluetooth device object
          LocalDevice local;
        try {
            local = LocalDevice.getLocalDevice();
            bluetoothId.setString(local.getBluetoothAddress());
        } catch (BluetoothStateException e) {
            alertError("Failed to retrieve the local device (" +
                e.getMessage() + ")");
            return;
        }
          
          
         PresenceLookup parser = new PresenceLookup(url, this);
         executeBackgroundAction(parser);
        
    }
    
      public void commandAction(Command c, Displayable d) {
        if(d == chatScreen) {
            if(c == sendCommand) {
                sendMessage(message.getString());
            } else if(c == backCommand) {
               display.setCurrent(infoScreen);
            } 
        } else if(d == infoScreen) {
            if(c == exitCommand) {
                notifyDestroyed();
            } else if(c == chatCommand) {
                display.setCurrent(chatScreen);
                 try {
                  TimerTask webTask = new WebTimerTask();
                  timer = new Timer();
                  timer.schedule(webTask,0,60000);
                 } catch(Exception e) {
                    alertError("Exception occurs when starting/scheduling WebTimerTask" + e.toString()); 
                 }
                
            }
        }
    }
    
    public void sendMessage(String message) {
         Communicate parser = new Communicate(commURL,bluetoothId.getString().toUpperCase(),message,this);
         executeBackgroundAction(parser);
    }
    
    public void newMessage(Vector messages) {
        chatScreen.deleteAll();
        chatScreen.append(bluetoothId);
        chatScreen.append(message);
         for(int i =0; i < messages.size(); i++) {
          chatScreen.append(new StringItem(null,(String)messages.elementAt(i)+"\n"));
        }
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    
      public void alertError(String error) {
        Alert alert = new Alert("Error", error, null, AlertType.ERROR);
        Display display = Display.getDisplay(this);
        Displayable current = display.getCurrent();
        if (!(current instanceof Alert)) {
            // This next call can't be done when current is an Alert
            display.setCurrent(alert, current);
        }
    }
      
    public void presenceResults(Vector names)  {
        for(int i =0; i < names.size(); i++) {
          infoScreen.append(new StringItem(null,(String)names.elementAt(i)+"\n"));
        }
        display.setCurrent(infoScreen);
    }
    
    public void executeBackgroundAction(Action action) {  
           Thread bgThread = new BackgroundExecuter(action);
           bgThread.start();   
    }

    private class BackgroundExecuter extends Thread {
        private final Action action;
        public BackgroundExecuter(Action action) {
            super();
            this.action = action;
        }
        
        public void run() {
           
            action.execute();
            
        }
        
      
    }
    
     private class WebTimerTask extends TimerTask {
     
        public void run() {
             Communicate parser = new Communicate(commURL,bluetoothId.getString().toUpperCase(),null,Roomware.this);
             executeBackgroundAction(parser);
        }
    }
}
