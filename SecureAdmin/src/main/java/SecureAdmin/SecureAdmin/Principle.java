package SecureAdmin.SecureAdmin;

public class Principle  extends Object{

	String principleID;
	Object xml; //This XML contains Security Automaton and Fragments
	
	public Principle(String inprincipleID,Object inxml) {
		principleID = inprincipleID;
		xml = inxml;
	}
	
	/**
	 * Gets principle ID
	 * @return
	 */
	public String getID() {
		return principleID;
	}
	
	/**
	 * Gets xml that contains Security Automaton and Fragments
	 * @return
	 */
	public Object getxml() {
		return xml;
	}	
	
}
