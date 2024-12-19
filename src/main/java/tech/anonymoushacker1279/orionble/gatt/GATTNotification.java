package tech.anonymoushacker1279.orionble.gatt;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import tech.anonymoushacker1279.orionble.OrionBLE;
import tech.anonymoushacker1279.orionble.devices.BLEDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A record representing a GATT notification.
 *
 * @param serviceUUID        the UUID of the service
 * @param characteristicUUID the UUID of the characteristic
 * @param value              the value of the notification
 */
public record GATTNotification(String serviceUUID, String characteristicUUID, String value) {

	/**
	 * Parse a list of GATT notifications from a JSON response. Called by
	 * {@link OrionBLE#getNotifications(BLEDevice, GATTService, GATTCharacteristic)}.
	 *
	 * @param response the JSON response
	 * @return a list of GATT notifications. Will be empty if an error occurs, or if the response is empty.
	 */
	public static List<GATTNotification> parseNotifications(String response) {
		try {
			Gson gson = new Gson();
			List<Map<String, String>> raw = gson.fromJson(response, TypeToken.getParameterized(List.class, Map.class).getType());
			List<GATTNotification> notifications = new ArrayList<>();
			for (Map<String, String> s : raw) {
				notifications.add(new GATTNotification(
						s.get("Service"),
						s.get("Characteristic"),
						s.get("Value")
				));
			}

			return notifications;
		} catch (JsonSyntaxException | NullPointerException e) {
			return List.of();
		}
	}

	@Override
	public String toString() {
		return "Notification: %s - %s - %s".formatted(serviceUUID, characteristicUUID, value);
	}
}