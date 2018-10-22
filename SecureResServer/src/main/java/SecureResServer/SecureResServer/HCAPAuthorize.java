package SecureResServer.SecureResServer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;

import ExceptionList.ExceptionList;

/**
 * This class is used to check request for its authenticity. It performs operations such as
 * extracting the capability from the request, verifying the hash contained in the capability 
 * amongst others. Once authorization succeeds, the request is forwarded to HCAPHandleRequest
 * class.
 * 
 * @author lakshya.tandon
 *
 */
public class HCAPAuthorize 
{
	private String userID;
	private long permissionID;
	private String sharedKey = HCAPResourceServer.serverSharedKey;
	private HashMap<String, Object> returnMap;
	
	private HashMap<String, Object> payloadMap;
	
	/**
	 * HCAPAuthorize constructor. 
	 * 
	 * @param inUserID
	 * @param inPayload
	 * @param inPermissionID
	 * @param propFilePath
	 * @param inSharedKey
	 */
	public HCAPAuthorize(String inUserID, byte[] inPayload, long inPermissionID)
	{
		userID = inUserID;
		permissionID = inPermissionID;
		//sharedKey = inSharedKey;
		
		if(HCAPResourceServer.isCBOR)
    	{
    		cborConverter cCon = new cborConverter();
    		//System.out.println("HCAP Authorize1: " + System.currentTimeMillis());
    		payloadMap = cCon.convertFromCBOR(inPayload);
    		//payloadMap = cborConverter.convertFromCBOR(inPayload);
    		//System.out.println("HCAP Authorize2: " + System.currentTimeMillis());

    	}
    	else
    	{
    		jsonConverter jCon = new jsonConverter();
    		payloadMap = jCon.convertFromJSON(inPayload);
    	}
		
	}
	
	
	/**
	 * This method is used to evaluate the request, It checks the integrity of the capability 
	 * contained in the request.
	 * 
	 * @return false if the check fails, true otherwise.
	 */
	public boolean evaluateRequest()
	{
		//System.out.println("Evaluate Request: " + System.currentTimeMillis());
		@SuppressWarnings("unchecked")
		HashMap<String, Object> ticketMap = (HashMap<String, Object>) payloadMap.get("ticket");
		
		//System.out.println("capability received at client " + ticketMap.toString());
		
		if(ticketMap.containsKey("certType"))
		{
			//long sessID = Long.parseLong(ticketMap.get("sessID").toString());
			
			/*
			boolean hashRes = HashFunctionality.verifyHash(ticketMap.get("hash").toString(), ticketMap, sharedKey, userID);
			//System.out.println(hashRes);				// for testing 
			if(hashRes == false)
			{
				System.out.println("Hash verification failed.");
				return false;
			}
			*/
			
			String vID = ticketMap.get("vID").toString();
			if(vID.equals(HCAPResourceServer.rsID))
			{
				//verify locally
				ValidationAlgorithm va = new ValidationAlgorithm();
				boolean result = va.validate(ticketMap, userID);
				if(!result)
				{
					System.out.println("Validation Failed.");
					return false;
				}
				
			}
			else
			{
				//ask the other resource server(vid) to verify the capability -- send a validation request to the other resource server
				long sessionID = Long.parseLong(ticketMap.get("sessID").toString());
				HashMap<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("userID", userID);
				requestMap.put("ticket", ticketMap);
				
				byte[] payload;
				if(HCAPResourceServer.isCBOR)
				{
					cborConverter cCon = new cborConverter();
					payload = cCon.convertToCBOR(requestMap);
				}
				else
				{
					jsonConverter jCon = new jsonConverter();
					payload = jCon.convertToJSON(requestMap);
				}
				
				
				Request request = new Request(Code.POST);
			    request.setURI(vID + "/hcap/validateResource");
			    request.setPayload(payload);
				request.getOptions().setSize1(payload.length);
				
				CoapClient cl = null;
				if(HCAPResourceServer.clientsForRS.containsKey(vID))
				{
					cl = HCAPResourceServer.clientsForRS.get(vID);
				}
				else
				{
					//create a new client, add it to map and use that client
					CoapClient client = new CoapClient("coaps://" + vID);
					client.setEndpoint(new CoapEndpoint(HCAPResourceServer.buildConnectorForRS(), NetworkConfig.getStandard()));
					client.setTimeout(1000000);
					HCAPResourceServer.clientsForRS.put(vID, client);
					cl = client;
				}
				
				CoapResponse response = cl.advanced(request);
				byte[] responsePayload = response.getPayload();
				
				HashMap<String, Object> responseMap;
				if(HCAPResourceServer.isCBOR)
				{
					cborConverter cCon = new cborConverter();
					responseMap = cCon.convertFromCBOR(responsePayload);
				}
				else
				{
					jsonConverter jCon = new jsonConverter();
					responseMap = jCon.convertFromJSON(responsePayload);
				}
				
				if(responseMap.containsKey("exceptionList"))
				{
					if(responseMap.get("exceptionList") == null)
					{
						return false;
					}
				}
				else
				{
					System.out.println("Validation Failed2.");
					return false;
				}
				
				/*
				if(!(Boolean) responseMap.get("validationSucceeded"))
				{
					return false;
				}
				*/
				
				//if validation succeeds, then update the baton which is received as response to the request. 
				ExceptionList ex = convertBatonToExList((HashMap<String, Object>) responseMap.get("exceptionList"));
				HCAPResourceServer.lisMap.put(sessionID, ex);
			}
			
			
			//locking for handling a request
			boolean isGCLocked = false;
			if(HCAPResourceServer.lockCounter.tryLock())
			{
				try
				{
					HCAPResourceServer.lCounter++;
					if(HCAPResourceServer.lCounter == 1)
					{
						if(HCAPResourceServer.lockGC.tryLock())
						{
							isGCLocked = true;
						}
					}	
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				finally
				{
					HCAPResourceServer.lockCounter.unlock();	
				}
			}
			
			//handle request inside lock				----			get capability hash verification out of handle request
			//check hash before acquiring lock
			//System.out.println(jObj.toJSONString());
			HCAPHandleRequest hReq = new HCAPHandleRequest(ticketMap, HCAPResourceServer.lisMap, userID, permissionID);
			//System.out.println("Compute response1: " + System.currentTimeMillis());
			returnMap = hReq.computeResponse();
			//System.out.println("Compute response2: " + System.currentTimeMillis());
			//System.out.println(retObj.toJSONString());
			//System.out.println("-----------------------");
			//System.out.println();
			//lisArr = hReq.getExList();
			//exListIndex = hReq.getExIndex();
			
			//System.out.println("Request Counter is: " + requestCount);
			
			if(HCAPResourceServer.lockCounter.tryLock())
			{
				try
				{
					HCAPResourceServer.lCounter--;
					if(HCAPResourceServer.lCounter == 0)
					{
						HCAPResourceServer.lockGC.unlock();
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				finally
				{
					HCAPResourceServer.lockCounter.unlock();	
				}
			}
			
			if(returnMap.containsKey("messageCode") && Integer.parseInt(returnMap.get("messageCode").toString()) == 0)
			{
				System.out.println(returnMap);
				return false;
			}
			return true;
		}
		System.out.println("Failed after HandleRequest2");
		return false;
	}
	
	/**
	 * This method is used to convert exception list contained in the baton to 
	 * ExceptionList data structure used in our protocol.
	 * 
	 * @param inBaton
	 * @return
	 */
	private ExceptionList convertBatonToExList(HashMap<String, Object> inBaton)
	{
		HashMap<String, Object> getExList = (HashMap<String, Object>) inBaton.get("ex");
		ExceptionList tempList = null;
		
		if(getExList != null)
    	{
			tempList = convertBatonToExList(getExList);
    	}
    	else
    	{
    		ExceptionList exLis = new ExceptionList("noName", 0, 0, null);
    		return exLis;
    		//return null;
    	}
    	
		
    	String name = (String) inBaton.get("name");
    	long perm = Long.parseLong(inBaton.get("perm").toString());
    	long time = Long.parseLong(inBaton.get("exTime").toString());
    	ExceptionList returnList = new ExceptionList(name, perm, time, tempList);
    	return returnList;
	}
	
	
	/**
	 * This method returns the request payload as a Map.
	 * 
	 * @return
	 */
	public HashMap<String, Object> getPayloadMap()
	{
		if(payloadMap != null)
		{
			return payloadMap;
		}
		return null;
	}
	
	/**
	 * This method returns the new computed ticket(capability or state update request).
	 * 
	 * @return
	 */
	public HashMap<String, Object> getNewTicket()
	{
		if(returnMap != null)
		{
			return returnMap;	
		}
		return null;
	}
}
