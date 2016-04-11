/**
 * 
 */
package ru.spbstu.icc.kspt.architecture.martynov.domain;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Semen Martynov
 * 
 *         Regulator is a special type of user, witch can block some trader or
 *         instrument according to his private logic.
 */
public class Regulator extends User implements IMarketDataConsumer {
	/**
	 * Market data, received from the exchange.
	 */
	private Set<String> marketData = new LinkedHashSet<String>();

	/**
	 * Create new regulator
	 * 
	 * @param name
	 *            of the regulator
	 * @param userName
	 *            for login
	 * @param userPassword
	 *            for login
	 */
	public Regulator(String name, String userName, String userPassword) {
		super((long) 0, name, userName, userPassword, User.Access.REGULATOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.spbstu.icc.kspt.edu_arh.martynov.domain.IMarketDataConsumer#
	 * sendMarketData(java.lang.String)
	 */
	@Override
	public Boolean sendMarketData(String marketData) {
		return this.marketData.add(marketData);
	}

	/**
	 * Get collected marker data.
	 * 
	 * @return marketData, received from the exchange
	 */
	public Set<String> getMarketData() {
		return marketData;
	}
}
