module tech.anonymoushacker1279.OrionBLE {
	requires static org.jetbrains.annotations;
	requires java.desktop;
	requires java.net.http;
	requires transitive com.google.gson;

	exports tech.anonymoushacker1279.orionble;
	exports tech.anonymoushacker1279.orionble.devices;
	exports tech.anonymoushacker1279.orionble.gatt;
	exports tech.anonymoushacker1279.orionble.internal;
}