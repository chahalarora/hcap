package ExceptionList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ExceptionList implements Serializable 
{
	
	//This maps permission to the time when exception occurred.
	//HashMap<String, Long> exMap  = new HashMap<String, Long>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long perm;
	long exTime;
	ExceptionList oldEx = null;
	private String name, initialState;
	private HashMap<Integer, Object> translis;
	private HashMap<String , String> permNameMatching = new HashMap<String, String>(); 

	public ExceptionList(String inName, long inPerm, long inExTime, ExceptionList inEx)
	{
		
		name = inName;
		perm = inPerm;
		exTime = inExTime;
		oldEx = inEx;
		printExceptionList();
		
	}
	
	public ExceptionList(long inPerm, long inExTime, ExceptionList inEx, HashMap<Integer , Object> transitionaryPermissionsList, HashMap<String,Object> defs)
	{
		translis = transitionaryPermissionsList;
		perm = inPerm; //the actual permission
		exTime = inExTime; //the permission execution time
		oldEx = inEx; //the old permission
		printExceptionList();
		printTransitionaryPermissions();
		printDefs(defs); //stores the defs transitions in permNameMatching
	}
	
	public ExceptionList(long inPerm, long inExTime, ExceptionList inEx, String initialState)
	{
		perm = inPerm; //the actual permission
		exTime = inExTime; //the permission execution time
		oldEx = inEx; //the old permission
		this.initialState = initialState;
	}
	
	public void setEx(ExceptionList inEx)
	{
		oldEx = inEx;
	}
	
	public long getExTime()
	{
		return exTime;
	}
	
	public ExceptionList getEx()
	{
		return oldEx;
	}
	/*
	public long getExceptionTime()
	{
		return exceptionTime;
		//return ExTime;
	}
	*/
	/*
	public ExceptionList getExceptionList()
	{
		return exceptionList;
		//return ex;
	}
	*/
	
	public long getPerm()
	{
		return perm;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getInitialState()
	{
		return initialState;
	}
	/*
	 * This will print the exception list in a concise manner.
	 * The first iteration will contain the names of the states, but will eventually be phased out.
	 * 5th April 2018.
	 * */
	public void printExceptionList() {
		ExceptionList temp=oldEx;
		if(temp!=null) {
			System.out.println("Name: "+name+" exTime: "+exTime);
			System.out.println("Name: "+temp.getName()+" exTime: "+temp.getExTime());
			oldEx.printExceptionList();
		}
		//System.out.println("Print Complete: Exception list");
	}
	
	public void printTransitionaryPermissions(){
		//System.out.println("Transitionary Permissions: ");
		for (Object key : translis.keySet()) {
		    //System.out.println(key + " " + translis.get(key));
		}
		//System.out.println(translis.toString());
	}
	
	public void printDefs(HashMap <String, Object> defs) {
		System.out.println("Defs: ");
		for (Object key : defs.keySet()) { //key of defs = n0,n1,n2 etc
			System.out.print("Defs for state: "+defs.get(key) + "-->");
			ArrayList<Object> firstValue=(ArrayList<Object>) defs.get(key);
			HashMap <String, Object> oneDef = (HashMap <String, Object>)firstValue.get(1);
			//System.out.print("Arraylist 0: "+firstValue.get(0) + "-->");
			for(String keyDef : oneDef.keySet()) {
				//System.out.print("permission: " + keyDef + ", state: " + oneDef.get(keyDef)+ "--");
				permNameMatching.put(keyDef, oneDef.get(keyDef)+"");
				
			}
			//System.out.println();
		    //System.out.println(key + " " + defs.get(key));
		}
		
		for(String nameMatchKey: permNameMatching.keySet()) {
			System.out.print("permission: " + nameMatchKey + ", state: " + permNameMatching.get(nameMatchKey)+ "--");
		}
	}
	
	
}
