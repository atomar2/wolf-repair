package edu.ncsu.csc216.wolf_repair.view.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import edu.ncsu.csc216.wolf_repair.model.command.Command;
import edu.ncsu.csc216.wolf_repair.model.manager.WolfRepair;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Container for the WolfRepair system that has the menu options for adding a
 * new repair site, loading/saving a repair sites file, and quitting.
 * Depending on user actions, other JPanels are loaded for the different ways
 * users interact with the UI.
 * @author Sarah Heckman
 * @author Chandrika Satyavolu
 */
public class WolfRepairGUI extends JFrame implements ActionListener {

	/** ID number used for object serialization. */
	private static final long serialVersionUID = 1L;
	/** Title for top of GUI. */
	private static final String APP_TITLE = "Wolf Repair";
	/** Text for the File Menu. */
	private static final String FILE_MENU_TITLE = "File";
	/** Text for the add repair site menu item. */
	private static final String ADD_SITE_TITLE = "Add Repair Site";
	/** Text for the Load menu item. */
	private static final String LOAD_TITLE = "Load Repair Sites File";
	/** Text for the Save menu item. */
	private static final String SAVE_TITLE = "Save Repair Sites";
	/** Text for the Quit menu item. */
	private static final String QUIT_TITLE = "Quit";

	/** Menu bar for the GUI that contains Menus. */
	private JMenuBar menuBar;
	/** Menu for the GUI. */
	private JMenu menu;
	/** Menu item for creating a new repair site. */
	private JMenuItem itemAddSite;
	/** Menu item for loading a file containing one or more repair sites and their work orders. */
	private JMenuItem itemLoadSites;
	/** Menu item for saving the repair sites and their work orders. */
	private JMenuItem itemSaveSites;
	/** Menu item for quitting the program. */
	private JMenuItem itemQuit;
	/** Panel that will contain different views of the data. */
	private JPanel panel;

	/** Constant to identify RepairSiteListPanel for CardLayout. */
	private static final String SITE_LIST_PANEL = "SiteListPanel";
	/** Constant to identify SubmittedStatePanel for CardLayout. */
	private static final String SUBMITTED_PANEL = "SubmittedPanel";
	/** Constant to identify ReviewStatePanel for CardLayout. */
	private static final String REVIEW_PANEL = "ReviewPanel";
	/** Constant to identify AssignedStatePanel for CardLayout. */
	private static final String ASSIGNED_PANEL = "AssignedPanel";
	/** Constant to identify WorkStatePanel for CardLayout. */
	private static final String IN_PROGRESS_PANEL = "InProgressPanel";
	/** Constant to identify RejectedStatePanel for CardLayout. */
	private static final String REJECTED_PANEL = "RejectedPanel";
	/** Constant to identify CompletedStatePanel for CardLayout. */
	private static final String COMPLETED_PANEL = "CompletedPanel";
	/** Constant to identify AddWorkOrderPanel for CardLayout. */
	private static final String ADD_WORK_ORDER_PANEL = "AddWorkOrder";
	/** Constant to identify AddRepairSitePanel for CardLayout. */
	private static final String ADD_SITE_PANEL = "AddRepairSite";

	/** RepairSite list panel - we only need one instance, so it's final. */
	private final RepairSiteListPanel pnlSiteList = new RepairSiteListPanel();
	/** Submitted panel - we only need one instance, so it's final. */
	private final SubmittedStatePanel pnlSubmitted = new SubmittedStatePanel();
	/** UnderReview panel - we only need one instance, so it's final. */
	private final ReviewStatePanel pnlReview = new ReviewStatePanel();
	/** Assigned panel - we only need one instance, so it's final. */
	private final AssignedStatePanel pnlAssigned = new AssignedStatePanel();
	/** InProgress panel - we only need one instance, so it's final. */
	private final WorkStatePanel pnlInProgress = new WorkStatePanel();
	/** Rejected panel - we only need one instance, so it's final. */
	private final RejectedStatePanel pnlRejected = new RejectedStatePanel();
	/** Completed panel - we only need one instance, so it's final. */
	private final CompletedStatePanel pnlCompleted = new CompletedStatePanel();
	/** Add work order panel - we only need one instance, so it's final. */
	private final AddWorkOrderPanel pnlAddWorkOrder = new AddWorkOrderPanel();
	/** Add repair site panel - we only need one instance, so it's final. */
	private final AddRepairSitePanel pnlAddSite = new AddRepairSitePanel();

	/** Reference to CardLayout for panel. Stacks all of the panels. */
	private CardLayout cardLayout;

