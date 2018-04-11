package SecureResServer.SecureResServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.Resource;

import ExceptionList.ExceptionList;
import ExperimentResources.ConstructResourcesEx1;
import ExperimentResources.ConstructResourcesEx2;
import ExperimentResources.ConstructResourcesEx5;
import ExperimentResources.ConstructResourcesTestMachine;
import ExperimentResources.buildExceptionList;


/**
 * Sample code to run Resource Server.
 * 
 * @author lakshya
 *
 */

public class App_Ex 
{
	int run = 100;
	long[] timeArr = new long[run];
	
	
	public boolean pingAuthServer()
	{	
		try
		{
			Request req = new Request(Code.POST);
			req.setURI(HCAPResourceServer.authServerAddress + "/hcap/ping");

			CoapResponse response = HCAPResourceServer.resToAuth.advanced(req);
			
			
			String responseStr = response.getResponseText();
			
			if(responseStr.equals("Ping Successful"))
			{
				System.out.println("Authorization Server ping successful.");
				return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
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
	
	private void printExListForSessions(HashMap<Long, ExceptionList> inMap)
	{
		ArrayList<Long> sessionIDs = new ArrayList<Long>();
		for(long sessID : inMap.keySet())
		{
			sessionIDs.add(sessID);
		}
		
		for(long sessID : sessionIDs)
		{
			System.out.println("Session: " + sessID);
			System.out.println("Exception List: ");
			ExceptionList lis = inMap.get(sessID);
			while(lis.getEx() != null)
			{
				System.out.print("(" + lis.getName() + "," + lis.getPerm() + ") --> ");
				lis = lis.getEx();
			}
			System.out.println();
		}
	}
	
	public void runEx3(int inExListCount)
	{
		pingAuthServer();
		for(int i = 0; i < run; i++)
		{
			buildExceptionList ex = new buildExceptionList(HCAPResourceServer.lisMap);
	        ex.buildListMoreEntries(inExListCount);
	        HCAPResourceServer.lisMap = ex.getExList();
	        
	        int size = batonSize(HCAPResourceServer.lisMap.get((long) 100));
	        //printExListForSessions(HCAPResourceServer.lisMap);
	        updateExList exLis = new updateExList(HCAPResourceServer.lisMap, false);
	        long startTime =  System.currentTimeMillis();
	        boolean res = exLis.sendRequest();
	        long endTime =  System.currentTimeMillis();
	        //System.out.println(res);
	        
	        long timeElapsed = endTime - startTime;
	        
	        timeArr[i] = timeElapsed;
	        
	        //reset exception List after it  has been updated.
	        HCAPResourceServer.lisMap.clear();
	        
	        //send a reset request to Authorization Server
	        resetAuthServer(HCAPResourceServer.authServerAddress);
	        
	        System.out.println("Run Count: " + (i+1));
		}
		System.out.println("Experiment finished.");
	}
	
	public void printToFile(String folderName, int inTransReq)
	{
		try
		{
			PrintWriter pr = new PrintWriter(folderName + "/" + inTransReq + ".csv");
			for(int i = 0; i < run; i++)
			{
				long time = timeArr[i];
				pr.println(time);
			}
			pr.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean resetAuthServer(String authServerAddress) 
	{
		//String newAuthServerAddress = "coaps://" + authServerAddress;
		
		//CoapClient resetClient = new CoapClient(newAuthServerAddress);
		
		Request req = new Request(Code.POST);
		req.setURI(authServerAddress + "/hcap/reset");
		req.setPayload("payload".getBytes());
		req.getOptions().setSize1("payload".getBytes().length);
		
		//CoapResponse response = resetClient.advanced(req);
		CoapResponse response = HCAPResourceServer.resToAuth.advanced(req);
		
		if(new String(response.getPayload()).equals("resetDone"))
		{
			return true;
		}
		return false;
	}
	
	//CREATE PERM MAP FOR TEST MACHINE
	/*
	public HashMap<Pair, Long> createMap()
	{
		HashMap<Pair, Long> inPermMap = new HashMap<Pair, Long>();
		
		for(int i = 1; i <= 20; i++)
		{
			String res = "demoResource";
			
			Pair p = new Pair("POST", res+i);
			
			inPermMap.put(p, (long) i);
		}
		
		return inPermMap;
	}
	*/
	
	
	//CREATE PERM MAP FOR EXPERIMENT 1
	public HashMap<Pair, Long> createMap()
	{
		HashMap<Pair, Long> inPermMap = new HashMap<Pair, Long>();
		
		for(int i = 0; i <= 1; i++)
		{
			String res = "demoResource";
			
			Pair p = new Pair("POST", res+i);
			
			inPermMap.put(p, (long) i);
		}
		return inPermMap;
	}
	
	
	//CREATE PERM MAP FOR EXPERIMENT 2
	public HashMap<Pair, Long> createMap(int machineNumber)
	{
		HashMap<Pair, Long> inPermMap = new HashMap<Pair, Long>();
		
		for(int i = 1; i <= machineNumber; i++)
		{
			String res = "demoResource";
			
			Pair p = new Pair("POST", res+i);
			
			inPermMap.put(p, (long) i);
		}
		return inPermMap;
	}
	
    public static void main(String[] args)
    {
    	String propFileLocation = "C:\\Users\\lakshya.tandon\\Documents\\GitHub\\hcap\\PropertiesFiles\\ResServer.properties";
    	
    	int machNumber = 0;
    	int exNumber = 0;
    	String authServerAddress = null;
    	
    	CoapResource[] resT = null;
    	HashMap<Pair, Long> inPermMap = null;
    	
    	
    	//CODE FOR EXPERIMENTS STARTS
    	
    	//read experiment number and machine number from properties file
    	//int exNumber = 0;
    	//int machNumber = 0;
    	try
		{
			File file = new File(propFileLocation);
			InputStream in = new FileInputStream(file);
		    Properties prop = new Properties();
		    prop.load(in);
		    
		    
		    
		    
		    
		    exNumber = Integer.parseInt(prop.getProperty("experimentNumber"));
		    machNumber = Integer.parseInt(prop.getProperty("machineNumber"));
		    authServerAddress = prop.getProperty("authserveraddress");
 
		    in.close();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	
    	//Construct resources for test machine
		/*ConstructResourcesTestMachine resTest = new ConstructResourcesTestMachine();
    	resT =  resTest.construct();
    	inPermMap = new App().createMap();*/
    	

    	
    	if(exNumber == 0)
    	{
    		System.out.println("Demo resources initiated for resource server.");
    		//Construct resources for test machine
    		ConstructResourcesTestMachine resTest = new ConstructResourcesTestMachine();
        	resT =  resTest.construct(20);
        	inPermMap = new App_Ex().createMap(12);
    	}
    	else if(exNumber == 1)
    	{
    		//Construct resources for machine 1 -- EXPERIMENT 1
    		ConstructResourcesEx1 resources1 = new ConstructResourcesEx1();
        	resT =  resources1.construct();
        	inPermMap = new App_Ex().createMap();
    	}
    	else if(exNumber == 2)
    	{
    		System.out.println("Experiment 2 running");
    		System.out.println("Machine Number: " + machNumber);
    		//Construct resources for machine 2 -- EXPERIMENT 2
    		ConstructResourcesEx2 resources2 = new ConstructResourcesEx2();
    		resT = resources2.construct(machNumber);
    		inPermMap = new App_Ex().createMap(machNumber);
    	}
    	else if(exNumber == 3)
    	{
    		//just define some demo resources in order for resource server to not produce any errors
    		//Construct resources for test machine
    		ConstructResourcesTestMachine resTest = new ConstructResourcesTestMachine();
        	resT =  resTest.construct(20);
    	}
    	else if(exNumber == 4)
    	{
    		System.out.println("Experiment 4 running");
    		System.out.println("Machine Number: " + machNumber);
    		//Construct resources for machine 2 -- EXPERIMENT 2
    		ConstructResourcesEx2 resources2 = new ConstructResourcesEx2();
    		resT = resources2.construct(12);
    		inPermMap = new App_Ex().createMap(12);
    	}
    	else if(exNumber == 5)
    	{
    		ConstructResourcesEx5 resources5 = new ConstructResourcesEx5();
    		resT = resources5.construct(2);
    		inPermMap = new App_Ex().createMap(2);
    	}
    	else if(exNumber == 6)
    	{
    		ConstructResourcesEx5 resources5 = new ConstructResourcesEx5();
    		resT = resources5.construct(2);
    		inPermMap = new App_Ex().createMap(2);
    	}
    	else
    	{
    		System.out.println("Experiment not defined yet.");
    	}
    	
    	//CODE FOR EXPERIMENTS ENDS

    	//CoapResource resource = CoapDemoResource.createResource();
    	
    	
    	//there would be a recover resource to recover the lost capability
    
    	
    	
    	HCAPResourceServer server = new HCAPResourceServer(propFileLocation, inPermMap);
    	//initialize above before actual server runs.
    	
    	
        //METHOD TO START SERVER
    	/*
    	CoapResource[] resourcesFinal = new CoapResource[resT.length + 1];
    	for(int i = 0; i < resT.length + 1; i++)
    	{
    		if(i == resT.length)
    		{
    			CoapResource parentRes = CoapHcapParentResource.createResource();
    			resourcesFinal[i] = parentRes;
    		}
    		else
    		{
    			resourcesFinal[i] = resT[i];	
    		}
    	}
    	*/
    	
    	server.addResourcesToServer(resT);
    	
    	CoapResource parentRes = CoapHcapParentResource.createResource();
    	server.addResourcesToServer(parentRes);

    	
    	Object serverObj = server.startHCAPServer();
    	
    	if(exNumber == 1 || exNumber == 2 || exNumber == 4 || exNumber == 5 || exNumber == 6)
    	{
    		//ping authorization server
    		new App_Ex().pingAuthServer();
    	}
    	if(exNumber == 6)
    	{
    		//ping the other resource server also -- Only do this if necessary
    	}
    	
    	
    	//******************************** CODE FOR EXPERIMENT 3 **********************************//
    	if(exNumber == 3)
    	{
    		//this is Experiment 3 Part 1
    		int exLisCount = 90000;
        	App_Ex app = new App_Ex();
        	app.runEx3(exLisCount);
        	app.printToFile("/home/grads/lakshya.tandon/Desktop/MultiResourceServer-23Oct2017/ExperimentResults/Experiment3/normal/", exLisCount);
        	//printTime array to a file
        	
    		//new App().resetAuthServer(authServerAddress);
    	}
    	
    	//******************************** CODE FOR EXPERIMENT 3 **********************************//
    	
    	
    	
    	//METHOD TO STOP SERVER
    	Scanner in = new Scanner(System.in);
        int read = in.nextInt();
		while(read != 1)
		{
			read = in.nextInt();
		}
		if(read == 1)
		{
			//stop server
			server.stopHCAPServer(serverObj);
		}
		in.close();
    }
}
