/**
 * (c) Simon Beaver 2018
 */
package org.imrryr.floowtest;

/**
 * Application level exception class to use in error handling
 *
 * @author Simon Beaver
 */
public class TestException extends Exception {

	/** Serial version ID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param msg Error message.
	 */
	public TestException(String msg) {
		super(msg);
	}
}