	/**
	 * Constructs a WolfRepairGUI object that will contain a JMenuBar and a
	 * JPanel that will hold different possible views of the data in the
	 * WolfRepair system.
	 */
	public WolfRepairGUI() {
		super();

		setSize(500, 700);
		setLocation(50, 50);
		setTitle(APP_TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setUpMenuBar();

		panel = new JPanel();
		cardLayout = new CardLayout();
		panel.setLayout(cardLayout);
		panel.add(pnlSiteList, SITE_LIST_PANEL);
		panel.add(pnlSubmitted, SUBMITTED_PANEL);
		panel.add(pnlReview, REVIEW_PANEL);
		panel.add(pnlAssigned, ASSIGNED_PANEL);
		panel.add(pnlInProgress, IN_PROGRESS_PANEL);
		panel.add(pnlRejected, REJECTED_PANEL);
		panel.add(pnlCompleted, COMPLETED_PANEL);
		panel.add(pnlAddWorkOrder, ADD_WORK_ORDER_PANEL);
		panel.add(pnlAddSite, ADD_SITE_PANEL);
		cardLayout.show(panel, SITE_LIST_PANEL);

		Container c = getContentPane();
		c.add(panel, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {

			/**
			 * Ask the user to save the repair sites file when closing the GUI.
			 * @param e WindowEvent leading to GUI closing
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				WolfRepair model = WolfRepair.getInstance();
				try {
					model.saveRepairSitesToFile(getFileName(false));
				} catch (IllegalArgumentException exp) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, exp.getMessage());
				} catch (IllegalStateException exp) {
					// Don't do anything - user canceled (or error)
				}
			}

		});

		setVisible(true);
	}

	/**
	 * Makes the GUI Menu bar that contains options for adding a repair site,
	 * loading/saving a repair sites file, or quitting.
	 */
	private void setUpMenuBar() {
		menuBar = new JMenuBar();
		menu = new JMenu(FILE_MENU_TITLE);
		itemAddSite = new JMenuItem(ADD_SITE_TITLE);
		itemLoadSites = new JMenuItem(LOAD_TITLE);
		itemSaveSites = new JMenuItem(SAVE_TITLE);
		itemQuit = new JMenuItem(QUIT_TITLE);
		itemAddSite.addActionListener(this);
		itemLoadSites.addActionListener(this);
		itemSaveSites.addActionListener(this);
		itemQuit.addActionListener(this);

		itemSaveSites.setEnabled(false);

		menu.add(itemAddSite);
		menu.add(itemLoadSites);
		menu.add(itemSaveSites);
		menu.add(itemQuit);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}

	/**
	 * Performs an action based on the given ActionEvent.
	 * @param e user event that triggers an action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		WolfRepair model = WolfRepair.getInstance();
		if (e.getSource() == itemAddSite) {
			try {
				cardLayout.show(panel, ADD_SITE_PANEL);
				validate();
				repaint();
			} catch (IllegalArgumentException exp) {
				JOptionPane.showMessageDialog(this, exp.getMessage());
			}
		} else if (e.getSource() == itemLoadSites) {
			try {
				model.loadRepairSitesFromFile(getFileName(true));
				itemSaveSites.setEnabled(true);
				pnlSiteList.updateSite();
				cardLayout.show(panel, SITE_LIST_PANEL);
				validate();
				repaint();
			} catch (IllegalArgumentException exp) {
				JOptionPane.showMessageDialog(this, exp.getMessage());
			} catch (IllegalStateException exp) {
				// Don't do anything - user canceled (or error)
			}
		} else if (e.getSource() == itemSaveSites) {
			try {
				model.saveRepairSitesToFile(getFileName(false));
			} catch (IllegalArgumentException exp) {
				JOptionPane.showMessageDialog(this, exp.getMessage());
			} catch (IllegalStateException exp) {
				// Don't do anything - user canceled (or error)
			}
		} else if (e.getSource() == itemQuit) {
			try {
				model.saveRepairSitesToFile(getFileName(false));
				System.exit(0);
			} catch (IllegalArgumentException exp) {
				JOptionPane.showMessageDialog(this, exp.getMessage());
			} catch (IllegalStateException exp) {
				// Don't do anything - user canceled (or error)
			}
		}
	}

	/**
	 * Returns a file name generated through interactions with a JFileChooser
	 * object.
	 * @param load true if loading a file, false if saving
	 * @return the file name selected through JFileChooser
	 * @throws IllegalStateException if no file name provided
	 */
	private String getFileName(boolean load) {
		JFileChooser fc = new JFileChooser("./");
		int returnVal = Integer.MIN_VALUE;
		if (load) {
			returnVal = fc.showOpenDialog(this);
		} else {
			returnVal = fc.showSaveDialog(this);
		}
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			throw new IllegalStateException();
		}
		File selectedFile = fc.getSelectedFile();
		return selectedFile.getAbsolutePath();
	}

	/**
	 * Starts the GUI for the WolfRepair system.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		new WolfRepairGUI();
	}

	/**
	 * Inner class that creates the look and behavior for the JPanel that
	 * shows the active repair site and its list of work orders.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class RepairSiteListPanel extends JPanel implements ActionListener {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;

		/** Label for selecting active repair site */
		private JLabel lblActiveSite;
		/** Combo box for repair site list */
		private JComboBox<String> comboSiteList;
		/** Button to update to the selected repair site */
		private JButton btnUpdateSite;

		/** Button for filing a new work order */
		private JButton btnAdd;
		/** Button for deleting the selected work order in the list */
		private JButton btnDelete;
		/** Button for editing the selected work order in the list */
		private JButton btnEdit;

		/** Label for filter by state */
		private JLabel lblFilterState;
		/** Combo box for states */
		private JComboBox<String> comboFilterState;
		/** Button to update to filter by state */
		private JButton btnFilterState;

		/** JTable for displaying the list of work orders */
		private JTable tableWorkOrders;
		/** TableModel for work orders */
		private WorkOrderTableModel tableModel;

		/**
		 * Creates the repair site panel with the work order list.
		 */
		public RepairSiteListPanel() {
			super(new BorderLayout());

			lblActiveSite = new JLabel("Repair Site");
			comboSiteList = new JComboBox<String>();
			btnUpdateSite = new JButton("Select Site");
			btnUpdateSite.addActionListener(this);

			btnAdd = new JButton("Add Work Order");
			btnAdd.addActionListener(this);
			btnDelete = new JButton("Delete Work Order");
			btnDelete.addActionListener(this);
			btnEdit = new JButton("Edit Work Order");
			btnEdit.addActionListener(this);

			lblFilterState = new JLabel("Filter by State");
			comboFilterState = new JComboBox<String>();
			btnFilterState = new JButton("Filter");
			btnFilterState.addActionListener(this);

			comboFilterState.addItem("All");
			comboFilterState.addItem(WorkOrder.SUBMITTED_NAME);
			comboFilterState.addItem(WorkOrder.REVIEW_NAME);
			comboFilterState.addItem(WorkOrder.ASSIGNED_NAME);
			comboFilterState.addItem(WorkOrder.WORK_NAME);
			comboFilterState.addItem(WorkOrder.REJECTED_NAME);
			comboFilterState.addItem(WorkOrder.COMPLETED_NAME);

			JPanel pnlActions = new JPanel();
			pnlActions.setLayout(new GridLayout(3, 3));
			pnlActions.add(lblActiveSite);
			pnlActions.add(comboSiteList);
			pnlActions.add(btnUpdateSite);
			pnlActions.add(btnAdd);
			pnlActions.add(btnDelete);
			pnlActions.add(btnEdit);
			pnlActions.add(lblFilterState);
			pnlActions.add(comboFilterState);
			pnlActions.add(btnFilterState);

			tableModel = new WorkOrderTableModel();
			tableWorkOrders = new JTable(tableModel);
			tableWorkOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tableWorkOrders.setPreferredScrollableViewportSize(new Dimension(500, 500));
			tableWorkOrders.setFillsViewportHeight(true);

			JScrollPane listScrollPane = new JScrollPane(tableWorkOrders);

			add(pnlActions, BorderLayout.NORTH);
			add(listScrollPane, BorderLayout.CENTER);

			updateSite();
		}

		/**
		 * Performs an action based on the given ActionEvent.
		 * @param e user event that triggers an action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnUpdateSite) {
				int idx = comboSiteList.getSelectedIndex();

				if (idx == -1) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "No repair site selected");
				} else {
					String siteName = comboSiteList.getItemAt(idx);
					WolfRepair.getInstance().loadRepairSite(siteName);
				}
				updateSite();
			} else if (e.getSource() == btnFilterState) {
				int idx = comboFilterState.getSelectedIndex();

				if (idx == -1) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "No state selected");
				} else {
					String stateName = comboFilterState.getItemAt(idx);
					tableModel.updateData(stateName);
				}
			} else if (e.getSource() == btnAdd) {
				cardLayout.show(panel, ADD_WORK_ORDER_PANEL);
			} else if (e.getSource() == btnDelete) {
				int row = tableWorkOrders.getSelectedRow();
				if (row == -1) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "No work order selected");
				} else {
					try {
						int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
						WolfRepair.getInstance().deleteWorkOrderById(id);
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid id");
					}
				}
				updateSite();
			} else if (e.getSource() == btnEdit) {
				int row = tableWorkOrders.getSelectedRow();
				if (row == -1) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "No work order selected");
				} else {
					try {
						int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
						String stateName = WolfRepair.getInstance().getWorkOrderById(id).getState();
						if (stateName.equals(WorkOrder.SUBMITTED_NAME)) {
							cardLayout.show(panel, SUBMITTED_PANEL);
							pnlSubmitted.setInfo(id);
						} else if (stateName.equals(WorkOrder.REVIEW_NAME)) {
							cardLayout.show(panel, REVIEW_PANEL);
							pnlReview.setInfo(id);
						} else if (stateName.equals(WorkOrder.ASSIGNED_NAME)) {
							cardLayout.show(panel, ASSIGNED_PANEL);
							pnlAssigned.setInfo(id);
						} else if (stateName.equals(WorkOrder.WORK_NAME)) {
							cardLayout.show(panel, IN_PROGRESS_PANEL);
							pnlInProgress.setInfo(id);
						} else if (stateName.equals(WorkOrder.REJECTED_NAME)) {
							cardLayout.show(panel, REJECTED_PANEL);
							pnlRejected.setInfo(id);
						} else if (stateName.equals(WorkOrder.COMPLETED_NAME)) {
							cardLayout.show(panel, COMPLETED_PANEL);
							pnlCompleted.setInfo(id);
						}
					} catch (NumberFormatException | NullPointerException idExp) {
						JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid id");
					}
				}
			}
			WolfRepairGUI.this.repaint();
			WolfRepairGUI.this.validate();
		}

		/**
		 * Refreshes the site combo box, action button state, and work order
		 * table from the model.
		 */
		public void updateSite() {
			tableModel.updateData("All");

			String siteName = WolfRepair.getInstance().getActiveRepairSiteName();

			if (siteName == null) {
				siteName = "Create a Repair Site";
				btnAdd.setEnabled(false);
				btnDelete.setEnabled(false);
				btnEdit.setEnabled(false);
				btnUpdateSite.setEnabled(false);
				btnFilterState.setEnabled(false);
			} else {
				btnAdd.setEnabled(true);
				btnDelete.setEnabled(true);
				btnEdit.setEnabled(true);
				btnUpdateSite.setEnabled(true);
				btnFilterState.setEnabled(true);
			}

			comboSiteList.removeAllItems();
			String[] siteList = WolfRepair.getInstance().getRepairSiteList();
			for (int i = 0; i < siteList.length; i++) {
				comboSiteList.addItem(siteList[i]);
			}
			comboSiteList.setSelectedItem(WolfRepair.getInstance().getActiveRepairSiteName());

			Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder border = BorderFactory.createTitledBorder(lowerEtched, "Repair Site: " + siteName);
			setBorder(border);
			setToolTipText("Repair Site: " + siteName);
		}

		/**
		 * WorkOrderTableModel is the object underlying the JTable object that
		 * displays the list of work orders to the user.
		 * @author Sarah Heckman
		 * @author Chandrika Satyavolu
		 */
		private class WorkOrderTableModel extends AbstractTableModel {

			/** ID number used for object serialization. */
			private static final long serialVersionUID = 1L;
			/** Column names for the table */
			private String[] columnNames = { "ID", "State", "Requester", "Description", "Technician" };
			/** Data stored in the table */
			private Object[][] data;

			/**
			 * Constructs the WorkOrderTableModel by requesting the latest
			 * information from the model.
			 */
			public WorkOrderTableModel() {
				updateData("All");
			}

			/**
			 * Returns the number of columns in the table.
			 * @return the number of columns in the table.
			 */
			@Override
			public int getColumnCount() {
				return columnNames.length;
			}

			/**
			 * Returns the number of rows in the table.
			 * @return the number of rows in the table.
			 */
			@Override
			public int getRowCount() {
				if (data == null) {
					return 0;
				}
				return data.length;
			}

			/**
			 * Returns the column name at the given index.
			 * @param col the column index
			 * @return the column name at the given column.
			 */
			@Override
			public String getColumnName(int col) {
				return columnNames[col];
			}

			/**
			 * Returns the data at the given {row, col} index.
			 * @param row the row index
			 * @param col the column index
			 * @return the data at the given location.
			 */
			@Override
			public Object getValueAt(int row, int col) {
				if (data == null) {
					return null;
				}
				return data[row][col];
			}

			/**
			 * Sets the given value to the given {row, col} location.
			 * @param value Object to modify in the data.
			 * @param row the row index
			 * @param col the column index
			 */
			@Override
			public void setValueAt(Object value, int row, int col) {
				data[row][col] = value;
				fireTableCellUpdated(row, col);
			}

			/**
			 * Updates the given model with work order information from the
			 * WolfRepair system.
			 * @param stateName name of state to filter on.
			 */
			private void updateData(String stateName) {
				WolfRepair m = WolfRepair.getInstance();
				data = m.getWorkOrdersAsArray(stateName);
			}
		}
	}

	/**
	 * Read-only panel that displays a WorkOrder's information. Shared by
	 * every per-state edit panel.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class WorkOrderInfoPanel extends JPanel {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;

		/** Label - state */
		private JLabel lblState;
		/** JTextField - state */
		private JTextField txtState;

		/** Label - requester */
		private JLabel lblRequester;
		/** JTextField - requester */
		private JTextField txtRequester;

		/** Label - description */
		private JLabel lblDescription;
		/** JTextField - description */
		private JTextField txtDescription;

		/** Label - technician */
		private JLabel lblTechnician;
		/** JTextField - technician */
		private JTextField txtTechnician;

		/** Label - note */
		private JLabel lblNote;
		/** JTextField - note */
		private JTextField txtNote;

		/**
		 * Construct the panel for the information.
		 */
		public WorkOrderInfoPanel() {
			super(new GridBagLayout());

			lblState = new JLabel("State: ");
			lblRequester = new JLabel("Requester: ");
			lblDescription = new JLabel("Description: ");
			lblTechnician = new JLabel("Technician: ");
			lblNote = new JLabel("Note: ");

			txtState = new JTextField(15);
			txtRequester = new JTextField(15);
			txtDescription = new JTextField(15);
			txtTechnician = new JTextField(15);
			txtNote = new JTextField(15);

			txtState.setEditable(false);
			txtRequester.setEditable(false);
			txtDescription.setEditable(false);
			txtTechnician.setEditable(false);
			txtNote.setEditable(false);

			GridBagConstraints c = new GridBagConstraints();

			JPanel row1 = new JPanel(new GridLayout(1, 2));
			row1.add(lblState);
			row1.add(txtState);
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.anchor = GridBagConstraints.LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(row1, c);

			JPanel row2 = new JPanel(new GridLayout(1, 2));
			row2.add(lblRequester);
			row2.add(txtRequester);
			c.gridx = 0;
			c.gridy = 1;
			add(row2, c);

			JPanel row3 = new JPanel(new GridLayout(1, 2));
			row3.add(lblDescription);
			row3.add(txtDescription);
			c.gridx = 0;
			c.gridy = 2;
			add(row3, c);

			JPanel row4 = new JPanel(new GridLayout(1, 2));
			row4.add(lblTechnician);
			row4.add(txtTechnician);
			c.gridx = 0;
			c.gridy = 3;
			add(row4, c);

			JPanel row5 = new JPanel(new GridLayout(1, 2));
			row5.add(lblNote);
			row5.add(txtNote);
			c.gridx = 0;
			c.gridy = 4;
			add(row5, c);
		}

		/**
		 * Populates the display with the given WorkOrder's information.
		 * @param id the id of the work order to display
		 */
		public void setWorkOrderInfo(int id) {
			WorkOrder workOrder = WolfRepair.getInstance().getWorkOrderById(id);
			if (workOrder == null) {
				JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid id");
				cardLayout.show(panel, SITE_LIST_PANEL);
				WolfRepairGUI.this.repaint();
				WolfRepairGUI.this.validate();
			} else {
				String siteName = WolfRepair.getInstance().getActiveRepairSiteName();

				Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
				TitledBorder border = BorderFactory.createTitledBorder(lowerEtched,
						siteName + " - Work Order #" + workOrder.getWorkOrderId());
				setBorder(border);
				setToolTipText(siteName + " work order " + workOrder.getWorkOrderId() + " - " + workOrder.getState());

				txtState.setText(workOrder.getState());
				txtRequester.setText(workOrder.getRequester());
				txtDescription.setText(workOrder.getDescription());
				txtTechnician.setText(workOrder.getTechnician());
				txtNote.setText(workOrder.getNote());
			}
		}
	}

	/**
	 * Inner class for the JPanel that interacts with a work order in the
	 * Submitted state.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class SubmittedStatePanel extends JPanel implements ActionListener {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;
		/** Info panel that presents the work order's information to the user */
		private WorkOrderInfoPanel pnlInfo;
		/** Current work order's id */
		private int id;

		/** Button - Submit for review */
		private JButton btnSubmit;
		/** Button - cancel edit */
		private JButton btnCancel;

		/**
		 * Constructs the JPanel for editing a work order in the Submitted state.
		 */
		public SubmittedStatePanel() {
			super(new BorderLayout());

			pnlInfo = new WorkOrderInfoPanel();

			btnSubmit = new JButton("Submit");
			btnCancel = new JButton("Cancel");
			btnSubmit.addActionListener(this);
			btnCancel.addActionListener(this);

			JPanel pnlCommands = new JPanel();
			Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder border = BorderFactory.createTitledBorder(lowerEtched, "Commands");
			pnlCommands.setBorder(border);
			pnlCommands.setLayout(new GridLayout(1, 2));
			pnlCommands.add(btnSubmit);
			pnlCommands.add(btnCancel);

			add(pnlInfo, BorderLayout.NORTH);
			add(pnlCommands, BorderLayout.SOUTH);
		}

		/**
		 * Set the info panel with the given work order's data.
		 * @param id id of the work order
		 */
		public void setInfo(int id) {
			this.id = id;
			pnlInfo.setWorkOrderInfo(this.id);
		}

		/**
		 * Performs an action based on the given ActionEvent.
		 * @param e user event that triggers an action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean reset = true;
			if (e.getSource() == btnSubmit) {
				try {
					Command c = new Command(Command.CommandValue.SUBMIT, null);
					WolfRepair.getInstance().executeCommand(id, c);
				} catch (IllegalArgumentException iae) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
					reset = false;
				} catch (UnsupportedOperationException uoe) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid state transition");
					reset = false;
				}
			}
			if (reset) {
				cardLayout.show(panel, SITE_LIST_PANEL);
				pnlSiteList.updateSite();
				WolfRepairGUI.this.repaint();
				WolfRepairGUI.this.validate();
			}
		}

	}

	/**
	 * Inner class for the JPanel that interacts with a work order in the
	 * UnderReview state.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class ReviewStatePanel extends JPanel implements ActionListener {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;
		/** Info panel that presents the work order's information to the user */
		private WorkOrderInfoPanel pnlInfo;
		/** Current work order's id */
		private int id;

		/** Label - review note */
		private JLabel lblNote;
		/** Text Field - review note */
		private JTextField txtNote;

		/** Label - rejection reason */
		private JLabel lblRejectionReason;
		/** Combo Box - rejection reason, required to deny */
		private JComboBox<String> cmbRejectionReason;

		/** Button - Approve the request */
		private JButton btnApprove;
		/** Button - Deny the request */
		private JButton btnDeny;
		/** Button - cancel edit */
		private JButton btnCancel;

		/**
		 * Constructs the JPanel for editing a work order in the UnderReview state.
		 */
		public ReviewStatePanel() {
			super(new BorderLayout());

			pnlInfo = new WorkOrderInfoPanel();

			lblNote = new JLabel("Review Note:");
			txtNote = new JTextField(15);

			lblRejectionReason = new JLabel("Rejection Reason:");
			cmbRejectionReason = new JComboBox<String>();
			cmbRejectionReason.addItem(WorkOrder.DENIED);
			cmbRejectionReason.addItem(WorkOrder.CANCELLED);
			cmbRejectionReason.setSelectedIndex(-1);

			btnApprove = new JButton("Approve");
			btnDeny = new JButton("Deny");
			btnCancel = new JButton("Cancel");
			btnApprove.addActionListener(this);
			btnDeny.addActionListener(this);
			btnCancel.addActionListener(this);

			JPanel pnlCommands = new JPanel();
			Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder border = BorderFactory.createTitledBorder(lowerEtched, "Commands");
			pnlCommands.setBorder(border);
			pnlCommands.setLayout(new GridBagLayout());

			JPanel pnlNoteRow = new JPanel(new GridLayout(1, 2));
			pnlNoteRow.add(lblNote);
			pnlNoteRow.add(txtNote);

			JPanel pnlReasonRow = new JPanel(new GridLayout(1, 2));
			pnlReasonRow.add(lblRejectionReason);
			pnlReasonRow.add(cmbRejectionReason);

			JPanel pnlButtonRow = new JPanel(new GridLayout(1, 3));
			pnlButtonRow.add(btnApprove);
			pnlButtonRow.add(btnDeny);
			pnlButtonRow.add(btnCancel);

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.anchor = GridBagConstraints.LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			pnlCommands.add(pnlNoteRow, c);

			c.gridx = 0;
			c.gridy = 1;
			pnlCommands.add(pnlReasonRow, c);

			c.gridx = 0;
			c.gridy = 2;
			pnlCommands.add(pnlButtonRow, c);

			add(pnlInfo, BorderLayout.NORTH);
			add(pnlCommands, BorderLayout.SOUTH);
		}

		/**
		 * Set the info panel with the given work order's data.
		 * @param id id of the work order
		 */
		public void setInfo(int id) {
			this.id = id;
			pnlInfo.setWorkOrderInfo(this.id);
		}

		/**
		 * Performs an action based on the given ActionEvent.
		 * @param e user event that triggers an action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean reset = true;
			if (e.getSource() == btnApprove) {
				try {
					Command c = new Command(Command.CommandValue.APPROVE, txtNote.getText());
					WolfRepair.getInstance().executeCommand(id, c);
				} catch (IllegalArgumentException iae) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
					reset = false;
				} catch (UnsupportedOperationException uoe) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid state transition");
					reset = false;
				}
			} else if (e.getSource() == btnDeny) {
				String reason = (String) cmbRejectionReason.getSelectedItem();
				if (reason == null) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
					reset = false;
				} else {
					try {
						Command c = new Command(Command.CommandValue.DENY, reason);
						WolfRepair.getInstance().executeCommand(id, c);
					} catch (IllegalArgumentException iae) {
						JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
						reset = false;
					} catch (UnsupportedOperationException uoe) {
						JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid state transition");
						reset = false;
					}
				}
			}
			if (reset) {
				cardLayout.show(panel, SITE_LIST_PANEL);
				pnlSiteList.updateSite();
				WolfRepairGUI.this.repaint();
				WolfRepairGUI.this.validate();
				txtNote.setText("");
				cmbRejectionReason.setSelectedIndex(-1);
			}
		}

	}

	/**
	 * Inner class for the JPanel that interacts with a work order in the
	 * Assigned state.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class AssignedStatePanel extends JPanel implements ActionListener {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;
		/** Info panel that presents the work order's information to the user */
		private WorkOrderInfoPanel pnlInfo;
		/** Current work order's id */
		private int id;

		/** Label - technician */
		private JLabel lblTechnician;
		/** Text Field - technician */
		private JTextField txtTechnician;
		/** Button - Dispatch a technician, or leave blank if none is available */
		private JButton btnDispatch;

		/** Label - rejection reason */
		private JLabel lblRejectionReason;
		/** Combo Box - rejection reason, required when the technician field is left blank */
		private JComboBox<String> cmbRejectionReason;

		/** Button - cancel edit */
		private JButton btnCancel;

		/**
		 * Constructs the JPanel for editing a work order in the Assigned state.
		 */
		public AssignedStatePanel() {
			super(new BorderLayout());

			pnlInfo = new WorkOrderInfoPanel();

			lblTechnician = new JLabel("Technician Id (blank if none available):");
			txtTechnician = new JTextField(15);
			btnDispatch = new JButton("Dispatch");
			btnDispatch.addActionListener(this);

			lblRejectionReason = new JLabel("Rejection Reason (required if no technician):");
			cmbRejectionReason = new JComboBox<String>();
			cmbRejectionReason.addItem(WorkOrder.WAITING);
			cmbRejectionReason.addItem(WorkOrder.CANCELLED);
			cmbRejectionReason.setSelectedIndex(-1);

			btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(this);

			JPanel pnlCommands = new JPanel();
			Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder border = BorderFactory.createTitledBorder(lowerEtched, "Commands");
			pnlCommands.setBorder(border);
			pnlCommands.setLayout(new GridBagLayout());

			JPanel pnlDispatch = new JPanel(new GridLayout(1, 3));
			pnlDispatch.add(lblTechnician);
			pnlDispatch.add(txtTechnician);
			pnlDispatch.add(btnDispatch);

			JPanel pnlReasonRow = new JPanel(new GridLayout(1, 2));
			pnlReasonRow.add(lblRejectionReason);
			pnlReasonRow.add(cmbRejectionReason);

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.anchor = GridBagConstraints.LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			pnlCommands.add(pnlDispatch, c);

			c.gridx = 0;
			c.gridy = 1;
			pnlCommands.add(pnlReasonRow, c);

			c.gridx = 0;
			c.gridy = 2;
			pnlCommands.add(btnCancel, c);

			add(pnlInfo, BorderLayout.NORTH);
			add(pnlCommands, BorderLayout.SOUTH);
		}

		/**
		 * Set the info panel with the given work order's data.
		 * @param id id of the work order
		 */
		public void setInfo(int id) {
			this.id = id;
			pnlInfo.setWorkOrderInfo(this.id);
		}

		/**
		 * Performs an action based on the given ActionEvent.
		 * @param e user event that triggers an action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean reset = true;
			if (e.getSource() == btnDispatch) {
				String technician = txtTechnician.getText();
				String commandInfo = technician;
				if (technician == null || "".equals(technician)) {
					commandInfo = (String) cmbRejectionReason.getSelectedItem();
				}
				if (commandInfo == null) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
					reset = false;
				} else {
					try {
						Command c = new Command(Command.CommandValue.DISPATCH, commandInfo);
						WolfRepair.getInstance().executeCommand(id, c);
					} catch (IllegalArgumentException iae) {
						JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
						reset = false;
					} catch (UnsupportedOperationException uoe) {
						JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid state transition");
						reset = false;
					}
				}
			}
			if (reset) {
				cardLayout.show(panel, SITE_LIST_PANEL);
				pnlSiteList.updateSite();
				WolfRepairGUI.this.repaint();
				WolfRepairGUI.this.validate();
				txtTechnician.setText("");
				cmbRejectionReason.setSelectedIndex(-1);
			}
		}

	}

	/**
	 * Inner class for the JPanel that interacts with a work order in the
	 * InProgress state.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class WorkStatePanel extends JPanel implements ActionListener {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;
		/** Info panel that presents the work order's information to the user */
		private WorkOrderInfoPanel pnlInfo;
		/** Current work order's id */
		private int id;

		/** Button - reassign to a different technician */
		private JButton btnReassign;

		/** Label - rejection reason */
		private JLabel lblRejectionReason;
		/** Combo Box - rejection reason, required to cancel */
		private JComboBox<String> cmbRejectionReason;
		/** Button - requester cancels the job */
		private JButton btnCancelJob;

		/** Label - status note */
		private JLabel lblStatusNote;
		/** Text Field - status note, required */
		private JTextField txtStatusNote;
		/** Button - log a status update (self-loop) */
		private JButton btnStatusUpdate;

		/** Label - completion note */
		private JLabel lblNote;
		/** Text Field - completion note */
		private JTextField txtNote;
		/** Button - mark the job complete */
		private JButton btnComplete;

		/** Button - return to the list without a command */
		private JButton btnBack;

		/**
		 * Constructs the JPanel for editing a work order in the InProgress state.
		 */
		public WorkStatePanel() {
			super(new BorderLayout());

			pnlInfo = new WorkOrderInfoPanel();

			btnReassign = new JButton("Reassign");
			btnReassign.addActionListener(this);

			lblRejectionReason = new JLabel("Rejection Reason:");
			cmbRejectionReason = new JComboBox<String>();
			cmbRejectionReason.addItem(WorkOrder.WAITING);
			cmbRejectionReason.addItem(WorkOrder.CANCELLED);
			cmbRejectionReason.setSelectedIndex(-1);
			btnCancelJob = new JButton("Cancel Job");
			btnCancelJob.addActionListener(this);

			lblStatusNote = new JLabel("Status Note:");
			txtStatusNote = new JTextField(15);
			btnStatusUpdate = new JButton("Status Update");
			btnStatusUpdate.addActionListener(this);

			lblNote = new JLabel("Completion Note:");
			txtNote = new JTextField(15);
			btnComplete = new JButton("Complete");
			btnComplete.addActionListener(this);

			btnBack = new JButton("Back");
			btnBack.addActionListener(this);

			JPanel pnlCommands = new JPanel();
			Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder border = BorderFactory.createTitledBorder(lowerEtched, "Commands");
			pnlCommands.setBorder(border);
			pnlCommands.setLayout(new GridBagLayout());

			JPanel pnlUpdateRow = new JPanel(new GridLayout(1, 1));
			pnlUpdateRow.add(btnReassign);

			JPanel pnlCancelRow = new JPanel(new GridLayout(1, 3));
			pnlCancelRow.add(lblRejectionReason);
			pnlCancelRow.add(cmbRejectionReason);
			pnlCancelRow.add(btnCancelJob);

			JPanel pnlStatusRow = new JPanel(new GridLayout(1, 3));
			pnlStatusRow.add(lblStatusNote);
			pnlStatusRow.add(txtStatusNote);
			pnlStatusRow.add(btnStatusUpdate);

			JPanel pnlCompleteRow = new JPanel(new GridLayout(1, 3));
			pnlCompleteRow.add(lblNote);
			pnlCompleteRow.add(txtNote);
			pnlCompleteRow.add(btnComplete);

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.anchor = GridBagConstraints.LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			pnlCommands.add(pnlUpdateRow, c);

			c.gridx = 0;
			c.gridy = 1;
			pnlCommands.add(pnlCancelRow, c);

			c.gridx = 0;
			c.gridy = 2;
			pnlCommands.add(pnlStatusRow, c);

			c.gridx = 0;
			c.gridy = 3;
			pnlCommands.add(pnlCompleteRow, c);

			c.gridx = 0;
			c.gridy = 4;
			pnlCommands.add(btnBack, c);

			add(pnlInfo, BorderLayout.NORTH);
			add(pnlCommands, BorderLayout.SOUTH);
		}

		/**
		 * Set the info panel with the given work order's data.
		 * @param id id of the work order
		 */
		public void setInfo(int id) {
			this.id = id;
			pnlInfo.setWorkOrderInfo(this.id);
		}

		/**
		 * Performs an action based on the given ActionEvent.
		 * @param e user event that triggers an action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean reset = true;
			if (e.getSource() == btnReassign) {
				reset = runCommand(new Command(Command.CommandValue.REASSIGN, null));
			} else if (e.getSource() == btnStatusUpdate) {
				try {
					Command c = new Command(Command.CommandValue.STATUS, txtStatusNote.getText());
					reset = runCommand(c);
				} catch (IllegalArgumentException iae) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
					reset = false;
				}
			} else if (e.getSource() == btnCancelJob) {
				String reason = (String) cmbRejectionReason.getSelectedItem();
				if (reason == null) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
					reset = false;
				} else {
					reset = runCommand(new Command(Command.CommandValue.CANCEL, reason));
				}
			} else if (e.getSource() == btnComplete) {
				reset = runCommand(new Command(Command.CommandValue.COMPLETE, txtNote.getText()));
			}
			if (reset) {
				cardLayout.show(panel, SITE_LIST_PANEL);
				pnlSiteList.updateSite();
				WolfRepairGUI.this.repaint();
				WolfRepairGUI.this.validate();
				txtStatusNote.setText("");
				txtNote.setText("");
				cmbRejectionReason.setSelectedIndex(-1);
			}
		}

		/**
		 * Executes the given command on this panel's work order.
		 * @param c command to execute
		 * @return true if the command succeeded
		 */
		private boolean runCommand(Command c) {
			try {
				WolfRepair.getInstance().executeCommand(id, c);
				return true;
			} catch (IllegalArgumentException iae) {
				JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
				return false;
			} catch (UnsupportedOperationException uoe) {
				JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid state transition");
				return false;
			}
		}

	}

