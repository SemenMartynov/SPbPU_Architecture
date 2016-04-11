package ru.spbstu.icc.kspt.architecture.martynov.domain;

/**
 * @author Semen Martynov
 * 
 *         Interface for object witch can be temporary disabled.
 *
 */
public interface IEnableable {
	
	/**
	 * Check object enabled status.
	 * 
	 * @return enabled status.
	 */
	public Boolean isEnabled();

	/**
	 * Change status to enabled.
	 */
	public void setEnabled();

	/**
	 * Change status to disabled
	 */
	public void setDisabled();
}
