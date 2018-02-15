package ExperimentResources;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import ExceptionList.ExceptionList;
import SecureResServer.SecureResServer.HCAPResourceServer;
import SecureResServer.SecureResServer.batonCompression;

public class buildExceptionList 
{
	private HashMap<Long, ExceptionList> exMap;
	
	//change this for different tests.
	//long listEntries = 10000;
	
	public buildExceptionList(HashMap<Long, ExceptionList> inMap)
	{
		exMap = inMap;
	}
	
	HashMap<Long, Stack<String>> testMap = new HashMap<Long, Stack<String>>();
	HashMap<Integer, Long> permMap = new HashMap<Integer, Long>();
	
	
	public void createPermMap()
	{
		for(int i = 1; i <= 12; i++)
		{
			long perm = i;
			permMap.put(i, perm);
		}
	}
	
	
	public void buildListMoreEntries(int inCount)
	{
		createPermMap();
		
		int count = inCount;
		HashMap<Long, Integer> stPermNumber = new HashMap<Long, Integer>();
		
		
		for(long i = 1; i <= 100; i++)
		{
			stPermNumber.put(i, 1);
		}
		
		/*
		for(long i = 1; i <= (count/100); i++)
		{
			stPermNumber.put(i, 1);
		}
		*/
		
		//for each session build exception list
		for(long i = 1; i <= 100; i++)
		{
			long sessID = i;
			//System.out.println("SessionID: " + sessID);
			//String userID = "c" + sessID;
			
			if(!exMap.containsKey(sessID))
			{
				//first entry for a session, handle according to algorithm.
				long exTime1 = System.currentTimeMillis();
				ExceptionList ex1 = new ExceptionList("noName", 0, exTime1, null);
				exMap.put(sessID, ex1);
			}
			for(int j = 0; j < (inCount/100); j++)
			{
				int permNumber = generatePermission();
				
				//while stationary permission is generated, try to generate a transitioning permission
				while(permNumber == stPermNumber.get(sessID))
				{
					//System.out.println("Same number generated.");
					//System.out.println("For session ID: " + sessID);
					permNumber = generatePermission();
				}
				
				
				/*if(i == 1)
				{
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}*/
				
				
				long perm = permNumber;
				String name = "n" + permNumber;
				long exTime = System.currentTimeMillis() + 1;
				//long exTime = System.currentTimeMillis();
				ExceptionList oldEx = exMap.get(sessID);
				ExceptionList putEx = new ExceptionList(name, perm, exTime, oldEx);
				
				//printPerm(perm);
				
				if(HCAPResourceServer.batonCompression)
				{
					//if size of baton > 12 compress baton
					if(batonSize(putEx) > 12)
					{
						batonCompression comp = new batonCompression(putEx);
						putEx = comp.compress();	
					}
				}
				
				exMap.put(sessID, putEx);
				stPermNumber.put(sessID, permNumber);
		}
		
		/*
		for(long i = 1; i <= (count/100); i++)
		{
			long sessID = i;
			//System.out.println("SessionID: " + sessID);
			//String userID = "c" + sessID;
			
			if(!exMap.containsKey(sessID))
			{
				//first entry for a session, handle according to algorithm.
				long exTime1 = System.currentTimeMillis();
				ExceptionList ex1 = new ExceptionList("noName", 0, exTime1, null);
				exMap.put(sessID, ex1);
			}
			for(int j = 0; j < 100; j++)
			{
				int permNumber = generatePermission();
				
				//while stationary permission is generated, try to generate a transitioning permission
				while(permNumber == stPermNumber.get(sessID))
				{
					//System.out.println("Same number generated.");
					//System.out.println("For session ID: " + sessID);
					permNumber = generatePermission();
				}
				
				
				
				long perm = permNumber;
				String name = "n" + permNumber;
				long exTime = System.currentTimeMillis() + 1;
				//long exTime = System.currentTimeMillis();
				ExceptionList oldEx = exMap.get(sessID);
				ExceptionList putEx = new ExceptionList(name, perm, exTime, oldEx);
				
				//printPerm(perm);
				
				if(HCAPResourceServer.batonCompression)
				{
					//if size of baton > 12 compress baton
					if(batonSize(putEx) > 12)
					{
						batonCompression comp = new batonCompression(putEx);
						putEx = comp.compress();	
					}
				}
				
				exMap.put(sessID, putEx);
				stPermNumber.put(sessID, permNumber);	
			}
		 	*/
			//System.out.println("Client completed: " + i);
		}
		//System.out.println("Client completed.");	
	}
	
	private int batonSize(ExceptionList ex)
	{
		int size = 0;
		while(ex.getEx() != null)
		{
			ex = ex.getEx();
			size++;
		}
		return size;
	}
	
	public void printTestMap()
	{
		Set<Long> sess =  testMap.keySet();
		
		//System.out.println(testMap);	
		
		for(long sessID : sess)
		{
			//Stack getStack = testMap.get(sess);
			//System.out.print("SessID" + sessID);
			//System.out.println("		Stack" + getStack);
			//System.out.println("		Stack" + testMap.get(sess));
		}
	}
	
	//function to generate a client id
	public int generateClient()
	{
		int sessID = 0;
		
		Random ran = new Random();
		sessID = ran.nextInt(10) + 1;
		
		return sessID;
	}
	
	//function to generate a permission number
	public int generatePermission()
	{
		int permNumber = 0;
		
		Random ran = new Random();
		permNumber = ran.nextInt(12) + 1;
		
		return permNumber;
	}
	
	public HashMap<Long, ExceptionList> getExList()
	{
		return exMap;
	}
	
	private void printPerm(long perm)
	{
		System.out.print(perm + "-->");
	}
}
