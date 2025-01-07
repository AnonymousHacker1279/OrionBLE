# OrionBLE

## A Java library that enables communication with Bluetooth Low Energy devices.

![](banner.png)

OrionBLE is a custom implementation of Bluetooth Low Energy (BLE) APIs that allows developers to communicate with nearby
devices easily in a standard Java environment.

### Why is this necessary?

Well, the only specification for Bluetooth Java APIs is
the [Java Bluetooth API (JSR-82)](https://en.wikipedia.org/wiki/Java_APIs_for_Bluetooth), however it is effectively
abandoned and has been stuck on Maintenance Draft Review 4 since 2010. Additionally, it only supported Java ME, which
is also effectively abandoned and left standard desktop applications without a way to communicate with BLE devices.

### Are there any alternatives?

There seem to be two JSR-82 implementations for Java SE, BlueCove and AvetanaBluetooth. Both are abandoned and have been
unmaintained for years! Intel's IoT devkit, [TinyB](https://github.com/intel-iot-devkit/tinyb), has also been abandoned
and archived since 2023.

Thus, OrionBLE was born, to provide a modern, easy-to-use, and well-documented Java library for BLE communication.

## How does it work?

OrionBLE is *not* an implementation of JSR-82, but rather a custom implementation. As of now, it only supports the
Windows platform.

OrionBLE is broken into two separate parts: the OrionBLE Java library and
the [OrionBLEServer](https://github.com/AnonymousHacker1279/OrionBLEServer) .NET application. The
server backend exposes a REST API that the Java library communicates with to interact with BLE devices. The server
is automatically included, so no additional setup is required.

## How do I use it?

Update your project's buildscript to include my Maven repository:

```groovy
repositories {
	maven {
		url "https://maven.anonymoushacker1279.tech/releases"
	}
	// Alternatively, snapshot versions may be available under:
	maven {
		url "https://maven.anonymoushacker1279.tech/snapshots"
	}
}
```

Then, add the following dependency to your project:

```groovy
dependencies {
	implementation "tech.anonymoushacker1279:OrionBLE:<VERSION>"
}
```

The latest version can be found on the [releases page](https://github.com/AnonymousHacker1279/OrionBLE/releases). There
are three usable artifacts: the regular one, `slim`, and `all`. The regular one contains the library and its backend
server, the `slim` one contains only the library, and the `all` one contains the library, server, and all dependencies
(such as `gson`).

The library is very simple to use. Here is an example of how to scan for BLE devices:

```java
import tech.anonymoushacker1279.orionble.*;

public class ScanForDevices {

	public static void main(String[] args) {
		// Create a new OrionBLE instance, with the server running on localhost and port 5249
		OrionBLE orion = new OrionBLE("http://localhost", 5249);
		// Wait for a server connection. Creating an instance above will automatically try to launch the backend server.
		// You don't technically have to wait, but it's recommended so you don't run into connection issues.
		// If you're sure a server is already running, feel free to skip this step.
		orion.waitForConnection(5);

		System.out.println("Discovering devices...");
		// Discover devices and print them to the console
		for (BLEDevice device : orion.discoverDevices()) {
			System.out.println(device);
		}
	}
}
```

You'll get an output similar to this:

```
Discovering devices...
Some Device (ADDRESS) - Not Paired
Another Device (ADDRESS) - Paired
Yet Another Device (ADDRESS) - Paired
```

There are more examples available in the repository under the test sources.

## BLE Feature Support

Support is added as needed. If you need a feature that is not currently supported, feel free to open an issue or
contribute to the project.

### GATT Characteristics

| Property                   | Supported |
|----------------------------|-----------|
| Broadcast                  | No        |
| Read                       | Yes       |
| Write with No Response     | Yes       |
| Write                      | No        |
| Notify                     | Yes       |
| Indicate                   | No        |
| Authenticated Signed Write | No        |

## License

OrionBLE is MIT licensed. See the [LICENSE](LICENSE) file for more information.