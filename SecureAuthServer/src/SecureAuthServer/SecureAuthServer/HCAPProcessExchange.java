package SecureAuthServer.SecureAuthServer;

import java.security.Principal;
import java.util.HashMap;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * This class is used to process a CoAP exchange which has been received at endpoint. It
 * extracts useful information for further functioning of the protocol.
 *  
 * @author lakshya.tandon
 */
public class HCAPProcessExchange  
{
	private String ClientID;
	private long sessID = 0;
	private byte[] requestPayload;
	private HashMap<String, Object> ticket;
	private HashMap<String, Object> payloadMap;
	//private HashMap<String, ArrayList<Object>> saMap;
	//private ArrayList<Object> lis;
	
	/**
	 * HCAP Process Exchange constructor. Expects an CoAP Exchange and data interchange format 
	 * as parameter, reads information from CoAP Exchange.
	 * 
	 * @param exchange
	 * @param isCBOR
	 */
	@SuppressWarnings("unchecked")
	public HCAPProcessExchange(CoapExchange exchange, boolean isCBOR)
	{
		Exchange ex = exchange.advanced();
		Request request = ex.getRequest();
		Principal principal = request.getSenderIdentity(); 
		ClientID =  principal.getName();
		requestPayload = request.getPayload();
		
		if(requestPayload != null)
		{
			if(isCBOR)
			{
				cborConverter convert = new cborConverter();
				payloadMap = convert.convertFromCBOR(requestPayload);
			}
			else
			{
				jsonConverter convert = new jsonConverter();
				payloadMap = convert.convertFromJSON(requestPayload);
			}
			
			if(payloadMap.containsKey("ticket") && payloadMap.get("ticket") != null)
			{
				ticket = (HashMap<String, Object>) payloadMap.get("ticket");	
			}
			else
			{
				ticket = null;
			}
			
			if(payloadMap.containsKey("sessID") && payloadMap.get("sessID") != null)
			{
				sessID = Long.parseLong(payloadMap.get("sessID").toString());
			}
		}
	}
	/**
	 * This method is used to get client ID from the exchange.
	 * 
	 * @return
	 */
	public String getClientID()
	{
		return ClientID;
	}
	
	/**
	 * This method is used to get sessionID from the exchange.
	 * 
	 * @return
	 */
	public long getSessID()
	{
		return sessID;
	}
	
	/**
	 * This method is used to get payload from the exchange.
	 * 
	 * @return
	 */
	public byte[] getPayload()
	{
		return requestPayload;
	}
	
	/**
	 * This method is used to get ticket contained in the exchange.
	 * 
	 * @return
	 */
	public HashMap<String, Object> getTicket()
	{
		return ticket;
	}
	
	
	/*
	public SecurityAutomaton getAutomaton()
	{
		return (SecurityAutomaton) lis.get(0);
	}
	
	public int getFragLength()
	{
		return (Integer) lis.get(1);
	}
	*/
}
