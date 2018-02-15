package SecureAuthServer.SecureAuthServer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class manages the user's session for the users part of the protocol.
 * 
 * @author lakshya.tandon
 *
 */
public class ManageUsers 
{
	private String userID; 
	private long sessionID = 0;
	private String currstate;
	private long entryTime;
	private boolean baton = false;
	
	private String stateBeforeGC;
	
	//maps states to nameDefs for a session -- This is done for the first time permission is requested, so that every time the overhead of doing this is reduced.
	private HashMap<String, ArrayList<Object>> nameDefsMap;
	
	//maps states to transitioning permissions in the map -- This is done for the first time permission is requested, so that every time the overhead of doing this is reduced.
	private HashMap<String, HashMap<Integer, Object>> transArrMap;
	
	/**
	 * HCAP Manage Users constructor, expects userID and session ID as parameter.
	 * 
	 * @param inSessionID
	 * @param inUserID
	 */
	public ManageUsers(long inSessionID, String inUserID)
	{
		nameDefsMap = new HashMap<String, ArrayList<Object>> ();
		transArrMap = new HashMap<String, HashMap<Integer, Object>> ();
		
		if(inSessionID ==  0)
		{
			sessionID = 1;
		}
		else
		{
			sessionID = inSessionID + 1;
		}
		userID = inUserID;
	}
	
	/**
	 * This method returns transitioning permissions map for a user in a particular session. 
	 * 
	 * @return
	 */
	public HashMap<String, HashMap<Integer, Object>> getTransArrMap()
	{
		return transArrMap;
	}
	
	/**
	 * This method is used for adding transitioning permission for a user in a particular state.
	 * 
	 * @param inState
	 * @param inTransArr
	 */
	public void setTransArr(String inState, HashMap<Integer, Object> inTransArr)
	{
		transArrMap.put(inState, inTransArr);
	}
	
	/**
	 * This method is used to get the names definitions map.
	 * 
	 * @return
	 */
	public HashMap<String, ArrayList<Object>> getNamesMap()
	{
		return nameDefsMap;
	}
	
	/**
	 * This method is used to set the name definitions for a user in a particular state.
	 * 
	 * @param inState
	 * @param inNameDefs
	 */
	public void setNameDefs(String inState, ArrayList<Object> inNameDefs)
	{
		nameDefsMap.put(inState, inNameDefs);
	}
	
	/**
	 * This method is used to update the current state of the user. 
	 * 
	 * @param inState
	 */
	public void setState(String inState)
	{
		currstate = inState;
		entryTime = System.currentTimeMillis();
	}
	
	/**
	 * This method is used to set the entry time into the state for a user.
	 * 
	 * @param inTime
	 */
	public void setEntryTime(long inTime)
	{
		entryTime = inTime;
	}
	
	/**
	 * This method gets the user ID.
	 * 
	 * @return
	 */
	public String getUserID()
	{
		return userID; 
	}
	
	/**
	 * This method gets the session ID.
	 * 
	 * @return
	 */
	
	public long getSessionID()
	{
		return sessionID;
	}
	
	/**
	 * This method gets the current state of the user.
	 * 
	 * @return
	 */
	public String getState()
	{
		return currstate;
	}
	
	/**
	 * This method gets time when the user entered into its current state.
	 * 
	 * @return
	 */
	public long getEntryTime()
	{
		return entryTime;
	}
	
	/**
	 * This method is used to set the value of baton to true or false.
	 * 
	 * @param inValue
	 */
	public void setBaton(boolean inValue)
	{
		baton = inValue;
	}
	
	/**
	 * This method is used to get the value of baton.
	 * 
	 * @return
	 */
	public boolean getBaton()
	{
		return baton;
	}
	
	public void setStateBeforeGC(String inState)
	{
		stateBeforeGC = inState;
	}
	
	public String getStateBeforeGC()
	{
		return stateBeforeGC;
	}
	
}
