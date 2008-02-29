/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.roomwareproject.module.fake;

import org.roomwareproject.server.*;
/**
 *
 * @author tvburger
 */
public class FooDeviceAddress extends DeviceAddress {
protected String address;

	public FooDeviceAddress(String addressString) {
		address = addressString;
	}

	public int hashCode() {
		return address.hashCode();
	}

	public boolean equals(Object object) {
		if(object instanceof FooDeviceAddress) {
			FooDeviceAddress other = (FooDeviceAddress) object;
			return address.equals(other.address);
		}
		return false;
	}



	public String toString() {
		return address;
	}
}
