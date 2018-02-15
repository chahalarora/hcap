package SecureAuthServer.SecureAuthServer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import ExceptionList.ExceptionList;

/**
 * This class represents the resource which performs garbage collection initiated 
 * by the resource servers.
 * 
 * @author lakshya.tandon
 *
 */
public class CoapFlushResource 
{	
	public static CoapResource createResource()
	{
		CoapResource flushResource  = new CoapResource("flush")
		{
			private String currUserState;
			private Stack<Long> permStack = new Stack<Long>();
			private int i = 0;
			
			@Override
			public void handleGET(CoapExchange exchange) 
			{
				System.out.println("Got something in GET.");
				exchange.respond(ResponseCode.CONTENT, "This is authorization server, currently it doesn't support GET.");
			}
					
			@Override
			public void handlePOST(CoapExchange exchange)
			{
				HashMap<String, Object> inPayloadMap = new HashMap<String, Object>();
				HashMap<Long, Object> inExMap = new HashMap<Long, Object>();
				HashMap<Long, Boolean> inBatonMap = new HashMap<Long, Boolean>();
				System.out.println("Flush Request Received.");
				
				/*
				if(HCAPAuthorizationServer.isPaused)
				{
					//Response response = new Response(ResponseCode.CONTENT);
					HashMap<String, Object> responseMap = new HashMap<String, Object>();
					responseMap.put("messageCode", 3);
					responseMap.put("messageText", "REQUEST CANCELLED");
					byte[] responsePayload = encodePayload(responseMap);
					
					exchange.respond(ResponseCode.CONTENT, responsePayload);
					return;
				}
				*/
				
				
				//byte[] payload = exchange.getRequestPayload();
				
				//startTime = System.currentTimeMillis();			-- THIS IS FOR EXPERIMENT 3
				
				
        		//Exception list map is received from the client.
				
				//no need to pause the complete server, that is for all session IDs
				//only pause the server for specific session for which the state is being updated.
        		//HCAPAuthorizationServer.isPaused = true;
        		
        		//HashMap<Long, ExceptionList> inExMap = null;
        		
        		byte[] inByteArr = exchange.getRequestPayload();
        		System.out.println("Payload Size: " + exchange.getRequestPayload().length);
        		
        		
        		
        		
        		if(HCAPAuthorizationServer.isCBOR)
        		{
        			cborConverter cCon = new cborConverter();
        			inPayloadMap = cCon.convertFromCBOR(inByteArr);
        		}
        		else
        		{
        			jsonConverter jCon = new jsonConverter();
        			inPayloadMap = jCon.convertFromJSON(inByteArr);
        		} 
        		
        		/*
        		 *OLD CODE, when only exception list was sent to the other side.
        		 * 
        		try
        		{
        			ByteArrayInputStream byteIn = new ByteArrayInputStream(inByteArr);
            	    ObjectInputStream in = new ObjectInputStream(byteIn);
            	    //inExMap = (HashMap<Long, ExceptionList>) in.readObject();
            	    payloadMap = (HashMap<String, Object>) in.readObject();
            	    //inExMap = (HashMap<Long, ExceptionList>) in.readObject();
        		}
        		catch(Exception ex)
        		{
        			ex.printStackTrace();
        		}
        		*/
        		
        		if(inPayloadMap != null)
        		{
        			inExMap = (HashMap<Long, Object>) inPayloadMap.get("ExList");
        			inBatonMap = (HashMap<Long, Boolean>) inPayloadMap.get("Baton");
        		}
        		
        		
            	//now update the exception List
            	//ArrayList<Long> sessIDs = (ArrayList<Long>) inExMap.keySet();
            	ArrayList<String> sessIDs = new ArrayList<String>();
            	
            	//Set<Long> set = inExMap.keySet();
            	
            	
            	for(Object key : inExMap.keySet())
            	{
            		sessIDs.add((String) key);
            	}
            	
            	//System.out.println("Sess IDs count are: " + sessIDs.size());
            	
            	for(String sessID : sessIDs)
            	{
            		HCAPAuthorizationServer.updateLockMap.put(Long.parseLong(sessID), true);
            		HashMap<String, Object> tempLis = (HashMap<String, Object>) inExMap.get(sessID);
            		
            		
            		ExceptionList exLis = createExceptionListFromMap(tempLis);
            		ManageUsers mgUser = HCAPAuthorizationServer.userMap.get(Long.parseLong(sessID));
            		
            		if(mgUser == null)
            		{
            			System.out.println(sessID);
            			System.out.println("NULL USER");
            		}
            		
            		currUserState = mgUser.getState();
            		mgUser.setStateBeforeGC(currUserState);
            		//currUserStateEntryTime = mgUser.getEntryTime();
            		
            		ArrayList lis = HCAPAuthorizationServer.sessionMap.get(Long.parseLong(sessID));
    				
            		//get states and transitions
    				States sObj = null;
    				StateTransitions tObj = null;
    				
    				for(Object o : lis)
    				{
    					if(o instanceof States)
    					{
    						sObj = (States) o;
    					}
    					
    					if(o instanceof StateTransitions)
    					{
    						tObj = (StateTransitions) o;
    					}
    				}
            		
            		
            		if(mgUser.getEntryTime() < exLis.getExTime())
            		{
            			processExList(exLis);
            			//Stack<String> pStack = processExList(exLis);
            			Stack<Long> pStack = permStack;
            			//System.out.print(pStack);
            			//System.out.println("		for session id: " + sessID);
            			
            			while(!pStack.isEmpty())
                    	{
                    		long exPerm = pStack.pop();
                        	if(exPerm != 0)
                        	{
                        		//write code to update state here.
                        		HashMap<String, String> oldNewStateMap = tObj.getMap().get((int)exPerm);
                        		String newUserState  = oldNewStateMap.get(currUserState);
                        		
                        		if(newUserState == null)
                        		{
                        			System.out.println("NULL STATE");
                        		}
                        		else
                        		{
                        			currUserState = newUserState;	
                        		}
                        	}
                    	}
            			
            			mgUser.setState(currUserState);
            			mgUser.setEntryTime(System.currentTimeMillis());
            			System.out.println("State entry time for state: " + currUserState + "  " + mgUser.getEntryTime() + "  session ID  " + sessID);
            		}
            		HCAPAuthorizationServer.updateLockMap.put(Long.parseLong(sessID), false);
            		
            		//otherwise,it is assumed that the list is already updated by the client using the update certificate.
            		//System.out.println("State updated for session ID: " + sessID);
            	}
            	
            	//after updating the exceptions, update the baton map of Authorization Server
            	for(Object ID : inBatonMap.keySet())
            	{
            		Long sessID = Long.parseLong(ID.toString());
            		ManageUsers mgr = HCAPAuthorizationServer.userMap.get(sessID);
            		if(inBatonMap.get(ID))
            		{
            			mgr.setBaton(true);
            		}
            		else
            		{
            			mgr.setBaton(false);
            		}
            	}
            	
            	
            	
            	// FOR EXPERIMENT 3
            	//endTime = System.currentTimeMillis();
            	
            	//timeArr[index] = (endTime - startTime);
            	//index++;
            	
            	//System.out.println("Updated state is: " + currUserState);
            	//System.out.println("States updated.");
            	
            	//System.out.println("Time elapsed: " + (endTime - startTime));
            	/*
            	if(index > 9)
            	{
            		//print timeArr to file.
            		//printToFile();
            	}
            	*/
        		
            	//after responding it can start processing the exception list -- this is done to avoid the resource server waiting
            	//System.out.println("\n\n\n\n");
            	
            	//System.out.println("Previous state was: " + );
            	
            	/*if(exchange.getRequestText() == null)
            	{
            		System.out.println("Null exception list received from resource server.");
            	}
            	
            	else if(exchange.getRequestText().equals("done"))
            	{
            		//start updating the exception list using exMap
            		processExceptionList(exMap, inReqsessID);
            		
            		ManageUsers mgUsr = userMap.get(inReqsessID);
        			mgUsr.setEntryTime(System.currentTimeMillis());
            		
            		
            		//after updation, send "updated" as response.
            		exMap.clear();
            		
            		response = "updated";
                	exchange.respond(response);
            	}
            	else
            	{
            		JSONObject getMap = null;
                	JSONParser par = new JSONParser();
            		try {
        				
            			JSONObject getObj = (JSONObject) par.parse(exchange.getRequestText());
            			inReqsessID = (Long) getObj.get("sessID");
        				
        				getMap = (JSONObject) getObj.get("exceptions");

        			} catch (ParseException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
            		exMap.putAll(getMap);
            		//exMap.putAll(convertToExList(getMap));
            		
            		response = "Success";
                	exchange.respond(response);
            	}*/
            	
            	
            	
            	
            	//ExListcounter++;
            	//System.out.println(ExListcounter);
            	
            	//processExceptionList(exMap);

            	//System.out.println("Current State update by resource server.");
            	//System.out.println("Previous state was: " + currState);
            	
            	
        		//System.out.println("\n\n\n\n");
        		//testPrint();
        		
        		//reply to resource server
        		String response = "Success";
            	exchange.respond(response);
        		
        		
            	//HCAPAuthorizationServer.isPaused = false;
			}
			
			
			public ExceptionList createExceptionListFromMap(HashMap<String, Object> exList)
		    {
		    	HashMap<String, Object> getExList = (HashMap<String, Object>) exList.get("ex");
		    	ExceptionList list = null;
		    	if(getExList != null)
		    	{
		    		list = createExceptionListFromMap(getExList);
		    	}
		    	else
		    	{
		    		ExceptionList exLis = new ExceptionList("noName", 0, 0, null);
		    		return exLis;
		    		//return null;
		    	}
		    	
		    	
		    	String name = (String) exList.get("name");
		    	long perm = Long.parseLong(exList.get("perm").toString());
		    	long time = Long.parseLong(exList.get("exTime").toString());
		    	ExceptionList returnList = new ExceptionList(name, perm, time, list);
		    	return returnList;
		    }
			
			public void processExList(ExceptionList inLis)
	        {
	        	//System.out.println("Into Process Exception List		" + i);
	        	
	        	//States inSObj = SObj;
	        	//StateTransitions inTObj = TObj;
	        	//long inSessID = sessID;
	        	//ManageUsers user = inUser;	        	
	        	while(inLis.getEx() != null)
	        	{
	        		//as you are going into recursion, keep the permissions in a stack.
	        		if(inLis.getPerm() != 0)
	        		{
	        			permStack.add(inLis.getPerm());
	        		}
	        		inLis = inLis.getEx();
	        		//i++;
	        		processExList(inLis);
	        		return;
	        	}
	        	//return permStack;
	        }
			
			private byte[] encodePayload(HashMap<String, Object> inMap)
			{
				byte[] returnValue = null;
				if(HCAPAuthorizationServer.isCBOR)
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
			
			private HashMap<String, Object> decodePayload(byte[] inData)
			{
				HashMap<String, Object> returnMap = null;
				if(HCAPAuthorizationServer.isCBOR)
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
		};
		return flushResource;
	}
}
