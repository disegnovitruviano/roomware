package org.roomwareproject.server;

import java.io.*;

public abstract class DeviceAddress implements Serializable {

	public abstract String toString();

	public abstract boolean equals(Object obj);

	public abstract int hashCode();

	public abstract String getType ();

}
