package ru.spbstu.icc.kspt.architecture.martynov.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Administrator;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Regulator;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Trader;
import ru.spbstu.icc.kspt.architecture.martynov.domain.User;
import ru.spbstu.icc.kspt.architecture.martynov.infrastructure.UserStorage;

/**
 * @author Semen Martynov
 * 
 *         API for managing users.
 */
public class UserService {
	/**
	 * Access to users storage.
	 */
	private UserStorage userStorage = new UserStorage();

	/**
	 * Create new user and add hit to the storage
	 * 
	 * @param name
	 *            of new user
	 * @param userName
	 *            of new user
	 * @param userPassword
	 *            of new user
	 * @return new user
	 */
	public Trader addUser(String name, String userName, String userPassword) {
		if (userStorage.checkUniqueUserName(userName) == false) {
			return null;
		}

		Trader preTrader = new Trader(name, userName, userPassword);
		return (Trader) userStorage.create(preTrader);
	}

	/**
	 * Get user from storage by id.
	 * 
	 * @param id
	 *            of the user
	 * @return user
	 */
	public Trader getTrader(Long id) {
		return (Trader) userStorage.read(id);
	}

	/**
	 * Update stored trader.
	 * 
	 * @param trader
	 *            for update
	 * @return true, if operation finished successfully
	 */
	public Boolean updateTrader(Trader trader) {
		return userStorage.update(trader);
	}

	/**
	 * Set user's status to enabled.
	 * 
	 * @param trader
	 *            for status update
	 * @return true, if operation finished successfully
	 */
	public Boolean enableTrader(Trader trader) {
		trader.setEnabled();
		return userStorage.update(trader);
	}

	/**
	 * Set user's status to disabled.
	 * 
	 * @param trader
	 *            for status update
	 * @return true, if operation finished successfully
	 */
	public Boolean disableTrader(Trader trader) {
		trader.setDisabled();
		return userStorage.update(trader);
	}

	/**
	 * Delete trader and update database.
	 * 
	 * @param trader
	 *            for delete
	 * @return true, if operation finished successfully
	 */
	public Boolean deleteTrader(Trader trader) {
		return userStorage.delete(trader);
	}

	/**
	 * Theck that user with presented username doesn't exist.
	 * 
	 * @param userName
	 *            for check
	 * @return true, if username is unique
	 */
	public Boolean checkUniqueUserName(String userName) {
		return userStorage.checkUniqueUserName(userName);
	}

	/**
	 * Get set of traders
	 * 
	 * @return set of traders
	 */
	public Set<Trader> getAllTraders() {
		Set<Trader> result = new HashSet<Trader>();
		Iterator<User> iterator = userStorage.getAllUsers().iterator();
		while (iterator.hasNext()) {
			Trader trader = (Trader) iterator.next();
			result.add(trader);
		}
		return result;
	}

	/**
	 * Get set of Administrators.
	 * 
	 * @return set of administrators
	 */
	public Set<Administrator> getAllAdministrators() {
		Set<Administrator> result = new HashSet<Administrator>();
		Iterator<User> iterator = userStorage.getAdministrativeUsers().iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (user.getAccess() == User.Access.ADMIN) {
				result.add((Administrator) user);
			}
		}
		return result;
	}

	/**
	 * Get set of regulators.
	 * 
	 * @return set of regulators
	 */
	public Set<Regulator> getAllRegulators() {
		Set<Regulator> result = new HashSet<Regulator>();
		Iterator<User> iterator = userStorage.getAdministrativeUsers().iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (user.getAccess() == User.Access.REGULATOR) {
				result.add((Regulator) user);
			}
		}
		return result;
	}

	/**
	 * Get trader by username (expensive operation!).
	 * 
	 * @param userName
	 *            for search
	 * @return trader, if found
	 */
	public Trader getTraderByUserName(String userName) {
		Iterator<User> iterator = userStorage.getAllUsers().iterator();
		while (iterator.hasNext()) {
			Trader trader = (Trader) iterator.next();
			if (trader.getUserName().equals(userName)) {
				return trader;
			}
		}
		return null;
	}

	/**
	 * Check, that trader is enabled.
	 * 
	 * @param traderId
	 *            for check
	 * @return true, if trader is enabled
	 */
	public boolean userEnabled(Long traderId) {
		return userStorage.userEnabled(traderId);
	}
}
