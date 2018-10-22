package ExceptionList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ExceptionList_noName implements Serializable 
{
	
	//This maps permission to the time when exception occurred.
	//HashMap<String, Long> exMap  = new HashMap<String, Long>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long perm;
	long exTime;
	ExceptionList ex = null;
	private String name;
	
	public ExceptionList_noName(String inName, long inPerm, long inExTime, ExceptionList inEx)
	{
		name = inName;
		perm = inPerm;
		exTime = inExTime;
		ex = inEx;
	}
	
	public void setEx(ExceptionList inEx)
	{
		ex = inEx;
	}
	
	public long getExTime()
	{
		return exTime;
	}
	
	public ExceptionList getEx()
	{
		return ex;
	}

	public long getPerm()
	{
		return perm;
	}
	
	public String getName()
	{
		return name;
	}
}
