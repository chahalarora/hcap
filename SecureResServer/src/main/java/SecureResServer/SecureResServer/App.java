package SecureResServer.SecureResServer;

import java.util.HashMap;

import org.eclipse.californium.core.CoapResource;
import ExperimentResources.ConstructResourcesTestMachine;

public class App {
	
	public static HCAPResourceServer server;
	public static Object serverObj;
	
	public static void main(String[] args)
    {
		String propFileLocation = "C:\\Users\\lakshya.tandon\\Documents\\GitHub\\hcap\\PropertiesFiles\\ResServer.properties";
    	
    	CoapResource[] resT = null;
    	HashMap<Pair, Long> inPermMap = null;
    	//to be taken as user input
    	inPermMap = new App().createMap(12);
    	server = new HCAPResourceServer(propFileLocation, inPermMap);
    	
    	System.out.println("Demo resources initiated for resource server.");
    	//Construct resources for test machine
    	ConstructResourcesTestMachine resTest = new ConstructResourcesTestMachine();
    	resT =  resTest.construct(20);
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

}
