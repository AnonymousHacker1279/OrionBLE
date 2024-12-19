package tech.anonymoushacker1279.orionble;

import tech.anonymoushacker1279.orionble.devices.BLEDevice;
import tech.anonymoushacker1279.orionble.devices.DeviceFilter;

public class ListDevices {

	public static void main(String[] args) {
		System.out.println("Starting OrionBLE tests...");
		OrionBLE orion = new OrionBLE("http://localhost", 5249);
		orion.waitForConnection(5);

		System.out.println("Discovering devices...");
		for (BLEDevice device : orion.discoverDevices()) {
			System.out.println(device);
		}

		System.out.println("Discovering devices with filter...");
		DeviceFilter filter = new DeviceFilter.Builder().namePrefix("CODE").build();
		for (BLEDevice device : orion.discoverDevices(filter)) {
			System.out.println(device);
		}
	}
}