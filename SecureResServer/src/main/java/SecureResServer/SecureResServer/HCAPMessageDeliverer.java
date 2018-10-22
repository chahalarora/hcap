package SecureResServer.SecureResServer;


import java.io.PrintWriter;
import java.security.Principal;
import java.util.HashMap;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.MessageDeliverer;
import org.eclipse.californium.core.server.ServerMessageDeliverer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

/**
 * This class encapsulates the access control code. Use it as a message deliverer on your server 
 * to check a request for its validity.
 * 
 * @author lakshya.tandon
 *
 */
public class HCAPMessageDeliverer implements MessageDeliverer
{
	private Resource root;
	private HashMap<Pair, Long> referenceMap; 			//to get the permissionID from pair
	private MessageDeliverer extendedDeliverer = null;
	private HashMap<String, Object> newTicket;
	
	/**
	 * Creates a new instance of HCAPMessageDeliverer using the default delivery method.
	 *
	 * @param inRoot
	 * @param inReferenceMap
	 */
	public HCAPMessageDeliverer(Resource inRoot, HashMap<Pair, Long> inReferenceMap)
	{
		root = inRoot;
		referenceMap = inReferenceMap;
	}
	
	/**
	 * Creates a new instance of HCAPMessageDeliverer using the extended deliverer which 
	 * further delivers request to default protocol(E.g. Californium) deliverer. This constructor
	 * will only deliver request to the extended deliverer. It is the job of extended deliverer 
	 * to deliver request to default deliverer.
	 * 
	 * @param inRoot
	 * @param inDeliverer
	 * @param inReferenceMap
	 */
	public HCAPMessageDeliverer(Resource inRoot, MessageDeliverer inDeliverer, HashMap<Pair, Long> inReferenceMap)
	{
		root = inRoot;
		referenceMap = inReferenceMap;
		extendedDeliverer = inDeliverer;
	}
	
	/**
	 * This method is executed when the request reaches the server message deliverer, it is used to deliver 
	 * request to the specified resource. Access control checks are performed in this method. 
	 * 
	 */
	public void deliverRequest(final Exchange exchange)
	{
		
		if(HCAPResourceServer.requestCounter == 10000)
		{
			//print request counter to file
			printToFile("/home/grads/lakshya.tandon/Desktop/MultiResourceServer-23Oct2017/ExperimentResults/Experiment5/batonCompression/Count", HCAPResourceServer.numberOfSessions);
			
		}
		
		//System.out.println(HCAPResourceServer.requestCount);
		Request req = exchange.getRequest();
		
		//System.out.println("Request arrival time Is: " + System.currentTimeMillis());
		
		//check if resource server is paused, do not continue if paused
		if(HCAPResourceServer.isPaused)
		{
			System.out.println("Server is paused");
			CoapExchange ex = new CoapExchange(exchange, (CoapResource) root);
			
			HashMap<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("messageCode", 2);
			responseMap.put("messageText", "isPaused");
			
			byte[] responsePayload = encodePayload(responseMap);
			
			ex.respond(ResponseCode.CONTENT, responsePayload);
			req.cancel();
			return;
		}
		
		//first check if garbage collection is required before this request can be processed.
		int GCValue = checkForGarbageCollection();
		if(GCValue != 0)
		{
			System.out.println("Garbage collection initiated");
			CoapExchange ex = new CoapExchange(exchange, (CoapResource) root);
			
			//Response response = new Response(ResponseCode.CONTENT);
			HashMap<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("messageCode", 2);
			responseMap.put("messageText", "isPaused");
			
			byte[] responsePayload = encodePayload(responseMap);
			
			ex.respond(ResponseCode.CONTENT, responsePayload);
			req.cancel();
			
			if(GCValue == 1)
			{
				performGarbageCollection(false);	
			}
			else if(GCValue == 2)
			{
				performGarbageCollection(true);
			}
			return;
		}
		
		
		//change this variable as per access decision.
		boolean cancelReq = false;
		
		byte[] requestPayload = req.getPayload();
		
		//get the Code of request(i.e. GET, POST, PUT or DELETE)
		Code code = req.getCode();
		String codeString = code.toString();
		
		//get the resource from the URI
		String uri = req.getURI();
		String[] splitArray = uri.split("/");
		String resID = splitArray[splitArray.length - 1];
		String resourceID = resID;
		
		
		//if the request is not a ping request
		if(!((resourceID.equals("ping") || resourceID.equals("validateResource")) && splitArray[splitArray.length -2].equals("hcap")))
		{
			//System.out.println("if time: " + System.currentTimeMillis());
			//using code and resourceID, get the permissionID using permLookup.
			Pair pair = new Pair(codeString, resourceID);
			long permissionID = (Long) referenceMap.get(pair);
			
			//System.out.println("before authorize time" + System.currentTimeMillis());
			//get Sender identity from certificate
			//Object o = req.getSenderIdentity().getClass();
			Principal pr = req.getSenderIdentity();
			String userID = pr.getName();
			
			
			//pass userID, permissionID, and requestPayload to the access control code.
			HCAPAuthorize auth = new HCAPAuthorize(userID, requestPayload, permissionID);
			cancelReq = auth.evaluateRequest();
			//System.out.println("after authorize time" + System.currentTimeMillis());
			
			//perform access control here, if access control fails, make cancelReq = true.
			if(!cancelReq)
			{
				System.out.println("Request Cancelled");
				CoapExchange ex = new CoapExchange(exchange, (CoapResource) root);
				
				
				//Response response = new Response(ResponseCode.CONTENT);
				HashMap<String, Object> responseMap = new HashMap<String, Object>();
				responseMap.put("messageCode", 3);
				responseMap.put("messageText", "REQUEST CANCELLED");
				
				byte[] responsePayload = encodePayload(responseMap);
				
				ex.respond(ResponseCode.CONTENT, responsePayload);
				req.cancel();
				return;
			}
			
			//if it comes here, that means that client has gained access
			
			newTicket = auth.getNewTicket();
			
			//System.out.println("New Ticket: " + newTicket);
			
			//add the new ticket to ticket field in payload and continue.
			HashMap<String, Object> payloadMap = auth.getPayloadMap();
			byte[] newPayload = changeTicket(payloadMap, newTicket);
			System.out.println("Payload Length: " + newPayload.length);
			req.setPayload(newPayload);
			
			HCAPResourceServer.requestCounter++;
			System.out.println("Confirmation request count: " + HCAPResourceServer.confirmationRequestCount);
		}
		
		//deliver new exchange to resources
		if(extendedDeliverer == null)
		{
			//System.out.println("Message deliverer " + System.currentTimeMillis());
			ServerMessageDeliverer finalDeliverer = new ServerMessageDeliverer(root);
			finalDeliverer.deliverRequest(exchange);	
		}
		else
		{
			//a custom message deliverer can be added here
			extendedDeliverer.deliverRequest(exchange);
		}
	}
	
