package ru.spbstu.icc.kspt.architecture.martynov.representation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Trader;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 *
 * Add trader
 */
@SuppressWarnings("serial")
public class AddTraderDialog extends JFrame {
	/**
	 * Constructor
	 * 
	 * @param parent frame
	 * @param API for market access
	 * @param traders lisr
	 * @param trader for edit
	 * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
	 */
	public AddTraderDialog(JFrame parent, MarketService API, DefaultListModel<String> traders, Trader trader)
			throws HeadlessException {
		super("Add Trader Dialog");
		this.API = API;
		this.traders = traders;
		this.trader = trader;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(380, 180);
		setMinimumSize(new Dimension(380, 180));

		// Real name
		Box nameBox = Box.createHorizontalBox();
		JLabel nameLabel = new JLabel("Real name:");
		nameLabel.setHorizontalAlignment(JLabel.RIGHT);
		nameBox.add(nameLabel);
		nameBox.add(Box.createHorizontalStrut(6));
		nameBox.add(name);
		nameBox.setMaximumSize(new Dimension(260, 24));

		// User name
		Box userNameBox = Box.createHorizontalBox();
		JLabel userNameLabel = new JLabel("User name:");
		userNameLabel.setHorizontalAlignment(JLabel.RIGHT);
		userNameBox.add(userNameLabel);
		userNameBox.add(Box.createHorizontalStrut(6));
		userNameBox.add(userName);
		userNameBox.setMaximumSize(new Dimension(260, 24));

		// Password
		Box passwordBox = Box.createHorizontalBox();
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setHorizontalAlignment(JLabel.RIGHT);
		passwordBox.add(passwordLabel);
		passwordBox.add(Box.createHorizontalStrut(6));
		passwordBox.add(password);
		passwordBox.setMaximumSize(new Dimension(260, 24));

		// Password
		Box password2Box = Box.createHorizontalBox();
		JLabel password2Label = new JLabel("Password (for check):");
		password2Label.setHorizontalAlignment(JLabel.RIGHT);
		passwordLabel.setPreferredSize(password2Label.getPreferredSize());
		userNameLabel.setPreferredSize(password2Label.getPreferredSize());
		nameLabel.setPreferredSize(password2Label.getPreferredSize());
		password2Box.add(password2Label);
		password2Box.add(Box.createHorizontalStrut(6));
		password2Box.add(password2);
		password2Box.setMaximumSize(new Dimension(260, 24));

		// Buttons box
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(new JButton(new CancelAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalStrut(6));
		buttonBox.add(new JButton(new ClearFormAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalStrut(6));
		buttonBox.add(new JButton(new AddAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.setMaximumSize(new Dimension(260, 24));

		// Main panel
		Box mainBox = Box.createVerticalBox();
		mainBox.setBorder(new EmptyBorder(6, 0, 6, 6));
		mainBox.add(nameBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(userNameBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(passwordBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(password2Box);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(buttonBox);
		getContentPane().add(mainBox, BorderLayout.NORTH);

		if (trader != null) {
			name.setText(trader.getName());
			userName.setEditable(false);
			userName.setText(trader.getUserName());
		}

		this.parent = parent;
		parent.setEnabled(false);
	}

	/**
	 * @author Semen Martynov
	 *
	 * Clear form
	 */
	private class ClearFormAction extends AbstractAction {
		/**
		 * Constructor
		 */
		ClearFormAction() {
			putValue(Action.NAME, "Clear");
			putValue(Action.SHORT_DESCRIPTION, "Clear form");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (trader == null) {
				userName.setText("");
			}
			name.setText("");
			password.setText("");
			password2.setText("");
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 * Cancel
	 */
	private class CancelAction extends AbstractAction {
		/**
		 * Constructor
		 */
		CancelAction() {
			putValue(Action.NAME, "Cancel");
			putValue(Action.SHORT_DESCRIPTION, "Cancel");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			trader = null;
			userName.setEditable(true);
			parent.setEnabled(true);
			setVisible(false);
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 * Add trader
	 */
	private class AddAction extends AbstractAction {
		/**
		 * Constructor
		 */
		AddAction() {
			putValue(Action.NAME, "Add");
			putValue(Action.SHORT_DESCRIPTION, "Add trader");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (userName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "User name is not set!", "User name is not set!",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			String pass = new String(password.getPassword());
			String pass2 = new String(password2.getPassword());
			if (password.equals(pass2) == false) {
				JOptionPane.showMessageDialog(null, "Passwords are not equal!", "Password error!",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			if (trader == null) {
				trader = API.getUserService().addUser(name.getText(), userName.getText(), pass);
				JOptionPane.showMessageDialog(null, "Trader " + trader.getName() + " is added!", "Trader is added!",
						JOptionPane.INFORMATION_MESSAGE);
				traders.addElement(trader.toString());

			} else {

				trader.setName(name.getText());
				trader.setUserPassword(pass);
				API.getUserService().updateTrader(trader);

				JOptionPane.showMessageDialog(null, "Trader " + trader.getName() + " is updated!", "Trader is updated!",
						JOptionPane.INFORMATION_MESSAGE);
			}

			trader = null;
			userName.setEditable(true);
			parent.setEnabled(true);
			setVisible(false);
		}
	}

	/**
	 * Trade's name field
	 */
	private JTextField name = new JTextField();
	/**
	 * Trader's username field
	 */
	private JTextField userName = new JTextField();
	/**
	 * Trader's first password field
	 */
	private JPasswordField password = new JPasswordField();
	/**
	 * Trader's second password field (for error check)
	 */
	private JPasswordField password2 = new JPasswordField();

	/**
	 * Trader
	 */
	private Trader trader;
	/**
	 * Parent frame
	 */
	private JFrame parent;
	/**
	 * API for market access
	 */
	private MarketService API;
	/**
	 * traders list
	 */
	private DefaultListModel<String> traders;

}
