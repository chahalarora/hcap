package SecureResServer.SecureResServer;

import java.util.HashMap;

import org.eclipse.californium.core.CoapResource;
import ExperimentResources.ConstructResourcesTestMachine;

public class App {
	
	public static HCAPResourceServer server;
	public static Object serverObj;
	
	public static void main(String[] args)
    {
		String propFileLocation = "/D:/uCalgary/HCAPv1/HCAPCode/PropertiesFiles/ResServer.properties";
    	
    	CoapResource[] resT = null;
    	HashMap<Pair, Long> inPermMap = null;
    	
    	System.out.println("Demo resources initiated for resource server.");
    	//Construct resources for test machine
    	ConstructResourcesTestMachine resTest = new ConstructResourcesTestMachine();
    	resT =  resTest.construct();
    	
    	
    	//to be taken as user input
    	inPermMap = new App_Ex().createMap(12);    
    	
    	
    	server = new HCAPResourceServer(propFileLocation, inPermMap);
    	
    	server.addResourcesToServer(resT);
    	
    	CoapResource parentRes = CoapHcapParentResource.createResource();
    	server.addResourcesToServer(parentRes);

    	
    	serverObj = server.startHCAPServer();
    	
    	
    	//METHOD TO STOP SERVER
    	/*
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
		*/
    }

}
