package SecureResServer.SecureResServer;

import java.util.HashMap;

import ExceptionList.ExceptionList;

public class TestConversions 
{
	
	public static void main(String[] args)
	{
		
		ExceptionList ex  = new ExceptionList(null, 0, 100, null);
		ExceptionList exNew  = new ExceptionList(null, 0, 200, ex);
		
		HashMap<String, Object> exList = new HashMap<String, Object>();
		
		exList.put("exceptionList", exNew);
		
		cborConverter cCon = new cborConverter();
		byte[] data = cCon.convertToCBOR(exList);
		
		
		HashMap<String, Object> retMap = cCon.convertFromCBOR(data);
		HashMap<String, Object> test = (HashMap<String, Object>) retMap.get("exceptionList");
		
		HashMap<String, Object> ExList = (HashMap<String, Object>) test.get("ex");
		
		//HashMap<String, Object> retMapNew = cCon.convertFromCBORExList(data);
		
		//ExceptionList newEx = (ExceptionList) retMapNew.get("exceptionList");
		
		//System.out.println(retMap);
		
	}

}
