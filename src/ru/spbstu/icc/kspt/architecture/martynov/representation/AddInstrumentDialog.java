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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 *
 * Add instrument dialog
 */
@SuppressWarnings("serial")
public class AddInstrumentDialog extends JFrame {
	/**
	 * Constructor
	 * 
	 * @param parent frame
	 * @param API for market access
	 * @param instruments list
	 * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true
	 */
	public AddInstrumentDialog(JFrame parent, MarketService API, DefaultListModel<String> instruments)
			throws HeadlessException {
		super("Add Instrument Dialog");
		this.API = API;
		this.instruments = instruments;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(380, 180);
		setMinimumSize(new Dimension(380, 180));

		// Real name
		Box nameBox = Box.createHorizontalBox();
		JLabel nameLabel = new JLabel("Instrument code:");
		nameLabel.setHorizontalAlignment(JLabel.RIGHT);
		nameBox.add(nameLabel);
		nameBox.add(Box.createHorizontalStrut(6));
		nameBox.add(code);
		nameBox.setMaximumSize(new Dimension(260, 24));

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
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(buttonBox);
		getContentPane().add(mainBox, BorderLayout.NORTH);

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
			code.setText("");
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 * Cancel action
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
			parent.setEnabled(true);
			setVisible(false);
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 * Add instrument
	 */
	private class AddAction extends AbstractAction {
		/**
		 * Constructor
		 */
		AddAction() {
			putValue(Action.NAME, "Add");
			putValue(Action.SHORT_DESCRIPTION, "Add instrument");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (code.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Instrument code is not set!",
						"Instrument code is not set!", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			if (API.getInstrumentService().checkUniqueCode(code.getText()) == false) {
				JOptionPane.showMessageDialog(null, "Instrument code " + code.getText() + " is not unique!",
						"Code is not unique!", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			Instrument instrument = API.getInstrumentService().newInstrument(code.getText());
			API.getMarket().addInstrument(instrument);

			JOptionPane.showMessageDialog(null, "New instrument " + instrument.toString() + " is added!",
					"Instrument is added!", JOptionPane.INFORMATION_MESSAGE);
			instruments.addElement(instrument.getId().toString() + ". " + instrument.toString() + ", enabled="
					+ instrument.isEnabled());

			parent.setEnabled(true);
			setVisible(false);
		}
	}

	/**
	 * Instrument code field
	 */
	private JTextField code = new JTextField();

	/**
	 * parent frame
	 */
	private JFrame parent;
	/**
	 * API for market access
	 */
	private MarketService API;
	/**
	 * Instruments list
	 */
	private DefaultListModel<String> instruments;

}
