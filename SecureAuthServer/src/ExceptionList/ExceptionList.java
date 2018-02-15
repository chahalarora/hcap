package ExceptionList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ExceptionList implements Serializable 
{
	
	//This maps permission to the time when exception occurred.
	//HashMap<String, Long> exMap  = new HashMap<String, Long>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//ArrayList<Exception> exList = new ArrayList<Exception> ();
	long perm;
	long exTime;
	//long exceptionTime;
	ExceptionList ex = null;
	//ExceptionList exceptionList = null;
	//private String userID;
	private String name;
	 
	/*
	public ExceptionList()
	{
		
	}
	*/
	
	public ExceptionList(String inName, long inPerm, long inExTime, ExceptionList inEx)
	{
		name = inName;
		perm = inPerm;
		//exceptionTime = inExTime;
		//exceptionList = inEx;
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
	/*
	public long getExceptionTime()
	{
		return exceptionTime;
		//return ExTime;
	}
	*/
	/*
	public ExceptionList getExceptionList()
	{
		return exceptionList;
		//return ex;
	}
	*/
	
	public long getPerm()
	{
		return perm;
	}
	
	public String getName()
	{
		return name;
	}
}
