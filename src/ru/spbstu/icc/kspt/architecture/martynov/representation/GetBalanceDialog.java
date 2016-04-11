package ru.spbstu.icc.kspt.architecture.martynov.representation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;

import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 *
 * Get balance dialog
 */
@SuppressWarnings("serial")
public class GetBalanceDialog extends JFrame {

	/**
	 * Constructor
	 * 
	 * @param parent frame
	 * @param API to the market
	 */
	public GetBalanceDialog(JFrame parent, MarketService API) {
		super("Get balance");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(380, 165);
		setMinimumSize(new Dimension(380, 165));

		this.API = API;
		dateFrom = new JSpinner(new SpinnerDateModel());
		dateTo = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor dateFromEditor = new JSpinner.DateEditor(dateFrom, "dd/MM/yyyy HH:mm:ss");
		JSpinner.DateEditor dateToEditor = new JSpinner.DateEditor(dateTo, "dd/MM/yyyy HH:mm:ss");
		dateFrom.setEditor(dateFromEditor);
		dateTo.setEditor(dateToEditor);
		dateFrom.setValue(new Date()); // will only show the current time
		dateTo.setValue(new Date()); // will only show the current time

		// Date from
		Box dateFromBox = Box.createHorizontalBox();
		JLabel dateFromLabel = new JLabel("Date from:");
		dateFromLabel.setHorizontalAlignment(JLabel.RIGHT);
		dateFromBox.add(dateFromLabel);
		dateFromBox.add(Box.createHorizontalStrut(6));
		dateFromBox.add(dateFrom);
		dateFromBox.setMaximumSize(new Dimension(260, 24));

		// Date to
		Box dateToBox = Box.createHorizontalBox();
		JLabel dateToLabel = new JLabel("Date to:");
		dateToLabel.setHorizontalAlignment(JLabel.RIGHT);
		dateToBox.add(dateToLabel);
		dateToBox.add(Box.createHorizontalStrut(6));
		dateToBox.add(dateTo);
		dateToBox.setMaximumSize(new Dimension(260, 24));
		// Size correction
		dateToLabel.setPreferredSize(dateFromLabel.getPreferredSize());

		// result box
		Box resultBox = Box.createHorizontalBox();
		JLabel resultLabel = new JLabel("Result:");
		resultShow = new JLabel("00");
		resultLabel.setHorizontalAlignment(JLabel.RIGHT);
		resultBox.add(resultLabel);
		resultBox.add(Box.createHorizontalStrut(6));
		resultBox.add(resultShow);
		resultBox.setMaximumSize(new Dimension(260, 24));
		// Size correction
		resultLabel.setPreferredSize(dateFromLabel.getPreferredSize());

		// Buttons box
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(new JButton(new CancelFormAction()) {
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
		buttonBox.add(new JButton(new CalculateAction()) {
			{
				// setMaximumSize(new Dimension(100, 50));
			}
		});
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.setMaximumSize(new Dimension(360, 24));

		// Main box
		Box mainBox = Box.createVerticalBox();
		mainBox.setBorder(new EmptyBorder(6, 0, 6, 6));
		mainBox.add(dateFromBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(dateToBox);
		mainBox.add(Box.createVerticalStrut(6));
		mainBox.add(resultBox);
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(buttonBox);
		getContentPane().add(mainBox, BorderLayout.CENTER);

		this.parent = parent;
		parent.setEnabled(false);
	}

	/**
	 * @author Semen Martynov
	 *
	 * Cancel form action
	 */
	private class CancelFormAction extends AbstractAction {
		/**
		 * Constructor
		 */
		CancelFormAction() {
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
			dateFrom.setValue(new Date());
			dateTo.setValue(new Date());
			resultShow.setText("00");
		}
	}

	/**
	 * @author Semen Martynov
	 *
	 * Calculate action
	 */
	private class CalculateAction extends AbstractAction {
		/**
		 * Constructor
		 */
		CalculateAction() {
			putValue(Action.NAME, "Calculate");
			putValue(Action.SHORT_DESCRIPTION, "Calculate balance");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Calendar begin = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			begin.setTime((Date) dateFrom.getValue());
			end.setTime((Date) dateTo.getValue());
			if (begin.getTime().getTime() >= end.getTime().getTime()) {
				return;
			}
			Long balance = API.getBalance(API.getAuthService().getUser().getId(), begin, end);
			resultShow.setText(balance.toString());
		}
	}
	
	/**
	 * Parent frame
	 */
	private JFrame parent;
	/**
	 * Market access
	 */
	private MarketService API;

	/**
	 * Period begin
	 */
	private JSpinner dateFrom;
	/**
	 * Perion end
	 */
	private JSpinner dateTo;
	/**
	 * Label for results
	 */
	private JLabel resultShow;
}
