package SecureAuthServer.SecureAuthServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import StateMachines.InitStateMachine;
import StateMachines.InitStateMachineEP11;
import StateMachines.InitStateMachineEP22;
import StateMachines.InitStateMachineEP5;

/**
 * Sample Driver for authorization server
 *
 */
public class App 
{	
	//EXPERIMENT 3 PART 1
	public void runEx3()
	{
		System.out.println("Setup initialized for experiment 3.");
		//here the authorization server should have clients configured so as to support exception list update for server
		int numClients = numClientsFromProps;
		for(int i = 1; i <= numClients; i++)
    	{
    		ManageUsers mgr = new ManageUsers(i-1, ("c" + i));
    		long sessID = mgr.getSessionID();
    		
    		mgr.setState("St1");
    		mgr.setEntryTime(System.currentTimeMillis());
    		long stateInTime = mgr.getEntryTime();
    		String currState = mgr.getState();
    		HCAPAuthorizationServer.userMap.put(sessID, mgr);
    		
    		//state machine for experiment 2 - new
    		InitStateMachineEP22 init = new InitStateMachineEP22(12);
    		init.initStates();
    		init.initTransitions();
    		
    		
    		ArrayList tempLis = new ArrayList();					//can have an interface which is being implemented by both classes.
    		States sObj = init.getStatesObject();
    		StateTransitions stObj = init.getStateTransObject();
    		
    		tempLis.add(sObj);
    		tempLis.add(stObj);
    		HCAPAuthorizationServer.sessionMap.put(sessID, tempLis);
    	}		
	}
	
	public static int exNumber;
	public static int machineNumber;
	public static int numClientsFromProps;
	
	
	public void readFile(String propFileLocation)
	{
		try
		{
			File file = new File(propFileLocation);
			InputStream in = new FileInputStream(file);
		    Properties prop = new Properties();
		    prop.load(in);
		    exNumber = Integer.parseInt(prop.getProperty("experimentNumber"));
		    machineNumber = Integer.parseInt(prop.getProperty("machineNumber"));
		    numClientsFromProps = Integer.parseInt(prop.getProperty("numClients"));
		    in.close();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
    public static void main(String[] args )
    {
        String propFileLocation  = "/home/grads/lakshya.tandon/Desktop/MultiResourceServer-23Oct2017/PropertiesFiles/AuthServer.properties";
        
        //read experiment number and machine number
        new App().readFile(propFileLocation);
        
        
        //only pass properties file's location as parameter and rest everything can be read from properties file.
        //METHOD TO START SERVER
        HCAPAuthorizationServer server = new HCAPAuthorizationServer(propFileLocation);
        
        String rs1 = "127.0.0.1:8080";
    	String rs2 = "127.0.0.1:8081";
        if(exNumber == 0)
        {
        	//Test Machine
        	server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachine(), -1);
        	
        	//just to test
        	server.addResourceServer("127.0.0.1:8080", "myKey1");
        	server.addToStateRSMap("St1", "127.0.0.1:8080");
        }
        else if(exNumber == 1)
        {
        	//StateMachine for Experiment 1
            server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachineEP11(), 0);
            
            server.addResourceServer(rs1, "myKey1");
        	server.addToStateRSMap("St1", rs1);
        	server.addToStateRSMap("St2", rs1);
        }
        else if(exNumber == 2)
        {
        	//State Machine for Experiment 2
        	server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachineEP22(machineNumber), -1);
        	server.addResourceServer(rs1, "myKey1");
        	server.addToStateRSMap("St1", rs1);
        	server.addToStateRSMap("St2", rs1);
        }
        else if(exNumber == 3)	//Experiment 3 Part 1
        {
        	new App().runEx3();
        }
        else if(exNumber == 4)	//Experiment 3 Part 2
        {
        	System.out.println("Experiment 6 running.");
        	server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachineEP22(12), -1);
        	
        	//Initialize RS key. 
        	server.addResourceServer(rs1, "myKey1");
        	server.addToStateRSMap("St1", rs1);
        	server.addToStateRSMap("St2", rs1);
        	server.addToStateRSMap("St3", rs1);
        	server.addToStateRSMap("St4", rs1);
        	server.addToStateRSMap("St5", rs1);
        	server.addToStateRSMap("St6", rs1);
        	server.addToStateRSMap("St7", rs1);
        	server.addToStateRSMap("St8", rs1);
        	server.addToStateRSMap("St9", rs1);
        	server.addToStateRSMap("St10", rs1);
        	server.addToStateRSMap("St11", rs1);
        	server.addToStateRSMap("St12", rs1);
        	
