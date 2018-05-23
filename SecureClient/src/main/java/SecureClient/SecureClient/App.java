package SecureClient.SecureClient;

import java.util.HashMap;
import java.util.Stack;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	new App().demoClient();
    }
    
    public void demoClient()
    {
    	//to be taken from command line
    	String propFileLocation = "C:\\Users\\lakshya.tandon\\Documents\\GitHub\\hcap\\PropertiesFiles\\Client.properties";
    	
    	
        ClientBuilder client = new ClientBuilder(propFileLocation);
        
        HashMap<String, Object> capability = null;
        HashMap<String, Object> oldCapability = null;
        
        
        if(client.getCapability())
        {
        	capability = client.getTicket();
        	oldCapability = capability;
        	
        	System.out.println(capability.toString());
        	
        }
        
        //reIssueRequest
        /*
        if(client.reIssueCapabilityFromAuthServer(1))
        {
        	capability = client.getTicket();
        	System.out.println(capability.toString());
        }
        */
        
        
        if(client.requestAccess(capability, null, "demoResource3"))
        {
        	capability = client.getTicket();
        	
        	System.out.println(capability.toString());
        }
        
       
        
        //get a new capability after state update request is issued
        /*
        HashMap<String, Object> updateRequest = capability;
        if(client.sendUpdateRequestAndGetCapability(updateRequest))
        {
        	capability = client.getTicket();
        	
        	System.out.println(capability.toString());	
        }
        */
        
        
        if(client.requestAccess(capability, null, "demoResource6"))
        {
        	capability = client.getTicket();
        	
        	System.out.println(capability.toString());
        }
        
      
       
        
        if(client.requestAccess(capability, null, "demoResource8"))
        {
        	capability = client.getTicket();
        	
        	System.out.println(capability.toString());
        }
    }
}
