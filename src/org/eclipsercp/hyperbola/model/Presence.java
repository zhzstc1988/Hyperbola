package org.eclipsercp.hyperbola.model;

public class Presence {
	public static final Presence ONLINE = new Presence("Online"); //$NON-NLS-1$

	public static final Presence AWAY = new Presence("Away"); //$NON-NLS-1$

	public static final Presence DO_NOT_DISTURB = new Presence("Do Not Disturb"); //$NON-NLS-1$

	public static final Presence INVISIBLE = new Presence("Offline"); //$NON-NLS-1$

	private String value;

	private Presence(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}