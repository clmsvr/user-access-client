package cms.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Data;

@Data
public class UserModel
{
	//private Long id;
	
	private String oidcId;
	private String providerName; 
	
    private String email; 
    private String name;
	
    private String city;
    private String state;
    
    private int    numBlocksSubtitled;
    private int    numBlocksTranslated;    
    
    private String comment;       
    
    //@DateTimeFormat(iso = ISO.DATE_TIME) //!! para o spring entender o formado da string de data.
    private LocalDateTime   creationDate ;
    //@DateTimeFormat(iso = ISO.DATE_TIME) //!! para o spring entender o formado da string de data.
    private LocalDateTime   updateDate ; 
}
