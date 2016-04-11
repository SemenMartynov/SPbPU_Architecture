package ru.spbstu.icc.kspt.architecture.martynov.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Trader;
import ru.spbstu.icc.kspt.architecture.martynov.domain.User;

/**
 * @author Semen Martynov
 * 
 *         This class synchronize users with database and cache.
 *
 */
public class UserStorage extends DataBase implements IStorable<User> {
	/**
	 * Users cache
	 */
	static private Cache<User> cache = new Cache<User>();

	/**
	 * Checking the uniqueness of the traders login name.
	 * 
	 * @param userName
	 *            for check
	 * @return true if name is unique
	 */
	public Boolean checkUniqueUserName(String userName) {
		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement("SELECT * FROM 'users' WHERE  userName = ?;");
			statement.setString(1, userName);
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
	public User create(User user) {
		if (user.getAccess() == User.Access.ADMIN || user.getAccess() == User.Access.REGULATOR) {
			// Can't create such type of user in the database
			return null;
		}

		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement(
					"INSERT INTO 'users' (name, userName, userPassword, enabled, deleted) VALUES (?, ?, ?, ?, 0);",
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, user.getName());
			statement.setString(2, user.getUserName());
			statement.setString(3, user.getUserPassword());
			statement.setBoolean(4, ((Trader) user).isEnabled());
			statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				Long id = resultSet.getLong(1);
				if (id == 0) {
					return null;
				}
				// Create new trader, based on new ID, and save it to the
				// cache
				Trader result = new Trader(id, user.getName(), user.getUserName(), user.getUserPassword(),
						user.getAccess(), ((Trader) user).isEnabled());
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
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.IStorable#delete(java.
	 * lang.Long)
	 */
	@Override
	public Boolean delete(Long id) {
		// Don't delete instrument from DB, only mark it as deleted
		if (id == 0) {
			throw new RuntimeException("Trader was not stored in the database!");
		}
		try {
			Connect();
			statement = connection.prepareStatement("UPDATE 'users' SET deleted = 1 WHERE id = ?;");
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
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.IStorable#delete(ru.
	 * spbstu.icc.kspt.edu_arh.martynov.infrastructure.ICacheable)
	 */
	@Override
	public Boolean delete(User user) {
		// Don't delete instrument from DB, only mark it as deleted
		return delete(user.getId());
	}

	/**
	 * Get a list of all the administrative user.
	 * 
	 * @return a list of administrative user
	 */
	public Set<User> getAdministrativeUsers() {
		FileStorage fileStorage = new FileStorage();
		return fileStorage.getManagers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.IStorable#read(java.
	 * lang.Long)
	 */
	@Override
	public User read(Long id) {
		// If object can be received from the cache -- just return it from the
		// cache.
		User result = cache.get(id);
		if (result != null) {
			return result;
		}

		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement("SELECT * FROM 'users' WHERE deleted = 0 AND id = ?;");
			statement.setLong(1, id);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				String name = resultSet.getString("name");
				String userName = resultSet.getString("userName");
				String userPassword = resultSet.getString("userPassword");
				Boolean enabled = resultSet.getBoolean("enabled");
				// Create new trader and put it to the cache
				result = new Trader(id, name, userName, userPassword, User.Access.TRADER, enabled);
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
	public Boolean update(User user) {
		if (user.getId() == 0) {
			throw new RuntimeException("Instrument " + user.getName() + " was not stored in the database!");
		}
		try {
			Connect();
			statement = connection.prepareStatement(
					"UPDATE 'users' SET name = ?, userName = ?, userPassword = ?, enabled = ? WHERE id = ?;");
			statement.setString(1, user.getName());
			statement.setString(2, user.getUserName());
			statement.setString(3, user.getUserPassword());
			statement.setBoolean(4, ((Trader) user).isEnabled());
			statement.setLong(5, user.getId());
			statement.executeUpdate();
			// Update cache
			cache.put(user);
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
		return false;
	}

	/**
	 * Get set of all non deleted users.
	 * 
	 * @return set of users
	 */
	public Set<User> getAllUsers() {
		Set<User> result = new HashSet<User>();
		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement("SELECT * FROM 'users' WHERE deleted = 0;");
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				Long id = resultSet.getLong("id");
				// check cache
				User tmp = cache.get(id);
				if (tmp == null) {
					String name = resultSet.getString("name");
					String userName = resultSet.getString("userName");
					String userPassword = resultSet.getString("userPassword");
					Boolean enabled = resultSet.getBoolean("enabled");
					// Create new trader and put it to the cache
					tmp = new Trader(id, name, userName, userPassword, User.Access.TRADER, enabled);
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
	 * Check if user enabled.
	 * 
	 * @param id
	 *            of user for check
	 * @return true, if user is enabled
	 */
	public boolean userEnabled(Long id) {
		User user = cache.get(id);
		if (user != null) {
			return ((Trader) user).isEnabled();
		}

		ResultSet resultSet = null;
		try {
			Connect();
			statement = connection.prepareStatement("SELECT * FROM 'users' WHERE deleted = 0 AND id = ?;");
			statement.setLong(1, id);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				Boolean enabled = resultSet.getBoolean("enabled");
				return enabled;
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
		return false;
	}
}
