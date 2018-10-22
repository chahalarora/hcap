package SecureAuthServer.SecureAuthServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * This class is used to create a new capability to be issued to a client.
 * 
 * @author lakshya.tandon
 *
 */
public class CreateCapability {
	
	private long tempSessID;
	private String clientID;

	private int nameCount = 1;
	private HashMap<Integer, Object> transArr;
	private ArrayList<Object> stArr;
	private ArrayList<Object> namesArr;
	private String vID;

	
	States sObj;
	StateTransitions tObj;

	private String currState;
	private long inTCreated;
	private int capProp;
	
	
	
	// get states - {permission} map
	private HashMap<String, ArrayList<Integer>> checkstPerm;
	private HashMap<String, ArrayList<Integer>> checktransPerm;
	
	// get permissions - currentState - nextState MAP
	private HashMap<Integer, HashMap<String, String>> checkTrans;
	
	//hashMap for nameDefs
	private HashMap<String, ArrayList<Object>> nameDefsMap;
	//hashMap for transArr
	private HashMap<String, HashMap<Integer, Object>> transArrMap;
	
	//hashMap for fragment
	private HashMap<Long, HashMap<String, ArrayList<Object>>> fragMap;
	
	
	private Stack<String> stateStack = new Stack<String>();
	
	private String sharedKey = HCAPAuthorizationServer.serverSharedKey;
	
	//this maps state name to a name definition valid for that state
	private HashMap<String, String> stateNameDefMap = new HashMap<String, String> ();
		
	//this maps a name definition to a fragment corresponding to the name def
	private HashMap<String, ArrayList<Object>> nameDefToFrag = new HashMap<String, ArrayList<Object>> ();
		
	//Queue to store states
	private Queue<String> stateQueue = new LinkedList<String>();
	
	private ArrayList<Object> tempNamesArr;
	private HashMap<Integer, Object> tempTransArr;
	
	/**
	 * Create capability constructor, expects session ID as parameter. 
	 * 
	 * @param inSessID
	 */
	public CreateCapability(long inSessID)
	{
		tempSessID = inSessID;
	}
	
	/**
	 * Create capability constructor, expects session ID and a state as a parameter. 
	 * To be used when a capability has to be created for a specific state. 
	 * 
	 * @param inSessID
	 * @param inState
	 */
	public CreateCapability(long inSessID, String inState)
	{
		tempSessID = inSessID;
		currState = inState;
	}
	
	/**
	 *  Create capability constructor.
	 * 
	 * @param inSessID
	 * @param inClientID
	 * @param inState
	 * @param stateInTime
	 * @param prop
	 * @param inNamesMap
	 * @param inTransMap
	 * @param inFragMap
	 */
	public CreateCapability(String inVId, long inSessID, String inClientID, String inState, long stateInTime, int prop, HashMap<String, ArrayList<Object>> inNamesMap, HashMap<String, HashMap<Integer, Object>> inTransMap, HashMap<Long, HashMap<String, ArrayList<Object>>> inFragMap)
	{
		tempSessID = inSessID;
		clientID = inClientID;
		currState = inState;
		inTCreated = stateInTime;
		capProp = prop;
		nameDefsMap = inNamesMap;
		transArrMap = inTransMap;
		fragMap = inFragMap;
		vID = inVId;
	}
	
