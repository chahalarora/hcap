package SecureAuthServer.SecureAuthServer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used ton handle different type of requests which arrive at the authorization server, 
 * the request could be to issue a capability or update the state.
 * 
 * @author lakshya.tandon
 *
 */
public class HCAPRequestHandler 
{
	private String clientID;
	private HashMap<String, Object> ticket = new HashMap<String, Object>();
	//maintains the number of sessions
	private long sessID;
	
	//capability propagation value
	//capability propagation is always (number of states included in the capability) - 1
	//If we want to put the complete DFA in the capability, we specify propagation as (-1)
	private int propagation;
	
	/**
	 * This is the constructor for handle request.
	 * 
	 * @param inTicket
	 * @param inClientID
	 * @param capPropagation
	 */
	public HCAPRequestHandler(HashMap<String, Object> inTicket, String inClientID, int capPropagation)
	{
		ticket = inTicket;
		clientID = inClientID;
		propagation = capPropagation;
	}
	
	/**
	 * This method is used to handle the state update request received from the client 
	 * update the state for the client's state machine by performing transition(s).
	 * 
	 * @param ticket
	 * @return
	 */
	public HashMap<String, Object> handleUpdateRequest(HashMap<String, Object> ticket)
	{
		long sessID = Long.parseLong(ticket.get("sessID").toString());
		
		if(isSessionLocked(sessID))
		{
			HashMap<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("messageCode", 3);
			responseMap.put("messageText", "isPaused");
			return responseMap;
		}
		
		String vID = ticket.get("vID").toString();
		String currentState = getCurrrentStatefromSessID(sessID);
    	Long stateEntryTime = getStateEntryTime(sessID);
    	
		HashMap<Integer, ArrayList<String>> exceptionMap = new HashMap<Integer, ArrayList<String>> ();
		exceptionMap = (HashMap<Integer, ArrayList<String>>) ticket.get("exceptions");
		
		for(int i = 0; i <= propagation; i++)
		{
			ArrayList lis = HCAPAuthorizationServer.sessionMap.get(sessID);
			
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
			
			HashMap<String, ArrayList<Integer>> tempMap = sObj.getTransPermMap();
			ArrayList<Integer> tPermLis = tempMap.get(currentState);
			
			for(int j = 1; j <= exceptionMap.size(); j++)
			{
				ArrayList<String> ex = exceptionMap.get(Integer.toString(j));
				Object exPerm = ex.get(0);
				Object tempExTime = ex.get(1);
				long exTime = Long.parseLong(tempExTime.toString());
				if(exTime > stateEntryTime)
				{
					if(tObj.getMap().containsKey(exPerm))
					{
						HashMap<String, String> transitionFromPerm = tObj.getMap().get(exPerm);
						if(transitionFromPerm.containsKey(currentState))
						{
							String nextState = transitionFromPerm.get(currentState);
							currentState = nextState;
						}
					}
				}	
			}	
		}
		stateEntryTime  = System.currentTimeMillis();
		
		putCurrentStatefromSessID(sessID, currentState);
		putStateEntryTime(sessID, stateEntryTime);
		
		ManageUsers mgUsr = HCAPAuthorizationServer.userMap.get(sessID);
		
		
		HashMap<String, ArrayList<Object>> getNamesMap = mgUsr.getNamesMap();
		HashMap<String, HashMap<Integer, Object>> getTransMap= mgUsr.getTransArrMap();
		
		CreateCapability crCap = new CreateCapability(vID, sessID, clientID, currentState, stateEntryTime, propagation, getNamesMap, getTransMap, HCAPAuthorizationServer.fragamentMap);
		return crCap.returnCap(HCAPAuthorizationServer.sessionMap);
	}
	
	/**
	 * This method is used to get the current state of a client in the specified session.
	 * 
	 * @param inSess
	 * @return
	 */
	private String getCurrrentStatefromSessID(long inSess)
    {
    	ManageUsers usrObj = HCAPAuthorizationServer.userMap.get(inSess);
    	return usrObj.getState();
    }
	
	/**
	 * This method is used to get the time at which the client in a session entered its
	 * current state.
	 * 
	 * @param inSess
	 * @return
	 */
	private long getStateEntryTime(long inSess)
    {
    	ManageUsers usrObj = HCAPAuthorizationServer.userMap.get(inSess);
    	return usrObj.getEntryTime();
    }
	
	/**
	 * This method is used to set the current state for a client in a specific session.
	 * 
	 * @param inSess
	 * @param inCurrState
	 */
	private void putCurrentStatefromSessID(long inSess, String inCurrState)
    {
    	ManageUsers usrObj = HCAPAuthorizationServer.userMap.get(inSess);
    	usrObj.setState(inCurrState);
    }
    
	/**
	 * This method is used to set the state entry for a client in a specific session.
	 * 
	 * @param inSess
	 * @param inTime
	 */
    private void putStateEntryTime(long inSess, long inTime)
    {
    	ManageUsers usrObj = HCAPAuthorizationServer.userMap.get(inSess);
    	usrObj.setEntryTime(inTime);
    }
	
