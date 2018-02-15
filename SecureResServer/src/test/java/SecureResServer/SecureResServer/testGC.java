package SecureResServer.SecureResServer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ExceptionList.ExceptionList;

public class testGC 
{
	
	@Before
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
	
	@Test
	public void test() 
	{
		updateExList update = new updateExList(HCAPResourceServer.lisMap, false);
		update.sendRequest();
	}
}