	/**
	 * This method is used to create capability permissions when the capability
	 * is supposed to contain no information about the permissions in the next 
	 * state.
	 * 
	 * @param inMap
	 */
	public void createPermsArrayNoDFA(HashMap<Long, ArrayList<Object>> inMap)
	{
		if(!fragMap.containsKey(tempSessID))
		{
			//Object fragObj = fragMap.get(tempSessID);
			ArrayList<Object> tempLis = inMap.get(tempSessID);
			
			//create a fragMap for all states in DFA
			//get state permissions
			sObj = (States) getDFA(tempLis).get("oStates");
			//get state transitions
			tObj = (StateTransitions) getDFA(tempLis).get("oTransitions");
			
			
			//get states - {permission} map
			checkstPerm = sObj.getStPermMap();
			checktransPerm = sObj.getTransPermMap();
			
			// get permissions - currentState - nextState MAP
			checkTrans = tObj.getMap();
			
			
			//add states to arrayList
			ArrayList<String> stateList = new ArrayList<String>();
			for(String s : checkstPerm.keySet())
			{
				stateList.add(s);
			}
			
			HashMap<String, ArrayList<Object>> statePermMap = new HashMap<String, ArrayList<Object>> ();
			
			//now foreach state in stateDiagram -- add st perms and trans perms for it in fragMap
			for(String State : stateList)
			{
				stArr.clear();
				transArr.clear();
				//add stationary permissions for the current state
				for(int s : checkstPerm.get(State))
				{
					stArr.add(s);
				}
				
				//add transitioning permission for current state
				if(capProp > 0)
				{
					if(nameDefsMap.isEmpty())
					{
						//foreach state in DFA
						for(String s : checktransPerm.keySet())
						{
							//if(s.equals("St1"))
							{
								tempNamesArr = new ArrayList<Object>();
								tempTransArr = new HashMap<Integer, Object>();
								createNameDefinitions(s, capProp);
							}
							//int val = putDFA(s, capProp);
						}
					}
					transArr = transArrMap.get(currState);
				}
				else
				{
					for(int s : checktransPerm.get(State))
					{
						transArr.put(s, null);
					}
				}
				
				HashMap<Integer, Object> transArrTemp = new HashMap<Integer, Object> ();
				transArrTemp = (HashMap<Integer, Object>) transArr.clone();
				ArrayList<Object> stArrTemp = new ArrayList<Object>();
				stArrTemp = (ArrayList<Object>) stArr.clone();
				
				ArrayList<Object> tempStateArrayList = new ArrayList<Object> ();
				tempStateArrayList.add(stArrTemp);
				tempStateArrayList.add(transArrTemp);
				
				statePermMap.put(State, tempStateArrayList);	
			}
			
			//add the fragments to the map
			fragMap.put(tempSessID, statePermMap);
		}
		else
		{
			//get st and trans permissions to frag map
			HashMap<String, ArrayList<Object>> getPermMap  = fragMap.get(tempSessID);
			
			ArrayList<Object>  permsList = getPermMap.get(currState);
			
			for(Object o: permsList)
			{
				if(o instanceof ArrayList)
				{
					stArr = (ArrayList<Object>) o;
				}
				else if(o instanceof HashMap)
				{
					transArr = (HashMap<Integer, Object>) o;
				}
			}
			
		}
	}
	
	/**
	 * This method is used to create capability permissions when the capability
	 * is supposed to contain the the full State machine within itself.
	 * 
	 * @param inMap
	 * @param inState
	 */
	public void createPermArrayFullDFA(HashMap<Long, ArrayList<Object>> inMap, String inState)
	{
		// this is the first state
		ArrayList<Object> tempLis = inMap.get(tempSessID);
		
		//get states
		sObj = (States) getDFA(tempLis).get("oStates");
		//get state transitions
		tObj = (StateTransitions) getDFA(tempLis).get("oTransitions");
					
		//get states - {permission} map
		checkstPerm = sObj.getStPermMap();
		checktransPerm = sObj.getTransPermMap();
		
		// get permissions - currentState - nextState MAP - gives Map<Int , HashMap<String, String>> permission(int) saying state(string) to state(string)
		checkTrans = tObj.getMap();
					
		stateQueue.add(inState);
		
		for(int s : checkstPerm.get(inState))
		{
			stArr.add(s);
		}
		
		for(int aPerm : checktransPerm.get(inState))
		{
			HashMap<String, String> currToPrev = checkTrans.get(aPerm);
			String newState = currToPrev.get(inState);
			
			stateQueue.add(newState);
			
			if(stateNameDefMap.containsKey(newState))
			{
				String getName = stateNameDefMap.get(newState);
				transArr.put(aPerm, getName);
			}
			else
			{
				//add mapping of state to namedef
				String name = "n" + nameCount;
				stateNameDefMap.put(newState, name);
				transArr.put(aPerm, name);
				nameCount++;
			}
		}
		
		String initNameDef = "n0";
		stateNameDefMap.put(inState, initNameDef);
		ArrayList<Object> initPerms = new ArrayList<Object>();
		initPerms.add(stArr);
		initPerms.add(transArr);
		nameDefToFrag.put(initNameDef, initPerms);
		
		while(!stateQueue.isEmpty())
		{
			String getState = stateQueue.poll();
			constructNameDefs(inMap, getState);
		}
	}
	
