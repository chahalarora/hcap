package SecureResServer.SecureResServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;

import ExceptionList.ExceptionList;
/**
 * This class is used in the process of Garbage Collection, it basically updates the 
 * exception list by sending it as a request to the Authorization server.
 * 
 * @author lakshya.tandon
 *
 */
public class updateExList
{
	private ConcurrentHashMap<Long, ExceptionList> exMap;
	
	long mapSize;
	Long[] timeArr;
	long startTime = 0;
	long endTime = 0;
	int k = 0;
	
	private HashMap<Long, ExceptionList> toSendExList = new HashMap<Long, ExceptionList>();
	private HashMap<Long, Boolean> batonMap = new HashMap<Long, Boolean>();
	private HashMap<String, Object> payloadMap = new HashMap<String, Object>();
	
	private boolean isTimeThresholdBased;
	
	
	/**
	 * UpdateExcpetionList constructor, expects exception list map as an argument.
	 * 
	 * @param inExMap
	 */
	public updateExList(HashMap<Long, ExceptionList> inExMap, boolean inIsTimeThresholdBased)
	{
		exMap = new ConcurrentHashMap(inExMap);
		mapSize = inExMap.size();
		int timeArrSize =  (int) mapSize;
		timeArr = new Long[timeArrSize];
		isTimeThresholdBased = inIsTimeThresholdBased;
	}
	
	/**
	 * This method is used to compute the session IDs for which SoftGC is to be done
	 * and the IDs for which Hard GC is to be done.
	 * 
	 */
	/*
	public void computeForSoftGC()
	{
		for(long sessionID : exMap.keySet())
		{
			ExceptionList exlis = exMap.get(sessionID);
			if(exlis != null)
			{
				if(exlis.getEx() != null)
				{
					toSendExList.put(sessionID, exlis);
					long time = exlis.getExTime();
					String name = exlis.getName();
					ExceptionList newExLis = new ExceptionList(name, 0, time, null);
					//exMap.remove(sessionID);
					exMap.put(sessionID, newExLis);
					batonMap.put(sessionID, true);
				}
			}
		}
		
		long currentTime = System.currentTimeMillis();
		for(long sessionID : exMap.keySet())
		{
			ExceptionList exlis = exMap.get(sessionID);
			if(exlis != null)
			{
				if(exlis.getEx() == null)
				{
					//performing hard GC
					if((exlis.getExTime() + HCAPResourceServer.hardGCThreshold) > currentTime)
					{
						exMap.remove(sessionID);
						batonMap.put(sessionID, false);
						
						//HCAPResourceServer.hardGCRequestCount = 0;
					}
				}
			}
		}
	}
	*/
	
