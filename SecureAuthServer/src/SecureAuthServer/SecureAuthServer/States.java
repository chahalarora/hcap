package SecureAuthServer.SecureAuthServer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used as a data structure to store the stationary and transitioning permission for a state.
 * 
 * @author lakshya.tandon
 *
 */

public class States 
{
	//each state mapped to set of stationary permissions.
	private HashMap<String, ArrayList<Integer>> stPerm = new HashMap<String, ArrayList<Integer>> ();
	//each state mapped to set of transitioning permissions.
	private HashMap<String, ArrayList<Integer>> transPerm = new HashMap<String, ArrayList<Integer>> ();
	

	/**
	 * This method is used to add stationary permissions for a particular state.
	 * 
	 * @param state
	 * @param perms
	 */
	public void addStPerm(String state, ArrayList<Integer> perms)
	{
		stPerm.put(state, perms);
	}
	
	/**
	 * This method is used to add transitioning permissions for a particular state.
	 * 
	 * @param state
	 * @param perms
	 */
	public void addTransPerm(String state, ArrayList<Integer> perms)
	{
		transPerm.put(state, perms);
	}
	
	/**
	 * This is used to get the map which contains stationary permissions associated with a state.
	 * 
	 * @return
	 */
	public HashMap<String, ArrayList<Integer>> getStPermMap()
	{
		return stPerm;
	}
	
	/**
	 * This is used to get the map which contains transitioning permissions associated with a state.
	 * 
	 * @return
	 */
	public HashMap<String, ArrayList<Integer>> getTransPermMap()
	{
		return transPerm;
	}
}
