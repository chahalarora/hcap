package StateMachines;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.util.Base64;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

import SecureAuthServer.SecureAuthServer.SecurityAutomaton;
import SecureAuthServer.SecureAuthServer.StateTransitions;
import SecureAuthServer.SecureAuthServer.States;

public class InitStateMachine implements SecurityAutomaton
{	
	private String initialState = "St1";
	//private int fragmentLength;
	private States sObj = new States();
	private StateTransitions tObj = new StateTransitions();
	
	public InitStateMachine()
	{
		//initialState = inIntialState;
	}
	
	public void initStates()
	{
		ArrayList<Integer> slis1 = new ArrayList<Integer>();
		ArrayList<Integer> tlis1 = new ArrayList<Integer>();
		
		//add state1
		slis1.add(1);
		slis1.add(2);
		tlis1.add(3);
		
		//test
		tlis1.add(12);
		sObj.addStPerm("St1", slis1);
		sObj.addTransPerm("St1", tlis1);
		
		//add state2
		ArrayList<Integer> slis2 = new ArrayList<Integer>();
		ArrayList<Integer> tlis2 = new ArrayList<Integer>();
		slis2.add(4);
		slis2.add(5);
		tlis2.add(6);
		sObj.addStPerm("St2", slis2);
		sObj.addTransPerm("St2", tlis2);
		
		//add state3
		ArrayList<Integer> slis3 = new ArrayList<Integer>();
		ArrayList<Integer> tlis3 = new ArrayList<Integer>();
		slis3.add(7);
		tlis3.add(8);
		sObj.addStPerm("St3", slis3);
		sObj.addTransPerm("St3", tlis3);		
		
		//add state4
		ArrayList<Integer> slis4 = new ArrayList<Integer>();
		ArrayList<Integer> tlis4 = new ArrayList<Integer>();
		slis4.clear();
		tlis4.clear();
		slis4.add(9);
		tlis4.add(10);
		sObj.addStPerm("St4", slis4);
		sObj.addTransPerm("St4", tlis4);		
		
		//add state5
		ArrayList<Integer> slis5 = new ArrayList<Integer>();
		ArrayList<Integer> tlis5 = new ArrayList<Integer>();
		slis5.clear();
		tlis5.clear();
		slis5.add(11);
		sObj.addStPerm("St5", slis5);
		sObj.addTransPerm("St5", tlis5);
		
		//add state6
		ArrayList<Integer> slistt = new ArrayList<Integer>();
		ArrayList<Integer> tlistt = new ArrayList<Integer>();
		slistt.clear();
		tlistt.clear();
		slistt.add(13);
		tlistt.add(14);
		sObj.addStPerm("St6", slistt);
		sObj.addTransPerm("St6", tlistt);
		
		//add state7
		ArrayList<Integer> slistt1 = new ArrayList<Integer>();
		ArrayList<Integer> tlistt1 = new ArrayList<Integer>();
		slistt1.clear();
		tlistt1.clear();
		slistt1.add(15);
		sObj.addStPerm("St7", slistt1);
		sObj.addTransPerm("St7", tlistt1);	
		
		
		//stateMap.put(sessionID, sObj);
		//printStates(sObj);
	}
	
	public void initTransitions()
	{
		/*
		 * Shauvik Notes: Read from file to setup the conditions.
		 * Then put them in the appropriate transitions.
		 * Needs Editing. This will be done by the SIC--------------------------------- Shauvik-incomplete
		 * */
		List<String> conditions=new ArrayList<String>();;
		try {
		File file = new File("/D:/uCalgary/HCAPv1/HCAPCode/PropertiesFiles/ConditionList.txt"); 
		  
		  BufferedReader buffReader = new BufferedReader(new FileReader(file)); 
		  
		  String line = buffReader.readLine();
		  while ((line = buffReader.readLine()) != null) { 
		    conditions.add(line);
		    }
		}catch(Exception E) {
			System.out.println("Issue in the conditions reading file-------- \n"+E);
		}
		for(int i = 0 ; i< conditions.size();i++) {
			System.out.println("Conditions---- for "+i);
			System.out.println(conditions.get(i));
		}
		
		//trans1
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("St1", "St2");
		tObj.addStTransitions(3, map1);
		tObj.addConditionsToTransitions(3, conditions.get(0));
		
		//trans2
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("St2", "St3");
		tObj.addStTransitions(6, map2);
		tObj.addConditionsToTransitions(6, conditions.get(1));
		
		//trans3
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("St3", "St4");
		tObj.addStTransitions(8, map3);
		tObj.addConditionsToTransitions(8, conditions.get(2));
		
		//trans4
		HashMap<String, String> map4 = new HashMap<String, String>();
		map4.put("St4", "St5");
		tObj.addStTransitions(10, map4);
		tObj.addConditionsToTransitions(10, conditions.get(2));
		
		//trans - test
		HashMap<String, String> maptest = new HashMap<String, String>();
		maptest.put("St1", "St6");
		tObj.addStTransitions(12, maptest);
		tObj.addConditionsToTransitions(12, conditions.get(0));
		
		//trans - test2
		HashMap<String, String> maptest1 = new HashMap<String, String>();
		maptest1.put("St6", "St7");
		tObj.addStTransitions(14, maptest1);
		
	}
	
	public States getStatesObject()
	{
		return sObj;
	}
	
	public StateTransitions getStateTransObject()
	{
		return tObj;
	}
			
	
	public void printTransitions(StateTransitions st)
	{
		for(Map.Entry<Integer, HashMap<String, String>> entry : st.getMap().entrySet())
		{
			System.out.print(entry.getKey() + "\t");
			for(Map.Entry<String, String> entry1 : entry.getValue().entrySet())
			{
				System.out.print(entry1.getKey() + "\t" + entry1.getValue());
			}
			System.out.print("\n");
		}
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

	public String getInitialState() 
	{
		return initialState;
	}

	/*
	public int getFragmentLength() 
	{
		return fragmentLength;
	}
	*/
}
