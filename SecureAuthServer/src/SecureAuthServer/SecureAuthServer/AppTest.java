package SecureAuthServer.SecureAuthServer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import StateMachines.InitStateMachine;
import StateMachines.TestingFrameworkStateMachine;

public class AppTest {
	
	
	public static HCAPAuthorizationServer server;
	public static Object serverObj;
	
	public static void main(String[] args )
    {
        String propFileLocation  = "/D:/uCalgary/HCAP/hcap/delete/hcap-master/PropertiesFiles/AuthServer.properties";
        String xmlFileLocation = "/D:/uCalgary/HCAP/hcap/delete/hcap-master/TestingFrameworkXMLFiles/Test1.xml";
        
        //read experiment number and machine number
        //new App_Ex().readFile(propFileLocation);
        
        
        //only pass properties file's location as parameter and rest everything can be read from properties file.
        //METHOD TO START SERVER
        server = new HCAPAuthorizationServer(propFileLocation);
        
        //String rs1 = "127.0.0.1:8080";
    	//String rs2 = "127.0.0.1:8081";
    	//Test Machine
        
        Document doc = new AppTest().readXML(xmlFileLocation);
        
        
    	
    	//server.addClientStateMachine("C=CA,L=Ottawa,O=Eclipse IoT,OU=Californium,CN=cf-client", new InitStateMachine(), -1);
    	
    	ArrayList<clientStateMachineInfo> lis = new AppTest().getClientInfoFromXML(doc);
    	for(clientStateMachineInfo info : lis)
    	{
    		server.addClientStateMachine(info.getClientName(), info.getSA(), info.getPropagation());
    	}
    	
    	
    	// add key for each resource server
    	HashMap<String, String> addRSMap = new AppTest().getResourceServerKeyFromXML(doc);
    	for (Map.Entry<String, String> entry : addRSMap.entrySet())
    	{
    		server.addResourceServer(entry.getKey(), entry.getValue());	
    	}
    	
        
    	HashMap<String, String> stateToRS = new AppTest().getResourceServerForStateFromXML(doc);
    	for (Map.Entry<String, String> entry : stateToRS.entrySet())
    	{
    		server.addToStateRSMap(entry.getKey(), entry.getValue());	
    	}
        
        
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
	
	private Document readXML(String inLoc)
	{
		Document doc = null;
		try {
			File inputFile = new File(inLoc); 
        	SAXBuilder saxBuilder = new SAXBuilder(); 
        	doc = saxBuilder.build(inputFile);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	
	private HashMap<String, String> getResourceServerForStateFromXML(Document doc)
	{
		HashMap<String, String> retMap = new HashMap<String, String>();
		
		Element root = doc.getRootElement();
		
		Element rsKey = root.getChild("AuthServer").getChild("StateToResServer");
		
		for(Element e : rsKey.getChildren())
		{
			String state = e.getAttribute("state").getValue();
			String server = e.getAttribute("server").getValue();
			
			
			//add to return map
			retMap.put(state, server);
		}
		
		return retMap;
		
	}
	
	private HashMap<String, String> getResourceServerKeyFromXML(Document doc)
	{
		HashMap<String, String> retMap = new HashMap<String, String>();
		
		Element root = doc.getRootElement();
		
		Element rsKey = root.getChild("AuthServer").getChild("ResourceServerKey");
		
		for(Element e : rsKey.getChildren())
		{
			String rs = e.getAttribute("resourceServer").getValue();
			String key = e.getAttribute("key").getValue();
			
			
			//add to return map
			retMap.put(rs, key);
		}
		
		return retMap;
	}
	
	/**
	 * TODO: write logic for state machine
	 * 
	 * @param doc
	 * @return
	 */
	private ArrayList<clientStateMachineInfo> getClientInfoFromXML(Document doc) 
	{
		ArrayList<clientStateMachineInfo> returnList = new ArrayList<clientStateMachineInfo>(); 
		try 
		{
			Element root = doc.getRootElement();
			
			Element clients = root.getChild("AuthServer").getChild("clients");
			
			for(Element e : clients.getChildren())
			{
				String clientName = e.getChild("name").getText();
				int clientProp = Integer.parseInt(e.getChild("propagation").getText());
				
				
				//now read the state machine and create an object of SecurityAutomaton class
				Element saElement = e.getChild("SA");
				String initialState = saElement.getChild("initialState").getText();
				
				Element statesElement = saElement.getChild("states");
				
				States sObj = new States();
				StateTransitions tObj = new StateTransitions();
				
				for(Element se : statesElement.getChildren())
				{
					String stateName = se.getAttribute("name").getValue();
					
					Element stPermElement = se.getChild("stPerms");
					Element transPermElement = se.getChild("transPerms");
					
					ArrayList<Integer> stPermLis = new ArrayList<Integer>(); 
					for(Element st : stPermElement.getChildren())
					{
						//associate states with st perms
						stPermLis.add(Integer.parseInt(st.getText()));
					}
					sObj.addStPerm(stateName, stPermLis);
					
					ArrayList<Integer> transPermLis = new ArrayList<Integer>();
					for(Element tr : transPermElement.getChildren())
					{
						//associate states with trans perms
						transPermLis.add(Integer.parseInt(tr.getText()));
					}
					sObj.addTransPerm(stateName, transPermLis);
					
				}
				
				
				Element transitionElement = saElement.getChild("transitions");
				
				for(Element te : transitionElement.getChildren())
				{
					int perm = Integer.parseInt(te.getAttribute("perm").getValue());
					
					String fromState = te.getChild("from").getValue();
					String toState = te.getChild("to").getValue();
					
					HashMap<String, String> fromTo = new HashMap<String, String>();
					fromTo.put(fromState, toState);
					
					tObj.addStTransitions(perm, fromTo);
				}
			
				TestingFrameworkStateMachine machine = new TestingFrameworkStateMachine(initialState, sObj, tObj);
				clientStateMachineInfo info = new clientStateMachineInfo(machine, clientProp, clientName);

				returnList.add(info);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
        return returnList;
	}

}