    /**
     * This method is used to generate a capability for a client in a specific session.
     * 
     * @param inSessionID
     * @return
     */
	public HashMap<String, Object> generateCapability(long inSessionID)
	{
		if(isSessionLocked(inSessionID))
		{
			HashMap<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("messageCode", 3);
			responseMap.put("messageText", "isPaused");
			return responseMap;
		}
		
		String currState;
		long stateInTime;
		ManageUsers usrObj = null;
		
		if(!HCAPAuthorizationServer.userMap.containsKey(inSessionID))
		{
			return null;
		}
		
		usrObj = HCAPAuthorizationServer.userMap.get(inSessionID);
		sessID = inSessionID;
		currState = usrObj.getState();
		stateInTime = usrObj.getEntryTime();
		
		boolean batonValue = usrObj.getBaton();
		
		//generate nameDefsMap for the first time capability is issued
		HashMap<String, ArrayList<Object>> getNamesMap = usrObj.getNamesMap();
		HashMap<String, HashMap<Integer, Object>> getTransMap= usrObj.getTransArrMap();
		
		//add capability propagation from current state. for e.g., propagation = 3 means, from the current state the capability would be able to access next 3 states.
		String vID;
		if(batonValue)
		{
			//then issue the capability where vID = (resource server which has the baton)
			String stateBeforeGC = usrObj.getStateBeforeGC(); 
			vID = HCAPAuthorizationServer.stateRSMap.get(stateBeforeGC);
		}
		else
		{
			//issue the capability where vID = (resource server which corresponds to the current state of client)
			vID = HCAPAuthorizationServer.stateRSMap.get(currState);
		}
		//String vID = HCAPAuthorizationServer.stateRSMap.get(currState);
		CreateCapability crCap = new CreateCapability(vID, sessID, clientID, currState, stateInTime, propagation, getNamesMap, getTransMap, HCAPAuthorizationServer.fragamentMap);
		HashMap<String, Object> returnedCapability = crCap.returnCap(HCAPAuthorizationServer.sessionMap);
		return returnedCapability;
	}
	
	/**
	 * This method is used to check if a session is locked. A session gets locked when its exceptionlist
	 * and baton is being updated by any of the resource servers during garbage collection
	 * 
	 * @param inSessionID
	 * @return
	 */
	private boolean isSessionLocked(long inSessionID)
	{
		if(HCAPAuthorizationServer.updateLockMap.containsKey(inSessionID))
		{
			if(HCAPAuthorizationServer.updateLockMap.get(inSessionID))
			{
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * This method is used to generate the first capability for a client in a specific session. 
	 * 
	 * @param inAutomaton
	 * @param initialState
	 * @return
	 */
	public HashMap<String, Object> generateFirstCapability(SecurityAutomaton inAutomaton, String initialState)
	{
		String currState;
		long stateInTime;
		ManageUsers newUser = new ManageUsers(HCAPAuthorizationServer.countingSessID, clientID);
		HCAPAuthorizationServer.countingSessID = newUser.getSessionID();
		sessID = HCAPAuthorizationServer.countingSessID;
		newUser.setState(initialState);
		newUser.setEntryTime(System.currentTimeMillis());
		stateInTime = newUser.getEntryTime();
		currState = newUser.getState();
		HCAPAuthorizationServer.userMap.put(sessID, newUser);

		//init should be the state machine for a client requesting the capability.
		SecurityAutomaton init = inAutomaton;
		
		ArrayList<Object> tempLis = new ArrayList<Object>();
		
		init.initStates();
		init.initTransitions();
		tempLis.add(init.getStatesObject());
		tempLis.add(init.getStateTransObject());
		
		//-----------------
		System.out.println("HCAPRequestHandler.generateFirstCapability:::");
		java.util.Map<Integer,java.util.Set> conditions=init.getStateTransObject().getConditions();
		for(Integer k:conditions.keySet()) {
			System.out.println(k+":::"+conditions.get(k));
		}
		//-----------------
		
		HCAPAuthorizationServer.sessionMap.put(sessID, tempLis);
		
		//generate nameDefsMap for the first time capability is issued
		HashMap<String, ArrayList<Object>> getNamesMap = newUser.getNamesMap();
		HashMap<String, HashMap<Integer, Object>> getTransMap= newUser.getTransArrMap();
		
		
		//add capability propagation from current state. for e.g., propagation = 3 means, from the current state the capability would be able to access next 3 states.
		String vID = HCAPAuthorizationServer.stateRSMap.get(currState);
		CreateCapability crCap = new CreateCapability(vID, sessID, clientID, currState, stateInTime, propagation, getNamesMap, getTransMap, HCAPAuthorizationServer.fragamentMap);
		//response = crCap.returnCap(sessionMap).toJSONString();
		HashMap<String, Object> returnedCapability = crCap.returnCap(HCAPAuthorizationServer.sessionMap);
		return returnedCapability;
	}
	
}
