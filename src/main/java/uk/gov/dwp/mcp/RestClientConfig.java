package uk.gov.dwp.mcp;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

	@Bean
	public static ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	@LoadBalanced
	public static RestOperations restOperations(ClientHttpRequestFactory clientHttpRequestFactory) throws Exception {
		return new RestTemplate(clientHttpRequestFactory);
	}

	@Value("${server.client.key-store}")
	private String keyStoreLocation;

	@Value("${server.client.key-store-password}")
	private String keyStorePassword;

	@Value("${server.client.protocol}")
	private String protocol;

	@Value("${server.client.trust-store}")
	private String trustStoreLocation;

	@Value("${server.client.trust-store-password}")
	private String trustStorePassword;

	@Bean
	public HttpClient httpClient() throws Exception {
		final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		try {
			String resourcePath = RestClientConfig.class.getClassLoader().getResource(this.keyStoreLocation).getFile();
			try (FileInputStream instream = new FileInputStream(resourcePath)) {
				keyStore.load(instream, this.keyStorePassword.toCharArray());
			}
		} catch (Exception e) {
			File filePath = new File(this.keyStoreLocation);
			try (FileInputStream instream = new FileInputStream(filePath)) {
				keyStore.load(instream, this.keyStorePassword.toCharArray());
			}
		}
		try {
			String resourcePath = RestClientConfig.class.getClassLoader().getResource(this.trustStoreLocation)
					.getFile();
			try (FileInputStream instream = new FileInputStream(resourcePath)) {
				trustStore.load(instream, this.trustStorePassword.toCharArray());
			}
		} catch (Exception e) {
			File filePath = new File(this.trustStoreLocation);
			try (FileInputStream instream = new FileInputStream(filePath)) {
				trustStore.load(instream, this.trustStorePassword.toCharArray());
			}
		}
		final SSLContext sslcontext = new SSLContextBuilder().useProtocol(this.protocol)
				.loadKeyMaterial(keyStore, this.keyStorePassword.toCharArray())
				.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();

		final HostnameVerifier hostVerifier = NoopHostnameVerifier.INSTANCE;
		final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, hostVerifier);
		return HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}

}
