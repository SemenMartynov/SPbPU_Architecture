package ru.spbstu.icc.kspt.architecture.martynov.representation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Order;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 * 
 *         Add order dialog
 */
@SuppressWarnings("serial")
public class AddOrderDialog extends JFrame {

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            frame
	 * @param API
	 *            for market
	 * @param orders
	 *            list
	 * @throws HeadlessException
	 *             if GraphicsEnvironment.isHeadless() returns true.
	 */
	public AddOrderDialog(JFrame parent, MarketService API, DefaultListModel<String> orders) throws HeadlessException {
		super("Add Order Dialog");
		this.API = API;
		this.orders = orders;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(380, 180);
		setMinimumSize(new Dimension(380, 180));

		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
		DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
		decimalFormat.setGroupingUsed(false);

		// Volume
		Box volumeBox = Box.createHorizontalBox();
		JLabel volumeLabel = new JLabel("Volume:");
		volumeLabel.setHorizontalAlignment(JLabel.RIGHT);
		volumeBox.add(volumeLabel);
		volumeBox.add(Box.createHorizontalStrut(6));
		volume = new JFormattedTextField(decimalFormat);
		volumeBox.add(volume);
		volumeBox.setMaximumSize(new Dimension(260, 24));

		// Price
		Box priceBox = Box.createHorizontalBox();
		JLabel priceLabel = new JLabel("Price:");
		priceLabel.setHorizontalAlignment(JLabel.RIGHT);
		priceBox.add(priceLabel);
		priceBox.add(Box.createHorizontalStrut(6));
		price = new JFormattedTextField(decimalFormat);
		priceBox.add(price);
		priceBox.setMaximumSize(new Dimension(260, 24));

		// Direction box
		Box directionBox = Box.createHorizontalBox();
		JLabel directionLabel = new JLabel("Direct:");
		directionLabel.setHorizontalAlignment(JLabel.RIGHT);
		directionBox.add(directionLabel);
		directionBox.add(Box.createHorizontalStrut(6));
		directionBox.add(direction);
		directionBox.setMaximumSize(new Dimension(260, 24));

		// Instument box
		Box instumentBox = Box.createHorizontalBox();
		JLabel instrumentLabel = new JLabel("Instrument:");
		instrumentLabel.setHorizontalAlignment(JLabel.RIGHT);
		directionLabel.setPreferredSize(instrumentLabel.getPreferredSize());
		priceLabel.setPreferredSize(instrumentLabel.getPreferredSize());
		volumeLabel.setPreferredSize(instrumentLabel.getPreferredSize());
		instumentBox.add(instrumentLabel);
		instumentBox.add(Box.createHorizontalStrut(6));
		for (Instrument instrument : API.getInstrumentService().getEnabledInstruments(API.getMarket())) {
			instruments.addItem(instrument.getCode());
		}
		instumentBox.add(instruments);
		instumentBox.setMaximumSize(new Dimension(260, 24));

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
		mainBox.add(volumeBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(priceBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(directionBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(instumentBox);
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
	 *         Clear form action
	 */
	private class ClearFormAction extends AbstractAction {
		/**
		 * Constructor
		 */
		ClearFormAction() {
			putValue(Action.NAME, "Clear");
			putValue(Action.SHORT_DESCRIPTION, "Clear form");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			price.setText("");
			volume.setText("");
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Cancel action
	 */
	private class CancelAction extends AbstractAction {
		/**
		 * Constructor
		 */
		CancelAction() {
			putValue(Action.NAME, "Cancel");
			putValue(Action.SHORT_DESCRIPTION, "Cancel");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
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
	 *         Add action
	 */
	private class AddAction extends AbstractAction {
		/**
		 * Constructor
		 */
		AddAction() {
			putValue(Action.NAME, "Add");
			putValue(Action.SHORT_DESCRIPTION, "Add order");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Order.Direction selecterDirection;
			if (direction.getSelectedIndex() == 0) {
				selecterDirection = Order.Direction.ASK;
			} else {
				selecterDirection = Order.Direction.BID;
			}

			Long instumentId = API.getInstrumentService()
					.getInstrumentByCode(API.getMarket(), String.valueOf(instruments.getSelectedItem())).getId();

			Order order = API.getTradeService().newOrder(Long.parseLong(price.getText()),
					Long.parseLong(volume.getText()), selecterDirection, API.getAuthService().getUser().getId(),
					instumentId);
			if (order != null) {
				API.addOrder(order);
				orders.addElement(order.getId().toString() + ". " + order.toString());
			}

			parent.setEnabled(true);
			setVisible(false);
		}
	}

	/**
	 * parent frame
	 */
	private JFrame parent;
	/**
	 * API for market access
	 */
	private MarketService API;

	/**
	 * price field
	 */
	private JTextField price;
	/**
	 * volume field
	 */
	private JTextField volume;
	/**
	 * Possible direction
	 */
	private String[] directionStrings = { "ASK", "BID" };
	/**
	 * Direction selector
	 */
	private JComboBox<String> direction = new JComboBox<String>(directionStrings);
	/**
	 * Instrument selector
	 */
	private JComboBox<String> instruments = new JComboBox<String>();
	/**
	 * Orders list
	 */
	private DefaultListModel<String> orders;
}
