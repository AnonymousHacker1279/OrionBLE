package tech.anonymoushacker1279.orionble.internal;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class RESTHandler {

	private final HttpClient client;
	private final String serverAddress;

	/**
	 * Create a RESTHandler instance. All API requests are made through this class.
	 *
	 * @param client        a {@link HttpClient} instance
	 * @param serverAddress the server address
	 */
	public RESTHandler(HttpClient client, String serverAddress) {
		this.client = client;
		this.serverAddress = serverAddress;
	}

	/**
	 * Make a request to the server.
	 *
	 * @param request the {@link HttpRequest} to make
	 * @return the response from the server
	 */
	private String makeRequest(HttpRequest request) {
		try {
			return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Make a GET request to the server with no parameters.
	 *
	 * @param endpoint the endpoint to make the request to. Find endpoints in {@link APIEndpoints}.
	 * @return the response from the server
	 */
	public String getRequest(String endpoint) {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(serverAddress + endpoint))
				.build();

		return makeRequest(request);
	}

	/**
	 * Make a GET request to the server with parameters.
	 *
	 * @param endpoint the endpoint to make the request to. Find endpoints in {@link APIEndpoints}.
	 * @param params   the parameters to include in the request
	 * @return the response from the server
	 */
	public String getRequest(String endpoint, Map<String, String> params) {
		StringBuilder uri = new StringBuilder(serverAddress + endpoint);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			uri.append("?").append(entry.getKey()).append("=").append(entry.getValue());
		}

		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(uri.toString()))
				.build();

		return makeRequest(request);
	}

	/**
	 * Make a POST request to the server.
	 *
	 * @param endpoint the endpoint to make the request to. Find endpoints in {@link APIEndpoints}.
	 * @param message  the message to send in the request
	 * @return the response from the server, if any
	 */
	public String postRequest(String endpoint, JsonObject message) {
		HttpRequest request = HttpRequest.newBuilder()
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(message.toString()))
				.uri(URI.create(serverAddress + endpoint))
				.build();

		return makeRequest(request);
	}
}