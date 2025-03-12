/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cms.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joe Grandja
 */
@Slf4j
@Configuration
@EnableWebSecurity 
public class SecurityConfig {

	public static final String clientID = "cognito";
	
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
	throws Exception 
	{
		http
//			.authorizeHttpRequests(requests -> requests
//				.anyRequest().permitAll()
//			)
//			.csrf(c -> c.disable())
			.oauth2Client(Customizer.withDefaults())
		;
		
		return http.build();
	}
	
//Opcional	
//	@Bean
//	OAuth2AuthorizedClientManager authorizedClientManager(
//			ClientRegistrationRepository clientRegistrationRepository,
//			OAuth2AuthorizedClientRepository authorizedClientRepository) 
//	{
//		OAuth2AuthorizedClientProvider authorizedClientProvider =
//				OAuth2AuthorizedClientProviderBuilder.builder()
//						.authorizationCode()
//						.refreshToken()
//						.clientCredentials()
//						//.password()
//						.build();
//		DefaultOAuth2AuthorizedClientManager authorizedClientManager = 
//				new DefaultOAuth2AuthorizedClientManager(
//						clientRegistrationRepository, authorizedClientRepository);
//		
//		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//		return authorizedClientManager;
//	}
    
	
	@Bean
	public RestTemplate restTemplate(
			RestTemplateBuilder builder,
			OAuth2AuthorizedClientManager clientManager) 
	{
		return builder.additionalRequestCustomizers( request -> {

			//montar a requisicao para o clent manager
			OAuth2AuthorizeRequest auth2AuthorizeRequest = OAuth2AuthorizeRequest
					.withClientRegistrationId(clientID)
					.principal("ClientTest") //nome qualquer para exibicao
					.build();

			OAuth2AuthorizedClient authorize = clientManager.authorize(auth2AuthorizeRequest);

			if(authorize != null) {
				OAuth2AccessToken accessToken = authorize.getAccessToken();
				
				//System.out.println(accessToken.getTokenValue());
				
				//request.getHeaders().setBasicAuth(usename, password);
				request.getHeaders().setBearerAuth(accessToken.getTokenValue());
			}
			else
			{
				log.error("Falha obtendo token com Authorization Server.");
			}
		}).build();
	}
	
	@Bean
	public RestClient restClient(RestClient.Builder builder, 
			OAuth2AuthorizedClientManager authorizedClientManager) 
	{
		OAuth2ClientHttpRequestInterceptor requestInterceptor =
			new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
		
		//https://github.com/spring-projects/spring-security/issues/16374
		
		builder
			.requestInterceptor(requestInterceptor)  //para autorizações oauth2
			//.requestInterceptor(new LoggerRequestInterceptor()) // para logar requisições
			//.baseUrl("https://api.example.com")
	        .defaultRequest( requestSpec ->
	          	requestSpec.attributes(
	              RequestAttributeClientRegistrationIdResolver.clientRegistrationId(clientID))
	        );
	        
		return builder.build();
	}	
	
}