	/**
	 * Inner class for the JPanel that interacts with a work order in the
	 * Rejected state. Which command is offered depends on the work order's
	 * rejection note: a WAITING note offers Dispatch, rechecking technician
	 * availability straight into InProgress; any other note offers Reopen,
	 * sending the order back to UnderReview for reconsideration.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class RejectedStatePanel extends JPanel implements ActionListener {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;
		/** Info panel that presents the work order's information to the user */
		private WorkOrderInfoPanel pnlInfo;
		/** Current work order's id */
		private int id;

		/** Label - technician id, now that one may be available */
		private JLabel lblTechnician;
		/** Text Field - technician id */
		private JTextField txtTechnician;
		/** Button - dispatch the technician; only shown when the note is WAITING */
		private JButton btnDispatch;
		/** Row holding the dispatch controls, shown only when the note is WAITING */
		private JPanel pnlDispatchRow;

		/** Label - note carried into UnderReview on reconsideration */
		private JLabel lblReopenNote;
		/** Text Field - reconsideration note */
		private JTextField txtReopenNote;
		/** Button - send back to UnderReview; only shown when the note isn't WAITING */
		private JButton btnReopen;
		/** Row holding the reopen controls, shown only when the note isn't WAITING */
		private JPanel pnlReopenRow;

