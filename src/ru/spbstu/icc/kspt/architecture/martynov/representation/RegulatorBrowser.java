package ru.spbstu.icc.kspt.architecture.martynov.representation;

import java.awt.BorderLayout;
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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Regulator;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Trader;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 * 
 *         Regulator browser
 */
@SuppressWarnings("serial")
public class RegulatorBrowser extends JFrame {
	/**
	 * Constructor
	 * 
	 * @param API
	 *            for market access
	 */
	public RegulatorBrowser(MarketService API) {
		super("Regulator -- " + API.getAuthService().getUser().getName());
		this.API = API;
		regulator = (Regulator) API.getAuthService().getUser();
		API.getMarket().addConsumer(regulator);

		traders = new DefaultListModel<String>();
		Iterator<Trader> titerator = API.getUserService().getAllTraders().iterator();
		while (titerator.hasNext()) {
			Trader trader = titerator.next();
			traders.addElement(trader.toString());
		}
		tradersList = new JList<String>(traders);

		instruments = new DefaultListModel<String>();
		Iterator<Instrument> iiterator = API.getInstrumentService().getAllInstruments(API.getMarket()).iterator();
		while (iiterator.hasNext()) {
			Instrument instrument = iiterator.next();
			instruments.addElement(instrument.getId().toString() + ". " + instrument.toString() + ", enabled="
					+ instrument.isEnabled());
		}
		instrumentsList = new JList<String>(instruments);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(800, 600);
		setMinimumSize(new Dimension(600, 200));

		// Main menu
		setJMenuBar(new JMenuBar() {
			{
				add(new JMenu("File") {
					{
						add(new DisableTraderAction());
						addSeparator();
						add(new DisableInstrumentAction());
						addSeparator();
						add(new RefreshAction());
						add(new ExitAction());
					}
				});
			}
		});

		// mardet data
		Box westBox = Box.createVerticalBox();
		westBox.setBorder(new EmptyBorder(6, 6, 6, 6));

		Box marketDataLabelBox = Box.createHorizontalBox();
		JLabel marketDataLabel = new JLabel("Market data:");
		marketDataLabel.setHorizontalAlignment(JLabel.CENTER);
		marketDataLabelBox.add(marketDataLabel);
		marketDataLabelBox.add(Box.createHorizontalGlue());
		westBox.add(marketDataLabelBox);
		marketData = new JTextArea(19, 30);
		marketData.setLineWrap(true); // wraps the words
		marketData.setEditable(false);
		marketData.setWrapStyleWord(true); // wraps by word instead of character
		JScrollPane MarketDataPane = new JScrollPane(marketData);
		westBox.add(MarketDataPane);
		// getContentPane().add(westBox, BorderLayout.EAST);

		Box mainBox = Box.createVerticalBox();
		mainBox.setBorder(new EmptyBorder(6, 6, 6, 6));

		// Traders
		Box tradersLabelBox = Box.createHorizontalBox();
		JLabel tradersLabel = new JLabel("Traders:");
		tradersLabel.setHorizontalAlignment(JLabel.CENTER);
		tradersLabelBox.add(tradersLabel);
		tradersLabelBox.add(Box.createHorizontalGlue());
		mainBox.add(tradersLabelBox);
		JScrollPane tradersPane = new JScrollPane(tradersList);
		instrumentsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		mainBox.add(tradersPane);
		getContentPane().add(mainBox, BorderLayout.NORTH);

		// Trader buttons box
		Box traderButtonBox = Box.createHorizontalBox();
		traderButtonBox.add(Box.createHorizontalGlue());
		traderButtonBox.add(new JButton(new DisableTraderAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		traderButtonBox.add(Box.createHorizontalStrut(6));
		traderButtonBox.add(new JButton(new RemoveTraderAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		traderButtonBox.add(Box.createHorizontalGlue());
		traderButtonBox.setMaximumSize(new Dimension(400, 24));
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(traderButtonBox);
		mainBox.add(Box.createVerticalStrut(6));

		// Instruments
		Box instrumentsLabelBox = Box.createHorizontalBox();
		JLabel istrumentsLabel = new JLabel("Instruments:");
		istrumentsLabel.setHorizontalAlignment(JLabel.LEFT);
		instrumentsLabelBox.add(istrumentsLabel);
		instrumentsLabelBox.add(Box.createHorizontalGlue());
		mainBox.add(instrumentsLabelBox);
		JScrollPane instrumentsPane = new JScrollPane(instrumentsList);
		instrumentsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		mainBox.add(instrumentsPane);
		// getContentPane().add(mainBox, BorderLayout.NORTH);

		// Instrument buttons box
		Box instrumentButtonBox = Box.createHorizontalBox();
		instrumentButtonBox.add(Box.createHorizontalGlue());
		instrumentButtonBox.add(new JButton(new DisableInstrumentAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		instrumentButtonBox.add(Box.createHorizontalStrut(6));
		instrumentButtonBox.add(new JButton(new RemoveInstrumentAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		instrumentButtonBox.add(Box.createHorizontalGlue());
		instrumentButtonBox.setMaximumSize(new Dimension(400, 24));
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(instrumentButtonBox);
		mainBox.add(Box.createVerticalStrut(6));

		Box northBox = Box.createHorizontalBox();
		northBox.add(Box.createHorizontalGlue());
		northBox.add(westBox);
		northBox.add(Box.createHorizontalStrut(6));
		northBox.add(mainBox);
		northBox.add(Box.createHorizontalGlue());
		getContentPane().add(northBox, BorderLayout.NORTH);

		// bottom buttons

		Box separatorBox = Box.createHorizontalBox();
		separatorBox.setBorder(new EmptyBorder(6, 6, 0, 6));
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separatorBox.add(separator);
		// marketDataLabelBox.add(Box.createHorizontalGlue());
		mainBox.add(separatorBox);

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
			traders.clear();
			Iterator<Trader> titerator = API.getUserService().getAllTraders().iterator();
			while (titerator.hasNext()) {
				Trader trader = titerator.next();
				traders.addElement(trader.toString());
			}

			instruments.clear();
			Iterator<Instrument> iiterator = API.getInstrumentService().getAllInstruments(API.getMarket()).iterator();
			while (iiterator.hasNext()) {
				Instrument instrument = iiterator.next();
				instruments.addElement(instrument.getId().toString() + ". " + instrument.toString() + ", enabled="
						+ instrument.isEnabled());
			}

			if (regulator.getMarketData().isEmpty() == false) {
				marketData.setText(String.join("\n", regulator.getMarketData()));
			}
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Disable trader
	 */
	private class DisableTraderAction extends AbstractAction {
		/**
		 * Constructor
		 */
		DisableTraderAction() {
			putValue(Action.NAME, "Disable");
			putValue(Action.SHORT_DESCRIPTION, "Disable/Enable trader");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedTrader = tradersList.getSelectedValue();
			if (selectedTrader == null) {
				return;
			}

			String selectedTraderId = selectedTrader.split("\\.")[0];
			if (selectedTraderId.isEmpty()) {
				return;
			}

			Long traderId = Long.parseLong(selectedTraderId);
			Trader trader = API.getUserService().getTrader(traderId);
			if (trader.isEnabled()) {
				API.getUserService().disableTrader(trader);
				JOptionPane.showMessageDialog(null, "Trader " + trader.getName() + " disabled!", "Trader disabled!",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				API.getUserService().enableTrader(trader);
				JOptionPane.showMessageDialog(null, "Trader " + trader.getName() + " enabled!", "Trader enabled!",
						JOptionPane.INFORMATION_MESSAGE);
			}
			tradersList.remove(tradersList.getSelectedIndex());
			traders.addElement(trader.toString());
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Remove trader
	 */
	private class RemoveTraderAction extends AbstractAction {
		/**
		 * Constructor
		 */
		RemoveTraderAction() {
			putValue(Action.NAME, "Remove");
			putValue(Action.SHORT_DESCRIPTION, "Complitly remove trader");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedTrader = tradersList.getSelectedValue();
			if (selectedTrader == null) {
				return;
			}

			String selectedTraderId = selectedTrader.split("\\.")[0];
			if (selectedTraderId.isEmpty()) {
				return;
			}

			Long traderId = Long.parseLong(selectedTraderId);
			Trader trader = API.getUserService().getTrader(traderId);
			JOptionPane.showMessageDialog(null, "Trader " + trader.getName() + " is removed!", "Trader is removed!",
					JOptionPane.INFORMATION_MESSAGE);
			tradersList.remove(tradersList.getSelectedIndex());
			API.getUserService().deleteTrader(trader);
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Disable instrument
	 */
	private class DisableInstrumentAction extends AbstractAction {
		/**
		 * Constructor
		 */
		DisableInstrumentAction() {
			putValue(Action.NAME, "Disable");
			putValue(Action.SHORT_DESCRIPTION, "Disable/Enable instrument");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedInstrument = instrumentsList.getSelectedValue();
			if (selectedInstrument == null) {
				return;
			}

			String selectedInstrumentId = selectedInstrument.split("\\.")[0];
			if (selectedInstrumentId.isEmpty()) {
				return;
			}

			Long instrumentId = Long.parseLong(selectedInstrumentId);
			Instrument instrument = API.getInstrumentService().getInstrument(instrumentId);
			if (instrument.isEnabled()) {
				API.getInstrumentService().disableInstrument(instrument);
				JOptionPane.showMessageDialog(null, "Instrument " + instrument.getCode() + " disabled!",
						"Instrument disabled!", JOptionPane.INFORMATION_MESSAGE);
			} else {
				API.getInstrumentService().enableInstrument(instrument);
				JOptionPane.showMessageDialog(null, "OK", "Instrument " + instrument.getCode() + " enabled!",
						JOptionPane.INFORMATION_MESSAGE);
			}
			instrumentsList.remove(instrumentsList.getSelectedIndex());
			instruments.addElement(instrument.getId().toString() + ". " + instrument.toString() + ", enabled="
					+ instrument.isEnabled());
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Remove instrument
	 */
	private class RemoveInstrumentAction extends AbstractAction {
		/**
		 * Constructor
		 */
		RemoveInstrumentAction() {
			putValue(Action.NAME, "Remove");
			putValue(Action.SHORT_DESCRIPTION, "Complitly remove instrument");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedInstrument = instrumentsList.getSelectedValue();
			if (selectedInstrument == null) {
				return;
			}

			String selectedInstrumentId = selectedInstrument.split("\\.")[0];
			if (selectedInstrumentId.isEmpty()) {
				return;
			}

			Long instrumentId = Long.parseLong(selectedInstrumentId);
			Instrument instrument = API.getInstrumentService().getInstrument(instrumentId);
			JOptionPane.showMessageDialog(null, "Instrument " + instrument.getCode() + " is removed!",
					"Instrument is removed!", JOptionPane.INFORMATION_MESSAGE);
			instrumentsList.remove(instrumentsList.getSelectedIndex());
			API.getInstrumentService().deleteInstrument(instrument);
		}
	}

	/**
	 * @author Semen Martynov
	 * 
	 *         Exits.
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
	 * market data field
	 */
	private JTextArea marketData;
	/**
	 * Regulator
	 */
	private Regulator regulator;
	/**
	 * API for market data access
	 */
	private MarketService API;

	/**
	 * Trader list
	 */
	private DefaultListModel<String> traders;
	/**
	 * Trader selector
	 */
	private JList<String> tradersList;

	/**
	 * Instrument list
	 */
	private DefaultListModel<String> instruments;
	/**
	 * Instrument selector
	 */
	private JList<String> instrumentsList;
}