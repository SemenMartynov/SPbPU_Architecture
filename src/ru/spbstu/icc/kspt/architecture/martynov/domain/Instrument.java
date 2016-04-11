package ru.spbstu.icc.kspt.architecture.martynov.domain;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ru.spbstu.icc.kspt.architecture.martynov.infrastructure.ICacheable;

import static java.lang.Math.toIntExact;

/**
 * @author Semen Martynov
 * 
 *         Instrument is a tradable financial asset.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Security_%28finance%29">https://
 *      en.wikipedia.org/wiki/Security_%28finance%29</a>
 */
public class Instrument implements ICacheable, IEnableable {
	/**
	 * Instrument's identification number.
	 */
	private final Long id;
	/**
	 * Unique instrument name.
	 */
	private final String code;
	/**
	 * Set of ASK orders. All orders are stored in ascending order.
	 */
	private SortedSet<Order> askOrders = new TreeSet<Order>(new AskComparator());
	/**
	 * Set of BID orders. All orders are stored in descending order.
	 */
	private SortedSet<Order> bidOrders = new TreeSet<Order>(new BidComparator());
	/**
	 * Instrument can be temporary blocked.
	 */
	private Boolean enabled = true;

	/**
	 * Create pre-instrument with zero id.
	 * 
	 * @param code
	 *            name of the new instrument
	 */
	public Instrument(String code) {
		this.id = (long) 0;
		this.code = code;
	}

	/**
	 * Create new instrument.
	 * 
	 * @param id
	 *            according to the database
	 * @param code
	 *            name of the new instrument
	 * @param enabled
	 *            status
	 */
	public Instrument(Long id, String code, Boolean enabled) {
		this.id = id;
		this.code = code;
		this.enabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getCode();
	}

	/**
	 * Get code name of the new instrument.
	 * 
	 * @return code name of the new instrument
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Add new order to the orders list.
	 * 
	 * @param order
	 *            to add
	 * @return true, if operation ended successfully
	 */
	public boolean addOrder(Order order) {
		if (order.getDirection() == Order.Direction.ASK) {
			return askOrders.add(order);
		} else {
			return bidOrders.add(order);
		}
	}

	/**
	 * Remove order from order's list.
	 * 
	 * @param order
	 *            for remove
	 * @return true, if operation ended successfully
	 */
	public boolean removeOrder(Order order) {
		if (order.getDirection() == Order.Direction.ASK) {
			return askOrders.remove(order);
		} else {
			return bidOrders.remove(order);
		}
	}

	/**
	 * Check if we can make deal for the order.
	 * 
	 * @param order
	 *            for check
	 * @return deal order
	 */
	public Order checkDeal(Order order) {
		if (order.getDirection() == Order.Direction.ASK) {
			return checkDeal(bidOrders, order);
		} else {
			return checkDeal(askOrders, order);
		}
	}

	/**
	 * Implementation of checkDeal method.
	 * 
	 * @param orders
	 *            for check
	 * @param order
	 *            for check
	 * @return deal order
	 */
	private Order checkDeal(SortedSet<Order> orders, Order order) {
		for (Order pair : orders) {
			if (order.dealWith(pair)) {
				return pair;
			}
		}
		return null;
	}

	/**
	 * Collect all expired orders.
	 * 
	 * @return list of expired orders
	 */
	public List<Order> getExpiredOrders() {
		List<Order> result = new LinkedList<Order>();

		Iterator<Order> iterator = bidOrders.iterator();
		while (iterator.hasNext()) {
			Order Order = iterator.next();
			if (Order.isExpired()) {
				result.add(Order);
			}
		}

		iterator = askOrders.iterator();
		while (iterator.hasNext()) {
			Order Order = iterator.next();
			if (Order.isExpired()) {
				result.add(Order);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.spbstu.icc.kspt.edu_arh.martynov.domain.IEnableable#isEnabled()
	 */
	@Override
	public Boolean isEnabled() {
		return enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.spbstu.icc.kspt.edu_arh.martynov.domain.IEnableable#setEnabled()
	 */
	@Override
	public void setEnabled() {
		enabled = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.spbstu.icc.kspt.edu_arh.martynov.domain.IEnableable#setDisabled()
	 */
	@Override
	public void setDisabled() {
		enabled = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.ICacheable#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Get set of all orders.
	 * 
	 * @return set of all orders
	 */
	public Set<Order> getOrders() {
		Set<Order> result = new HashSet<Order>();

		for (Order order : askOrders) {
			result.add(order);
		}

		for (Order order : bidOrders) {
			result.add(order);
		}

		return result;
	}

	/**
	 * @author Semen Martynov
	 * 
	 *         Sorting orders in ascending order.
	 *
	 */
	private class AskComparator implements Comparator<Order> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Order o1, Order o2) {
			if (o1.equals(o2)) {
				return 0;
			}
			return toIntExact(o2.getPrice() - o1.getPrice());
		}
	}

	/**
	 * @author Semen Martynov
	 * 
	 *         Sorting orders in descending order.
	 *
	 */
	private class BidComparator implements Comparator<Order> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Order o1, Order o2) {
			if (o1.equals(o2)) {
				return 0;
			}
			return toIntExact(o1.getPrice() - o2.getPrice());
		}
	}
}
