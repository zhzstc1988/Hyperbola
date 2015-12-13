package org.eclipsercp.hyperbola.model;

public class Presence {
	public static final Presence ONLINE = new Presence("Online");

	public static final Presence AWAY = new Presence("Away");

	public static final Presence DO_NOT_DISTURB = new Presence("Do Not Disturb");

	public static final Presence INVISIBLE = new Presence("Offline");

	private String value;

	private Presence(String value) {
		this.value = value;
	}

	public String toString() {
		return value;
	}
}