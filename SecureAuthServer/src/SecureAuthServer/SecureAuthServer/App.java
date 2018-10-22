package SecureAuthServer.SecureAuthServer;

import java.util.HashMap;
import java.util.Map;

/*
 * Shauvik Working on this- 2014 - 10 -14
 * 
 * */
import StateMachines.InitStateMachine;

public class App {
	
	
	public static HCAPAuthorizationServer server;
	public static Object serverObj;
	
	public static void main(String[] args )
    {
        String propFileLocation  ="/home/chahal/CourseWork/Fall2018/CPSC-626/Project/HCAP/hcap/PropertiesFiles/AuthServer.properties";
        //read experiment number and machine number
        //new App_Ex().readFile(propFileLocation);
        
        
        //only pass properties file's location as parameter and rest everything can be read from properties file.
        //METHOD TO START SERVER
        server = new HCAPAuthorizationServer(propFileLocation);
        
        //String rs1 = "127.0.0.1:8080";
    	//String rs2 = "127.0.0.1:8081";
    	//Test Machine
        
    	InitStateMachine voidStateMachine = new InitStateMachine();
    	
    	server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", voidStateMachine , -1);
        server.addResourceServer("127.0.0.1:8080", "myKey");
        server.addToStateRSMap("St1", "127.0.0.1:8080");
        
       
        serverObj = server.startHCAPServer();
        
        //testCaseCodeNoSession();
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
			//
			server.stopHCAPServer(serverObj);
		}
		in.close();
		*/
    }

}
