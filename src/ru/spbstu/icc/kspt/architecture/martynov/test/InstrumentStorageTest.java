package ru.spbstu.icc.kspt.architecture.martynov.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Market;
import ru.spbstu.icc.kspt.architecture.martynov.service.MarketService;

/**
 * @author Semen Martynov
 *
 *         Test instrument storage
 */
public class InstrumentStorageTest {
	/**
	 * API for managing market.
	 */
	private MarketService API;

	/**
	 * Constructor
	 */
	public InstrumentStorageTest() {
		this.API = new MarketService(new Market());
	}

	/**
	 * Test method for
	 * {@link ru.spbstu.icc.kspt.architecture.martynov.infrastructure.InstrumentStorage#create(ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument)}
	 * .
	 */
	@Test
	public void testCreate() {
		Instrument instrument = API.getInstrumentService().newInstrument("AABB");
		if (instrument == null) {
			fail("Create instrument case failed!");
		}
	}

	/**
	 * Test method for
	 * {@link ru.spbstu.icc.kspt.architecture.martynov.infrastructure.InstrumentStorage#delete(ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument)}
	 * .
	 */
	@Test
	public void testDeleteInstrument() {
		Instrument instrument = API.getInstrumentService().newInstrument("BBAA");
		if (API.getInstrumentService().deleteInstrument(instrument) == false) {
			fail("Delete instrument case failed!");
		}
	}

}
