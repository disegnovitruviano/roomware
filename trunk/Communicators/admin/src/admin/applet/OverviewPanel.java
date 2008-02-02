package admin.applet;


import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;


public class OverviewPanel extends JPanel implements ActionListener,
	ListSelectionListener {


	protected AppletConnection connection;
	protected Set<String> moduleNames;
	protected Set<String> commNames;
	protected JList moduleList;
	protected JList commList;
	protected MButton updateModuleListButton;
	protected MButton updateCommListButton;
	protected MButton serverButton;

	protected Set<ActionListener> listeners;


	public final static int
		MODULE_SELECTION = 1,
		COMMUNICATOR_SELECTION = 2,
		SERVER_SELECTION = 3;


	public OverviewPanel(AppletConnection connection) {
		super();

		listeners = new HashSet<ActionListener>();
		this.connection = connection;
		connection.addActionListener(this);

		moduleNames = new HashSet<String>();
		commNames = new HashSet<String>();

		init();
	}


	public synchronized void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}


	public synchronized void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}


	protected synchronized void fireActionEvent(ActionEvent action) {
		for(ActionListener listener: listeners) {
			listener.actionPerformed(action);
		}
	}


	public void init() {
		updateModuleListButton = new MButton("Update module list");
		updateModuleListButton.setPreferredSize(new Dimension(200, 50));
		updateModuleListButton.setEnabled(false);
		updateModuleListButton.addActionListener(this);

		moduleList = new JList();
		moduleList.addListSelectionListener(this);
		moduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane moduleScroll = new JScrollPane(moduleList);
		moduleScroll.setPreferredSize(new Dimension(300, 70));

		JPanel umlbp = new JPanel();
		umlbp.add(updateModuleListButton);

		JPanel modulesPanel = new JPanel();
		modulesPanel.setLayout(new BoxLayout(modulesPanel, BoxLayout.X_AXIS));
		modulesPanel.add(umlbp);
		modulesPanel.add(moduleScroll);


		updateCommListButton = new MButton("Update communicator list");
		updateCommListButton.setPreferredSize(new Dimension(200, 50));
		updateCommListButton.setEnabled(false);
		updateCommListButton.addActionListener(this);

		commList = new JList();
		commList.addListSelectionListener(this);
		commList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane commScroll = new JScrollPane(commList);
		commScroll.setPreferredSize(new Dimension(300, 70));

		JPanel uclbp = new JPanel();
		uclbp.add(updateCommListButton);

		JPanel commPanel = new JPanel();
		commPanel.setLayout(new BoxLayout(commPanel, BoxLayout.X_AXIS));
		commPanel.add(uclbp);
		commPanel.add(commScroll);

		serverButton = new MButton("Server");
		serverButton.setPreferredSize(new Dimension(400, 30));
		serverButton.setEnabled(false);
		serverButton.addActionListener(this);

		JPanel serverPanel = new JPanel();
		serverPanel.add(serverButton);

		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.add(commPanel);
		main.add(modulesPanel);
		main.add(serverPanel);

		add(main);
	}


	public void doListModules() {
		synchronized(moduleNames) {
			connection.listModules(moduleNames);
		}
	}


	public void doListComm() {
		synchronized(commNames) {
			connection.listCommunicators(commNames);
		}
	}


	protected void handleListModules() {
		synchronized(moduleNames) {
			Set<String> names = new HashSet<String>(moduleNames);
			moduleList.setListData(names.toArray());
		}

	}


	protected void handleListComms() {
		synchronized(commNames) {
			Set<String> names = new HashSet<String>(commNames);
			commList.setListData(names.toArray());
		}
	}


	protected void handleDisconnect() {
		disableButtons();

		synchronized(commNames) {
			commNames.clear();
			commList.setListData(commNames.toArray());
		}

		synchronized(moduleNames) {
			moduleNames.clear();
			moduleList.setListData(moduleNames.toArray());
		}
	}


	protected void handleConnect() {
		enableButtons();

		doListComm();
		doListModules();
	}


	protected void disableButtons() {
		updateModuleListButton.setEnabled(false);
		updateCommListButton.setEnabled(false);
		serverButton.setEnabled(false);
	}


	protected void enableButtons() {
		updateModuleListButton.setEnabled(true);
		updateCommListButton.setEnabled(true);
		serverButton.setEnabled(true);
	}


	public void valueChanged(ListSelectionEvent selection) {
		if(selection.getSource() == moduleList) {
			int index = moduleList.getSelectedIndex();
			if(index == -1) return;

			Object select = moduleList.getSelectedValue();
			if(select == null) return;
			String name = select.toString();
			fireActionEvent(new ActionEvent(this, MODULE_SELECTION, name));

			commList.clearSelection();
		}
		else if(selection.getSource() == commList) {
			int index = commList.getSelectedIndex();
			if(index == -1) return;

			Object select = commList.getSelectedValue();
			if(select == null) return;
			String name = select.toString();
			fireActionEvent(new ActionEvent(
								this, COMMUNICATOR_SELECTION, name));

			moduleList.clearSelection();
		}
	}


	protected void doServer() {
		fireActionEvent(new ActionEvent(this, SERVER_SELECTION, "server"));
	}


	public void actionPerformed(ActionEvent action) {
		if(action.getSource() == updateModuleListButton) {
			doListModules();
		} 
		else if(action.getSource() == updateCommListButton) {
			doListComm();
		}
		else if(action.getSource() == serverButton) {
			doServer();
		}
		else if(action.getSource() == connection) {
			if(action.getID() == connection.ACTION_LIST_MODULES) {
				handleListModules();
			}
			else if(action.getID() == connection.ACTION_LIST_COMMUNICATORS) {
				handleListComms();
			}
			else if(action.getID() == connection.ACTION_CONNECT) {
				handleConnect();
			}
			else if(action.getID() == connection.ACTION_DISCONNECT) {
				handleDisconnect();
			}
		}
	}
}
