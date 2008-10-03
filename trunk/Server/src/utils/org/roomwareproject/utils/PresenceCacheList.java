package org.roomwareproject.utils;

import org.roomwareproject.server.*;
import java.util.*;


class PresenceRecord {

	protected Presence p;
	protected Date t;

	PresenceRecord (Presence p) {
		t = new Date ();
		this.p = p;
	}

	public long getTime () {
		return t.getTime ();
	}

	public Presence getPresence () {
		return p;
	}
 
	public boolean equals (Object o) {
		if (o instanceof PresenceRecord) {
			PresenceRecord dp = (PresenceRecord) o;
			return dp.p.equals (p);
		}
		return false;
	}

	public void update() {
		t = new Date ();
	}
}


public class PresenceCacheList {

	protected Set<PresenceRecord> prs;
	protected int timeout;

	public PresenceCacheList (int timeout) {
		prs = new HashSet<PresenceRecord> ();
		this.timeout = timeout;
	}

	public synchronized void addPresence (Presence p) {
		for (PresenceRecord pr: prs) {
			if (pr.equals (p)) {
				pr.update ();
				return;
			}
		}
		prs.add(new PresenceRecord (p));
	}

	public synchronized Set<Presence> getPresences () {
		Set<Presence> presences = new HashSet<Presence> ();

		Iterator<PresenceRecord> it = prs.iterator ();
		long now = new Date().getTime();
		long limit = now - 1000 * timeout;

		while (it.hasNext()) {
			PresenceRecord pr = it.next ();
			if (pr.getTime () < limit) {
				it.remove ();
			}
			else presences.add (pr.getPresence ());
		}

		return presences;
	}

}
