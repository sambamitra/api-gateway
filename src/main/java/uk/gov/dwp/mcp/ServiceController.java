package uk.gov.dwp.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/api/")
@Api(value = "/", tags = { "API Gateway" }, description = "API Gateway")
public class ServiceController {

	private static Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);

	@Autowired
	private RequestService requestService;

	@GetMapping(value = "/book/available", produces = MediaType.TEXT_PLAIN_VALUE)
	@ApiOperation(httpMethod = "GET", value = "/book/available", notes = "This endpoint fetches the available books")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Available books fetched successfully"),
			@ApiResponse(code = 404, message = "No available book found"),
			@ApiResponse(code = 500, message = "Error while processing the request") })
	public ResponseEntity<String> available() {
		LOGGER.info("Fetching available books from Book service");
		final ResponseEntity<String> response = this.requestService.getAvailableBooks();
		return new ResponseEntity<String>(response.getBody(), response.getStatusCode());
	}

}