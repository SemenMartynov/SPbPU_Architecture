package ru.spbstu.icc.kspt.architecture.martynov.domain;

import ru.spbstu.icc.kspt.architecture.martynov.infrastructure.ICacheable;

/**
 * @author Semen Martynov
 * 
 *         Abstract class for represent all types of users in the system.
 *
 */
public abstract class User implements ICacheable {
	/**
	 * Fore types of access: ANONYMOUS -- Anonymous user, ADMIN --
	 * Administrator, REGULATOR -- Regulator, TRADER -- Trader.
	 */
	public enum Access {
		ANONYMOUS, ADMIN, REGULATOR, TRADER
	}

	/**
	 * User identification number.
	 */
	private final Long id;

	/**
	 * User real name
	 */
	private String name;

	/**
	 * User name for login. It should be unique in every access category.
	 */
	private final String userName;

	/**
	 * User's secret password
	 */
	private String userPassword;

	/**
	 * Access level
	 */
	private final Access access;

	/**
	 * Crate new user.
	 *
	 * @param id
	 *            of the user
	 * @param name
	 *            of the user
	 * @param userName
	 *            for login
	 * @param userPassword
	 *            for login
	 * @param access
	 *            level
	 */
	public User(Long id, String name, String userName, String userPassword, Access access) {
		this.id = id;
		this.name = name;
		this.userName = userName;
		this.userPassword = userPassword;
		this.access = access;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.spbstu.icc.kspt.architecture.martynov.infrastructure.ICacheable#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Get user's real name.
	 * 
	 * @return real user's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set user's real name.
	 * 
	 * @param user's
	 *            real name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return user's password
	 * 
	 * @return user's password
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * Set new password for user
	 * 
	 * @param password
	 *            for set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/**
	 * Get user's access level.
	 * 
	 * @return access level
	 */
	public Access getAccess() {
		return access;
	}

	/**
	 * Get user's name for login.
	 * 
	 * @return user's login name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Check equality with other user.
	 * 
	 * @param other user for check
	 * @return true, is users are equal
	 */
	public Boolean equals(User other) {
		return userName.equals(other.getUserName()) && userPassword.equals(other.getUserPassword())
				&& access == other.getAccess();
	}
}
