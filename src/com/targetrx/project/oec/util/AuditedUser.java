package com.targetrx.project.oec.util;

/**
 * @author pkukk
 * @since Feb 19, 2007
 */
public class AuditedUser {

	private static ThreadLocal user;
	
	static {
		user = new ThreadLocal();
	}

	public static String getName() {
		return (String) user.get();
	}
	
	public static void setName(String name) {
		user.set(name);
	}
}