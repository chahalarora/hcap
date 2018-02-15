package SecureAuthServer.SecureAuthServer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashingAlgo {
	
	
	private String sharedKey;
	
	public HashingAlgo()
	{
		
	}
	
	public HashingAlgo(String vID)
	{
		sharedKey = HCAPAuthorizationServer.sharedSecretsMap.get(vID);
	}
	public String SHA256hash(String input)
	{
		String output = null;
		try{
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(input.getBytes(StandardCharsets.UTF_8)	);
			byte[] digest = md.digest();
			output = convertByteArrayToHexString(digest);
			
		}
		catch(Exception ex){
			System.out.println(ex.toString());
		}
		return output;
	}
	
	public String getSharedKey()
	{
		return sharedKey;
	}
	
	private  String convertByteArrayToHexString(byte[] arrayBytes) 
	{
	    StringBuffer stringBuffer = new StringBuffer();
	    for (int i = 0; i < arrayBytes.length; i++) {
	        stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
	                .substring(1));
	    }
	    return stringBuffer.toString();
	}

}
