package jenkins.plugin.assembla.api;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class AssemblaHttpClient {

	private String url;
	private String base64AuthString;
	private HttpClient httpClient;
	private InputStream responseStream;

	public AssemblaHttpClient(String url, String username, String password) {

		this.url = url;

		if (!url.startsWith("http")) {

			this.url = "http://" + url;
		}

		this.base64AuthString = new String(
				Base64.encode((username + ":" + password).getBytes()));
		this.httpClient = new HttpClient();
	}

	public AssemblaHttpClient(AssemblaSite assemblaSite) {

		this(assemblaSite.getUrl(), assemblaSite.getUsername(), assemblaSite
				.getPassword());
	}

	public InputStream getResponseStream() {
		return responseStream;
	}

	private GetMethod getHttpGet(String space, String resource) {

		String query = url;

		if (space != null) {

			query += "/spaces/" + space;
		}

		if (resource != null) {

			query += "/" + resource;
		}

		GetMethod getQuery = new GetMethod(query);
		getQuery.addRequestHeader("Accept", "application/xml");
		getQuery.addRequestHeader("Authorization", "Basic " + base64AuthString);
		return getQuery;
	}

	private PutMethod getHttpPut(String space, String resource) {

		String query = url;

		if (space != null) {

			query += "/spaces/" + space;
		}

		if (resource != null) {

			query += "/" + resource;
		}

		PutMethod putQuery = new PutMethod(query);
		putQuery.addRequestHeader("Accept", "application/xml");
		putQuery.addRequestHeader("Authorization", "Basic " + base64AuthString);
		return putQuery;
	}

	public int executeHttpGet(String space, String resource) throws IOException {

		GetMethod getQuery = getHttpGet(space, resource);
		int responseCode = httpClient.executeMethod(getQuery);
		responseStream = getQuery.getResponseBodyAsStream();
		return responseCode;
	}

	public int executeHttpPut(String space, String resource, String data)
			throws IOException {

		PutMethod putQuery = getHttpPut(space, resource);
		putQuery.setRequestEntity(new StringRequestEntity(data, "text/xml",
				"UTF-8"));
		int responseCode = httpClient.executeMethod(putQuery);
		responseStream = putQuery.getResponseBodyAsStream();
		return responseCode;
	}
}
