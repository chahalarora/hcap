package SecureResServer.SecureResServer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;

import ExceptionList.ExceptionList;

public class ValidationAlgorithm 
{
	/*
	private CoapExchange exchange;
	public ValidationAlgorithm()
	{
		exchange = inExchange;
	}
	*/
	
	public boolean validate(Map<String, Object> inCapability, String userID)
	{
		Map<String, Object> capability = inCapability;
		String inHash = (String) capability.get("hash");
		String sharedKey = HCAPResourceServer.serverSharedKey;
		
		boolean validationResult = HashFunctionality.verifyHash(inHash, capability, sharedKey, userID);
		
		//HashMap<String, Object> replyMap = new HashMap<String, Object>();
		if(!validationResult)
		{
			//replyWithFailure(replyMap, exchange);
			System.out.println("Hash verification failed.");
			return false;
		}
		
		//capability hash validation has succeeded
		long sessionID = Long.parseLong(capability.get("sessID").toString());
		System.out.println(sessionID);
		
		long tSer =  Long.parseLong(capability.get("tCreated").toString());
		
		//get rsTime for sessionID
		//long timeToCompare = HCAPResourceServer.sessionTimeMap.get(sessionID);
		
		
		if(HCAPResourceServer.lisMap.containsKey(sessionID))
		{
			System.out.println("tSer: " + tSer);
			
			if(HCAPResourceServer.lisMap.get(sessionID) == null)
			{
				System.out.println("Exception time: " + HCAPResourceServer.lisMap.get(sessionID).getExTime());	
			}
		}
		
		
		if(!HCAPResourceServer.lisMap.containsKey(sessionID))
		{
			if(HCAPResourceServer.sessionTimeMap.containsKey(sessionID))
			{
				long timeToCompare = HCAPResourceServer.sessionTimeMap.get(sessionID);
				if(tSer < timeToCompare)
				{
					System.out.println("Problem with time again.");
					return false;
				}
			}
			
			//Confirm two things with authorization server and get the time to compare for the sessionID
			//Store the time in sessionTime Map 
			
			Map<String, Object> toConfirmMap = new HashMap<String, Object>();
			toConfirmMap.put("sessionID", sessionID);
			toConfirmMap.put("tSerial", tSer);
			
			byte[] data = encodePayload(toConfirmMap);
			
			
			Request request = new Request(Code.POST);
		    request.setURI(HCAPResourceServer.authServerAddress + "/hcap/confirm");
		    request.setPayload(data);
			request.getOptions().setSize1(data.length);
			
			CoapResponse response = HCAPResourceServer.resToAuth.advanced(request);
			HCAPResourceServer.confirmationRequestCount++;
			
			byte[] getPayloadData = response.getPayload();
			
			Map<String, Object> getPayloadMap = decodePayload(getPayloadData);
			
			long timeToStore = Long.parseLong(getPayloadMap.get("timeForSession").toString());
			HCAPResourceServer.sessionTimeMap.put(sessionID, timeToStore);
			
			if((!(Boolean) getPayloadMap.get("confirmation")) || tSer < timeToStore)
			{
				System.out.println("Confirmation is false.");
				return false;
			}
			
			ExceptionList ex = new ExceptionList(null, 0, tSer, null);
			HCAPResourceServer.lisMap.put(sessionID, ex);
		}
		else if(tSer > HCAPResourceServer.lisMap.get(sessionID).getExTime())
		{
			ExceptionList ex = new ExceptionList(null, 0, tSer, null);
			HCAPResourceServer.lisMap.put(sessionID, ex);
		}
		else if(tSer < HCAPResourceServer.lisMap.get(sessionID).getExTime())
		{
			System.out.println("Problem comes from exception list.");
			System.out.println("tSer: " + tSer);
			System.out.println("Ex List: " + HCAPResourceServer.lisMap.get(sessionID).getExTime());
			return false;
		}
		
		/*
		replyMap.put("validationSucceeded", true);
		byte[] replyPayload = encodePayload(replyMap);
		//reply back to the other resource server
		exchange.respond(ResponseCode.CONTENT, replyPayload);
		*/
		return true;
	}
	
	private Map<String, Object> decodePayload(byte[] inData)
	{
		if(HCAPResourceServer.isCBOR)
		{
			cborConverter cCon = new cborConverter();
			return cCon.convertFromCBOR(inData);
		}
		else
		{
			jsonConverter jCon = new jsonConverter();
			return jCon.convertFromJSON(inData);
		}
	}
	
	private byte[] encodePayload(Map<String, Object> inMap)
	{
		if(HCAPResourceServer.isCBOR)
		{
			cborConverter cCon = new cborConverter();
			return cCon.convertToCBOR(inMap);
		}
		else
		{
			jsonConverter jCon = new jsonConverter();
			return jCon.convertToJSON(inMap);
		}
	}
}
