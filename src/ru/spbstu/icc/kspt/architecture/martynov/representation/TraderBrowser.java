package ru.spbstu.icc.kspt.architecture.martynov.representation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Order;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Trader;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 * 
 *         Trader browser
 */
@SuppressWarnings("serial")
public class TraderBrowser extends JFrame {

	/**
	 * Constructor
	 * 
	 * @param API
	 *            for market access
	 */
	public TraderBrowser(MarketService API) {
		super("Trader -- " + API.getAuthService().getUser().getName());
		this.API = API;
		trader = (Trader) API.getAuthService().getUser();
		API.getMarket().addConsumer(trader);

		orders = new DefaultListModel<String>();
		Iterator<Order> iterator = API.getTradeService().getAllOrdersByTrader(trader.getId()).iterator();
		while (iterator.hasNext()) {
			Order order = iterator.next();
			orders.addElement(order.getId().toString() + ". " + order.toString());
		}
		ordersList = new JList<String>(orders);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(800, 600);
		setMinimumSize(new Dimension(400, 200));

		// Main menu
		setJMenuBar(new JMenuBar() {
			{
				add(new JMenu("File") {
					{
						add(new CancelOrderAction());
						add(new GetBalanceAction());
						addSeparator();
						add(new RefreshAction());
						add(new ExitAction());
					}
				});
			}
		});

		// mardet data
		Box northBox = Box.createVerticalBox();
		northBox.setBorder(new EmptyBorder(6, 6, 6, 6));

		Box marketDataLabelBox = Box.createHorizontalBox();
		JLabel marketDataLabel = new JLabel("Market data:");
		marketDataLabel.setHorizontalAlignment(JLabel.CENTER);
		marketDataLabelBox.add(marketDataLabel);
		marketDataLabelBox.add(Box.createHorizontalGlue());
		northBox.add(marketDataLabelBox);
		marketData = new JTextArea(10, 10);
		marketData.setLineWrap(true); // wraps the words
		marketData.setEditable(false);
		marketData.setWrapStyleWord(true); // wraps by word instead of character
		JScrollPane MarketDataPane = new JScrollPane(marketData);
		northBox.add(MarketDataPane);

		// Buttons box
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(new JButton(new CreateNewOrderAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalStrut(6));
		buttonBox.add(new JButton(new CancelOrderAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalStrut(6));
		buttonBox.add(new JButton(new GetBalanceAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.setMaximumSize(new Dimension(400, 24));
		northBox.add(Box.createVerticalStrut(6));
		northBox.add(buttonBox);
		northBox.add(Box.createVerticalStrut(6));

		// Orders
		Box localOrdersLabelBox = Box.createHorizontalBox();
		JLabel localOrdersLabel = new JLabel("My orders:");
		localOrdersLabel.setHorizontalAlignment(JLabel.LEFT);
		localOrdersLabelBox.add(localOrdersLabel);
		localOrdersLabelBox.add(Box.createHorizontalGlue());
		northBox.add(localOrdersLabelBox);

		JScrollPane ordersPane = new JScrollPane(ordersList);
		ordersList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		northBox.add(ordersPane);
		getContentPane().add(northBox, BorderLayout.NORTH);

		// bottom buttons

		Box separatorBox = Box.createHorizontalBox();
		separatorBox.setBorder(new EmptyBorder(6, 6, 0, 6));
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separatorBox.add(separator);
		marketDataLabelBox.add(Box.createHorizontalGlue());
		northBox.add(separatorBox);

		Box bottomBox = Box.createHorizontalBox();
		bottomBox.setBorder(new EmptyBorder(0, 6, 6, 6));
		bottomBox.add(new JButton(new AboutAction()) {
			{
				setMaximumSize(new Dimension(100, 19));
			}
		}, BorderLayout.WEST);
		bottomBox.add(Box.createHorizontalStrut(6));
		bottomBox.add(new JButton(new RefreshAction()) {
			{
				setMaximumSize(new Dimension(100, 19));
			}
		}, BorderLayout.WEST);
		bottomBox.add(Box.createHorizontalStrut(6));
		bottomBox.add(new JButton(new ExitAction()) {
			{
				setMaximumSize(new Dimension(100, 19));
			}
		}, BorderLayout.WEST);
		getContentPane().add(bottomBox, BorderLayout.SOUTH);
		pack();
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Refresh action
	 */
	private class RefreshAction extends AbstractAction {
		/**
		 * Constructor
		 */
		RefreshAction() {
			putValue(Action.NAME, "Refresh");
			putValue(Action.SHORT_DESCRIPTION, "Refresh data");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (trader.getMarketData().isEmpty() == false) {
				marketData.setText(String.join("\n", trader.getMarketData()));
			}
		}
	}

	/**
	 * @author Semen Martynov
	 * 
	 *         Create new order
	 */
	private class CreateNewOrderAction extends AbstractAction {
		/**
		 * Constructor
		 */
		CreateNewOrderAction() {
			putValue(Action.NAME, "New");
			putValue(Action.SHORT_DESCRIPTION, "Create new order");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Component component = (Component) e.getSource();
			JFrame frame = (JFrame) SwingUtilities.getRoot(component);
			AddOrderDialog orderDialog = new AddOrderDialog(frame, API, orders);
			orderDialog.setVisible(true);
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Cancel order
	 */
	private class CancelOrderAction extends AbstractAction {
		/**
		 * Constructor
		 */
		CancelOrderAction() {
			putValue(Action.NAME, "Remove");
			putValue(Action.SHORT_DESCRIPTION, "Remove order");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedOrder = ordersList.getSelectedValue();
			if (selectedOrder == null) {
				return;
			}

			String selectedOrderId = selectedOrder.split("\\.")[0];
			if (selectedOrderId.isEmpty()) {
				return;
			}

			Long orderId = Long.parseLong(selectedOrderId);
			API.cancelOrder(orderId, API.getAuthService().getUser().getId());
			JOptionPane.showMessageDialog(null, "Order " + orderId.toString() + " removed!", "Order removed!",
					JOptionPane.INFORMATION_MESSAGE);
			ordersList.remove(ordersList.getSelectedIndex());
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Get balance
	 */
	private class GetBalanceAction extends AbstractAction {
		/**
		 * Constructor
		 */
		GetBalanceAction() {
			putValue(Action.NAME, "Balance");
			putValue(Action.SHORT_DESCRIPTION, "Get balance");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Component component = (Component) e.getSource();
			JFrame frame = (JFrame) SwingUtilities.getRoot(component);

			// JOptionPane.showMessageDialog(frame, "Balance: " +
			// trader.getBalance(), "Get balance", JOptionPane.PLAIN_MESSAGE);

			GetBalanceDialog getBalanceDialog = new GetBalanceDialog(frame, API);
			getBalanceDialog.setVisible(true);

		}
	}

	/**
	 * @author Semen Martynov
	 * 
	 *         Exits
	 */
	private class ExitAction extends AbstractAction {
		/**
		 * Constructor
		 */
		public ExitAction() {
			putValue(Action.NAME, "Exit");
			putValue(Action.SHORT_DESCRIPTION, "Exit program");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
			System.exit(0);
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         About...
	 */
	private class AboutAction extends AbstractAction {
		/**
		 * Constructor
		 */
		AboutAction() {
			putValue(Action.NAME, "About");
			putValue(Action.SHORT_DESCRIPTION, "About this program");
		}

		/**
		 * Displays a window with information about the program.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "Simple stock exchange.", "About...", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Market data field
	 */
	private JTextArea marketData;
	/**
	 * Current trader
	 */
	private Trader trader;
	/**
	 * API for market access
	 */
	private MarketService API;

	/**
	 * All trader's orders
	 */
	private DefaultListModel<String> orders;
	/**
	 * Order selector
	 */
	private JList<String> ordersList;
}