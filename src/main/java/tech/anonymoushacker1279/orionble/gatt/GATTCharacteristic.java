package tech.anonymoushacker1279.orionble.gatt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import tech.anonymoushacker1279.orionble.OrionBLE;
import tech.anonymoushacker1279.orionble.devices.BLEDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A record representing a GATT characteristic. The characteristic can be constructed manually if the information is
 * known, or discovered via {@link OrionBLE#discoverCharacteristics(BLEDevice, GATTService)}.
 *
 * @param uuid        the UUID of the characteristic
 * @param description the description of the characteristic
 * @param properties  the properties of the characteristic
 */
public record GATTCharacteristic(String uuid, String description, List<GATTProperties> properties) {

	/**
	 * Parse a list of GATT characteristics from a JSON response. Called by
	 * {@link OrionBLE#discoverCharacteristics(BLEDevice, GATTService)}.
	 *
	 * @param response the JSON response
	 * @return a list of GATT characteristics
	 */
	@ApiStatus.Internal
	public static List<GATTCharacteristic> parseCharacteristics(String response) {
		Gson gson = new GsonBuilder().create();
		List<Map<String, String>> raw = gson.fromJson(response, TypeToken.getParameterized(List.class, Map.class).getType());
		List<GATTCharacteristic> services = new ArrayList<>();
		for (Map<String, String> s : raw) {
			String[] rawProperties = s.get("Properties").split(", ");
			List<GATTProperties> properties = new ArrayList<>();
			for (String property : rawProperties) {
				properties.add(propertiesFromString(property));
			}

			services.add(new GATTCharacteristic(
					s.get("Uuid"),
					s.get("Description"),
					properties
			));
		}

		return services;
	}

	@Override
	public String toString() {
		return "%s - %s - %s".formatted(uuid, description, properties);
	}

	/**
	 * Convert a string to a GATT property.
	 *
	 * @param property the property as a string
	 * @return the GATT property
	 */
	private static GATTProperties propertiesFromString(String property) {
		for (GATTProperties p : GATTProperties.values()) {
			if (p.getProperty().equals(property)) {
				return p;
			}
		}

		throw new IllegalArgumentException("Invalid property: " + property);
	}

	/**
	 * An enum representing the properties of a GATT characteristic. Not all properties are supported yet.
	 */
	public enum GATTProperties {
		READ("Read"),
		WRITE_WITHOUT_RESPONSE("WriteWithoutResponse"),
		WRITE("Write"),
		NOTIFY("Notify"),
		INDICATE("Indicate");

		private final String property;

		GATTProperties(String property) {
			this.property = property;
		}

		public String getProperty() {
			return property;
		}
	}
}