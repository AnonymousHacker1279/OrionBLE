package tech.anonymoushacker1279.orionble;

import tech.anonymoushacker1279.orionble.devices.BLEDevice;
import tech.anonymoushacker1279.orionble.gatt.GATTCharacteristic;
import tech.anonymoushacker1279.orionble.gatt.GATTService;

public class ReadGATT {

	private static final BLEDevice device = new BLEDevice("CODE V02034E45U", "B0B1139AF459", false);

	public static void main(String[] args) {
		System.out.println("Starting OrionBLE tests...");
		OrionBLE orion = new OrionBLE("http://localhost", 5249);
		orion.waitForConnection(5);

		System.out.println("Discovering services and characteristics for device: " + device);
		for (GATTService service : orion.discoverServices(device)) {
			System.out.println(service);
			for (GATTCharacteristic characteristic : orion.discoverCharacteristics(device, service)) {
				System.out.println("-> " + characteristic);

				if (characteristic.properties().contains(GATTCharacteristic.GATTProperties.READ)) {
					System.out.println("--> Attempting to read characteristic...");
					String value = orion.readCharacteristic(device, service, characteristic);
					System.out.println("--> Value: " + value);
				}
			}
		}
	}
}