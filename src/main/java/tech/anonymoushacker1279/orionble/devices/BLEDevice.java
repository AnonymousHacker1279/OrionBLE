package tech.anonymoushacker1279.orionble.devices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import tech.anonymoushacker1279.orionble.OrionBLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A record representing a BLE device. The device can be constructed manually if the information is known, or discovered
 * via {@link OrionBLE#discoverDevices()}.
 *
 * @param name     the name of the device
 * @param address  the address of the device
 * @param isPaired whether the device is paired
 */
public record BLEDevice(String name, String address, boolean isPaired) {

	/**
	 * Parse a list of BLE devices from a JSON response. Called by {@link OrionBLE#discoverDevices()}.
	 *
	 * @param response the JSON response
	 * @return a list of BLE devices
	 */
	@ApiStatus.Internal
	public static List<BLEDevice> parseDevices(String response) {
		Gson gson = new GsonBuilder().create();
		List<Map<String, String>> raw = gson.fromJson(response, TypeToken.getParameterized(List.class, Map.class).getType());
		List<BLEDevice> devices = new ArrayList<>();
		for (Map<String, String> s : raw) {
			devices.add(new BLEDevice(
					s.get("Name"),
					s.get("Address"),
					Boolean.parseBoolean(s.get("Paired"))
			));
		}

		return devices;
	}

	public static boolean checkDeviceConnection(String response) {
		Gson gson = new GsonBuilder().create();
		try {
			String connected = gson.fromJson(response, Map.class).get("Connected").toString();
			return Boolean.parseBoolean(connected);
		} catch (JsonSyntaxException e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return "%s (%s) - %s".formatted(name, address, isPaired ? "Paired" : "Not Paired");
	}
}