		/** Button - return to the list without a command */
		private JButton btnBack;

		/**
		 * Constructs the JPanel for editing a work order in the Rejected state.
		 */
		public RejectedStatePanel() {
			super(new BorderLayout());

			pnlInfo = new WorkOrderInfoPanel();

			lblTechnician = new JLabel("Technician Id:");
			txtTechnician = new JTextField(15);
			btnDispatch = new JButton("Dispatch");
			btnDispatch.addActionListener(this);
			pnlDispatchRow = new JPanel(new GridLayout(1, 3));
			pnlDispatchRow.add(lblTechnician);
			pnlDispatchRow.add(txtTechnician);
			pnlDispatchRow.add(btnDispatch);

			lblReopenNote = new JLabel("Reconsideration Note:");
			txtReopenNote = new JTextField(15);
			btnReopen = new JButton("Reopen");
			btnReopen.addActionListener(this);
			pnlReopenRow = new JPanel(new GridLayout(1, 3));
			pnlReopenRow.add(lblReopenNote);
			pnlReopenRow.add(txtReopenNote);
			pnlReopenRow.add(btnReopen);

			btnBack = new JButton("Back");
			btnBack.addActionListener(this);

			JPanel pnlCommands = new JPanel();
			Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder border = BorderFactory.createTitledBorder(lowerEtched, "Commands");
			pnlCommands.setBorder(border);
			pnlCommands.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.anchor = GridBagConstraints.LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			pnlCommands.add(pnlDispatchRow, c);

			c.gridx = 0;
			c.gridy = 1;
			pnlCommands.add(pnlReopenRow, c);

			c.gridx = 0;
			c.gridy = 2;
			pnlCommands.add(btnBack, c);

			add(pnlInfo, BorderLayout.NORTH);
			add(pnlCommands, BorderLayout.SOUTH);
		}

