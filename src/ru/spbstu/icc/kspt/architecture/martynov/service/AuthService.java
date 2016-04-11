package ru.spbstu.icc.kspt.architecture.martynov.service;

import java.util.Iterator;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Administrator;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Regulator;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Trader;
import ru.spbstu.icc.kspt.architecture.martynov.domain.User;

/**
 * @author Semen Martynov
 *
 *         Class stores information about the user after login.
 */
public class AuthService {
	/**
	 * authorized user
	 */
	private User user;
	/**
	 * Access to the users API.
	 */
	private UserService userService = new UserService();

	/**
	 * Constructor
	 */
	public AuthService() {
		this.user = null;
	}

	/**
	 * Check the user's access rights.
	 * 
	 * @param user
	 *            name
	 * @param password
	 * @param access
	 *            level
	 * @return authorized user, if username and password found in the database
	 */
	public Boolean auth(String user, String pass, User.Access access) {
		if (access == User.Access.ADMIN) {
			Iterator<Administrator> iterator = userService.getAllAdministrators().iterator();
			while (iterator.hasNext()) {
				Administrator administrator = iterator.next();
				if (administrator.getUserName().equals(user) && administrator.getUserPassword().equals(pass)) {
					this.user = administrator;
					return true;
				}
			}
			return false;
		}

		if (access == User.Access.REGULATOR) {
			Iterator<Regulator> iterator = userService.getAllRegulators().iterator();
			while (iterator.hasNext()) {
				Regulator regulator = iterator.next();
				if (regulator.getUserName().equals(user) && regulator.getUserPassword().equals(pass)) {
					this.user = regulator;
					return true;
				}
			}
			return false;
		}

		if (access == User.Access.TRADER) {
			Trader trader = null;
			trader = userService.getTraderByUserName(user);
			if (trader != null && trader.getUserPassword().equals(pass) && trader.isEnabled()) {
				this.user = trader;
				return true;
			}
		}
		return false;
	}

	/**
	 * Get current authorized user.
	 * 
	 * @return authorized user
	 */
	public User getUser() {
		return user;
	}
}
