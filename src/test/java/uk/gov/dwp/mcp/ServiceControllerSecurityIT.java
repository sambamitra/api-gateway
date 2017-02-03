package uk.gov.dwp.mcp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class, classes = { RestClientConfig.class,
		ApplicationConfig.class })
public class ServiceControllerSecurityIT {

	@Autowired
	private Environment env;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${server.client.trust-store}")
	private String trustStoreLocation;

	@Value("${server.client.trust-store-password}")
	private String trustStorePassword;

	@Test(expected = ResourceAccessException.class)
	public void clientHttpRequestsAreRefused() {
		// Given
		final String requestEndpointUrl = getBookServiceApiEndpoint();
		String httpRestEndpointUrl = requestEndpointUrl;
		if (requestEndpointUrl.startsWith("https://")) {
			// Replace https with http
			httpRestEndpointUrl = requestEndpointUrl.replaceFirst("https:", "http:");
		}

		// When
		this.restTemplate.getForEntity(httpRestEndpointUrl + "/available", String.class);

		// Then - access denied
	}

	@Test(expected = ResourceAccessException.class)
	public void clientHttpsAccessDeniedForBadCertificate()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		// Given
		final SSLConnectionSocketFactory badCertSocketFactory = new SSLConnectionSocketFactory(
				new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

		final HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(badCertSocketFactory).build();
		((HttpComponentsClientHttpRequestFactory) this.restTemplate.getRequestFactory()).setHttpClient(httpClient);

		// When
		this.restTemplate.getForEntity(getBookServiceApiEndpoint() + "/available", String.class);

		// Then - access denied
	}

	@Test(expected = ResourceAccessException.class)
	public void clientHttpsAccessDeniedForTrustoreWithoutClientKeystore() throws KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, FileNotFoundException, IOException, CertificateException {
		// Given
		final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		try (FileInputStream instream = new FileInputStream(
				RestClientConfig.class.getClassLoader().getResource(this.trustStoreLocation).getFile())) {
			trustStore.load(instream, this.trustStorePassword.toCharArray());
		}

		final SSLConnectionSocketFactory socketFactoryWithoutKeystore = new SSLConnectionSocketFactory(
				new SSLContextBuilder().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build());

		final HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactoryWithoutKeystore).build();
		((HttpComponentsClientHttpRequestFactory) this.restTemplate.getRequestFactory()).setHttpClient(httpClient);

		// When
		this.restTemplate.getForEntity(getBookServiceApiEndpoint() + "/available", String.class);

		// Then - access denied
	}

	private String getBookServiceApiEndpoint() {
		return this.env.getProperty("service.api.url");
	}
}
