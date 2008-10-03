package org.roomwareproject.server;

import java.util.*;

public class Presence {

	protected Device device;
	protected String zone;
	protected Object detector;
	protected Date date;

	public Presence (Object detector, String zone, Device device, Date date) {
		this.detector = detector;
		this.zone = zone;
		this.device = device;
		this.date = date;
	}

	public Date getDetectTime () {
		return date;
	}

	public Device getDevice () {
		return device;
	}

	public String getZone () {
		return zone;
	}

	public Object getDetector () {
		return detector;
	}

	public boolean equals (Object o) {
		if (!(o instanceof Object)) return false;
		Presence p = (Presence) o;
		return (p.device.equals(device)) &&
			(p.zone.equals (zone)) &&
			(p.detector.equals (detector));
	}

}
