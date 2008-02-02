package admin.applet;


import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;


public class Applet extends JApplet implements ActionListener {


	JTabbedPane tabs;
	JTextArea log;

	AppletConnection connection;

	
	public Applet() {
		super();
		init();
	}


	public void stop() {
		log("stop");
	}


	public void init() {
		connection = new AppletConnection(this);
		connection.addActionListener(this);
		new Thread(connection).start();

		JPanel connectPanel = new ConnectPanel(connection);
		OverviewPanel overviewPanel = new OverviewPanel(connection);
		JPanel detailPanel = new DetailPanel(connection, overviewPanel);

		tabs = new JTabbedPane();
		tabs.add("Connection", connectPanel);
		tabs.add("Overview", overviewPanel);
		tabs.add("Details", detailPanel);

		log = new JTextArea(10, 50);
		log.setEditable(false);
		JScrollPane logScroll = new JScrollPane(log);
		JPanel messages = new JPanel();
		messages.add(logScroll);

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(tabs, BorderLayout.CENTER);
		main.add(messages, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		getContentPane().add(main);
	}


	protected void log(String s) {
		log.append(new Date() + ": " + s + "\n");
		validate();
		repaint();
	}


	public void actionPerformed(ActionEvent action) {
		if(action.getSource() == connection) {
			if(action.getID() == connection.ACTION_CONNECT) {
				log(action.getActionCommand());
			} else if(action.getID() == connection.ACTION_DISCONNECT) {
				log(action.getActionCommand());
			}
		}
	}


	public static void main(String[] args) {
		Applet applet = new Applet();
		JFrame frame = new JFrame("Admin Applet Test");
		frame.getContentPane().add(applet);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 600);
		frame.pack();
		frame.setVisible(true);
	}

}