	/**
	 * This method is used to differentiate between the session IDs for whom softGC is to be 
	 * done and the ones for whom hard GC is to be done. 
	 * 
	 */
	public void computeForSoftGC()
	{
		for(long sessionID : exMap.keySet())
		{
			ExceptionList exlis = exMap.get(sessionID);
			if(exlis != null)
			{
				if(exlis.getEx() != null)
				{
					toSendExList.put(sessionID, exlis);
					long time = exlis.getExTime();
					String name = exlis.getName();
					ExceptionList newExLis = new ExceptionList(name, 0, time, null);
					//exMap.remove(sessionID);
					exMap.put(sessionID, newExLis);
					batonMap.put(sessionID, true);
					
					HCAPResourceServer.batonLengthMap.put(sessionID, 0);
				}
			}
		}
		
		
		
		//code to check for hard GC
		boolean hardGCDone = false;
		long currentTime = System.currentTimeMillis();
		for(long sessionID : exMap.keySet())
		{
			ExceptionList exlis = exMap.get(sessionID);
			if(exlis != null)
			{
				if(exlis.getEx() == null)
				{
					if(isTimeThresholdBased)
					{
						//perform hard GC based on hardGCThreshold value
						if((exlis.getExTime() + HCAPResourceServer.hardGCThreshold) > currentTime)
						{
							System.out.println("Hard GC initiated.");
							System.out.println("Session Removed: " + sessionID);
							exMap.remove(sessionID);
							batonMap.put(sessionID, false);
							
							HCAPResourceServer.sessionTimeMap.remove(sessionID);
							HCAPResourceServer.batonLengthMap.remove(sessionID);
						}	
					}
					else
					{
						//perform hardGC based on requestCounter value
						if(HCAPResourceServer.hardGCCounter >= HCAPResourceServer.hardGCThreshold)
						{
							if(exlis.getExTime() < HCAPResourceServer.lastGCTime)
							{
								//System.out.println("Hard GC initiated at request number: " + HCAPResourceServer.hardGCCounter);
								System.out.println("Session Removed: " + sessionID);
								exMap.remove(sessionID);
								batonMap.put(sessionID, false);
								HCAPResourceServer.sessionTimeMap.remove(sessionID);
								HCAPResourceServer.batonLengthMap.remove(sessionID);
								
								hardGCDone = true;	
							}
						}
					}
				}
			}
		}
		
		if(hardGCDone)
		{
			//here make hardGC counter = 0 if hard GC was performed.
			HCAPResourceServer.lisMap = new HashMap<Long, ExceptionList>(exMap);
			HCAPResourceServer.hardGCCounter = 0;
		}
		
		
	}
	
	
	
	/**
	 * This method sends a request to the Authorization Server, the request contains exception list
	 * as payload. In most of the cases blockwise transfers would be activated as the request payload
	 * size might increase over 1024 Bytes.
	 * 
	 * @return true if exception list is successfully updated, false otherwise.
	 */
	public Boolean sendRequest()
	{
		
		//Delay for experiment 4 because things were going too fast
		/*
		try {
			Thread.sleep(5);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		Boolean res = false;
		
		computeForSoftGC();
		
		//if map is empty, return false
		if(toSendExList.isEmpty() || batonMap.isEmpty())
		{
			//System.out.println("Exception Map is empty");
			return false;
		}
		
		//otherwise, continue with blockwise transfer of exception List
	    try
	    {
	    	payloadMap.put("ExList", toSendExList);
	    	payloadMap.put("Baton", batonMap);
	    	
	    	byte[] payload;
	    	
	    	if(HCAPResourceServer.isCBOR)
	    	{
	    		cborConverter cCon = new cborConverter();
	    		payload = cCon.convertToCBOR(payloadMap);
	    	}
	    	else
	    	{
	    		jsonConverter jCon = new jsonConverter();
	    		payload = jCon.convertToJSON(payloadMap);
	    	}
	    	
	    	/*
	    	// Convert ExList to byte array	
	    	ByteArrayOutputStream byteOut;
			ObjectOutputStream outStr;
	    	byteOut = new ByteArrayOutputStream();
		    outStr = new ObjectOutputStream(byteOut);
			outStr.writeObject(payloadMap);
			*/
	    	
			//Perform blockwise Transfer
		    Request request = new Request(Code.POST);
		    request.setURI(HCAPResourceServer.authServerAddress + "/hcap/flush");
			//request.setPayload(byteOut.toByteArray());
			//request.getOptions().setSize1(byteOut.toByteArray().length);
		    request.setPayload(payload);
			request.getOptions().setSize1(payload.length);
		    
			
			
			System.out.println("Payload Size: " + request.getPayloadSize());
			CoapResponse response = HCAPResourceServer.resToAuth.advanced(request);
			
			
			
			if(response.getResponseText().equals("Success"))
			{
				//System.out.println("Exception List successfully transported.");
				//ResourceServerDriver.rsTime = System.currentTimeMillis();
				//exMap.clear();
				System.out.println("printing ex MAP");
				for(Object key : exMap.keySet())
				{
					ExceptionList lis = exMap.get(key);
					System.out.println(key + "	" +lis.getExTime());
				}
				System.out.println("\n");
				HCAPResourceServer.lastGCTime = System.currentTimeMillis();
				
				return true;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
