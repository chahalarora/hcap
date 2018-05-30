package SecureAuthServer.SecureAuthServer;

public class clientStateMachineInfo 
{	
	SecurityAutomaton sa;
	int prop;
	String clientName;
	
	public clientStateMachineInfo(SecurityAutomaton inSA, int inProp, String inClientName) 
	{
		sa = inSA;
		prop = inProp;
		clientName = inClientName;
	}
	
	public SecurityAutomaton getSA()
	{
		return sa;
	}
	
	public int getPropagation()
	{
		return prop;
	}
	
	public String getClientName() 
	{
		return clientName;
	}
	
}
