package SecureAuthServer.SecureAuthServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

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
	private Map<Integer , Set> conditionsMap = new HashMap<Integer , Set>();
	
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
	
	/*
	 * This method is used to add the conditions as "Strings" to the security automaton.
	 * The set of conditions need to be a set.
	 * 
	 * */
	public void addConditionsToTransitions(Integer state, String condition) {
		Set<String> previousSet;
		if(conditionsMap.containsKey(state)) {
			previousSet = conditionsMap.get(state);
			previousSet.add(condition);
			conditionsMap.put(state,previousSet);
		}
		else {
			previousSet = new HashSet<String>();
			previousSet.add(condition);
			conditionsMap.put(state, previousSet);
		}
		//return true;
		
	}
	
	public Map<Integer , Set> getConditions(){
		return conditionsMap;
	}
	
	public Set<String> getStateConditions(Long permission){
		return conditionsMap.get(permission);
	}
}
