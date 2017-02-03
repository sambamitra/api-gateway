package uk.gov.dwp.mcp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ServiceClient {

	@Autowired
	private Environment env;

	String getServiceUrl(String serviceId) {
		return "https://" + this.env.getProperty("service.api.id." + serviceId);
	}
}
