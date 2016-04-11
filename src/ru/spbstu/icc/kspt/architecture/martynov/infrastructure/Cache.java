package ru.spbstu.icc.kspt.architecture.martynov.infrastructure;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Semen Martynov
 *
 *         This class can store and return cacheable objects.
 * 
 * @param <T>
 *            the type of stored object
 */
public class Cache<T extends ICacheable> {
	/**
	 * All elements stored in hash map where key is the objects id.
	 */
	private Map<Long, T> cache = new HashMap<Long, T>();

	/**
	 * Get stored object from cache.
	 * 
	 * @param id
	 *            of stored object
	 * @return object specified by id, or null if this cache contains no mapping
	 *         for the id
	 */
	public T get(Long id) {
		return cache.get(id);

	}

	/**
	 * Add object to the cache.
	 * 
	 * @param object
	 *            for store
	 * @return true if there was no mapping for object id, and false if previous
	 *         value was associated with this key (i.e. object was replaced in
	 *         cache)
	 * @throws RuntimeException
	 *             if object doesn't have correct id
	 */
	public Boolean put(T object) {
		if (object.getId() == 0) {
			throw new RuntimeException("Object was not stored in the database!");
		}
		return cache.put(object.getId(), object) == null;
	}

	/**
	 * Remove object from cache.
	 * 
	 * @param id
	 *            of the object, witch should be removed
	 * @return true if object was removed or true if object was not found
	 */
	public Boolean remove(Long id) {
		return cache.remove(id) != null;
	}

	/**
	 * Remove object from cache.
	 * 
	 * @param object,
	 *            witch should be removed
	 * @return true if object was removed or true if object was not found
	 */
	public Boolean remove(T object) {
		return cache.remove(object.getId()) != null;
	}
}
