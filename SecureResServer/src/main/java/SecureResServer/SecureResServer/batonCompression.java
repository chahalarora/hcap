package SecureResServer.SecureResServer;

import java.util.HashMap;
import java.util.Map;

import ExceptionList.ExceptionList;

public class batonCompression 
{
	private ExceptionList ex;
	private int batonReduction;
	
	private ExceptionList tempExList;
	
	
	/**
	 * Baton compression constructor, expects exception list to be compressed.
	 * 
	 * @param inEx
	 */
	public batonCompression(ExceptionList inEx)
	{
		ex = inEx;
		tempExList = inEx;
	}
	
	/**
	 * Method used to compress baton (exception list).
	 * 
	 * @return ExceptionList (compressed)
	 */
	
	public ExceptionList compress()
	{
		//ArrayList<ExceptionList> exStore = new ArrayList<ExceptionList>();
		Map<String, ExceptionList> lisStore = new HashMap<String, ExceptionList>();
		
		while(ex.getEx() != null)
		{
			String name = ex.getName();
			
			if(lisStore.containsKey(name))
			{
				//trim exceptionList
				ExceptionList returnEx = lisStore.get(name);
				returnEx.setEx(ex.getEx());
				return returnEx;
			}
			else
			{
				lisStore.put(name, ex);
				ex = ex.getEx();
				batonReduction++;
			}
		}
		return null;
	}
	
	/**
	 * Method to calculate baton size.
	 * 
	 * 
	 * @param originalBatonSize
	 * @return
	 */
	public int getBatonSize(int originalBatonSize)
	{
		return originalBatonSize - batonReduction;
	}
}
