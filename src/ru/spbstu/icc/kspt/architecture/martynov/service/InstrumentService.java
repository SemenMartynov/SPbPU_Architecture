package ru.spbstu.icc.kspt.architecture.martynov.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Market;
import ru.spbstu.icc.kspt.architecture.martynov.infrastructure.InstrumentStorage;

/**
 * @author Semen Martynov
 *
 * API for managing instruments.
 */
public class InstrumentService {

	private final InstrumentStorage instrumentStorage = new InstrumentStorage();

	public Instrument newInstrument(String code) {
		Instrument preInstrument = new Instrument(code);
		return instrumentStorage.create(preInstrument);
	}

	public Instrument getInstrument(Long id) {
		return instrumentStorage.read(id);
	}

	public Boolean enableInstrument(Instrument instrument) {
		instrument.setEnabled();
		return instrumentStorage.update(instrument);
	}

	public Boolean disableInstrument(Instrument instrument) {
		instrument.setDisabled();
		return instrumentStorage.update(instrument);
	}

	public Boolean deleteInstrument(Instrument instrument) {
		return instrumentStorage.delete(instrument);
	}

	public Boolean checkUniqueCode(String code) {
		return instrumentStorage.checkUniqueCode(code);
	}
	
	public Set<Instrument> getAllInstruments() {
		return instrumentStorage.getAllInstruments();
	}

	public Set<Instrument> getAllInstruments(Market market) {
		Set<Instrument> result = new HashSet<Instrument>();
		Iterator<Instrument> iterator = market.getInstruments().iterator();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	public Set<Instrument> getEnabledInstruments(Market market) {
		Set<Instrument> result = new HashSet<Instrument>();
		Iterator<Instrument> iterator = market.getInstruments().iterator();
		while (iterator.hasNext()) {
			Instrument instrument = iterator.next();
			if (instrument.isEnabled()) {
				result.add(instrument);
			}
		}
		return result;
	}

	public Instrument getInstrumentByCode(Market market, String code) {
		Iterator<Instrument> iterator = market.getInstruments().iterator();
		while (iterator.hasNext()) {
			Instrument instrument = iterator.next();
			if (instrument.getCode().equals(code)) {
				return instrument;
			}
		}
		return null;
	}

}
