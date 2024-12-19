package tech.anonymoushacker1279.orionble;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import tech.anonymoushacker1279.orionble.devices.BLEDevice;
import tech.anonymoushacker1279.orionble.devices.DeviceFilter;
import tech.anonymoushacker1279.orionble.gatt.GATTCharacteristic;
import tech.anonymoushacker1279.orionble.gatt.GATTNotification;
import tech.anonymoushacker1279.orionble.gatt.GATTService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class OrionBLE {

	private final RESTHandler restHandler;
	private final Map<String, Thread> notificationThreads = new ConcurrentHashMap<>();
	private ProcessHandle orionBLEServer;

	/**
	 * Create a new OrionBLE instance. A backend server will be launched if one is not already running.
	 * <p>
	 * Please note, OrionBLE only supports Windows at this time.
	 *
	 * @param serverAddress the address of the server
	 * @param port          the port of the server
	 */
	public OrionBLE(String serverAddress, int port) {
		if (!isWindowsPlatform()) {
			throw new UnsupportedOperationException("OrionBLE is only supported on Windows platforms!");
		}

		String address = serverAddress + ":" + port;
		HttpClient client = HttpClient.newHttpClient();
		restHandler = new RESTHandler(client, address);

		launchOrionBLEServer(serverAddress, port);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> orionBLEServer.destroy()));
	}

	/**
	 * Check if the current platform is Windows.
	 *
	 * @return true if the platform is Windows, false otherwise
	 */
	private static boolean isWindowsPlatform() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	/**
	 * Wait for a connection to be established with the server. This should be called before any other API calls are
	 * made.
	 *
	 * @param maxRetries the maximum number of retries to attempt, with a one-second delay between each
	 */
	public void waitForConnection(int maxRetries) {
		// Wait for a server to be available
		while (maxRetries > 0) {
			try {
				restHandler.getRequest(APIEndpoints.ROOT.getEndpoint());
				break;
			} catch (RuntimeException e) {
				maxRetries--;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * Launch the OrionBLE server. It is located in the root of the JAR as "OrionBLEServer.exe".
	 *
	 * @param serverAddress the address of the server
	 * @param port          the port of the server
	 */
	private void launchOrionBLEServer(String serverAddress, int port) {
		// Check if a server is already running at this address
		try {
			restHandler.getRequest(APIEndpoints.ROOT.getEndpoint());
		} catch (RuntimeException e) {
			// Begin the extraction and launch process
			try {
				// Extract the server file from the JAR
				String serverFileName = "OrionBLEServer.exe";
				File tempFile = File.createTempFile("OrionBLEServer", ".exe");
				tempFile.deleteOnExit();

				try (InputStream is = getClass().getResourceAsStream("/" + serverFileName)) {
					if (is == null) {
						throw new IOException("Server file not found in JAR");
					}
					Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}

				String urls = "--urls=%s:%s".formatted(serverAddress, String.valueOf(port));    // Listening URLs
				String loggingLevel = "--Logging:LogLevel:Microsoft=Warning";   // Log level
				ProcessBuilder processBuilder = new ProcessBuilder(tempFile.getAbsolutePath(), urls, loggingLevel);
				processBuilder.inheritIO();
				Process process = processBuilder.start();
				orionBLEServer = process.toHandle();
			} catch (IOException e1) {
				throw new RuntimeException("Failed to launch the backend server!");
			}
		}
	}

	/**
	 * Discover all nearby BLE devices.
	 *
	 * @return a list of {@link BLEDevice} objects
	 */
	public List<BLEDevice> discoverDevices() {
		String response = restHandler.getRequest(APIEndpoints.DISCOVER_DEVICES.getEndpoint());
		return BLEDevice.parseDevices(response);
	}

	/**
	 * Discover nearby BLE devices based on a filter.
	 *
	 * @param filter the {@link DeviceFilter} to use
	 * @return a list of {@link BLEDevice} objects
	 */
	public List<BLEDevice> discoverDevices(DeviceFilter filter) {
		String response = restHandler.getRequest(APIEndpoints.DISCOVER_DEVICES.getEndpoint(), filter.toUriParams());
		return BLEDevice.parseDevices(response);
	}

	/**
	 * Discover GATT services for a given device.
	 *
	 * @param device the {@link BLEDevice} to discover services for
	 * @return a list of {@link GATTService} objects
	 */
	public List<GATTService> discoverServices(BLEDevice device) {
		String response = restHandler.getRequest(APIEndpoints.DISCOVER_GATT_SERVICES.getEndpoint(device.address()));
		return GATTService.parseServices(response);
	}

	/**
	 * Discover GATT characteristics for a given device and service.
	 *
	 * @param device  the {@link BLEDevice} to discover characteristics for
	 * @param service the {@link GATTService}
	 * @return a list of {@link GATTCharacteristic} objects
	 */
	public List<GATTCharacteristic> discoverCharacteristics(BLEDevice device, GATTService service) {
		String response = restHandler.getRequest(APIEndpoints.DISCOVER_GATT_CHARACTERISTICS.getEndpoint(device.address(), service.uuid()));
		return GATTCharacteristic.parseCharacteristics(response);
	}

	/**
	 * Read a value from a GATT characteristic.
	 *
	 * @param device         the {@link BLEDevice} to read from
	 * @param service        the {@link GATTService}
	 * @param characteristic the {@link GATTCharacteristic}
	 * @return the value of the characteristic
	 */
	public String readCharacteristic(BLEDevice device, GATTService service, GATTCharacteristic characteristic) {
		return restHandler.getRequest(APIEndpoints.READ_GATT_CHARACTERISTIC.getEndpoint(device.address(), service.uuid(), characteristic.uuid()));
	}

	/**
	 * Write a value to a GATT characteristic.
	 *
	 * @param device         the {@link BLEDevice} to write to
	 * @param service        the {@link GATTService}
	 * @param characteristic the {@link GATTCharacteristic}
	 * @param data           the data to write
	 */
	public void writeCharacteristic(BLEDevice device, GATTService service, GATTCharacteristic characteristic, int[] data) {
		JsonObject message = new JsonObject();
		JsonArray dataArray = new JsonArray();
		for (int i : data) {
			dataArray.add(i);
		}
		message.add("message", dataArray);
		restHandler.postRequest(APIEndpoints.WRITE_GATT_CHARACTERISTIC.getEndpoint(device.address(), service.uuid(), characteristic.uuid()), message);
	}

	/**
	 * Register a notify event for a GATT characteristic. This must be called before reading notifications or starting a
	 * listener.
	 *
	 * @param device         the {@link BLEDevice} to register the notify event for
	 * @param service        the {@link GATTService}
	 * @param characteristic the {@link GATTCharacteristic}
	 */
	public void registerNotifyEvent(BLEDevice device, GATTService service, GATTCharacteristic characteristic) {
		restHandler.postRequest(APIEndpoints.REGISTER_NOTIFY_EVENT.getEndpoint(device.address(), service.uuid(), characteristic.uuid()), new JsonObject());
	}

	/**
	 * Unregister a notify event for a GATT characteristic. This should be called when no longer listening for
	 * notifications, so that resources can be freed up.
	 *
	 * @param device         the {@link BLEDevice} to unregister the notify event for
	 * @param service        the {@link GATTService}
	 * @param characteristic the {@link GATTCharacteristic}
	 */
	public void unregisterNotifyEvent(BLEDevice device, GATTService service, GATTCharacteristic characteristic) {
		restHandler.postRequest(APIEndpoints.UNREGISTER_NOTIFY_EVENT.getEndpoint(device.address(), service.uuid(), characteristic.uuid()), new JsonObject());
	}

	/**
	 * Get all notifications for a GATT characteristic. This will return all notifications that have been received since
	 * the last call to this method.
	 * <p>
	 * Please note, you must call {@link #registerNotifyEvent(BLEDevice, GATTService, GATTCharacteristic)} before
	 * calling this method to receive anything.
	 *
	 * @param device         the {@link BLEDevice} to get notifications for
	 * @param service        the {@link GATTService}
	 * @param characteristic the {@link GATTCharacteristic}
	 * @return a list of {@link GATTNotification} objects. Will be empty if no notifications are available.
	 */
	public List<GATTNotification> getNotifications(BLEDevice device, GATTService service, GATTCharacteristic characteristic) {
		String response = restHandler.getRequest(APIEndpoints.GET_GATT_CHARACTERISTIC_NOTIFICATIONS.getEndpoint(device.address(), service.uuid(), characteristic.uuid()));
		return GATTNotification.parseNotifications(response);
	}

	/**
	 * Start a listener for notifications on a GATT characteristic. This will call the provided consumer with any
	 * notifications received at the specified interval.
	 *
	 * @param device         the {@link BLEDevice} to listen for notifications on
	 * @param service        the {@link GATTService}
	 * @param characteristic the {@link GATTCharacteristic}
	 * @param consumer       the consumer to call with notifications
	 * @param interval       the interval in milliseconds to check for notifications
	 */
	@SuppressWarnings("BusyWait")
	public void startNotificationListener(BLEDevice device, GATTService service, GATTCharacteristic characteristic, Consumer<GATTNotification> consumer, long interval) {
		String key = device.address() + service.uuid() + characteristic.uuid();
		Thread thread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					List<GATTNotification> notifications = getNotifications(device, service, characteristic);
					if (!notifications.isEmpty()) {
						notifications.forEach(consumer);
					}
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		thread.start();
		notificationThreads.put(key, thread);
	}

	/**
	 * Stop a listener for notifications on a GATT characteristic.
	 *
	 * @param device         the {@link BLEDevice} to stop listening for notifications on
	 * @param service        the {@link GATTService}
	 * @param characteristic the {@link GATTCharacteristic}
	 */
	public void stopNotificationListener(BLEDevice device, GATTService service, GATTCharacteristic characteristic) {
		String key = device.address() + service.uuid() + characteristic.uuid();
		Thread thread = notificationThreads.remove(key);
		if (thread != null) {
			thread.interrupt();
		}
	}
}