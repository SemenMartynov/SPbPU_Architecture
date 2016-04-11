package ru.spbstu.icc.kspt.architecture.martynov.test;

import static org.junit.Assert.fail;

import org.junit.Test;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Market;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Trader;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 *
 *         Test User storage
 */
public class UserStorageTest {
	/**
	 * API for managing market.
	 */
	private MarketService API;

	/**
	 * Constructor
	 */
	public UserStorageTest() {
		this.API = new MarketService(new Market());
	}

	/**
	 * Test method for
	 * {@link ru.spbstu.icc.kspt.architecture.martynov.infrastructure.UserStorage#create(ru.spbstu.icc.kspt.architecture.martynov.domain.User)}
	 * .
	 */
	@Test
	public void testCreate() {
		Trader trader = API.getUserService().addUser("John", "TraderJ", "PassJ");
		if (trader == null) {
			fail("Create user case failed!");
		}
	}

	/**
	 * Test method for
	 * {@link ru.spbstu.icc.kspt.architecture.martynov.infrastructure.UserStorage#delete(ru.spbstu.icc.kspt.architecture.martynov.domain.User)}
	 * .
	 */
	@Test
	public void testDeleteLong() {
		Trader trader = API.getUserService().addUser("John2", "TraderJ2", "PassJ2");
		if (API.getUserService().deleteTrader(trader) == false) {
			fail("Delete user case failed!");
		}
	}

}
