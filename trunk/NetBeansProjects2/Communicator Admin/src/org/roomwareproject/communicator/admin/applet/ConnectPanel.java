package org.roomwareproject.communicator.admin.applet;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;


public class ConnectPanel extends JPanel implements ActionListener {


	protected JTextField portField;
	protected MButton connectButton;
	protected MButton disconnectButton;
	protected AppletConnection connection;


	public ConnectPanel(AppletConnection connection) {
		super();
		this.connection = connection;
		connection.addActionListener(this);
		init();
	}


	protected void init() {
		connectButton = new MButton("Connect");
		connectButton.setPreferredSize(new Dimension(100, 50));
		connectButton.addActionListener(this);

		disconnectButton = new MButton("Disconnect");
		disconnectButton.setPreferredSize(new Dimension(100, 50));
		disconnectButton.addActionListener(this);
		disconnectButton.setEnabled(false);

		portField = new JTextField("4101");

		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));
		menuPanel.add(new JLabel("Admin's Applet Server listens on port: "));
		menuPanel.add(portField);

		JPanel cPanel = new JPanel();
		cPanel.add(connectButton);

		JPanel dPanel = new JPanel();
		dPanel.add(disconnectButton);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(menuPanel);
		panel.add(cPanel);
		panel.add(dPanel);
		
		add(panel);
	}


	protected void handleConnect() {
		try {
			int port = Integer.parseInt(portField.getText());
			connectButton.setEnabled(false);
			connection.connect(port);
		}
		catch(NumberFormatException cause) {
			System.err.println(cause.getMessage());
		}
	}


	protected void handleDisconnect() {
		disconnectButton.setEnabled(false);
		connection.disconnect();
	}


	protected void handleDisconnectDone() {
		connectButton.setEnabled(true);
	}


	protected void handleConnectDone() {
		disconnectButton.setEnabled(true);
	}


	public void actionPerformed(ActionEvent action) {
		if(action.getSource() == connection) {
			if(action.getID() == connection.ACTION_CONNECT) {
				handleConnectDone();
			} else if(action.getID() == connection.ACTION_DISCONNECT) {
				handleDisconnectDone();
			}
		}
		else if(action.getSource() == connectButton) {
			handleConnect();
		}
		else if(action.getSource() == disconnectButton) {
			handleDisconnect();
		}
	}
}
