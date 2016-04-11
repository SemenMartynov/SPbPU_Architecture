package ru.spbstu.icc.kspt.architecture.martynov.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Semen Martynov
 * 
 *         A market is a place where traders can buy and/or sell instruments.
 */
public class Market {
	/**
	 * List of users who have subscribed to the market data.
	 */
	private Set<IMarketDataConsumer> consumers;
	/**
	 * List of instruments available on the market.
	 */
	private Map<Long, Instrument> instruments;

	/**
	 * Constructor
	 */
	public Market() {
		this.consumers = new HashSet<IMarketDataConsumer>();
		this.instruments = new TreeMap<Long, Instrument>();
	}

	/**
	 * Add market data subscriber.
	 * 
	 * @param consumer
	 *            for subscription
	 * @return true, if operation finished successfully
	 */
	public Boolean addConsumer(IMarketDataConsumer consumer) {
		return consumers.add(consumer);
	}

	/**
	 * Remove market data consumer from subscribers list.
	 * 
	 * @param consumer
	 *            for unsubscribe
	 * @return true, if operation finished successfully
	 */
	public Boolean removeConsumer(User consumer) {
		return consumers.remove(consumer);
	}

	/**
	 * Add an instrument for trading on the market.
	 * 
	 * @param instrument
	 *            for trading
	 * @return true, if operation finished successfully
	 */
	public Boolean addInstrument(Instrument instrument) {
		if (instrument.getId() == 0) {
			throw new RuntimeException("Instrument " + instrument.getCode() + " was not stored in the database!");
		}
		return instruments.put(instrument.getId(), instrument) == null;
	}

	/**
	 * Remove an instrument for trading on the market.
	 * 
	 * @param instrument
	 *            for remove from market
	 * @return true, if operation finished successfully
	 */
	public Boolean removeInstrument(Instrument instrument) {
		if (instrument.getId() == 0) {
			throw new RuntimeException("Instrument " + instrument.getCode() + " was not stored in the database!");
		}
		return instruments.remove(instrument.getId(), instrument);
	}

	/**
	 * Place an order on the market.
	 * 
	 * @param order
	 *            for publish
	 * @return true, if operation finished successfully
	 */
	public Boolean addOrder(Order order) {
		Instrument instrument = instruments.get(order.getInstrumentId());
		if (instrument == null || instrument.isEnabled() == false) {
			return false;
		}
		synchronized (instrument) {
			sendMarketData(instrument.toString() + ": " + order.toString() + " NEW ORDER!");
			return instrument.addOrder(order);
		}
	}

	/**
	 * Cancel published order.
	 * 
	 * @param order
	 *            for cancel
	 * @return true, if operation finished successfully
	 */
	public Boolean cancellOrder(Order order) {
		Instrument instrument = instruments.get(order.getInstrumentId());
		if (instrument == null || instrument.isEnabled() == false) {
			return false;
		}
		synchronized (instrument) {
			if (instrument.removeOrder(order)) {
				sendMarketData(order.toString() + " CANCELED!");
				return true;
			}
		}
		return false;
	}

	/**
	 * Change order to 'expired' status and publish market data.
	 * 
	 * @param order
	 *            for expire
	 * @return true, if operation finished successfully
	 */
	public Boolean expireOrder(Order order) {
		Instrument instrument = instruments.get(order.getInstrumentId());
		if (instrument == null || instrument.isEnabled() == false) {
			return false;
		}
		synchronized (instrument) {
			sendMarketData(instrument.toString() + ": " + order.toString() + " EXPIRED!");
			return instrument.removeOrder(order);
		}
	}

	/**
	 * Check order for deal.
	 * 
	 * @param order
	 *            for check
	 * @return true, if operation finished successfully
	 */
	public Order checkDeal(Order order) {
		Instrument instrument = instruments.get(order.getInstrumentId());
		if (instrument == null || instrument.isEnabled() == false) {
			return null;
		}
		synchronized (instrument) {
			Order pair = instrument.checkDeal(order);
			if (pair != null) {
				sendMarketData(instrument.toString() + ": " + order.toString() + " COMPLETED!");
				instrument.removeOrder(order);
				sendMarketData(instrument.toString() + ": " + pair.toString() + " COMPLETED!");
				instrument.removeOrder(pair);
			}
			return pair;
		}
	}

	/**
	 * Publish market data to subscribers.
	 * 
	 * @param message
	 *            for publish
	 */
	private synchronized void sendMarketData(String message) { // Thread safe
		Date date = new Date();
		String log = date.toString() + " -- " + message;
		for (IMarketDataConsumer consumer : consumers) {
			consumer.sendMarketData(log);
		}
	}

	/**
	 * Get list of active instruments.
	 * 
	 * @return list of instruments
	 */
	public List<Instrument> getInstruments() {
		return new ArrayList<Instrument>(instruments.values());
	}

}
