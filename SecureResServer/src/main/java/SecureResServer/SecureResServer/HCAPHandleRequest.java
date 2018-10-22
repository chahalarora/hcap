package SecureResServer.SecureResServer;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;


import ExceptionList.ExceptionList;

/**
 * This class is basically used to handle the request payload. In other words we can say that it
 * can be used to change the capability contained in the request payload.
 * 
 * @author lakshya.tandon
 */
public class HCAPHandleRequest {
	
	//private JSONObject jObj;
	HashMap<String, Object> jObj;
	private String sharedKey = HCAPResourceServer.serverSharedKey;
	private boolean catValue;
	// DateFormat dateFormat;
	//Date date;
	
	///not required now
	private ExceptionList[] lisArr;
	
	//this is the session mapping to exception List
	//HashMap<Long, ExceptionList> exLisMap = new HashMap<Long, ExceptionList>();
	HashMap<Long, ExceptionList> exLisMap = new HashMap<Long, ExceptionList>();
	
	private int exArrIndex;
	//private long tsValid = System.currentTimeMillis();	//Resource server last update time stamp
	private long tsEx;
	private long usePerm;
	private String userID;
	private long sessID;
	private String hash = null;
	private long tsCreated;
	//private long tsRenewed;
	private long currTime = 0;
	public String currentState;
	
	private HashMap<String, Object> retObj = null;
	
	private HashMap<Integer, Object> translis;
	private ArrayList<Integer> stlis;
	private HashMap<String, Object> namesArr;//defs
	private HashMap<String, Object> recievedNamesArr;
	private ArrayList<String> stPerms;
	private String name = null;
	
	/**
	 * Handle Request class constructor, reads capability components and store them in local class variables.
	 * 
	 * @param inObj
	 * @param inMap
	 * @param inUserID
	 * @param inPermID
	 */
	public HCAPHandleRequest(HashMap<String, Object> inObj, HashMap<Long, ExceptionList> inMap, String inUserID, long inPermID)
	{
		userID = inUserID;
		stPerms = new ArrayList<String> ();

		exLisMap = inMap; //mapping sessid to ex list
		jObj = inObj; //capability
		
		//read components from capability
		usePerm = inPermID;
		sessID = Integer.parseInt(jObj.get("sessID").toString());
		tsEx = System.currentTimeMillis();
		hash = jObj.get("hash").toString();
		tsCreated = Long.parseLong(jObj.get("tCreated").toString());
		//tsRenewed = Long.parseLong(jObj.get("tRenewed").toString());
		
		//parsing capability to create trans perm hashmap
		name = jObj.get("name").toString();
		namesArr = (HashMap<String, Object>) jObj.get("nameDefs");
		recievedNamesArr = namesArr;
		
		ArrayList<Object> perms = (ArrayList<Object>) namesArr.get(name);
		
		for(Object o : perms)
		{
			//parsing capability to create stationary permissions for a state.
			if(o instanceof ArrayList)
			{
				stlis = (ArrayList<Integer>) o;
			}
			
			//parsing capability to create transitioning permissions for a state.
			if(o instanceof HashMap)
			{
				translis = (HashMap<Integer, Object>) o;
			}
		}
		
		//translis = (HashMap<Integer, Object>) jObj.get("transPerm");
		//stlis = (ArrayList<Integer>) jObj.get("stPerm");
		
		
		//Categorize request
		catValue = Boolean.parseBoolean(jObj.get("certType").toString());
	}
	
	/**
	 * This method is used to create a new state update request.
	 * 
	 * @return state update request as a Map
	 */
	public HashMap<String, Object> createUpdReq()
	{
		if(catValue == true)
		{
			retObj = new HashMap<String, Object>();
			retObj.put("sessID", sessID);
			retObj.put("vID", HCAPResourceServer.rsID);
			
			//get the hashMap inside exception list
			ExceptionList ex = exLisMap.get(sessID);
			
			HashMap<Integer, Object> putExObj = putExceptionsAsHashMap(ex);
			
			retObj.put("exceptions", putExObj);
			retObj.put("certType", new Boolean(false));
			//System.out.println(getStringUpd(retObj).concat(sharedKey).concat(userID));
			retObj.put("hash", HashFunctionality.SHA256hash(getStringUpd(retObj).concat(sharedKey).concat(userID)));
		}
		else if(catValue == false)
		{
			return null;
			//do something here.
			//if update certificate is sent to resource server by client
		}
		else
		{
			//do something here.
		}		
		return retObj;
	}
	
