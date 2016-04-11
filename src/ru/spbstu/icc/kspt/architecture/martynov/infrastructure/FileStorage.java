package ru.spbstu.icc.kspt.architecture.martynov.infrastructure;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ru.spbstu.icc.kspt.architecture.martynov.domain.Administrator;
import ru.spbstu.icc.kspt.architecture.martynov.domain.Regulator;
import ru.spbstu.icc.kspt.architecture.martynov.domain.User;

/**
 * @author Semen Martynov
 * 
 *         Class for reading administrators and regulators from the JSON-file.
 */
class FileStorage {
	/**
	 * Path to JSON-file.
	 */
	private final String path = "res/access.json";
	/**
	 * Object for reading character files.
	 */
	private FileReader reader;

	/**
	 * Get set of users (administrators and regulators) from file.
	 * 
	 * @return set of administrative users
	 */
	public Set<User> getManagers() {

		Set<User> users = new HashSet<User>();
		try {
			reader = new FileReader(path);
			final JSONParser parser = new JSONParser();
			final JSONObject json = (JSONObject) parser.parse(reader);

			final JSONArray jsonUsers = (JSONArray) json.get("users");
			final Iterator<?> it = jsonUsers.iterator();
			while (it.hasNext()) {
				final JSONObject jsonUser = (JSONObject) it.next();

				User.Access access;
				final Long accessTmp = (Long) jsonUser.get("access");
				switch (accessTmp.intValue()) {
				case 1:
					access = User.Access.ADMIN;
					break;
				case 2:
					access = User.Access.REGULATOR;
					break;
				default:
					throw new RuntimeException("Can't read user type from " + path);
				}

				final String name = (String) jsonUser.get("name");
				final String userName = (String) jsonUser.get("userName");
				final String userPassword = (String) jsonUser.get("userPassword");

				if (access == User.Access.ADMIN) {
					Administrator admin = new Administrator(name, userName, userPassword);
					users.add(admin);
				} else {
					Regulator regulator = new Regulator(name, userName, userPassword);
					users.add(regulator);
				}
			}

		} catch (IOException | org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return users;
	}
}
