package SecureResServer.SecureResServer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ExceptionList.ExceptionList;


public class batonCompressionTest 
{

	ExceptionList lis;
	int len;
	@Before
	public void initExList()
	{
		ExceptionList tempLis = null;
		for(int i = 1; i < 5; i++)
		{
			lis = new ExceptionList(("n" + i), 0, 0, tempLis);
			tempLis = lis;
		}
		
		for(int i = 1; i < 5; i++)
		{
			lis = new ExceptionList(("n" + i), 0, 0, tempLis);
			tempLis = lis;
		}
		
		len = calculateLength(lis);
	}
	
	@Test
	public void compressList() 
	{
		batonCompression comp = new batonCompression(lis);
		ExceptionList compressedList = comp.compress();
		
		
		//assertEquals(len, 4);
		System.out.println(comp.getBatonSize(len));
		assertEquals(comp.getBatonSize(len), 4);
	}
	
	private int calculateLength(ExceptionList inLis)
	{
		int length = 0;
		while(inLis != null)
		{
			inLis = inLis.getEx();
			length++;
		}
		return length;
	}

}
