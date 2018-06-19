package SecureAdmin.SecureAdmin;

import java.util.HashMap;

/**
 *  This class is used as a data structure to store the transitions originating from a particular state.
 * 
 * @author lakshya.tandon
 *
 */
public class StateTransitions 
{ 
	//private long sessionID; 
	private HashMap<Integer, HashMap<String,String>> transitionsMap = new HashMap<Integer, HashMap<String,String>>();	

	/**
	 * This method is used to add state transitions for a particular state.
	 * 
	 * @param currState
	 * @param aMap
	 */
	public void addStTransitions(Integer currState, HashMap<String, String> aMap)
	{
		transitionsMap.put(currState, aMap);
	}
	
	/**
	 * This method gets the state transitions map for states.
	 * 
	 * @return
	 */
	public HashMap<Integer, HashMap<String,String>> getMap()
	{
		return transitionsMap;
	}
	
	/**
	 * This method is used to get the current and the next state corresponding to a transition using a permission.
	 * 
	 * @param inPerm
	 * @return
	 */
	public HashMap<String, String> getStTransitions(Long inPerm)
	{
		HashMap<String, String> retMap = transitionsMap.get(inPerm);
		return retMap;
	}
}
