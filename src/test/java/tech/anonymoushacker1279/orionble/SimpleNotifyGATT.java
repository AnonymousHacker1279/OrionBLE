package tech.anonymoushacker1279.orionble;

import tech.anonymoushacker1279.orionble.devices.BLEDevice;
import tech.anonymoushacker1279.orionble.gatt.GATTCharacteristic;
import tech.anonymoushacker1279.orionble.gatt.GATTNotification;
import tech.anonymoushacker1279.orionble.gatt.GATTService;

import java.util.List;

public class SimpleNotifyGATT {

	private static final BLEDevice device = new BLEDevice("CODE V02034E45U", "B0B1139AF459", false);
	private static final GATTService service = new GATTService("14839ac4-7d7e-415c-9a42-167340cf2339", true);
	private static final GATTCharacteristic characteristic = new GATTCharacteristic(
			"0734594a-a8e7-4b1a-a6b1-cd5243059a57",
			"",
			List.of(GATTCharacteristic.GATTProperties.NOTIFY)
	);

	public static void main(String[] args) {
		System.out.println("Starting OrionBLE tests...");
		OrionBLE orion = new OrionBLE("http://localhost", 5249);
		orion.waitForConnection(5);

		System.out.println("Registering characteristic for notify event...");
		orion.registerNotifyEvent(device, service, characteristic);

		System.out.println("Waiting 5s for incoming notifications...");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.err.println("An error occurred while waiting for notifications: " + e.getMessage());
		}

		List<GATTNotification> notifications = orion.getNotifications(device, service, characteristic);

		if (notifications.isEmpty()) {
			System.out.println("No notifications received.");
		}

		for (GATTNotification notification : notifications) {
			System.out.println(notification);
		}

		System.out.println("Unregistering characteristic for notify event...");
		orion.unregisterNotifyEvent(device, service, characteristic);
	}
}