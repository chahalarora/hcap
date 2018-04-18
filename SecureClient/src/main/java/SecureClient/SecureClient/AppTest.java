package SecureClient.SecureClient;

import java.io.File;
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
    public static void main( String[] args )
    {
    	new AppTest().demoClient();
    }
    
    public void demoClient()
    {
    	//to be taken from command line
    	String propFileLocation = "C:\\Users\\lakshya.tandon\\Documents\\GitHub\\hcap\\PropertiesFiles\\Client.properties";
    	String xmlFileLocation = "C:\\Users\\lakshya.tandon\\Desktop\\HCAPTestingFramework\\Test1.xml";
    	
    	Queue<String> permissions = getClientResourcesFromXML(xmlFileLocation);
    	
    	
        ClientBuilder client = new ClientBuilder(propFileLocation);
        

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
            	
            	System.out.println(capability.toString());
            }
        }
    }
    

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
            
            Element resources = root.getChild("Client").getChild("clientResources");
            
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
}
