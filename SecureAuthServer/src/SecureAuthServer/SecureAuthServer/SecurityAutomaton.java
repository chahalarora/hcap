package SecureAuthServer.SecureAuthServer;

/**
 * This interface is to be implemented by the classes defining a State Machine (Security Automaton).
 * Refer sample implementation for usage.
 * 
 * @author lakshya.tandon
 *
 */
public interface SecurityAutomaton 
{
	/**
	 * This method is used to initialize states for a state machine.
	 * 
	 */
	public void initStates();
	
	/**
	 * This method is used to initialize state transitions for a state machine.
	 * 
	 */
	public void initTransitions();
	
	/**
	 * This method gets the object representing the states of the state machine.
	 * 
	 * @return
	 */
	public States getStatesObject();
	
	/**
	 * This method gets the object representing the state transitions of the state machine. 
	 * 
	 * @return
	 */
	public StateTransitions getStateTransObject();
	
	/**
	 * This method gets the initial state of the state machine.
	 * 
	 * @return
	 */
	public String getInitialState();
}
