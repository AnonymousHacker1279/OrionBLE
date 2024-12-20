package tech.anonymoushacker1279.orionble;

import tech.anonymoushacker1279.orionble.devices.BLEDevice;
import tech.anonymoushacker1279.orionble.gatt.GATTCharacteristic;
import tech.anonymoushacker1279.orionble.gatt.GATTNotification;
import tech.anonymoushacker1279.orionble.gatt.GATTService;

import java.util.List;
import java.util.function.Consumer;

public class AdvancedNotifyGATT {

	private static final BLEDevice device = new BLEDevice("CODE V02034E45U", "B0B1139AF459", false);
	private static final GATTService service = new GATTService("14839ac4-7d7e-415c-9a42-167340cf2339", true);
	private static final GATTCharacteristic characteristic = new GATTCharacteristic(
			"0734594a-a8e7-4b1a-a6b1-cd5243059a57",
			"",
			List.of(GATTCharacteristic.GATTProperties.NOTIFY)
	);

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Starting OrionBLE tests...");
		OrionBLE orion = new OrionBLE("http://localhost", 5249);
		orion.waitForConnection(5);

		Consumer<GATTNotification> consumer = notification -> System.out.println("Received notification: " + notification);

		System.out.println("Registering notify event and starting listener...");
		orion.registerNotifyEvent(device, service, characteristic);
		orion.startNotificationListener(device, service, characteristic, consumer, 500);

		// Let it run for 10 seconds for testing
		Thread.sleep(10000);

		System.out.println("Pausing listener...");
		orion.pauseNotificationListener(device, service, characteristic);

		Thread.sleep(5000);

		System.out.println("Resuming listener...");
		orion.resumeNotificationListener(device, service, characteristic);

		Thread.sleep(5000);

		orion.stopNotificationListener(device, service, characteristic);
		orion.unregisterNotifyEvent(device, service, characteristic);
		System.out.println("Stopping listener and unregistering notify event...");
	}
}