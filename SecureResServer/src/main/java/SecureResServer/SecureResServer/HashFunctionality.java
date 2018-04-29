package SecureResServer.SecureResServer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class HashFunctionality 
{

	/**
	 * This method is used to verify the hash contained in the capability.
	 * 
	 * @param inHash
	 * @param inMap
	 * @return true if verification succeeds, false otherwise.
	 */
	public static boolean verifyHash(String inHash, Map<String, Object> inMap, String sharedKey, String userID)
	{
    	//JSONObject tempObj = new JSONObject(inObj);
    	
    	HashMap<String, Object> tempMap = new HashMap<String, Object>(inMap);
    	
    	tempMap.remove("hash");

		
		String computedHash = SHA256hash(getString(tempMap).concat(sharedKey).concat(userID));
		
		if(computedHash.equals(inHash))
		{
			return true;
		}
		return false;
	}
	
	
	/**
	 * This method is for the hashing algorithm.
	 * 
	 * @param input
	 * @return computed hash as a string.
	 */
	public static String SHA256hash(String input)
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
	
	/**
	 * Support method for hashing algorithm method.
	 * 
	 * @param arrayBytes
	 * @return
	 */
	private static String convertByteArrayToHexString(byte[] arrayBytes) 
	{
	    StringBuffer stringBuffer = new StringBuffer();
	    for (int i = 0; i < arrayBytes.length; i++) 
	    {
	        stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return stringBuffer.toString();
	}
	
	public static String getString(Map<String, Object> jObj)
	{
		//System.out.println(inObj.toString());
		
		StringBuilder retStr = new StringBuilder();
		
		retStr.append("sessID");
		retStr.append(jObj.get("sessID").toString());
		//retStr.append("stPerm");
		//retStr.append(inObj.get("stPerm").toString());
		//retStr.append("transPerm");
		//retStr.append(inObj.get("transPerm").toString());
		retStr.append("name");
		retStr.append(jObj.get("name").toString());
		retStr.append("nameDefs");
		//retStr.append(inObj.get("nameDefs"));
		retStr.append("tCreated");
		retStr.append(jObj.get("tCreated").toString());
		//retStr.append("tRenewed");
		//retStr.append(inObj.get("tRenewed").toString());
		retStr.append("certType");
		retStr.append(jObj.get("certType").toString());
		
		return retStr.toString();
	}
}
