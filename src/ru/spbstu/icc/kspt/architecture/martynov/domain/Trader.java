/**
 * 
 */
package ru.spbstu.icc.kspt.architecture.martynov.domain;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Semen Martynov
 * 
 *         Trader is person, who plays on the Stock Exchange and receive profit.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Trader_%28finance%29">https://en.
 *      wikipedia.org/wiki/Trader_%28finance%29</a>
 */
public class Trader extends User implements IEnableable, IMarketDataConsumer {
	/**
	 * Trader can be temporary blocked.
	 */
	private Boolean enabled = true;

	/**
	 * Market data, received from the exchange.
	 */
	private Set<String> marketData = new LinkedHashSet<String>();

	/**
	 * Create pre-trader with zero id.
	 * 
	 * @param name
	 *            of the trader
	 * @param userName
	 *            for login
	 * @param userPassword
	 *            for login
	 */
	public Trader(String name, String userName, String userPassword) {
		super((long) 0, name, userName, userPassword, User.Access.TRADER);
	}

	/**
	 * Create new trader
	 * 
	 * @param id
	 *            according to the database
	 * @param name
	 *            of the trader
	 * @param userName
	 *            for login
	 * @param userPassword
	 *            for login
	 * @param access
	 *            level
	 * @param enabled
	 *            status
	 */
	public Trader(Long id, String name, String userName, String userPassword, Access access, Boolean enabled) {
		super(id, name, userName, userPassword, access);
		this.enabled = enabled;
	}

	/**
	 * Get collected marker data.
	 * 
	 * @return marketData, received from the exchange
	 */
	public Set<String> getMarketData() {
		return marketData;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getId() + ". " + getName() + " (" + getUserName() + "), enabled=" + enabled;
	}
}
