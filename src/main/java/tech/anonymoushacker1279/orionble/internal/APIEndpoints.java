package tech.anonymoushacker1279.orionble.internal;

/**
 * An enum of API endpoints.
 */
public enum APIEndpoints {
	ROOT("/"),
	DISCOVER_DEVICES("/devices/discover"),
	CHECK_DEVICE_CONNECTION("/devices/%s"),
	DISCOVER_GATT_SERVICES("/devices/%s/services"),
	DISCOVER_GATT_CHARACTERISTICS("/devices/%s/service/%s"),
	READ_GATT_CHARACTERISTIC("/devices/%s/service/%s/characteristic/%s/read"),
	WRITE_GATT_CHARACTERISTIC("/devices/%s/service/%s/characteristic/%s/write"),
	REGISTER_NOTIFY_EVENT("/devices/%s/service/%s/characteristic/%s/register_notify"),
	UNREGISTER_NOTIFY_EVENT("/devices/%s/service/%s/characteristic/%s/unregister_notify"),
	GET_GATT_CHARACTERISTIC_NOTIFICATIONS("/devices/%s/service/%s/characteristic/%s/notifications");

	private final String endpoint;

	APIEndpoints(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getEndpoint(String... args) {
		return String.format(endpoint, (Object[]) args);
	}
}