import java.io.Serializable;



public class email implements Serializable{
	//Each message contatined within a email object
	private String to;
	private String from;
	private String subject;
	private String body;
	private String attachType;
	private byte[] attachment; //attachment stored within byte array for ease of transport

	public String getFrom(){
        return from;
    }
    public String getBody(){
        return body;
	}
	public String getsubject(){
        return subject;
	}
	public String getto(){
        return to;
	}
	public String getattachType(){
        return attachType;
	}
	public byte[] getAttachment(){
	return attachment;
	}
	public void setAttachment(byte[] B){
	attachment = B;
	}
	public void setFrom(String F){
        from = F;
    	}
    	public void setBody(String B){
         body = B;
	}
	public void setsubject(String S){
        subject = S;
	}
	public void setto(String T){		
         to = T;
	}
	public void setattachType(String A){
        attachType = A;
    }
}