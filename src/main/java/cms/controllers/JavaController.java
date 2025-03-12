package cms.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;

import cms.model.TokenResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/java")
public class JavaController {

	String authorizeUrl = "https://us-east-1fssjl3xir.auth.us-east-1.amazoncognito.com/oauth2/authorize";
	
	String tokenUrl = "https://us-east-1fssjl3xir.auth.us-east-1.amazoncognito.com/oauth2/token";
	
	@Value("${spring.security.oauth2.client.registration.cognito.client-id}") 
	String clientId;
	
	@Value("${spring.security.oauth2.client.registration.cognito.client-secret}") 
	String clientSecret;
	
	String scope = "email openid phone";
	
	String callbackUrl = "http://localhost:5555/java/code";
	
	String usersApiUrl = "http://localhost:8080/api/users";
	
	
	@GetMapping
	public String java() {
		return "java";
	}

	@PostMapping
	public String javaConsulte(HttpSession session) {
		
		double d = Math.random();
		byte[] bytes = new String(""+d).getBytes();
		String state = new String (Base64.getEncoder().encode(bytes));
		session.setAttribute("state", state);
		
		String redirectUri = String.format("redirect:%s?response_type=code&client_id=%s&state=%s&redirect_uri=%s&scope=%s",
				authorizeUrl,clientId,state,callbackUrl,scope);
		
		return redirectUri;
	}
	
	
	@GetMapping("/code")
	public String javaCode(
			Model model,
			HttpSession session,
			@RequestParam(value = "code", required = false) String code, 
			@RequestParam(value = "state", required = false) String state, 
			@RequestParam(value = "error", required = false) String error) 
	{
		try 
		{
			if (code != null) 
			{
				//Code
				model.addAttribute("code", code);
				
				//Validar state
				String stateSession = (String)session.getAttribute("state");
				if(stateSession.equals(state) == false)
				{
					model.addAttribute("error", "'state' inválido!");
					return "java";
				}
				
				TokenResponse token = getToken(code);
				
				
				if (token.getAccess_token() == null)
				{
					model.addAttribute("error", "Token vazio");
					return "java";
				}
				 
				model.addAttribute("token", token.getAccess_token());
				
				String resultado = getUsers(token);
				
				model.addAttribute("resultado", resultado);
				
				return "java";
			}
			else {
				if (error != null) 
					model.addAttribute("error", error);
				else
					model.addAttribute("error", "erro desconhecido");
				return "java";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", e.toString());
			return "java";
		}
	}

	private TokenResponse getToken(String code) 
	throws URISyntaxException 
	{
		String credentials = clientId+":"+clientSecret ; 
		credentials =  Base64.getEncoder().encodeToString(credentials.getBytes() );
		
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "authorization_code");
		formData.add("code", code);
		formData.add("redirect_uri", callbackUrl);
		
		//obter token
		RestClient restClient = RestClient.create();
		
		TokenResponse token = restClient.post()
			.uri(new URI(tokenUrl))
			.header("Authorization", "Basic "+credentials)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(formData)
			.retrieve()
			.body(TokenResponse.class);
			
//				String json = restClient.post()
//						.uri(new URI(tokenUrl))
//						.header("Authorization", "Basic "+credentials)
//						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
//						.body(formData)
//						.retrieve()
//						.body(String.class);
//				
//				System.out.println(json);
//				
//				ObjectMapper mapper = new ObjectMapper();
//				TokenResponse token = mapper.readValue(json, TokenResponse.class);
		return token;
	}
	

	
	private String getUsers(TokenResponse token) 
	throws URISyntaxException, JsonProcessingException 
	{
		RestClient restClient = RestClient.create();
		
//		UserModel[] resultado = restClient.get()
//			.uri(new URI(usersApiUrl))
//			.header("Authorization", "Bearer "+token.getAccess_token())
//			.retrieve()
//			.body(UserModel[].class);
//		
////		@Autowired
////		ObjectMapper mapper;
//		
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.findAndRegisterModules();		
//		return mapper.writeValueAsString(resultado);
//      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultado);
		
		String resultado = restClient.get()
				.uri(new URI(usersApiUrl))
				.header("Authorization", "Bearer "+token.getAccess_token())
				.retrieve()
				.body(String.class);
		
		return resultado;
	}
}
