package cms.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

	@Autowired
	private RestClient restClient;
	
	String usersApiUrl = "http://localhost:8080/api/users";
	
	@GetMapping
	public String home() 
	{
		return "oauth2";
	}
	
	//@PostMapping  Nao pode ser POST , pois o REDIRECT da authotização eh via GET.
	@GetMapping("/users")
	public String listUsers(Model model) 
	{
		try {
			String resultado = restClient.get()
				.uri(new URI(usersApiUrl))
//				.attributes(RequestAttributeClientRegistrationIdResolver
//						.clientRegistrationId("cognito"))
				.retrieve()
				.body(String.class);
			
			model.addAttribute("resultado", resultado);
			return "oauth2";
		} 
		catch (ClientAuthorizationRequiredException e) {
			throw e; //!!!! Esta excdption precissa vazar para dar sequencia ao processo de autorização
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			model.addAttribute("error", e.toString());
			return "oauth2";
		}
	}
	
}


/*
//		restClient.get()
//		    .uri(uriBuilder -> uriBuilder
//		         .path("/" + pathVariable)
//		         .queryParam("param1", "value1")
//		         .queryParam("param2", "value2")
//		         .queryParam("param3", "value3")
//		         .queryParam("param4", "value4")
//		         .queryParam("param5", "value5")
//		         .build())
//		    .header("Content-Type", "application/json")
//		    .retrieve()
//		    .toEntity(String.class); 
*/