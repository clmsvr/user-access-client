package cms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {

	String id_token;
	String access_token;
	String refresh_token;
	Long expires_in;
	String token_type;
}
