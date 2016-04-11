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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Trader;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 * 
 *         Administrator browser
 */
@SuppressWarnings("serial")
public class AdministratorBrowser extends JFrame {

	/**
	 * Constructor
	 * 
	 * @param API
	 *            for market access
	 */
	public AdministratorBrowser(MarketService API) {
		super("Administrator -- " + API.getAuthService().getUser().getName());
		this.API = API;

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
		setMinimumSize(new Dimension(400, 200));

		// Main menu
		setJMenuBar(new JMenuBar() {
			{
				add(new JMenu("File") {
					{
						add(new CreateNewTraderAction());
						add(new EditTraderAction());
						add(new DisableTraderAction());
						addSeparator();
						add(new CreateNewInstrumentAction());
						add(new EditInstrumentAction());
						add(new DisableInstrumentAction());
						addSeparator();
						add(new RefreshAction());
						add(new ExitAction());
					}
				});
			}
		});

		Box northBox = Box.createVerticalBox();
		northBox.setBorder(new EmptyBorder(6, 6, 6, 6));

		// Traders
		Box tradersLabelBox = Box.createHorizontalBox();
		JLabel tradersLabel = new JLabel("Traders:");
		tradersLabel.setHorizontalAlignment(JLabel.CENTER);
		tradersLabelBox.add(tradersLabel);
		tradersLabelBox.add(Box.createHorizontalGlue());
		northBox.add(tradersLabelBox);
		JScrollPane tradersPane = new JScrollPane(tradersList);
		instrumentsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		northBox.add(tradersPane);
		getContentPane().add(northBox, BorderLayout.NORTH);

		// Trader buttons box
		Box traderButtonBox = Box.createHorizontalBox();
		traderButtonBox.add(Box.createHorizontalGlue());
		traderButtonBox.add(new JButton(new CreateNewTraderAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		traderButtonBox.add(Box.createHorizontalStrut(6));
		traderButtonBox.add(new JButton(new EditTraderAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		traderButtonBox.add(Box.createHorizontalStrut(6));
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
		northBox.add(Box.createVerticalStrut(6));
		northBox.add(traderButtonBox);
		northBox.add(Box.createVerticalStrut(6));

		// Instruments
		Box instrumentsLabelBox = Box.createHorizontalBox();
		JLabel istrumentsLabel = new JLabel("Instruments:");
		istrumentsLabel.setHorizontalAlignment(JLabel.LEFT);
		instrumentsLabelBox.add(istrumentsLabel);
		instrumentsLabelBox.add(Box.createHorizontalGlue());
		northBox.add(instrumentsLabelBox);
		JScrollPane instrumentsPane = new JScrollPane(instrumentsList);
		instrumentsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		northBox.add(instrumentsPane);
		getContentPane().add(northBox, BorderLayout.NORTH);

		// Instrument buttons box
		Box instrumentButtonBox = Box.createHorizontalBox();
		instrumentButtonBox.add(Box.createHorizontalGlue());
		instrumentButtonBox.add(new JButton(new CreateNewInstrumentAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		instrumentButtonBox.add(Box.createHorizontalStrut(6));
		instrumentButtonBox.add(new JButton(new EditInstrumentAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		instrumentButtonBox.add(Box.createHorizontalStrut(6));
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
		northBox.add(Box.createVerticalStrut(6));
		northBox.add(instrumentButtonBox);
		northBox.add(Box.createVerticalStrut(6));

		// bottom buttons

		Box separatorBox = Box.createHorizontalBox();
		separatorBox.setBorder(new EmptyBorder(6, 6, 0, 6));
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separatorBox.add(separator);
		// marketDataLabelBox.add(Box.createHorizontalGlue());
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
	 *         Refresh screen
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

			// if (trader.getMarketData().isEmpty() == false) {
			// marketData.setText(String.join("\n", trader.getMarketData()));
			// }
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Create new trader
	 */
	private class CreateNewTraderAction extends AbstractAction {
		/**
		 * Constructor
		 */
		CreateNewTraderAction() {
			putValue(Action.NAME, "New");
			putValue(Action.SHORT_DESCRIPTION, "Create new trader");
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

			AddTraderDialog addTraderDialog = new AddTraderDialog(frame, API, traders, null);
			addTraderDialog.setVisible(true);
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Edit trader
	 */
	private class EditTraderAction extends AbstractAction {
		/**
		 * Constructor
		 */
		EditTraderAction() {
			putValue(Action.NAME, "Edit");
			putValue(Action.SHORT_DESCRIPTION, "Edit trader");
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

			AddTraderDialog addTraderDialog = new AddTraderDialog(frame, API, traders, trader);
			addTraderDialog.setVisible(true);
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
	 *         Create new instrument
	 */
	private class CreateNewInstrumentAction extends AbstractAction {
		/**
		 * Constructor
		 */
		CreateNewInstrumentAction() {
			putValue(Action.NAME, "New");
			putValue(Action.SHORT_DESCRIPTION, "Create new instrument");
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

			AddInstrumentDialog addInstrumentDialog = new AddInstrumentDialog(frame, API, instruments);
			addInstrumentDialog.setVisible(true);
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 *         Edit instrument
	 */
	private class EditInstrumentAction extends AbstractAction {
		/**
		 * Constructor
		 */
		EditInstrumentAction() {
			putValue(Action.NAME, "Edit");
			putValue(Action.SHORT_DESCRIPTION, "Edit instrument");
			this.setEnabled(false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

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

		/**
		 * Exits.
		 */
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
	 * Api for market access
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