	/**
	 * This method puts Exceptions into a map, this map it to be embedded in state update request.
	 * 
	 * @param inEx
	 * @return map with exceptions
	 */
	private HashMap<Integer, Object> putExceptionsAsHashMap(ExceptionList inEx)
	{
		ExceptionList exlis = inEx;
		int exCounter = 1;
		HashMap<Integer, Object> exObj = new HashMap<Integer, Object>();
		
		Stack<ExceptionList> lisStack = new Stack<ExceptionList> ();
		
		while(exlis != null)
		{
			lisStack.add(exlis);
			exlis = exlis.getEx();
		}
		
		while(!lisStack.isEmpty())
		{
			ArrayList<Object> permTimeArr = new ArrayList<Object>();
			ExceptionList templis = lisStack.pop();
			if(tsCreated > templis.getExTime())
			{
				//don't add exception to the exObj
			}
			else
			{
				permTimeArr.add(templis.getPerm());
				permTimeArr.add(templis.getExTime());
				exObj.put(exCounter, permTimeArr);
				exCounter++;
			}
		}
		return exObj;
	}
	
	/**
	 * This method is used to create a new capability.
	 * 
	 * @return capability as a map.
	 */
	private HashMap<String, Object> createNewCapability()
	{
		HashMap<String, Object> jObj = new HashMap<String, Object>();
		
		HashMap<String, Object> transArrNew;
		ArrayList<Object> stArrNew;
		
		String name = (String) translis.get(Integer.toString((int)usePerm));
		
		//for(Object o : namesArr)
		{
			//if(namesArr.containsKey(name))
			HashMap<String, Object> obj = (HashMap<String, Object>) namesArr;
			if(obj.containsKey(name))			
			{
				ArrayList<Object> permList = (ArrayList<Object>) obj.get(name);
				stArrNew = (ArrayList<Object>) permList.get(0);
				transArrNew = (HashMap<String, Object>) permList.get(1);
				
				//long timeNew = exTime;
				//exLisMap.get(sessID);
				
				jObj.put("vID", HCAPResourceServer.rsID);
				jObj.put("sessID", sessID);
				jObj.put("name", name);
				//jObj.put("stPerm", stArrNew);
				//jObj.put("transPerm", transArrNew);
				jObj.put("nameDefs", namesArr);
				
				
				jObj.put("tCreated", currTime);		   
				//jObj.put("tRenewed", currTime);
				
				//System.out.println("Exception Time which goes into capability is: " + currTime);
				
				jObj.put("certType", new Boolean(true));
			    String toHashString = HashFunctionality.getString(jObj);
			    jObj.put("hash", HashFunctionality.SHA256hash(toHashString.concat(sharedKey).concat(userID)));
			}
		}
		return jObj;
	}
	
	/*
	private String getString(HashMap<String, Object> inObj)
	{
		
		StringBuilder retStr = new StringBuilder();
		
		retStr.append("sessID");
		retStr.append(inObj.get("sessID").toString());
		retStr.append("stPerm");
		retStr.append(inObj.get("stPerm").toString());
		retStr.append("transPerm");
		//retStr.append(inObj.get("transPerm").toString());
		retStr.append("nameDefs");
		//retStr.append(inObj.get("nameDefs"));
		retStr.append("tCreated");
		retStr.append(inObj.get("tCreated").toString());
		//retStr.append("tRenewed");
		//retStr.append(inObj.get("tRenewed").toString());
		retStr.append("certType");
		retStr.append(inObj.get("certType").toString());
		
		return retStr.toString();
	}
	*/
	