		/**
		 * Set the info panel with the given work order's data, showing only
		 * the Dispatch row if its note is WAITING, or only the Reopen row
		 * otherwise.
		 * @param id id of the work order
		 */
		public void setInfo(int id) {
			this.id = id;
			pnlInfo.setWorkOrderInfo(this.id);

			WorkOrder w = WolfRepair.getInstance().getWorkOrderById(id);
			boolean waiting = w != null && WorkOrder.WAITING.equals(w.getNote());
			pnlDispatchRow.setVisible(waiting);
			pnlReopenRow.setVisible(!waiting);
		}

		/**
		 * Performs an action based on the given ActionEvent.
		 * @param e user event that triggers an action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean reset = true;
			if (e.getSource() == btnDispatch) {
				try {
					Command c = new Command(Command.CommandValue.DISPATCH, txtTechnician.getText());
					WolfRepair.getInstance().executeCommand(id, c);
				} catch (IllegalArgumentException iae) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
					reset = false;
				} catch (UnsupportedOperationException uoe) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid state transition");
					reset = false;
				}
			} else if (e.getSource() == btnReopen) {
				try {
					Command c = new Command(Command.CommandValue.REOPEN, txtReopenNote.getText());
					WolfRepair.getInstance().executeCommand(id, c);
				} catch (IllegalArgumentException iae) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
					reset = false;
				} catch (UnsupportedOperationException uoe) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid state transition");
					reset = false;
				}
			}
			if (reset) {
				cardLayout.show(panel, SITE_LIST_PANEL);
				pnlSiteList.updateSite();
				WolfRepairGUI.this.repaint();
				WolfRepairGUI.this.validate();
				txtTechnician.setText("");
				txtReopenNote.setText("");
			}
		}

	}

	/**
	 * Inner class for the JPanel that interacts with a work order in the
	 * Completed state.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class CompletedStatePanel extends JPanel implements ActionListener {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;
		/** Info panel that presents the work order's information to the user */
		private WorkOrderInfoPanel pnlInfo;
		/** Current work order's id */
		private int id;

