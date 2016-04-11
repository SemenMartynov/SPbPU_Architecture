package ru.spbstu.icc.kspt.architecture.martynov.infrastructure;

/**
 * @author Semen Martynov
 * 
 *         This interface define standard CRUD operations.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Create,_read,_update_and_delete">
 *      https://en.wikipedia.org/wiki/Create,_read,_update_and_delete</a>
 * @param <T>
 *            Type of CRUD object
 */
public interface IStorable<T extends ICacheable> {
	/**
	 * Create an object from the prototype. The prototype does not contain an
	 * identification number that associates it with the database record.
	 * 
	 * @param Object's
	 *            prototype
	 * @return Object with identification number
	 */
	public T create(T t);

	/**
	 * Delete object in the database. In most cases, we don't really delete
	 * object but mark it as deleted.
	 * 
	 * @param object's
	 *            identificator for delete
	 * @return true, id object was deleted successfully
	 */
	public Boolean delete(Long id);

	/**
	 * Delete object in the database. In most cases, we don't really delete
	 * object but mark it as deleted.
	 * 
	 * @param object
	 *            for delete
	 * @return true, id object was deleted successfully
	 */
	public Boolean delete(T t);

	/**
	 * Read object from database and return it to the user.
	 * 
	 * @return object, that has been read from the database
	 */
	public T read(Long id);

	/**
	 * Update object in database.
	 * 
	 * @param object,
	 *            that should be updated in the database
	 * @return true, if update was successful.
	 */
	public Boolean update(T t);
}
