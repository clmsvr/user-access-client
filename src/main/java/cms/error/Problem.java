package cms.error;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;

@Data
public class Problem {

	private Integer status;
	private String type;
	private String title;
	private String detail;
	private OffsetDateTime timestamp;
	
	private List<Fields> fields;
	
	public String prettyPrint()
	{
		StringBuffer buff = new StringBuffer("""
				Problema:
				 status: %s
				 type: %s
				 title: %s
				 detail: %s
				 timestamp: %s
				 # Fields:""");
		
		if(fields != null) {
			fields.stream().forEach(p -> 
				buff.append("\n   - " +p.getName() + " : " + p.getUserMessage()) );
		}	
		
		return String.format(buff.toString(), status, type, title, detail,timestamp);
	}
}