		/** Label - reopen note */
		private JLabel lblNote;
		/** Text Field - reopen note */
		private JTextField txtNote;
		/** Button - reopen the work order */
		private JButton btnReopen;
		/** Button - return to the list without a command */
		private JButton btnCancel;

		/**
		 * Constructs the JPanel for editing a work order in the Completed state.
		 */
		public CompletedStatePanel() {
			super(new BorderLayout());

			pnlInfo = new WorkOrderInfoPanel();

			lblNote = new JLabel("Reopen Note:");
			txtNote = new JTextField(15);
			btnReopen = new JButton("Reopen");
			btnCancel = new JButton("Cancel");
			btnReopen.addActionListener(this);
			btnCancel.addActionListener(this);

			JPanel pnlCommands = new JPanel();
			Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder border = BorderFactory.createTitledBorder(lowerEtched, "Commands");
			pnlCommands.setBorder(border);
			pnlCommands.setLayout(new GridBagLayout());

			JPanel pnlReopenRow = new JPanel(new GridLayout(1, 3));
			pnlReopenRow.add(lblNote);
			pnlReopenRow.add(txtNote);
			pnlReopenRow.add(btnReopen);

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.anchor = GridBagConstraints.LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			pnlCommands.add(pnlReopenRow, c);

			c.gridx = 0;
			c.gridy = 1;
			pnlCommands.add(btnCancel, c);

			add(pnlInfo, BorderLayout.NORTH);
			add(pnlCommands, BorderLayout.SOUTH);
		}

