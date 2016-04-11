package ru.spbstu.icc.kspt.architecture.martynov.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Semen Martynov
 * 
 *         Class for working with database.
 */
public abstract class DataBase {
	/**
	 * A connection (session) with a database.
	 */
	protected static Connection connection;
	/**
	 * An object that represents a precompiled SQL statement.
	 */
	protected static PreparedStatement statement;

	/**
	 * Close database connection.
	 */
	protected static void Close() {
		// This code looks ugly but it prevent resources leak
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * Open connection to the database.
	 * 
	 * @throws ClassNotFoundException
	 *             if the class (org.sqlite.JDBC) cannot be located
	 * @throws SQLException
	 *             if a database access error occurs or the path is null
	 */
	protected static void Connect() throws ClassNotFoundException, SQLException {
		connection = null;
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:res/SimpleStockExchange.s3db");
	}

}
