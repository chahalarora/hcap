package StateMachines;

import SecureAuthServer.SecureAuthServer.SecurityAutomaton;
import SecureAuthServer.SecureAuthServer.StateTransitions;
import SecureAuthServer.SecureAuthServer.States;

public class TestingFrameworkStateMachine implements SecurityAutomaton
{
	private String initialState;
	private States sObj;
	private StateTransitions tObj;
	
	public TestingFrameworkStateMachine(String inInitialState, States inSObj, StateTransitions inTObj)
	{
		initialState = inInitialState;
		sObj = inSObj;
		tObj = inTObj;
	}
	
	public void initStates() {
		// TODO Auto-generated method stub
		
	}

	public void initTransitions() {
		// TODO Auto-generated method stub
		
	}

	public States getStatesObject() {
		// TODO Auto-generated method stub
		return sObj;
	}

	public StateTransitions getStateTransObject() {
		// TODO Auto-generated method stub
		return tObj;
	}

	public String getInitialState() {
		// TODO Auto-generated method stub
		return initialState;
	}

}