	private boolean checkSessionInLock()
	{
		if(HCAPResourceServer.lisMapLock.containsKey(sessID))
		{
			if(HCAPResourceServer.lisMapLock.get(sessID))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method is used to get the updated tickets which is to be a part of 
	 * response payload. Ticket could be a capability or a state update request.
	 * 
	 * @return
	 */
	public HashMap<String, Object> computeResponse()
	{
		if(checkSessionInLock())
		{
			System.out.println("Session ID is locked.");
			return null;
		}
		
		try
		{
			//lock session id which has to be operated upon
			HCAPResourceServer.lisMapLock.put(sessID, true);
			
			
			//continue further to process capability
			int primaryChecksResult = primaryChecks();
			
			//2 means transitioning permission is exercised.
			if(primaryChecksResult == 2)
			{
				currTime = System.currentTimeMillis();
				
				//check if namesArr has further propagation
				if(!checkNamesArrProp())
				{
					//create a new capability to issue to client
					retObj = createNewCapability();
					//retObj.put("hash", SHA256hash(getString(retObj).concat(sharedKey).concat(userID)));
				}
				else
				{
					//issue update certificate
					retObj = createUpdReq();
					//return retObj;
				}
				
				addExceptionToList(retObj);
				
				//System.out.println("Its a transitioning request.");
				
				//add baton length to map
				if(HCAPResourceServer.batonLengthMap.containsKey(sessID))
				{
					int len = HCAPResourceServer.batonLengthMap.get(sessID);
					HCAPResourceServer.batonLengthMap.put(sessID, len + 1);
				}
				else
				{
					HCAPResourceServer.batonLengthMap.put(sessID, 1);
				}
				
				//System.out.println(HCAPResourceServer.batonLengthMap.get(sessID));
				
				//code to implement baton compression
				if(HCAPResourceServer.batonCompression)
				{
					if(doBatonCompressionIfRequired())
					{
						System.out.println("Baton Compression Done.");
					}
				}
				
				//increment the request counter
				HCAPResourceServer.requestCount++;
				HCAPResourceServer.hardGCCounter++;
				HCAPResourceServer.counter++;
				return retObj;
			}
			//1 means stationary permission is exercised.
			else if(primaryChecksResult == 1)
			{
				retObj = new HashMap<String, Object>();
				retObj.put("messageCode", 1);
				retObj.put("messageText", "Stationary permission exercised..");
				System.out.println(retObj);
				return retObj;
			}
			else if(primaryChecksResult == -1)
			{
				// this means an old capability was presented to the client, now return the the updated capability to client.
				retObj = getLostCapability();
				return retObj;
			}
			//otherwise, cannot exercise a permission.
			else
			{
				retObj = new HashMap<String, Object>();
				retObj.put("messageCode", 0);
				retObj.put("messageText", "Cannot exercise permission..");
				return retObj;
			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			//unlock lock for session ID
			HCAPResourceServer.lisMapLock.put(sessID, false);
		}
		return null;
	}
	
	private int computeBatonLength(ExceptionList ex)
	{
		int retValue = 0;
		while(ex.getEx() != null)
		{
			retValue++;
			ex = ex.getEx();
		}
		return retValue;
	}
	
	private boolean doBatonCompressionIfRequired()
	{
		int numStates = recievedNamesArr.keySet().size();
		int originalBatonLengthOld = HCAPResourceServer.batonLengthMap.get(sessID);
		System.out.println("Original Baton Length: " + originalBatonLengthOld);
		int originalBatonLength = computeBatonLength(HCAPResourceServer.lisMap.get(sessID));
		System.out.println("Original Baton Length: " + originalBatonLength);
		if(originalBatonLength > numStates)
		{
			//compressBaton now
			System.out.println("Doing baton compression for session ID: " + sessID);
			//printExList(HCAPResourceServer.lisMap.get(sessID));
			
			
			batonCompression bComp = new batonCompression(HCAPResourceServer.lisMap.get(sessID));
			ExceptionList compressedList = bComp.compress();
			
			//printExList(compressedList);
			
			//assign the compressed list to session ID and add the length of list to baton length map
			HCAPResourceServer.lisMap.put(sessID, compressedList);
			HCAPResourceServer.batonLengthMap.put(sessID, bComp.getBatonSize(HCAPResourceServer.lisMap.get(sessID)));
			return true;
		}
		return false;
	}
	
	//this method is just for testing
	/*
	private void printExList(ExceptionList lis)
	{
		while(lis.getEx() != null)
		{
			System.out.print(lis.getName());
			System.out.print(", " + lis.getPerm());
			System.out.print(" --> ");
		}
		System.out.println("\n");
	}
	*/
	
	/**
	 * This method is used to get back a capability which was lost by the client, basically reissue
	 * the most recent capability known to resource server.
	 * 
	 * @return capability as a map.
	 */
	private HashMap<String, Object> getLostCapability()
	{
		// check exception list, go upto the point where exTime < tCreated, 
		// Once you reach that point start creating new capabilities till you reach the start of the exception list.
		// return the latest capability to the client.
		HashMap<String, Object> retObj = null;
		Stack<Integer> usedPermStack = new Stack<Integer>();
		Stack<Long> exTimeStack = new Stack<Long>();
		
		ExceptionList exList = exLisMap.get(sessID);
		while(tsCreated < exList.getExTime())
		{
			usedPermStack.add((int) exList.getPerm());
			exTimeStack.add(exList.getExTime());
			exList = exList.getEx();
		}
		
		//Now while the stack is empty, you need to exercise all the permissions in the stack to obtain the final capability.
		while(!usedPermStack.isEmpty() && !exTimeStack.isEmpty())
		{
			int perm = usedPermStack.pop();
			currTime = exTimeStack.pop();
			usePerm = perm;
			
			if(!checkNamesArrProp())
			{
				//create a new capability to issue to client
				retObj = createNewCapability();
				
				//re-initialize list of transitioning permission and list of stationary permissions which are read from the capability.
				translis = (HashMap<Integer, Object>) retObj.get("transPerm");
				//parsing capability to create st perm hashmap
				stlis = (ArrayList<Integer>) retObj.get("stPerm");
			}
			else
			{
				//issue update certificate
				retObj = createUpdReq();
			}
		}
		return retObj;
	}
	/**
	 * This method checks if the capability contains enought information for
	 * resource server to construct a new capability.
	 * 
	 * @return true if a new capability can be constructed, false otherwise.
	 */
	private boolean checkNamesArrProp()
	{
		if(translis.containsKey(Integer.toString((int) usePerm)))
		{
			if(translis.get(Integer.toString((int) usePerm)) == null)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method checks the time stamps in a capability. It checks if the capability hsa expired
	 * or not.
	 * 
	 * @return true if capability has not expired, false otherwise.
	 */
	private boolean timeStampCheck()
	{
		if((!exLisMap.containsKey(sessID)) || (exLisMap.containsKey(sessID) && tsCreated > exLisMap.get(sessID).getExTime()))
		{
			ExceptionList ex = new ExceptionList(name, 0, tsCreated, null);
			exLisMap.put(sessID, ex);
		}
		else if(tsCreated < exLisMap.get(sessID).getExTime())
		{
			//System.out.println("This condition is true.");
			return false;
		}
		return true;		
	}
	
	/**
	 * This methods is used to perform checks within a capability.
	 * 
	 * @return an integer representing the type of permission. 1 if stationary permission has been 
	 * exercised, 2 if transitioning permission has been exercised, -1 if the time stamp check fails, 
	 * 0 otherwise.
	 */
	public int primaryChecks()
	{
		
		//Function for primary checks before doing this. Should have boolean return type.
		//boolean hashRes = verifyHash(jObj.get("hash").toString(), jObj);
		//System.out.println(hashRes);				// for testing 
		/*if(hashRes == false)
		{
			System.out.println("Hash verification failed.");
			return 0;
		}*/
		//if(tsCreated > HCAPResourceServer.rsTime)
		{
			//System.out.println("ts Renewed " + tsRenewed);
			//System.out.println("Resource Server Time: " + HCAPResourceServer.rsTime);
			
			//System.out.println("Into Primary checks 1");
			
			if(!timeStampCheck())
			{
				//System.out.println("timestamp check failed");
				//if this returns -1, this means that the capability presented was old capability, issue newest capability or state update request.
				return -1;
			}
			
			
			//if(checkExListforPerm(usePerm))
			{
				if(stlis.contains((int)usePerm))
				{
					//System.out.println("Valid stationary permission..");
					//System.out.println();
					//System.out.println("Access granted for session: " + sessID);
					return 1;
				}
				
				else if(translis.containsKey(Integer.toString((int)usePerm)))
				{
					//System.out.println("valid transititoning permission..");
					//System.out.println();
					//System.out.println("Access granted for session: " + sessID);
					//add permission to exceptionList
					return 2;
				}
				
				else
				{
					//deny access - permission not contained in capability
					//System.out.println("Invalid permission..");
					return 0;
				}
			}
			/*else
			{
				System.out.println("check exLis for permission failed.");
				System.out.println("Cannot exercise the requested permission.");
				return 0;
			}*/
		}
		/*else
		{
			//System.out.println(tsRenewed);
			//System.out.println(HCAPResourceServer.rsTime);
			System.out.println("renewed mismatch");
			//System.out.println("Cannot use permission");
			return 0;
		}
		*/
	}
	
	/**
	 * This method is used to add a permission to the exception list of resource server.
	 * 
	 */
	public void addExceptionToList(HashMap<String, Object> inObj)
	{
		String addName = null;
		if(retObj.get("name") != null) //retObj is the capability
		{
			addName = retObj.get("name").toString();
		}
		//call print before new exception
		ExceptionList ex = exLisMap.get(sessID);
		//ExceptionList newEx = new ExceptionList(addName, usePerm, currTime, ex); //Lakshs Part which works
		ExceptionList newEx = new ExceptionList(usePerm, currTime, ex , translis, namesArr);
		exLisMap.put(sessID, newEx);
		//call print after
	}
	
	/**
	 * this is the accessor method for exception list
	 * 
	 * @return
	 */
	public ExceptionList[] getExList()
	{
		return lisArr;
	}
	
	/**
	 * this is the accessor method for exception list's current index
	 * 
	 * @return
	 */
	public int getExIndex()
	{
		return exArrIndex;
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