		/**
		 * Set the info panel with the given work order's data.
		 * @param id id of the work order
		 */
		public void setInfo(int id) {
			this.id = id;
			pnlInfo.setWorkOrderInfo(this.id);
		}

		/**
		 * Performs an action based on the given ActionEvent.
		 * @param e user event that triggers an action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean reset = true;
			if (e.getSource() == btnReopen) {
				try {
					Command c = new Command(Command.CommandValue.REOPEN, txtNote.getText());
					WolfRepair.getInstance().executeCommand(id, c);
				} catch (IllegalArgumentException iae) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid command");
					reset = false;
				} catch (UnsupportedOperationException uoe) {
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid state transition");
					reset = false;
				}
			}
			if (reset) {
				cardLayout.show(panel, SITE_LIST_PANEL);
				pnlSiteList.updateSite();
				WolfRepairGUI.this.repaint();
				WolfRepairGUI.this.validate();
				txtNote.setText("");
			}
		}

	}

	/**
	 * Inner class for the JPanel that allows filing a new work order against
	 * the active repair site.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class AddWorkOrderPanel extends JPanel implements ActionListener {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;

		/** Label - requester */
		private JLabel lblRequester;
		/** JTextField - requester */
		private JTextField txtRequester;

		/** Label - description */
		private JLabel lblDescription;
		/** JTextField - description */
		private JTextField txtDescription;