	/**
	 * This method is used to generate name definitions to be put inside the
	 * capability.
	 * 
	 * @param inMap
	 * @param inState
	 */
	public void constructNameDefs(HashMap<Long, ArrayList<Object>> inMap, String inState)
	{
		ArrayList tempLis = inMap.get(tempSessID);
		
		//get states
		States sObjTemp = (States) getDFA(tempLis).get("oStates");
		//get state transitions
		StateTransitions tObjTemp = (StateTransitions) getDFA(tempLis).get("oTransitions");
		
		//get states - {permission} map
		HashMap<String, ArrayList<Integer>> checkstPermTemp = sObjTemp.getStPermMap();
		HashMap<String, ArrayList<Integer>> checktransPermTemp = sObjTemp.getTransPermMap();
		
		// get permissions - currentState - nextState MAP
		HashMap<Integer, HashMap<String, String>> checkTransTemp = tObjTemp.getMap();
		
		
		ArrayList<Object> stArrTemp = new ArrayList<Object>();
		for(Integer s : checkstPermTemp.get(inState))
		{
			stArrTemp.add(s);
		}
		
		ArrayList<Object> defsLis = new ArrayList<Object> ();
		defsLis.add(stArrTemp);
		
		HashMap<Integer, Object> transArrTemp = new HashMap<Integer, Object>();
		
		for(Integer aPerm : checktransPerm.get(inState))
		{
			HashMap<String, String> currToPrev = checkTrans.get(aPerm);
			String newState = currToPrev.get(inState);
			
			String putName = null;
			
			if(stateNameDefMap.containsKey(newState))
			{
				putName = stateNameDefMap.get(newState);
			}
			else
			{
				putName = "n" + nameCount;
				stateNameDefMap.put(newState, putName);
				nameCount++;
			}
			
			
			if(!stateQueue.contains(newState))
			{
				if(!nameDefToFrag.containsKey(putName))
				{
					stateQueue.add(newState);
				}
			}
			
			
			transArrTemp.put(aPerm, putName);
			//inState = currToPrev.get(inState);
		}
		
		defsLis.add(transArrTemp);
		String getCurentName = stateNameDefMap.get(inState);
		nameDefToFrag.put(getCurentName, defsLis);
	}
	
	/**
	 * This method is used to create a capability which is to be returned when a state
	 * update request is presented to the authorization server. 
	 * 
	 * @param inMap
	 * @return
	 */
	public HashMap<String, Object> returnCap(HashMap<Long, ArrayList<Object>> inMap)   //reqType is used to check if an update certificate was sent, or a new capability is issued for the first time.
	{
			HashMap<String, Object> obj = null;
			
			obj = new HashMap<String, Object>();
			transArr = new HashMap<Integer, Object> ();
			stArr = new ArrayList<Object>();
			
			//namesArr = new ArrayList<Object>();

			if(capProp == 0)
			{
				//no fragment of DFA included in capability
				createPermsArrayNoDFA(inMap);	
				
				HashMap<String, ArrayList<Object>> tempStateMap = fragMap.get(tempSessID);
				ArrayList<Object> tempPermLis = tempStateMap.get(currState);
				
				String name = "n0";
				stateNameDefMap.put(currState, name);
				nameDefToFrag.put(name, tempPermLis);
				
				for(Object o : tempPermLis)
				{
					if(o instanceof ArrayList)
					{
						stArr = (ArrayList<Object>) o;
					}
					else if(o instanceof HashMap)
					{
						transArr = (HashMap<Integer, Object>) o;
					}
				}
				
			}
			else if(capProp == -1)
			{
				//we need the full DFA now
				createPermArrayFullDFA(inMap, currState);
			}
			else
			{
				//partial fragment of DFA needed
				
			}
			
			obj.put("vID", vID);
			obj.put("sessID", tempSessID);
			
			String getNameDef = stateNameDefMap.get(currState);
			obj.put("name", getNameDef);
			//obj.put("stPerm", stArr);
			//obj.put("transPerm", transArr);
			HashMap<String, Object> namesObj = new HashMap<String, Object>(nameDefToFrag);
			obj.put("nameDefs", namesObj);
		    obj.put("tCreated", inTCreated);
		    
		    //obj.put("tRenewed", System.currentTimeMillis());
		    obj.put("certType", new Boolean(true));
		    HashingAlgo algo = new HashingAlgo();
		    String toHashString = getString(obj);
		    //System.out.println(toHashString.concat(sharedKey));
		    String sharedKeyforRS = HCAPAuthorizationServer.sharedSecretsMap.get(vID);
		    System.out.println(sharedKeyforRS);
		    System.out.println(clientID);
		    obj.put("hash", algo.SHA256hash(toHashString.concat(sharedKeyforRS).concat(clientID)));
		    return obj;
	}
	
