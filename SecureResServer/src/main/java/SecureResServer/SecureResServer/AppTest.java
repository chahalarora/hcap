package SecureResServer.SecureResServer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import ExperimentResources.ConstructResourcesTestMachine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class AppTest {
	
	public static HCAPResourceServer server;
	public static Object serverObj;
	
	public static void main(String[] args)
    {
		String propFileLocation = "C:\\Users\\lakshya.tandon\\Documents\\GitHub\\hcap\\PropertiesFiles\\ResServer.properties";
		String xmlFileLocation = "C:\\Users\\lakshya.tandon\\Desktop\\HCAPTestingFramework\\Test1.xml";
    	
    	CoapResource[] resT = null;
    	HashMap<Pair, Long> inPermMap = null;
    	
    	//System.out.println("Demo resources initiated for resource server.");
    	
    	
    	//Construct 100 resources
    	//ConstructResourcesTestMachine resTest = new ConstructResourcesTestMachine();
    	//resT =  resTest.construct(100);
    	
    	//to be taken as user input
    	//inPermMap = new App().createMap(12);
    	inPermMap = new AppTest().getInPermMapFromXML(xmlFileLocation);
    	resT = new AppTest().getResourceFromXML(xmlFileLocation);
    	
    	
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
	
	public CoapResource[] getResourceFromXML(String inLoc) {
		ArrayList<CoapResource> resources = new ArrayList<CoapResource>();
		try {
			Document doc = readXML(inLoc);
			
			Element root = doc.getRootElement();
            
            Element res = root.getChild("ResServer").getChild("resources");
            
            for(Element eachRes : res.getChildren())
            {
            	String resourceName = eachRes.getText();
            	
            	//create a coap resource
            	CoapResource inDemoResource  = new CoapResource(resourceName)
				{
					@Override
					public void handleGET(CoapExchange exchange)
					{
						System.out.println("Got something in GET.");
						exchange.respond(ResponseCode.CONTENT, "This is authorization server, currently it doesn't support GET.");
					}
					
					@Override
					public void handlePOST(CoapExchange exchange)
					{
						byte[] requestPayload = exchange.getRequestPayload();
						Response response = new Response(ResponseCode.CONTENT);
						response.setPayload(requestPayload);
						response.getOptions().setSize2(requestPayload.length);
						exchange.respond(response);
					}
				};
				
				//add resource to array list
				resources.add(inDemoResource);
            }
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		 
		//convert array list to array
		CoapResource[] returnArray = new CoapResource[resources.size()]; 
		for(int i = 0; i < resources.size(); i++)
		{
			returnArray[i] = resources.get(i);
		}
		return returnArray;
	}
	
	public HashMap<Pair, Long> getInPermMapFromXML(String inLoc){
		
		HashMap<Pair, Long> retMap = new HashMap<Pair, Long>();
		
		try {
        	Document doc = readXML(inLoc);
        	
            //System.out.println("Root element :" + doc.getRootElement().toString());
            
            Element root = doc.getRootElement();
            
            Element resources = root.getChild("ResServer").getChild("permMap");
            
            for(Element e : resources.getChildren()) {
            	String method = e.getAttribute("method").getValue();
            	String resource = e.getAttribute("resource").getValue();
            	
            	Pair pair  = new Pair(method, resource);
            	
            	Long permission = Long.parseLong(e.getAttribute("perm").getValue());
            	
            	retMap.put(pair, permission);
            }
            
    	}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return retMap;
	}

}