	/**
	 * This method is used for garbage collection.
	 * 
	 */
	private void performGarbageCollection(boolean isTimeBased)
	{
		//now perform garbage collection
		//lock GC	----	try to lock GC	---- if lock acquired, then perform garbage collection.	
		if(HCAPResourceServer.lockGC.tryLock())
		{
			HCAPResourceServer.isPaused = true;
			try
			{
				//HCAPResourceServer.rsTime = System.currentTimeMillis();
				//System.out.println("Current RS time: " + HCAPResourceServer.rsTime);
				
				updateExList upd = new updateExList(HCAPResourceServer.lisMap, isTimeBased);
				Boolean res = upd.sendRequest();
				if(res)
				{
					//System.out.println("Current RS time: " + HCAPResourceServer.rsTime);
					//HCAPResourceServer.lisMap.clear();
					HCAPResourceServer.lastGCTime = System.currentTimeMillis();
					HCAPResourceServer.requestCount = 0;
					//System.out.println("Resource Server updated after exception list was updated.");
				}	
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				HCAPResourceServer.lockGC.unlock();	
			}
			HCAPResourceServer.isPaused = false;
		}
	}
	
	/**
	 * This method is used to check if garbage collection (GC)  is to be initiated.
	 * 
	 * @return true if GC is to be initiated, false otherwise.
	 */
	private int checkForGarbageCollection()
	{
		if(HCAPResourceServer.requestCount >= HCAPResourceServer.softGCCounter)
		{
			return 1;
		}
		
		if((System.currentTimeMillis() - HCAPResourceServer.lastGCTime) < HCAPResourceServer.softGCThreshold)
		{
			return 2;
		}
		return 0;
	}
	
	/**
	 * This method is used to change the ticket in the request payload.
	 * 
	 * @param inPayloadMap
	 * @param inNewTicket
	 * @return new request payload as byte array
	 */
	private byte[] changeTicket(HashMap<String, Object> inPayloadMap,  HashMap<String, Object> inNewTicket)
	{
		inPayloadMap.put("ticket", inNewTicket);
		
		byte[] returnPayloadData  = encodePayload(inPayloadMap);
		return returnPayloadData; 
	}
	
	/**
	 * This method is used to encode payload as JSON or CBOR data.
	 * 
	 * @param inMap
	 * @return encoded payload as a Byte Array
	 */
	private byte[] encodePayload(HashMap<String, Object> inMap)
	{
		byte[] returnValue = null;
		if(HCAPResourceServer.isCBOR)
		{
			cborConverter convert = new cborConverter();
			returnValue = convert.convertToCBOR(inMap);
		}
		else
		{
			jsonConverter convert = new jsonConverter();
			returnValue = convert.convertToJSON(inMap);
		}
		return returnValue;
	}
	
	/**
	 * This method is used to decode payload from JSON or CBOR.
	 * 
	 * @param inData
	 * @return decoded payload as a Map.
	 */
	private HashMap<String, Object> decodePayload(byte[] inData)
	{
		HashMap<String, Object> returnMap = null;
		if(HCAPResourceServer.isCBOR)
		{
			cborConverter convert = new cborConverter();
			returnMap = convert.convertFromCBOR(inData);
		}
		else
		{
			jsonConverter convert = new jsonConverter();
			returnMap = convert.convertFromJSON(inData);
		}
		return returnMap;
	}
	
	//experiment 5
	public void printToFile(String folderName, int inNumberOfSessions)
	{
		try
		{
			PrintWriter pr = new PrintWriter(folderName + "/" + inNumberOfSessions + ".csv");
			pr.println(HCAPResourceServer.confirmationRequestCount);
			pr.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void deliverResponse(Exchange exchange, Response response) {
		// TODO Auto-generated method stub
		
	}
}