		/** Button to file the work order */
		private JButton btnAdd;
		/** Button for canceling the add action */
		private JButton btnCancel;

		/**
		 * Creates the JPanel for filing a new work order against the active
		 * repair site.
		 */
		public AddWorkOrderPanel() {
			super(new BorderLayout());

			lblRequester = new JLabel("Requester: ");
			lblDescription = new JLabel("Description: ");

			txtRequester = new JTextField(25);
			txtDescription = new JTextField(25);

			btnAdd = new JButton("Submit");
			btnCancel = new JButton("Cancel");

			btnAdd.addActionListener(this);
			btnCancel.addActionListener(this);

			JPanel pnlAdd = new JPanel();
			Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder border = BorderFactory.createTitledBorder(lowerEtched, "Add Work Order");
			pnlAdd.setBorder(border);
			pnlAdd.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			JPanel row1 = new JPanel(new GridLayout(1, 2));
			row1.add(lblRequester);
			row1.add(txtRequester);

			JPanel row2 = new JPanel(new GridLayout(1, 2));
			row2.add(lblDescription);
			row2.add(txtDescription);

			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 2;
			c.anchor = GridBagConstraints.LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			pnlAdd.add(row1, c);

			c.gridx = 0;
			c.gridy = 1;
			pnlAdd.add(row2, c);

			JPanel row3 = new JPanel(new GridLayout(1, 2));
			row3.add(btnAdd);
			row3.add(btnCancel);

			c.gridx = 0;
			c.gridy = 2;
			c.weighty = 1;
			pnlAdd.add(row3, c);

			add(pnlAdd, BorderLayout.NORTH);
		}

		/**
		 * Performs an action based on the given ActionEvent.
		 * @param e user event that triggers an action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean reset = true;
			if (e.getSource() == btnAdd) {
				try {
					WolfRepair.getInstance().addWorkOrderToSite(txtRequester.getText(), txtDescription.getText());
				} catch (IllegalArgumentException exp) {
					reset = false;
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Work order cannot be created.");
				}
			}
			if (reset) {
				cardLayout.show(panel, SITE_LIST_PANEL);
				pnlSiteList.updateSite();
				WolfRepairGUI.this.repaint();
				WolfRepairGUI.this.validate();
				txtRequester.setText("");
				txtDescription.setText("");
			}
		}

	}

	/**
	 * Inner class for the JPanel that allows creating a new repair site.
	 * @author Sarah Heckman
	 * @author Chandrika Satyavolu
	 */
	private class AddRepairSitePanel extends JPanel implements ActionListener {
		/** ID number used for object serialization. */
		private static final long serialVersionUID = 1L;

		/** Label - site name */
		private JLabel lblSiteName;
		/** JTextField - site name */
		private JTextField txtSiteName;

		/** Label - site category */
		private JLabel lblSiteCategory;
		/** JTextField - site category */
		private JTextField txtSiteCategory;

		/** Label - budget */
		private JLabel lblBudget;
		/** JTextField - budget */
		private JTextField txtBudget;

		/** Button - add the repair site */
		private JButton btnAdd;
		/** Button - cancel */
		private JButton btnCancel;

		/**
		 * Constructs the new repair site panel.
		 */
		public AddRepairSitePanel() {
			super(new BorderLayout());

			lblSiteName = new JLabel("Site Name: ");
			lblSiteCategory = new JLabel("Site Category: ");
			lblBudget = new JLabel("Repair Budget: ");

			txtSiteName = new JTextField(25);
			txtSiteCategory = new JTextField(15);
			txtBudget = new JTextField(5);

			btnAdd = new JButton("Add Repair Site");
			btnCancel = new JButton("Cancel");

			btnAdd.addActionListener(this);
			btnCancel.addActionListener(this);

			JPanel pnlAdd = new JPanel();
			Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder border = BorderFactory.createTitledBorder(lowerEtched, "Add Repair Site");
			pnlAdd.setBorder(border);
			pnlAdd.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			JPanel row1 = new JPanel(new GridLayout(1, 2));
			row1.add(lblSiteName);
			row1.add(txtSiteName);
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			pnlAdd.add(row1, c);

			JPanel row2 = new JPanel(new GridLayout(1, 2));
			row2.add(lblSiteCategory);
			row2.add(txtSiteCategory);
			c.gridx = 0;
			c.gridy = 1;
			c.anchor = GridBagConstraints.LINE_START;
			pnlAdd.add(row2, c);

			JPanel row3 = new JPanel(new GridLayout(1, 2));
			row3.add(lblBudget);
			row3.add(txtBudget);
			c.gridx = 0;
			c.gridy = 2;
			pnlAdd.add(row3, c);

			JPanel row4 = new JPanel(new GridLayout(1, 2));
			row4.add(btnAdd);
			row4.add(btnCancel);
			c.gridx = 0;
			c.gridy = 3;
			c.weighty = 20;
			pnlAdd.add(row4, c);

			add(pnlAdd, BorderLayout.NORTH);
		}

		/**
		 * Performs an action based on the given ActionEvent.
		 * @param e user event that triggers an action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean reset = true;
			if (e.getSource() == btnAdd) {
				try {
					String siteName = txtSiteName.getText();
					String siteCategory = txtSiteCategory.getText();
					int budget = Integer.parseInt(txtBudget.getText());

					WolfRepair.getInstance().addNewRepairSite(siteName, siteCategory, budget);
				} catch (NumberFormatException nfe) {
					reset = false;
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Invalid budget");
				} catch (IllegalArgumentException exp) {
					reset = false;
					JOptionPane.showMessageDialog(WolfRepairGUI.this, "Repair site cannot be created.");
				}
			}
			if (reset) {
				cardLayout.show(panel, SITE_LIST_PANEL);
				pnlSiteList.updateSite();
				WolfRepairGUI.this.repaint();
				WolfRepairGUI.this.validate();
				txtSiteName.setText("");
				txtSiteCategory.setText("");
				txtBudget.setText("");
			}
		}

	}

}