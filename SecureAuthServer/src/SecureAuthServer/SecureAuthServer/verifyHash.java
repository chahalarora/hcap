
package SecureAuthServer.SecureAuthServer;

import java.util.HashMap;


public class verifyHash {
	
	public Boolean checkHash(HashMap<String, Object> obj, boolean certType, String clientID)
	{
		String inHash = obj.get("hash").toString();
		
		obj.remove("UserID");		
		obj.remove("hash");
		
		//System.out.println(obj.toJSONString());
		
		HashingAlgo hash = new HashingAlgo(obj.get("vID").toString());
		
		String computedHash;
		if(certType)
		{
			computedHash = hash.SHA256hash(getString(obj).concat(hash.getSharedKey()).concat(clientID));	
		}
		else
		{
			computedHash = hash.SHA256hash(getStringUpd(obj).concat(hash.getSharedKey()).concat(clientID));
		}
		
		if(computedHash.equals(inHash))
		{
			return true;
		}
		return false;
	}
	
	public String getString(HashMap<String, Object> inObj)
	{
		
		StringBuilder retStr = new StringBuilder();
		
		retStr.append("sessID");
		retStr.append(inObj.get("sessID").toString());
		retStr.append("stPerm");
		retStr.append(inObj.get("stPerm").toString());
		retStr.append("transPerm");
		retStr.append(inObj.get("transPerm").toString());
		retStr.append("nameDefs");
		retStr.append(inObj.get("nameDefs"));
		retStr.append("tCreated");
		retStr.append(inObj.get("tCreated").toString());
		//retStr.append("tRenewed");
		//retStr.append(inObj.get("tRenewed").toString());
		retStr.append("certType");
		retStr.append(inObj.get("certType").toString());
		
		return retStr.toString();
	}
	
	
	public String getStringUpd(HashMap<String, Object> inObj)
	{
		StringBuilder retStr = new StringBuilder();
		
		retStr.append("sessID");
		retStr.append(inObj.get("sessID").toString());
		retStr.append("exceptions");
		retStr.append(inObj.get("exceptions").toString());
		retStr.append("certType");
		retStr.append(inObj.get("certType").toString());
		
		//System.out.println(retStr.toString());
		
		return retStr.toString(); 
	}
	

}