	public String getString(HashMap<String, Object> inObj)
	{
		
		StringBuilder retStr = new StringBuilder();
		
		retStr.append("sessID");
		retStr.append(inObj.get("sessID").toString());
		//retStr.append("stPerm");
		//retStr.append(inObj.get("stPerm").toString());
		//retStr.append("transPerm");
		//retStr.append(inObj.get("transPerm").toString());
		retStr.append("name");
		retStr.append(inObj.get("name").toString());
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
	
	public String getStringUpd(HashMap<String, Object> inObj)
	{
		StringBuilder retStr = new StringBuilder();
		
		retStr.append("sessID");
		retStr.append(inObj.get("sessID").toString());
		retStr.append("exceptions");
		retStr.append(inObj.get("exceptions").toString());
		retStr.append("certType");
		retStr.append(inObj.get("certType").toString());
		
		return retStr.toString(); 
	}
	
	/**
	 * This method is used to get stationary permissions associated with a specific
	 * state.
	 * 
	 * @param inState
	 * @return stationary permissions as a list.
	 */
	private ArrayList<Object> getStationaryPermissions(String inState)
	{
		ArrayList<Object> tempArr = new ArrayList<Object>();
		if(checkstPerm.containsKey(inState))
		{
			ArrayList<Integer> getStPerm = checkstPerm.get(inState);
			for(Integer str : getStPerm)
			{
				tempArr.add(str);
			}
		}
		return tempArr;
	}
	
	/**
	 * This method is used to get transitioning permissions with their respective name definitions
	 * for a specific state.
	 * 
	 * @param inState
	 * @param prop
	 * @return map mapping a transitioning permissions to name definitions.
	 */
	private HashMap<Integer, Object> getTransitioningPermissions(String inState, int prop)
	{
		
		ArrayList<Integer> getTransperm = checktransPerm.get(inState);
		HashMap<Integer, Object> permNameMap = new HashMap<Integer, Object> ();
		for(Integer aPerm : getTransperm)
		{
			if(prop > 0)
			{
				permNameMap.put(aPerm, "n" + (nameCount-1));
			}
			else
			{
				permNameMap.put(aPerm, null);
			}
			
		}
		return permNameMap;
	}
	
	/**
	 * This method 
	 * 
	 * @param inState
	 * @return
	 */
	private ArrayList<Integer> getPreviousStateTransPerm(String inState)
	{
		ArrayList<Integer> retLis = new ArrayList<Integer> ();
		HashMap<String, ArrayList<Integer>> tempMap = sObj.getTransPermMap();
		retLis = tempMap.get(inState);
		return retLis;
	}
	
	
	private void createNameDefinitions(String inState, int prop)
	{
		ArrayList<Integer> transPermLis = new ArrayList<Integer>();
		if(prop > 0)
		{
			transPermLis = getPreviousStateTransPerm(inState);
		}
		
		for(Integer aPerm :  transPermLis)
		{
			{
				HashMap<String, String> getStates = checkTrans.get(aPerm);
				String newState = getStates.get(inState);
				
				createNameDefinitions(newState,prop-1);
				
				//now get the stPerm 
				ArrayList<Integer> getStPerm = checkstPerm.get(newState);
				ArrayList<Object> newStPerm = getStationaryPermissions(newState);
				
				//transPerm for this new state
				HashMap<Integer, Object> newTransPerm = getTransitioningPermissions(newState, prop-1);
				ArrayList<Object> permPair = new ArrayList<Object> ();
				permPair.add(newStPerm);
				permPair.add(newTransPerm);
				String nameCountVal = "n" + nameCount;
				
				HashMap<String, Object> tempObj = new HashMap<String, Object>();
				tempObj.put(nameCountVal, permPair);
				
				tempNamesArr.add(tempObj);
				nameCount++;
			}
			if(prop == capProp)
			{
				nameDefsMap.put(inState, tempNamesArr);
				tempTransArr.put(aPerm, "n" + (nameCount-1));
				transArrMap.put(inState, tempTransArr);
			}
		}
	}
	
	
	private HashMap<String, Object> getDFA(ArrayList inLis)
	{
		HashMap<String, Object> retMap = new HashMap<String, Object> ();
		
		for(Object obj : inLis)
		{
			if(obj instanceof States)
			{
				retMap.put("oStates", (States) obj);
				
			}
			else if(obj instanceof StateTransitions)
			{
				retMap.put("oTransitions", (StateTransitions) obj);
			}
		}
		return retMap;
	}
}
