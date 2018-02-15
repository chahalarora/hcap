package StateMachines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import SecureAuthServer.SecureAuthServer.SecurityAutomaton;
import SecureAuthServer.SecureAuthServer.StateTransitions;
import SecureAuthServer.SecureAuthServer.States;

//import AuthServer.AuthServer.StateTransitions;
//import AuthServer.AuthServer.States;


public class InitStateMachineEP11 implements SecurityAutomaton
{
	//private long sessionID;
	private String initialState = "St1";
	
	private States sObj = new States();
	private StateTransitions tObj = new StateTransitions();
	//HashMap<Long, States> stateMap = new HashMap<Long, States>();
	
	public InitStateMachineEP11()
	{
		//sessionID = inSessID; 
	}
	
	
	public void initStates()
	{
		for(int i = 1; i <= 2; i++)
		{
			ArrayList<Integer> slis1 = new ArrayList<Integer>();
			ArrayList<Integer> tlis1 = new ArrayList<Integer>();
			
			//add stPerm
			slis1.add(0);
			
			
			//add transPerm
			tlis1.add(1);
			sObj.addStPerm("St" + i, slis1);
			sObj.addTransPerm("St" + i, tlis1);	
		}
		
		//printStates(sObj);
	}
	
	
	public void initTransitions()
	{
		HashMap<String, String> map1 = new HashMap<String, String>();
		for(int i = 1; i <= 2; i++)
		{
			if(i == 1)
			{
				map1.put("St" + i, "St" + (i+1));
			}
			if(i == 2)
			{
				map1.put("St" + i, "St" + (i-1));
			}
			tObj.addStTransitions(1, map1);
		}
		//printTransitions(tObj);

	}
	
	public States getStatesObject()
	{
		return sObj;
	}
	
	public StateTransitions getStateTransObject()
	{
		return tObj;
	}
	
	
	public void printStates(States s)
	{
		HashMap<String, ArrayList<Integer>> sTempMapHashMap = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<Integer>> tTempMapHashMap = new HashMap<String, ArrayList<Integer>>();
		
		sTempMapHashMap = s.getStPermMap();
		tTempMapHashMap = s.getTransPermMap();
		
		for(Map.Entry<String, ArrayList<Integer>> entry : sTempMapHashMap.entrySet())
		{
			System.out.print(entry.getKey() + "\t");
			for(Integer str : entry.getValue())
			{
				System.out.print(str + ", ");
			}
			System.out.println("\n");
		}
		
		for(Map.Entry<String, ArrayList<Integer>> entry : tTempMapHashMap.entrySet())
		{
			System.out.print(entry.getKey() + "\t");
			for(Integer str : entry.getValue())
			{
				System.out.print(str + ", ");
			}
			System.out.println("\n");
		}
	}
	
	public void printTransitions(StateTransitions st)
	{
		System.out.println(tObj.getMap());
	}


	public String getInitialState() 
	{
		return initialState;
	}
}
