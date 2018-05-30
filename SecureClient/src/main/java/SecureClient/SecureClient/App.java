package SecureClient.SecureClient;

import java.util.HashMap;
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
<<<<<<< HEAD
    	String propFileLocation = "/D:/uCalgary/HCAPv1/HCAPCode/PropertiesFiles/Client.properties";
=======
    	
    	
>>>>>>> master
        ClientBuilder client = new ClientBuilder(propFileLocation);
        
        HashMap<String, Object> capability = null;
        HashMap<String, Object> oldCapability = null;
        
<<<<<<< HEAD
        // get first capability from the authorization server
=======
        
>>>>>>> master
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
        
        // access requests
        if(client.requestAccess(capability, null, "demoResource3"))
        {
        	capability = client.getTicket();
        	
        	System.out.println(capability.toString());
        }
        
<<<<<<< HEAD
=======
       
        
>>>>>>> master
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
