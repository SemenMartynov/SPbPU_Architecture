package ru.spbstu.icc.kspt.architecture.martynov.domain;

/**
 * @author Semen Martynov
 * 
 *         This interface combines all classes that subscribe to the market data
 *         updates from the exchange.
 *
 */
public interface IMarketDataConsumer {

	/**
	 * This method is called by the market for send new piece of data to the
	 * subscriber.
	 * 
	 * @param marketData
	 *            from the exchange
	 * @return true, if data received
	 */
	public Boolean sendMarketData(String marketData);

}
