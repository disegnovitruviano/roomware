package org.roomwareproject.communicator.admin.applet;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


public class DetailPanel extends JPanel implements ActionListener {

	protected AppletConnection connection;
	protected OverviewPanel overview;

	protected Set<String> devices;

	protected JList list;
	protected Properties properties;


	public DetailPanel(AppletConnection connection, OverviewPanel overview) {
		super();

		this.connection = connection;
		this.overview = overview;

		connection.addActionListener(this);
		overview.addActionListener(this);

		properties = new Properties();
		devices = new HashSet<String>();

		init();
	}


	public void init() {
		list = new JList();
		JScrollPane pane = new JScrollPane(list);

		add(pane);
	}


	protected void clearSelection() {
		synchronized(properties) {
			properties.clear();
			list.setListData(pr2sta(properties));
		}
	}
	

	protected Object[] pr2sta(Properties properties) {
		java.util.List<String> entries = new ArrayList<String>();
		for(Map.Entry<Object, Object> entry: properties.entrySet()) {
			entries.add(entry.getKey() + ": " + entry.getValue());
		}
		return entries.toArray();
	}


	protected void doModuleSelection(String name) {
		synchronized(properties) {
			connection.moduleProperties(name, properties);
		}
		clearSelection();
	}


	protected void doCommunicatorSelection(String name) {
		synchronized(properties) {
			connection.communicatorProperties(name, properties);
		}
		clearSelection();
	}


	protected void handleCommunicatorProperties() {
		synchronized(properties) {
			list.setListData(pr2sta(properties));
		}
	}


	protected void handleModuleProperties() {
		synchronized(properties) {
			list.setListData(pr2sta(properties));
		}
	}


	protected void doServerSelection() {
		synchronized(devices) {
			connection.getDevices(devices);
		}
	}


	protected void handleGetDevices() {
		synchronized(devices) {
			Set<String> set = new HashSet<String>(devices);
			list.setListData(set.toArray());
		}
	}


	public void actionPerformed(ActionEvent action) {
		if(action.getSource() == overview) {
			if(action.getID() == overview.MODULE_SELECTION) {
				String name = action.getActionCommand();
				doModuleSelection(name);
			}
			else if(action.getID() == overview.COMMUNICATOR_SELECTION) {
				String name = action.getActionCommand();
				doCommunicatorSelection(name);
			}
			else if(action.getID() == overview.SERVER_SELECTION) {
				doServerSelection();
			}
		}
		else if(action.getSource() == connection) {
			if(action.getID() == connection.ACTION_DISCONNECT) {
				clearSelection();
			}
			else if(action.getID() == connection.ACTION_COMMUNICATOR_PROPERTIES) {
				handleCommunicatorProperties();
			}
			else if(action.getID() == connection.ACTION_MODULE_PROPERTIES) {
				handleModuleProperties();
			}
			else if(action.getID() == connection.ACTION_GET_DEVICES) {
				handleGetDevices();
			}
		}
	}

}
