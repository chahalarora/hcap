package SecureAuthServer.SecureAuthServer;

import StateMachines.InitStateMachine;

public class App {
	
	
	public static HCAPAuthorizationServer server;
	public static Object serverObj;
	
	public static void main(String[] args )
    {
<<<<<<< HEAD
        String propFileLocation  ="/D:/uCalgary/HCAPv1/HCAPCode/PropertiesFiles/AuthServer.properties";
=======
        
>>>>>>> master
        //read experiment number and machine number
        //new App_Ex().readFile(propFileLocation);
        
        
        //only pass properties file's location as parameter and rest everything can be read from properties file.
        //METHOD TO START SERVER
        server = new HCAPAuthorizationServer(propFileLocation);
<<<<<<< HEAD
        
        //String rs1 = "127.0.0.1:8080";
    	//String rs2 = "127.0.0.1:8081";
    	//Test Machine
        
    	
    	server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachine(), -1);
        server.addResourceServer("127.0.0.1:8080", "myKey");
        server.addToStateRSMap("St1", "127.0.0.1:8080");
        
       
        serverObj = server.startHCAPServer();
        
=======
        
        //String rs1 = "127.0.0.1:8080";
    	//String rs2 = "127.0.0.1:8081";
    	//Test Machine
        
    	
        server.addToStateRSMap("St1", "127.0.0.1:8080");
        
        
>>>>>>> master
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
