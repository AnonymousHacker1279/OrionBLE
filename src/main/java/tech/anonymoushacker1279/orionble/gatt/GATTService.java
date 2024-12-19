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
 * A record representing a GATT service. The service can be constructed manually if the information is known, or
 * discovered via {@link OrionBLE#discoverServices(BLEDevice)}.
 *
 * @param uuid      the UUID of the service
 * @param isPrimary whether the service is primary
 */
public record GATTService(String uuid, boolean isPrimary) {

	/**
	 * Parse a list of GATT services from a JSON response. Called by {@link OrionBLE#discoverServices(BLEDevice)}.
	 *
	 * @param response the JSON response
	 * @return a list of GATT services
	 */
	@ApiStatus.Internal
	public static List<GATTService> parseServices(String response) {
		Gson gson = new GsonBuilder().create();
		List<Map<String, String>> raw = gson.fromJson(response, TypeToken.getParameterized(List.class, Map.class).getType());
		List<GATTService> services = new ArrayList<>();
		for (Map<String, String> s : raw) {
			services.add(new GATTService(
					s.get("Uuid"),
					Boolean.parseBoolean(s.get("IsPrimary"))
			));
		}

		return services;
	}

	@Override
	public String toString() {
		return "%s - %s".formatted(uuid, isPrimary ? "Primary" : "Secondary");
	}
}