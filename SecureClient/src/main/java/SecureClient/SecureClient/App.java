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
    	String propFileLocation = "/home/grads/lakshya.tandon/Desktop/MultiResourceServer-23Oct2017/PropertiesFiles/Client.properties";
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
        
        //extra part
        /*
        if(client.sendUpdateRequestAndGetCapability(capability, "update"))
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
