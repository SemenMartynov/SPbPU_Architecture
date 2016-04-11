package ru.spbstu.icc.kspt.architecture.martynov.service;

import java.util.Iterator;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Market;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Order;

/**
 * @author Semen Martynov
 *
 * Procedure for searching and removing expired orders.
 */
public class ExpirationCheckService implements Runnable {

	/**
	 * Access to the order API
	 */
	private TradeService tradeService = new TradeService();
	/**
	 * Access to the market
	 */
	private final Market market;

	/**
	 * Constructor.
	 * 
	 * @param market for monitoring
	 */
	public ExpirationCheckService(Market market) {
		this.market = market;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			checkExpiredOrder();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Instruments bypass.
	 */
	private void checkExpiredOrder() {
		Iterator<Instrument> iterator = market.getInstruments().iterator();
		while (iterator.hasNext()) {
			checkExpiredOrderInstrument(iterator.next());
		}		
	}

	/**
	 * Check orders in the selected instrument.
	 * 
	 * @param instrument for order expiration check
	 */
	private void checkExpiredOrderInstrument(Instrument instrument) {
		synchronized (instrument) { // thread safe
			Iterator<Order> iterator = instrument.getOrders().iterator();
			while (instrument.isEnabled() && iterator.hasNext()) { // Remove all expired orders
				Order order = iterator.next();
				if (order.isExpired()) {
					market.expireOrder(order);
					tradeService.cancelOrder(order);
					iterator.remove();
				}
			}
		}
	}

}
