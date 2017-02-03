package uk.gov.dwp.mcp;

import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

	@Value("${server.client.key-store}")
	private String keyStoreLocation;

	@Value("${server.client.key-store-password}")
	private String keyStorePassword;

	private Properties systemProperties;

	@Value("${server.client.trust-store}")
	private String trustStoreLocation;

	@Value("${server.client.trust-store-password}")
	private String trustStorePassword;

	public Properties getSystemProperties() {
		return this.systemProperties;
	}

	@Bean
	public MethodInvokingFactoryBean methodInvokingFactoryBean() {
		MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
		methodInvokingFactoryBean.setStaticMethod("java.lang.System.setProperties");
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		String keyStorePath, trustStorePath;
		try {
			keyStorePath = cl.getResource(this.keyStoreLocation).getPath();
		} catch (Exception e) {
			keyStorePath = this.keyStoreLocation;
		}
		try {
			trustStorePath = cl.getResource(this.trustStoreLocation).getPath();
		} catch (Exception e) {
			trustStorePath = this.trustStoreLocation;
		}
		this.systemProperties.setProperty("javax.net.ssl.trustStore", trustStorePath);
		this.systemProperties.setProperty("javax.net.ssl.keyStore", keyStorePath);
		this.systemProperties.setProperty("javax.net.ssl.keyStorePassword", this.keyStorePassword);
		this.systemProperties.setProperty("javax.net.ssl.trustStorePassword", this.trustStorePassword);
		methodInvokingFactoryBean.setArguments(new Object[] { this.systemProperties });
		return methodInvokingFactoryBean;
	}

	@Bean
	public Properties retrieveSystemProperties() {
		return System.getProperties();
	}

	@Resource(name = "retrieveSystemProperties")
	public void setSystemProperties(Properties systemProperties) {
		this.systemProperties = systemProperties;
	}
}
