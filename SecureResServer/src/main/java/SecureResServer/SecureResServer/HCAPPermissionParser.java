package SecureResServer.SecureResServer;

import java.util.HashMap;

import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.Exchange;

/**
 * This class is used to parse a permission, i.e. get code and resource ID from the request.
 * This is later used to obtain a permission ID using Pair class. Permission ID represents
 * the permission identifier for the permission client wants to exercise.
 * 
 * @author lakshya.tandon
 *
 */
public class HCAPPermissionParser 
{
	/**
	 * This method does the permission parsing.
	 * 
	 * @param exchange
	 * @param inReferenceMap
	 * @return permission ID
	 */
	public long parsePermission(Exchange exchange, HashMap<Pair, Object> inReferenceMap)
	{
		Request req = exchange.getRequest();
		//byte[] requestPayload = req.getPayload();
		
		//get the Code of request(i.e. GET, POST, PUT or DELETE)
		Code code = req.getCode();
		String codeString = code.toString();
		
		//get the resource from the URI
		String uri = req.getURI();
		String[] splitArray = uri.split("/");
		String resID = splitArray[splitArray.length - 1];
		String resourceID = resID;
		
		//using code and resourceID, get the permissionID using permLookup.
		Pair pair = new Pair(codeString, resourceID);
		long permissionID = (Long) inReferenceMap.get(pair);
		
		return permissionID;
	}
}
