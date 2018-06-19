package SecureClient.SecureClient;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * Hello world!
 *
 */
public class AppTest 
{
    public static void main(String[] args)
    {
    	new AppTest().demoClient();
    }
    
    public void demoClient()
    {
    	//to be taken from command line
    	String propFileLocation  = "/D:/uCalgary/HCAP/hcap/delete/hcap-master/PropertiesFiles/Client.properties";
        String xmlFileLocation = "/D:/uCalgary/HCAP/hcap/delete/hcap-master/TestingFrameworkXMLFiles/Test1.xml";
        
    	
    	//get number of tests to be run

        ClientBuilder client = new ClientBuilder(propFileLocation);
        
        
        executeTests(getClientTestsFromXML(xmlFileLocation), client, xmlFileLocation);


    }
    
    private void logToFile(String inStr)
    {
    	//log string a file
    	//File file = new File("TestLog.txt");
    	try
    	{
    		PrintWriter out = new PrintWriter("TestLog.txt");	
    		out.println(inStr);
    		out.close();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    private List<Element> getClientTestsFromXML(String inXMLFileLocation)
    {
        List<Element> lis = null;
    	try {
        	File inputFile = new File(inXMLFileLocation); 
        	SAXBuilder saxBuilder = new SAXBuilder();
        	Document doc = saxBuilder.build(inputFile);
        	
            System.out.println("Root element :" + doc.getRootElement().toString());
            
            Element root = doc.getRootElement();
            
            Element resources = root.getChild("Client").getChild("clientRequests");
            lis = resources.getChildren();
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return lis;
    }
    
    private Queue<String> getPermissionsFromTest(Element inElement)
    {
    	Queue<String> outRes = new PriorityQueue<String>();
    	
    	 List<Element> lis = inElement.getChildren();
         
         for(Element e : lis)
         {
         	outRes.add(e.getText());
         }
    	
    	return outRes;
    }
    
    private void executeTests(List<Element> elements, ClientBuilder client, String xmlFileLocation)
    {
    	int i = 1;
    	StringBuilder bldr = new StringBuilder();
    	for(Element e : elements)
    	{
    		String testNumber = "Test " + i;
    		boolean check = false;
        	Queue<String> permissions = getPermissionsFromTest(e);
            HashMap<String, Object> capability = null;
            
            if(client.getCapability())
            {
            	capability = client.getTicket();
            }
            
            while(!permissions.isEmpty())
            {
            	String requestedResource = permissions.poll();
            	System.out.println("Requested Resource is: " + requestedResource );
            	
            	if(client.requestAccess(capability, null, requestedResource))
                {
                	capability = client.getTicket();
                	
                	if(capability.containsKey("messageText"))
                	{
                		if(capability.get("messageText").equals("REQUEST CANCELLED"))
                		{
                			String failure = "Failed Test: " + testNumber + ", when requesting access to: " + requestedResource + ", from file: "+  xmlFileLocation;
                			bldr.append(failure);
                        	bldr.append(System.getProperty("line.separator"));
                        	check = true;
                        	i++;
                			break;
                		}
                	}
                	//System.out.println(capability.toString());
                }
            }
            if(check) 
            {
            	continue;
            }
        	String success = "Successfully executed test: "  + testNumber + ", from file: " + xmlFileLocation;
        	bldr.append(success);
        	bldr.append(System.getProperty("line.separator"));
        	i++;
        	
        	//reset servers for client after each test
    	}
    	logToFile(bldr.toString());
    }
    

    /*
    public Queue<String> getClientResourcesFromXML(String inXmlFileLocation)
    {
    	Queue<String> outRes = new PriorityQueue<String>();
    	//parse XML and get resources to be accessed.
    	try {
        	File inputFile = new File(inXmlFileLocation); 
        	SAXBuilder saxBuilder = new SAXBuilder(); 
        	Document doc = saxBuilder.build(inputFile);
        	
            System.out.println("Root element :" + doc.getRootElement().toString());
            
            Element root = doc.getRootElement();
            
            Element resources = root.getChild("Client").getChild("clientRequests");
            
            List<Element> lis = resources.getChildren();
            
            for(Element e : lis)
            {
            	outRes.add(e.getText());
            }
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    	}
    	
    	return outRes;
    }
    */
}