        	//EXPERIMENT 3 ************************************************** ********************************************************
            //initialize SA for experiment3 - 10 machines for 10 clients
        	//OLD CODE
        	/*
            server.addClientStateMachine("ni:///sha-256;qweS0C3Hz_xDhGvoFMP7z_t1akBldsTuzf7zU2456lI", new InitStateMachineEP22(12), -1);
            server.addClientStateMachine("ni:///sha-256;SU9tAkd5kwZDWJRE3acxZWt0LamPhgBo7lF1YajsvlI", new InitStateMachineEP22(12), -1);
            server.addClientStateMachine("ni:///sha-256;N0pDboj-wsGoULMNJlEH1O0slAkjiKRkMLpdOJdbzk4", new InitStateMachineEP22(12), -1);
            server.addClientStateMachine("ni:///sha-256;IuRKflyQ_TmXeWlqScaCPYmN8UH6YgnrYmCx819DI4A", new InitStateMachineEP22(12), -1);
            server.addClientStateMachine("ni:///sha-256;j96R8RRFfAukfA94JUEmVQDEmUk2E3e1eHlk2DbMF-Y", new InitStateMachineEP22(12), -1);
            server.addClientStateMachine("ni:///sha-256;LAzAX7MougH04sBBu3rWyoVAja0o1FpM709nGHVxOB4", new InitStateMachineEP22(12), -1);
            server.addClientStateMachine("ni:///sha-256;wvPN_k4ACAnIsZ4m8vLSNgjnlfkXAhRr7YzVa0pY3f0", new InitStateMachineEP22(12), -1);
            server.addClientStateMachine("ni:///sha-256;BBgi_wO4qBKu-vxBn_qLJt939BO2ah3wSY51qB5-zPk", new InitStateMachineEP22(12), -1);
            server.addClientStateMachine("ni:///sha-256;Jb7JzD2g2lE1zwYWfS4meNG4_BEzO6AZc0czy93C4bs", new InitStateMachineEP22(12), -1);
            server.addClientStateMachine("ni:///sha-256;KWjr6puZVUoFWz8AMZM-0CGcgBMEHZC-6Qk9mKv6wQ0", new InitStateMachineEP22(12), -1);
            */
            //EXPERIMENT 3 ************************************************** ********************************************************
        }
        else if(exNumber == 5)
        {
        	System.out.println("Experiment 5 running.");
        	server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachineEP5(), -1);
        	
        	//Initialize RS key. 
        	server.addResourceServer(rs1, "myKey1");
        	server.addToStateRSMap("St1", rs1);
        	server.addToStateRSMap("St2", rs1);
        }
        else if(exNumber == 6)
        {
        	System.out.println("Experiment 6 running.");
        	
        	server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachineEP5(), -1);
        	
        	server.addResourceServer(rs1, "myKey1");
        	server.addResourceServer(rs2, "myKey2");
        	
        	//states represent different resource servers
        	server.addToStateRSMap("St1", rs1);
        	server.addToStateRSMap("St2", rs2);
        }
        else
        {
        	System.out.println("Experiment not defined yet.");
        }
        
        
        
        
        
        //initialize SAMap
        //server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=client1", new InitStateMachineEP22(12), -1);
        
        //server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachineEP22(20), -1);
        //server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachineEP22(20), -1);
        Object serverObj = server.startHCAPServer();
        
        //****************************************** CODE FOR EXPERIMENT 3 ***************************************
        //new App().runEx3();
        //****************************************** CODE FOR EXPERIMENT 3 ***************************************
        
        testCaseCodeNoSession();
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
        
        
        //toDo
        /*
         * Authorization server need to have different resources for different type of services it provides
         * Issuing capabilities.
         * 	-> Issuing new capabilities
         * 	-> Issuing lost capabilities
         * updating state from the client and issuing capability
         * doing garbage collection
         */
    }
    
    public static void testCaseCodeNoSession()
    {
    	ManageUsers usr = new ManageUsers(1, "testUser");
    	usr.setState("St1");
    	usr.setEntryTime(11111111);
    	usr.setBaton(false);
    	HCAPAuthorizationServer.userMap.put((long) 1, usr);
    }
}
