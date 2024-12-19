package tech.anonymoushacker1279.orionble.devices;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a device filter that can be used during discovery. Can be created via the {@link Builder} or directly.
 *
 * @param name       the name of the device
 * @param namePrefix the name prefix of the device
 */
public record DeviceFilter(@Nullable String name, @Nullable String namePrefix) {

	/**
	 * Convert the device filter to URI parameters for API calls.
	 *
	 * @return the URI parameters as a map
	 */
	@ApiStatus.Internal
	public Map<String, String> toUriParams() {
		Map<String, String> params = new HashMap<>();
		if (name != null) {
			params.put("name", name);
		}
		if (namePrefix != null) {
			params.put("namePrefix", namePrefix);
		}

		return params;
	}

	public static class Builder {
		private String name;
		private String namePrefix;

		/**
		 * Create a new {@link DeviceFilter} builder.
		 */
		public Builder() {
		}

		/**
		 * Set the name of the device.
		 *
		 * @param name the name of the device
		 * @return the builder
		 */
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		/**
		 * Set the name prefix of the device.
		 *
		 * @param prefix the name prefix of the device
		 * @return the builder
		 */
		public Builder namePrefix(String prefix) {
			this.namePrefix = prefix;
			return this;
		}

		/**
		 * Build the device filter.
		 *
		 * @return the device filter
		 */
		public DeviceFilter build() {
			return new DeviceFilter(name, namePrefix);
		}
	}
}