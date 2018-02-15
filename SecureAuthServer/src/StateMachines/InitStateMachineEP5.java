package StateMachines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import SecureAuthServer.SecureAuthServer.SecurityAutomaton;
import SecureAuthServer.SecureAuthServer.StateTransitions;
import SecureAuthServer.SecureAuthServer.States;

public class InitStateMachineEP5 implements SecurityAutomaton
{

	private String initialState = "St1";
	
	private States sObj = new States();
	private StateTransitions tObj = new StateTransitions();
	//HashMap<Long, States> stateMap = new HashMap<Long, States>();
	
	private int nValue = 2;
	
	public InitStateMachineEP5()
	{
		//sessionID = inSessID; 
		//nValue = inVal;
	}
	
	public void initStates()
	{
		for(int i = 1; i <= nValue; i++)
		{
			ArrayList<Integer> slis1 = new ArrayList<Integer>();
			ArrayList<Integer> tlis1 = new ArrayList<Integer>();
			
			//add stPerm
			slis1.add(i);
			
			for(int j = 1; j <= nValue; j++)
			{
				if(j != i)
				{
					tlis1.add(j);
				}
			}
			sObj.addStPerm("St" + i, slis1);
			sObj.addTransPerm("St" + i, tlis1);
		}
		//printStates(sObj);
	}
	
	public void initTransitions()
	{
		for(int i = 1; i <= nValue; i++)
		{
			HashMap<String, String> map1 = new HashMap<String, String> ();
			
			for(int j = 1; j <= nValue; j++)
			{
				String currentState = "St" + j;
				if(i != j)
				{
					String newState = "St" + i;
					map1.put(currentState, newState);
				}
			}
			tObj.addStTransitions(i, map1);
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
		
		System.out.println("Stationary Permissions.");
		for(Map.Entry<String, ArrayList<Integer>> entry : sTempMapHashMap.entrySet())
		{
			System.out.print(entry.getKey() + "\t");
			for(Integer str : entry.getValue())
			{
				System.out.print(str + ", ");
			}
			System.out.println("\n");
		}
		
		System.out.println("Transitioning Permissions.");
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
