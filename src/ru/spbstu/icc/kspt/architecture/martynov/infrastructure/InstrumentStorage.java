package ru.spbstu.icc.kspt.architecture.martynov.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Instrument;

/**
 * @author Semen Martynov
 * 
 *         This class synchronize instruments with database and cache.
 *
 */
public class InstrumentStorage extends DataBase implements IStorable<Instrument> {
	/**
	 * Instruments cache
	 */
	static private Cache<Instrument> cache = new Cache<Instrument>();

	/**
	 * code uniqueness verification.
	 * 
	 * @param code
	 *            for verification
	 * @return true if code is unique
	 */
	public Boolean checkUniqueCode(String code) {
		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement("SELECT * FROM 'instruments' WHERE code = ?;");
			statement.setString(1, code);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return false;
			}
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				Close();
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.IStorable#create(ru.
	 * spbstu.icc.kspt.edu_arh.martynov.infrastructure.ICacheable)
	 */
	@Override
	public Instrument create(Instrument instrument) {
		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement(
					"INSERT INTO 'instruments' (code, enabled, deleted) VALUES (?, 1, 0);",
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, instrument.getCode());
			statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				Long id = resultSet.getLong(1);
				if (id == 0) {
					return null;
				}
				// Create new instrument, based on new ID, and save it to the
				// cache
				Instrument result = new Instrument(id, instrument.getCode(), instrument.isEnabled());
				cache.put(result);
				return result;
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				Close();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.IStorable#delete(ru.
	 * spbstu.icc.kspt.edu_arh.martynov.infrastructure.ICacheable)
	 */
	@Override
	public Boolean delete(Instrument instrument) {
		// Don't delete instrument from DB, only mark it as deleted
		return delete(instrument.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.IStorable#delete(java.
	 * lang.Long)
	 */
	@Override
	public Boolean delete(Long id) {
		// Don't delete instrument from DB, only mark it as deleted
		if (id == 0) {
			throw new RuntimeException("Instrument was not stored in the database!");
		}
		try {
			Connect();
			statement = connection.prepareStatement("UPDATE 'instruments' SET deleted = 1 WHERE id = ?;");
			statement.setLong(1, id);
			statement.executeUpdate();
			// Update cache
			return cache.remove(id);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.IStorable#read(java.
	 * lang.Long)
	 */
	@Override
	public Instrument read(Long id) {
		// If object can be received from the cache -- just return it from the
		// cache.
		Instrument result = cache.get(id);
		if (result != null) {
			return result;
		}

		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement("SELECT * FROM 'instruments' WHERE deleted = 0 AND id = ?;");
			statement.setLong(1, id);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				String code = resultSet.getString("code");
				Boolean enabled = resultSet.getBoolean("enabled");
				// Create new instrument and put it to the cache
				result = new Instrument(id, code, enabled);
				cache.put(result);
				return result;
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				Close();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.IStorable#update(ru.
	 * spbstu.icc.kspt.edu_arh.martynov.infrastructure.ICacheable)
	 */
	@Override
	public Boolean update(Instrument instrument) {
		if (instrument.getId() == 0) {
			throw new RuntimeException("Instrument " + instrument.getCode() + " was not stored in the database!");
		}
		try {
			Connect();
			statement = connection.prepareStatement("UPDATE 'instruments' SET code = ?, enabled = ? WHERE id = ?;");
			statement.setString(1, instrument.getCode());
			statement.setBoolean(2, instrument.isEnabled());
			statement.setLong(3, instrument.getId());
			statement.executeUpdate();
			// Update cache
			cache.put(instrument);
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
		return false;
	}

	/**
	 * Get set of non deleted instruments.
	 * 
	 * @return set of instruments
	 */
	public Set<Instrument> getAllInstruments() {
		Set<Instrument> result = new HashSet<Instrument>();
		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement("SELECT * FROM 'instruments' WHERE deleted = 0;");
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Long id = resultSet.getLong("id");
				// check cache
				Instrument tmp = cache.get(id);
				if (tmp == null) {
					String code = resultSet.getString("code");
					Boolean enabled = resultSet.getBoolean("enabled");
					// Create new instrument and put it to the cache
					tmp = new Instrument(id, code, enabled);
					cache.put(tmp);
				}
				result.add(tmp);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				Close();
			}
		}
		return result;
	}
}
