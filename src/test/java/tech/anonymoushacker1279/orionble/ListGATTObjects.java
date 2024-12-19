package tech.anonymoushacker1279.orionble;

import tech.anonymoushacker1279.orionble.devices.BLEDevice;
import tech.anonymoushacker1279.orionble.devices.DeviceFilter;
import tech.anonymoushacker1279.orionble.gatt.GATTCharacteristic;
import tech.anonymoushacker1279.orionble.gatt.GATTService;

public class ListGATTObjects {

	public static void main(String[] args) {
		System.out.println("Starting OrionBLE tests...");
		OrionBLE orion = new OrionBLE("http://localhost", 5249);
		orion.waitForConnection(5);

		System.out.println("Discovering devices with filter...");
		DeviceFilter filter = new DeviceFilter.Builder().namePrefix("CODE").build();
		for (BLEDevice device : orion.discoverDevices(filter)) {
			System.out.println("Discovering services and characteristics for device: " + device);
			for (GATTService service : orion.discoverServices(device)) {
				System.out.println(service);
				for (GATTCharacteristic characteristic : orion.discoverCharacteristics(device, service)) {
					System.out.println("-> " + characteristic);
				}
			}
		}
	}
}