package tech.anonymoushacker1279.orionble;

import tech.anonymoushacker1279.orionble.devices.BLEDevice;
import tech.anonymoushacker1279.orionble.gatt.GATTCharacteristic;
import tech.anonymoushacker1279.orionble.gatt.GATTService;

import java.util.List;

public class WriteGATT {

	private static final BLEDevice device = new BLEDevice("CODE V02034E45U", "B0B1139AF459", false);
	private static final GATTService service = new GATTService("14839ac4-7d7e-415c-9a42-167340cf2339", true);
	private static final GATTCharacteristic characteristic = new GATTCharacteristic(
			"ba04c4b2-892b-43be-b69c-5d13f2195392",
			"",
			List.of(GATTCharacteristic.GATTProperties.READ, GATTCharacteristic.GATTProperties.WRITE_WITHOUT_RESPONSE, GATTCharacteristic.GATTProperties.WRITE)
	);

	public static void main(String[] args) {
		System.out.println("Starting OrionBLE tests...");
		OrionBLE orion = new OrionBLE("http://localhost", 5249);
		orion.waitForConnection(5);

		System.out.println("Attempting to write data to: " + device);
		int[] data = {192, 5};  // MIDI preset change to 5
		orion.writeCharacteristic(device, service, characteristic, data);
	}
}