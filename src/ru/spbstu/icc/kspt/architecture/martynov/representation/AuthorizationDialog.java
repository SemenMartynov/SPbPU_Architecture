package ru.spbstu.icc.kspt.architecture.martynov.representation;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import ru.spbstu.icc.kspt.architecture.martynov.domain.User;
import ru.spbstu.icc.kspt.architecture.martynov.service.AuthService;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 *
 *         Authorization dialog
 */
@SuppressWarnings("serial")
public class AuthorizationDialog extends JFrame {

	/**
	 * Constructor
	 * 
	 * @param API
	 *            to market
	 */
	public AuthorizationDialog(MarketService API) {
		super("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(380, 165);
		// setMinimumSize(new Dimension(360, 145));
		setMinimumSize(new Dimension(380, 165));

		// Login box
		Box loginBox = Box.createHorizontalBox();
		JLabel loginLabel = new JLabel("Login:");
		loginLabel.setHorizontalAlignment(JLabel.RIGHT);
		loginBox.add(loginLabel);
		loginBox.add(Box.createHorizontalStrut(6));
		loginBox.add(login);
		loginBox.setMaximumSize(new Dimension(260, 24));
		// loginBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Password box
		Box passwordBox = Box.createHorizontalBox();
		JLabel passwordLabel = new JLabel("Password:");
		passwordBox.add(passwordLabel);
		passwordBox.add(Box.createHorizontalStrut(6));
		passwordBox.add(password);
		passwordBox.setMaximumSize(new Dimension(260, 24));
		// Size correction
		loginLabel.setPreferredSize(passwordLabel.getPreferredSize());
		// passwordBox.setBorder(new LineBorder(Color.BLUE, 4));

		// Role box
		Box roleBox = Box.createHorizontalBox();
		JLabel roleLabel = new JLabel("Role:");
		roleLabel.setHorizontalAlignment(JLabel.RIGHT);
		roleLabel.setPreferredSize(passwordLabel.getPreferredSize());
		roleBox.add(roleLabel);
		roleBox.add(Box.createHorizontalStrut(6));
		roleBox.add(role);
		roleBox.setMaximumSize(new Dimension(260, 24));
		// loginBox.setBorder(new LineBorder(Color.CORAL, 4));

		// Buttons box
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(new JButton(new ClearFormAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalStrut(6));
		buttonBox.add(new JButton(new RegisterAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalStrut(6));
		buttonBox.add(new JButton(new LoginAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.setMaximumSize(new Dimension(260, 24));

		// Left panel
		Box eastBox = Box.createVerticalBox();
		eastBox.setBorder(new EmptyBorder(6, 0, 6, 6));
		eastBox.add(loginBox);
		eastBox.add(Box.createVerticalStrut(6));
		eastBox.add(passwordBox);
		eastBox.add(Box.createVerticalStrut(6));
		eastBox.add(roleBox);
		eastBox.add(Box.createVerticalGlue());
		eastBox.add(buttonBox);
		getContentPane().add(eastBox, BorderLayout.EAST);

		// Progress bar
		getContentPane().add(progressBar, BorderLayout.SOUTH);

		// Image
		JPanel imagePanel = new ImagePanel(new ImageIcon("res/logo.jpg").getImage(), 6);
		getContentPane().add(imagePanel);

		this.API = API;
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Image panel
	 */
	private static class ImagePanel extends JPanel {
		/**
		 * Constructor
		 * 
		 * @param img
		 *            for display
		 * @param border
		 */
		public ImagePanel(Image img, int border) {
			this.image = img;
			this.border = Math.max(0, border);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		protected void paintComponent(Graphics g) {
			int min = Math.min(getWidth(), getHeight());
			min = Math.max(min - border * 2, 1);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(image, border, border, min, min, null);
		}

		/**
		 * Image for display
		 */
		private final Image image;
		/**
		 * Borders parameters
		 */
		private final int border;
	}

	/**
	 * @author Semen Martynov
	 * 
	 * Clear form action
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
			login.setText("");
			password.setText("");
		}
	}

	/**
	 * @author Semen Martynov
	 * 
	 * Register action
	 */
	private class RegisterAction extends AbstractAction {
		/**
		 * Constructor 
		 */
		RegisterAction() {
			putValue(Action.NAME, "Register");
			putValue(Action.SHORT_DESCRIPTION, "Register user");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (login.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Login is empty!", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "OK", "Registration compleeted", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 * Login action
	 */
	private class LoginAction extends AbstractAction {
		/**
		 * Constructor
		 */
		LoginAction() {
			putValue(Action.NAME, "Login");
			putValue(Action.SHORT_DESCRIPTION, "Login");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (login.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Login is empty!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			setEnabled(false);
			login.setEnabled(false);
			password.setEnabled(false);
			role.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			new Thread(new Runnable() {
				public void run() {
					for (int i = 0; i <= 20; i++) {
						try {
							final int progressValue = i * 5;
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									progressBar.setValue(progressValue);
								}
							});
							Thread.sleep(150);
						} catch (InterruptedException e1) {
							setEnabled(true);
							login.setEnabled(true);
							password.setEnabled(true);
							role.setEnabled(true);
							setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							e1.printStackTrace(System.err);
						}
					}

					AuthService authService = API.getAuthService();
					User.Access access;
					switch (role.getSelectedIndex()) {
					case 0:
						access = User.Access.ADMIN;
						break;
					case 1:
						access = User.Access.REGULATOR;
						break;
					default:
						access = User.Access.TRADER;
						break;
					}
					String pass = new String(password.getPassword());

					if (!authService.auth(login.getText(), pass, access)) {
						JOptionPane.showMessageDialog(null, "Login is not correct!", "Error",
								JOptionPane.ERROR_MESSAGE);
						setEnabled(true);
						login.setEnabled(true);
						password.setEnabled(true);
						role.setEnabled(true);
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						progressBar.setValue(0);
						return;
					}

					switch (authService.getUser().getAccess()) {
					case ADMIN:
						AdministratorBrowser administratorBrowser = new AdministratorBrowser(API);
						setVisible(false);
						administratorBrowser.setVisible(true);
						break;
					case REGULATOR:
						// JOptionPane.showMessageDialog(null, "Login is OK, but
						// you should use special API!", "Error",
						// JOptionPane.ERROR_MESSAGE);
						RegulatorBrowser regulatorBrowser = new RegulatorBrowser(API);
						setVisible(false);
						regulatorBrowser.setVisible(true);
						break;
					case TRADER:
						TraderBrowser traderBrowser = new TraderBrowser(API);
						setVisible(false);
						traderBrowser.setVisible(true);
						break;
					default:
						JOptionPane.showMessageDialog(null, "You can use system as anonymous user", "Error",
								JOptionPane.ERROR_MESSAGE);
						setEnabled(true);
						login.setEnabled(true);
						password.setEnabled(true);
						role.setEnabled(true);
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						progressBar.setValue(0);
					}
				}
			}).start();
		}
	}

	/**
	 * Progress bar
	 */
	private JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
	/**
	 * Login field
	 */
	private JTextField login = new JTextField();
	/**
	 * Password field
	 */
	private JPasswordField password = new JPasswordField();

	/**
	 * Available roles
	 */
	String[] roleStrings = { "Administrator", "Regulator", "Trader" };
	/**
	 * Roles combo box
	 */
	private JComboBox<String> role = new JComboBox<String>(roleStrings);
	/**
	 * Access to the market
	 */
	private MarketService API;
}