package uk.gov.dwp.mcp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

@RunWith(MockitoJUnitRunner.class)
public class RequestServiceTest {

	@Mock
	private RestOperations restTemplateMock;

	@InjectMocks
	private RequestService service;

	@Mock
	private ServiceClient serviceClientMock;

	@Test
	public void getAvailableBooksShouldReturnStatusOK() {
		// Given
		given(this.serviceClientMock.getServiceUrl(anyString())).willReturn("http://localhost:8081");
		final ResponseEntity<String> response = new ResponseEntity<>("Abcd", HttpStatus.OK);
		given(this.restTemplateMock.getForEntity(anyString(), eq(String.class))).willReturn(response);

		// When
		final ResponseEntity<String> actualResult = this.service.getAvailableBooks();

		// Then
		assertThat("Actual result not as expected.", actualResult.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat("Actual result not as expected.", actualResult.getBody(), is(equalTo("Abcd")));
	}

}
