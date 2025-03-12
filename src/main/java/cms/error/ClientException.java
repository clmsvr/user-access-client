package cms.error;

import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Problem problem;
	
	private HttpStatusCode httpStatusCode;
	private String         httpStatusText;
	private HttpHeaders    responseHeaders;
	private String         responseBody;
	

	public ClientException(Exception cause) {
		super(cause);
		deserializeProblem(cause);
	}

	private void deserializeProblem(Exception cause) 
	{
		if (cause instanceof RestClientResponseException)
		{
			var e = (RestClientResponseException) cause;
			httpStatusCode = e.getStatusCode();
			httpStatusText = e.getStatusText();
			responseHeaders = e.getResponseHeaders();
			responseBody = e.getResponseBodyAsString(Charset.forName("UTF-8"));
		}
		
		if (responseBody == null) return;
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JavaTimeModule());
		mapper.findAndRegisterModules();
		
		try {
			this.problem = mapper.readValue(responseBody, Problem.class);
		} catch (JsonProcessingException e) {
			log.debug("Não foi possível desserializar a resposta em um problema", e);
		}
	}
	
	@Override
	public String toString() {
		String error = """
				-------------------------------------------------------------
				%s
				Message: %s
				Http Status Code: %s
				Http Status Text: %s
				Headers:
				 %s
				Body:
				 %s
				%s
				-------------------------------------------------------------- 
				""";
		
		return String.format(error, 
				this.getClass().getName(),
				getMessage(),
				httpStatusCode.value(),
				httpStatusText,
				HttpHeaders.formatHeaders(responseHeaders),
				responseBody,
				problem.prettyPrint());
	}
}