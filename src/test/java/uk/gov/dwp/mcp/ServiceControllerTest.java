package uk.gov.dwp.mcp;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestOperations;

@RunWith(SpringRunner.class)
@WebMvcTest(ServiceController.class)
public class ServiceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RequestService serviceMock;

	@MockBean
	private RestOperations restOperations;

	@MockBean
	private ServiceClient serviceClient;

	@Test
	public void testAvailable() throws Exception {
		// Given
		given(this.serviceMock.getAvailableBooks()).willReturn(new ResponseEntity<>("Abcd", HttpStatus.OK));
		// When
		final ResultActions action = this.mockMvc.perform(get("/api/book/available"));
		// Then
		action.andExpect(status().isOk());
		assertEquals("Response not as expected", "Abcd", action.andReturn().getResponse().getContentAsString());
	}

}
