package uk.gov.dwp.mcp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

@Service
public class RequestService {

	private static final String BOOK_SERVICE_ID = "book";

	@Autowired
	private RestOperations restOperations;

	@Autowired
	private ServiceClient serviceClient;

	public ResponseEntity<String> getAvailableBooks() {
		final String serviceUrl = this.serviceClient.getServiceUrl(BOOK_SERVICE_ID);
		final String url = serviceUrl + "/api/book/available";
		return callRestfulServiceByGet(url, String.class);
	}

	private <T> ResponseEntity<T> callRestfulService(final String url, final HttpMethod httpMethod,
			final HttpEntity<?> entity, Class<T> responseClass, final String param) {
		try {
			return this.restOperations.exchange(url, httpMethod, entity, responseClass, param);
		} catch (final HttpClientErrorException ex) {
			switch (ex.getStatusCode()) {
			case NOT_FOUND:
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			case BAD_REQUEST:
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			case FORBIDDEN:
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			default:
				throw ex;
			}
		}
	}

	private <T> ResponseEntity<T> callRestfulServiceByGet(final String url, final Class<T> responseClass) {
		try {
			return this.restOperations.getForEntity(url, responseClass);
		} catch (HttpClientErrorException ex) {
			switch (ex.getStatusCode()) {
			case NOT_FOUND:
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			case BAD_REQUEST:
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			default:
				throw ex;
			}
		}
	}

	private <T> ResponseEntity<T> callRestfulServiceByPost(final String url, final Object request,
			final Class<T> responseClass) {
		try {
			return this.restOperations.postForEntity(url, request, responseClass);
		} catch (final HttpClientErrorException ex) {
			switch (ex.getStatusCode()) {
			case NOT_FOUND:
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			case BAD_REQUEST:
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			default:
				throw ex;
			}
		}

	}

}
