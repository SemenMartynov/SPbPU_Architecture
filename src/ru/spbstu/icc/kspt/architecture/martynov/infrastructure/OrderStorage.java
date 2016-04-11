package ru.spbstu.icc.kspt.architecture.martynov.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Order;

/**
 * @author Semen Martynov
 * 
 *         This class synchronize orders with database and cache.
 *
 */
public class OrderStorage extends DataBase implements IStorable<Order> {
	/**
	 * Orders cache
	 */
	static private Cache<Order> cache = new Cache<Order>();;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.IStorable#create(ru.
	 * spbstu.icc.kspt.edu_arh.martynov.infrastructure.ICacheable)
	 */
	@Override
	public Order create(Order order) {
		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement(
					"INSERT INTO 'orders' (price, volume, date, direction, traderID, instrumentID, completed, deleted) VALUES (?, ?, ?, ?, ?, ?, 0, 0);",
					Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, order.getPrice());
			statement.setLong(2, order.getVolume());
			statement.setDate(3, new java.sql.Date(order.getDate().getTimeInMillis()));
			if (order.getDirection() == Order.Direction.ASK) {
				statement.setLong(4, (long) 0);
			} else {
				statement.setLong(4, (long) 1);
			}
			statement.setLong(5, order.getTraderId());
			statement.setLong(6, order.getInstrumentId());
			statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();

			if (resultSet.next()) {
				Long id = resultSet.getLong(1);
				if (id == 0) {
					return null;
				}
				// Create new order, based on new ID, and save it to the
				// cache
				Order result = new Order(id, order.getPrice(), order.getVolume(), order.getDate(), order.getDirection(),
						order.getTraderId(), order.getInstrumentId());
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
	public Boolean delete(Order order) {
		// Don't delete order from DB, only mark it as deleted
		return delete(order.getId());
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
		// Don't delete order from DB, only mark it as deleted
		if (id == 0) {
			throw new RuntimeException("Order was not stored in the database!");
		}
		try {
			Connect();
			statement = connection.prepareStatement("UPDATE 'orders' SET deleted = 1 WHERE id = ?;");
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
	public Order read(Long id) {
		// If object can be received from the cache -- just return it from the
		// cache.
		Order result = cache.get(id);
		if (result != null) {
			return result;
		}

		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection
					.prepareStatement("SELECT * FROM 'orders' WHERE deleted = 0 AND completed = 0 AND id = ?;");
			statement.setLong(1, id);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				Long price = resultSet.getLong("price");
				Long volume = resultSet.getLong("volume");
				Calendar date = Calendar.getInstance();
				date.setTime(resultSet.getDate("date"));
				Long directionTmp = resultSet.getLong("direction");
				Order.Direction direction;
				if (directionTmp == 0) {
					direction = Order.Direction.ASK;
				} else {
					direction = Order.Direction.BID;
				}
				Long traderId = resultSet.getLong("traderID");
				Long instrumentId = resultSet.getLong("instrumentID");
				// Create new order and put it to the cache
				result = new Order(id, price, volume, date, direction, traderId, instrumentId);
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
	public Boolean update(Order order) {
		if (order.getId() == 0) {
			throw new RuntimeException("Order was not stored in the database!");
		}
		try {
			Connect();
			statement = connection.prepareStatement("UPDATE 'orders' SET completed = 1 WHERE id = ?;");
			statement.setLong(1, order.getId());
			statement.executeUpdate();
			// Update cache
			return cache.remove(order.getId());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
		return false;
	}

	/**
	 * Get set of all non expired, non completed and non canceled orders.
	 * 
	 * @return set of orders
	 */
	public Set<Order> getAllOrders() {
		Set<Order> result = new HashSet<Order>();
		ResultSet resultSet = null;
		try {
			Calendar startOfDay = Calendar.getInstance();
			startOfDay.set(Calendar.HOUR, 0);
			startOfDay.set(Calendar.MINUTE, 0);
			startOfDay.set(Calendar.SECOND, 0);
			startOfDay.set(Calendar.HOUR_OF_DAY, 0);

			Connect();
			statement = connection
					.prepareStatement("SELECT * FROM 'orders' WHERE date > ? AND completed = 0 AND deleted = 0;");
			statement.setDate(1, new java.sql.Date(startOfDay.getTimeInMillis()));
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Long id = resultSet.getLong("id");
				// check cache
				Order tmp = cache.get(id);
				if (tmp == null) {
					Long price = resultSet.getLong("price");
					Long volume = resultSet.getLong("volume");
					Calendar date = Calendar.getInstance();
					date.setTime(resultSet.getDate("date"));
					Long directionTmp = resultSet.getLong("direction");
					Order.Direction direction;
					if (directionTmp == 0) {
						direction = Order.Direction.ASK;
					} else {
						direction = Order.Direction.BID;
					}
					Long traderId = resultSet.getLong("traderID");
					Long instrumentId = resultSet.getLong("instrumentID");
					// Create new order and put it to the cache
					tmp = new Order(id, price, volume, date, direction, traderId, instrumentId);
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
		Long result = (long) 0;
		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement(
					"SELECT * FROM 'orders' WHERE traderId = ? AND date > ? AND date < ? And completed = 1 AND deleted = 0;");
			statement.setLong(1, traderId);
			statement.setDate(2, new java.sql.Date(begin.getTimeInMillis()));
			statement.setDate(3, new java.sql.Date(end.getTimeInMillis()));
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Long price = resultSet.getLong("price");
				Long volume = resultSet.getLong("volume");
				result += price * volume;
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
