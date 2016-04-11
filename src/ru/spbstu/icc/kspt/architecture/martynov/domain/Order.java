package ru.spbstu.icc.kspt.architecture.martynov.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.spbstu.icc.kspt.architecture.martynov.infrastructure.ICacheable;

/**
 * @author Seman Martynov
 * 
 *         Order is an instruction to buy or sell some instrument on a trading
 *         market.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Order_%28exchange%29">https://en.
 *      wikipedia.org/wiki/Order_%28exchange%29</a>
 */
public class Order implements ICacheable {
	/**
	 * Two types of direction: ASK -- offer price, BID -- highest price that
	 * trader is willing to pay.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Ask_price">https://en.
	 *      wikipedia.org/wiki/Ask_price</a>
	 * @see <a href="https://en.wikipedia.org/wiki/Bid_price">https://en.
	 *      wikipedia.org/wiki/Bid_price</a>
	 */
	public enum Direction {
		ASK, BID
	};

	/**
	 * Order identification number.
	 */
	private final Long id;
	/**
	 * Order price.
	 */
	private final Long price;
	/**
	 * Order volume.
	 */
	private final Long volume;
	/**
	 * Date and time of order creation.
	 */
	private final Calendar date;
	/**
	 * Order direction (BID/ASK).
	 */
	private final Direction direction;
	/**
	 * Number trader who placed the order.
	 */
	private final Long traderId;
	/**
	 * Instrument number that is bound to the order.
	 */
	private final Long instrumentId;

	/**
	 * Create pre-order with zero id.
	 * 
	 * @param price
	 *            of the order
	 * @param volume
	 *            of the order
	 * @param direction
	 *            of the order (ASK/BID)
	 * @param trader
	 *            number who placed the order
	 * @param instrument
	 *            number that is bound to the order
	 */
	public Order(Long price, Long volume, Direction direction, Long traderId, Long instrumentId) {
		this.id = (long) 0;
		this.price = price;
		this.volume = volume;
		this.date = Calendar.getInstance();
		this.direction = direction;
		this.traderId = traderId;
		this.instrumentId = instrumentId;
	}

	/**
	 * Create order.
	 * 
	 * @param id
	 *            of the order
	 * @param price
	 *            of the order
	 * @param volume
	 *            of the order
	 * @param date
	 *            and time of order creation
	 * @param direction
	 *            of the order (ASK/BID)
	 * @param trader
	 *            number who placed the order
	 * @param instrument
	 *            number that is bound to the order
	 */
	public Order(Long id, Long price, Long volume, Calendar date, Direction direction, Long traderId,
			Long instrumentId) {
		this.id = id;
		this.price = price;
		this.volume = volume;
		this.date = date;
		this.direction = direction;
		this.traderId = traderId;
		this.instrumentId = instrumentId;
	}

	/**
	 * Order deal: check that orders are different.
	 * 
	 * @param otherOrder
	 *            to check
	 * @return true, if we work with different orders
	 */
	private boolean checkIdentity(Order otherOrder) {
		return getId() != otherOrder.getId();
	}

	/**
	 * Order deal: check that orders are colliding and the price satisfies
	 * conditions deal.
	 * 
	 * @param otherOrder
	 *            to deal with
	 * @return true if deal is possible
	 */
	private boolean checkPrice(Order otherOrder) {
		if (getDirection() == Direction.ASK && otherOrder.getDirection() == Direction.BID
				&& getPrice() <= otherOrder.getPrice()) {
			return true;
		}
		if (getDirection() == Direction.BID && otherOrder.getDirection() == Direction.ASK
				&& getPrice() >= otherOrder.getPrice()) {
			return true;
		}
		return false;
	}

	/**
	 * Order deal: check that orders have the same volume.
	 * 
	 * @param otherOrder
	 *            for compare volume
	 * @return true, if orders have the same volume
	 */
	private boolean checkVolume(Order otherOrder) {
		return getVolume() == otherOrder.getVolume();
	}

	/**
	 * Check whether it is possible to make deal with order.
	 * 
	 * @param otherOrder
	 *            to deal with
	 * @return true, if deal is possible
	 */
	public Boolean dealWith(Order otherOrder) {
		return checkIdentity(otherOrder) && checkVolume(otherOrder) && checkPrice(otherOrder);
	}

	/**
	 * Check the expiration period of the order.
	 * 
	 * @return true, if the order is expired
	 */
	public Boolean isExpired() {
		Calendar today = Calendar.getInstance();
		return today.get(Calendar.DAY_OF_YEAR) != date.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * Get date and time of order creation.
	 * 
	 * @return date and time of order creation
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * Check the direction of the order: ASK or BID.
	 * 
	 * @return the direction (ASK/BID)
	 */
	public Direction getDirection() {
		return direction;
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
	 * Get instrument number, associated with the order.
	 * 
	 * @return the instrument Id
	 */
	public Long getInstrumentId() {
		return instrumentId;
	}

	/**
	 * Get order price.
	 * 
	 * @return order price
	 */
	public Long getPrice() {
		return price;
	}

	/**
	 * Gi id of trader, which published the order.
	 * 
	 * @return the trader Id
	 */
	public Long getTraderId() {
		return traderId;
	}

	/**
	 * Get order volume.
	 * 
	 * @return order volume
	 */
	public Long getVolume() {
		return volume;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer(128);
		stringBuffer.append("Order [price=");
		stringBuffer.append(price.toString());
		stringBuffer.append(", volume=");
		stringBuffer.append(volume.toString());
		stringBuffer.append(", date=");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		stringBuffer.append(simpleDateFormat.format(date.getTime()));
		stringBuffer.append(", direction=");
		stringBuffer.append(direction.toString());
		stringBuffer.append("]");
		return stringBuffer.toString();
	}

}
