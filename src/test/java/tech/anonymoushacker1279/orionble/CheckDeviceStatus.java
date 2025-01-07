package tech.anonymoushacker1279.orionble;

public class CheckDeviceStatus {

	public static void main(String[] args) {
		System.out.println("Starting OrionBLE tests...");
		OrionBLE orion = new OrionBLE("http://localhost", 5249);
		orion.waitForConnection(5);

		System.out.println("Checking if device is connected...");
		boolean connected = orion.isDeviceConnected("B0B1139AF459");
		System.out.println("Device is connected: " + connected);
	}
}