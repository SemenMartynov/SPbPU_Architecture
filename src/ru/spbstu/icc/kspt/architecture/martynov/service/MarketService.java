package ru.spbstu.icc.kspt.architecture.martynov.service;

import java.util.Calendar;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Market;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Order;

/**
 * @author Semen Martynov
 *
 *         API for managing market.
 */
public class MarketService {
	/**
	 * Instruments management API
	 */
	private final InstrumentService instrumentService = new InstrumentService();
	/**
	 * Orders management API
	 */
	private final TradeService tradeService = new TradeService();
	/**
	 * Users management API
	 */
	private final UserService userService = new UserService();
	/**
	 * Market management API
	 */
	private final Market market;

	/**
	 * Auth API
	 */
	private final AuthService authService = new AuthService();

	/**
	 * Get instrument service.
	 * 
	 * @return the instrumentService
	 */
	public InstrumentService getInstrumentService() {
		return instrumentService;
	}

	/**
	 * Get trade service.
	 * 
	 * @return the tradeService
	 */
	public TradeService getTradeService() {
		return tradeService;
	}

	/**
	 * Get user service.
	 * 
	 * @return the userService
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * Get auth service.
	 * 
	 * @return the authService
	 */
	public AuthService getAuthService() {
		return authService;
	}

	/**
	 * Get market.
	 * 
	 * @return the market
	 */
	public Market getMarket() {
		return market;
	}

	/**
	 * Constructor.
	 * 
	 * @param market
	 *            for management
	 */
	public MarketService(Market market) {
		this.market = market;

		// Get all instruments from database
		for (Instrument instrument : instrumentService.getAllInstruments()) {
			market.addInstrument(instrument);
		}

		// Get all actual orders
		for (Order order : tradeService.getAllOrders()) {
			market.addOrder(order);
		}
	}

	/**
	 * Add order to the market.
	 * 
	 * @param order
	 *            for add
	 */
	public void addOrder(Order order) {
		if (userService.userEnabled(order.getTraderId()) == false) {
			return;
		}

		if (market.addOrder(order)) {
			matchOrder(order);
		}
	}

	/**
	 * Check order match.
	 * 
	 * @param order
	 *            for match
	 */
	private void matchOrder(Order order) {
		Order pair = market.checkDeal(order);

		if (pair != null) {
			tradeService.completeOrder(order);
			tradeService.completeOrder(pair);
		}
		return;
	}

	/**
	 * Cancel order.
	 * 
	 * @param order
	 *            for cancel
	 * @return true, if operation finished successfully
	 */
	public Boolean cancelOrder(Order order) {
		if (market.cancellOrder(order)) {
			return tradeService.cancelOrder(order);
		}
		return false;
	}

	/**
	 * Check access and cancel order.
	 * 
	 * @param orderId
	 *            of the trader owner
	 * @param traderId
	 *            for cancel
	 * @return true, if operation finished successfully
	 */
	public Boolean cancelOrder(Long orderId, Long traderId) {
		Order order = tradeService.getOrder(orderId);
		if (order.getTraderId() == traderId && market.cancellOrder(order)) {
			return tradeService.cancelOrder(order);
		}
		return false;
	}

	/**
	 * Get trader balance for a specific period.
	 * 
	 * @param traderId
	 *            for balance calculation
	 * @param begin
	 *            of the period
	 * @param end
	 *            of the period
	 * @return trader balance
	 */
	public Long getBalance(Long traderId, Calendar begin, Calendar end) {
		return tradeService.getBalance(traderId, begin, end);
	}

}
