package SecureResServer.SecureResServer;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;

import ExceptionList.ExceptionList;

public class TestGC 
{
	public static void main(String[] args)
	{
		HCAPResourceServer res = new HCAPResourceServer("/home/lakshya/Desktop/multiResServerCode/DTLScodeMultiResServer/PropertiesFiles/ResServer.properties", null, null);
		res.startHCAPServer();
		
		HCAPResourceServer.authServerAddress = "coaps://127.0.0.1:5683";
		new TestGC().Initialize();
		updateExList update = new updateExList(HCAPResourceServer.lisMap, false);
		update.sendRequest();
	}
	
	public void Initialize()
	{
		for(int j = 1; j <= 10; j++)
		{
			ExceptionList ex = null;
			ExceptionList tempLis = null;
			for(int i = 1; i <= 20; i++)
			{
				ex = new ExceptionList("name", i, 0, tempLis);
				tempLis = ex;
			}
			
			HCAPResourceServer.lisMap.put((long) j, ex);
		}
	}
	
	
}
