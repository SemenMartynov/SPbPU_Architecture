package ru.spbstu.icc.kspt.architecture.martynov.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Order;
import ru.spbstu.icc.kspt.architecture.martynov.infrastructure.OrderStorage;

/**
 * @author Semen Martynov
 *
 *         API for managing orders.
 */
public class TradeService {

	/**
	 * Access to orders storage.
	 */
	private final OrderStorage orderStorage = new OrderStorage();

	/**
	 * Crate new order and store it to the storage.
	 * 
	 * @param price
	 *            of the order
	 * @param volume
	 *            of the order
	 * @param direction
	 *            of the order
	 * @param traderId
	 *            who placed the order
	 * @param instrumentId
	 *            that is bound to the order
	 * @return new order
	 */
	public Order newOrder(Long price, Long volume, Order.Direction direction, Long traderId, Long instrumentId) {
		Order preOrder = new Order(price, volume, direction, traderId, instrumentId);
		return orderStorage.create(preOrder);
	}

	/**
	 * Get order by id.
	 * 
	 * @param id
	 *            for order search
	 * @return order
	 */
	public Order getOrder(Long id) {
		return orderStorage.read(id);
	}

	/**
	 * Set order complete status to true
	 * 
	 * @param order
	 *            for update
	 * @return true, if operation finished successfully
	 */
	public Boolean completeOrder(Order order) {
		return orderStorage.update(order);
	}

	/**
	 * Cancel order.
	 * 
	 * @param order
	 *            for cancel
	 * @return true, if operation finished successfully
	 */
	public Boolean cancelOrder(Order order) {
		return orderStorage.delete(order);
	}

	/**
	 * Get set of orders.
	 * 
	 * @return set of orders
	 */
	public Set<Order> getAllOrders() {
		return orderStorage.getAllOrders();
	}

	/**
	 * Get all orders by instrument.
	 * 
	 * @param instrument
	 *            for orders
	 * @return set of orders
	 */
	public Set<Order> getAllOrders(Instrument instrument) {
		return instrument.getOrders();
	}

	/**
	 * Get all orders by instrument and trader
	 * 
	 * @param instrument
	 *            for search
	 * @param traderId
	 *            for search
	 * @return set of orders
	 */
	public Set<Order> getAllOrdersByTrader(Instrument instrument, Long traderId) {
		Set<Order> result = new HashSet<Order>();
		Iterator<Order> iterator = instrument.getOrders().iterator();
		while (iterator.hasNext()) {
			Order order = iterator.next();
			if (order.getTraderId() == traderId) {
				result.add(order);
			}
		}
		return result;
	}

	/**
	 * Get all orders by trader
	 * 
	 * @param traderId
	 *            for search
	 * @return set of orders
	 */
	public Set<Order> getAllOrdersByTrader(Long traderId) {
		Set<Order> result = new HashSet<Order>();
		Iterator<Order> iterator = getAllOrders().iterator();
		while (iterator.hasNext()) {
			Order order = iterator.next();
			if (order.getTraderId() == traderId) {
				result.add(order);
			}
		}
		return result;
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
		return orderStorage.getBalance(traderId, begin, end);
	}
}
