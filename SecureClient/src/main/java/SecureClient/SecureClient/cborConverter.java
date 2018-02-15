package SecureClient.SecureClient;

import java.io.File;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

public class cborConverter 
{
	//HashMap<String, Object> map = null;
	
	/*public cborConverter()
	{
		
	}
	
	public cborConverter(HashMap<String, Object> inMap)
	{
		map = inMap;
	}*/
	
	public HashMap<String, Object> readCBORFile(File file)
	{
		HashMap<String, Object> retMap = null;
		try
		{
			CBORFactory cfac = new CBORFactory();
			ObjectMapper mapper = new ObjectMapper(cfac);
			retMap = mapper.readValue(file, new TypeReference<HashMap<String, Object>>(){ });
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return retMap;
	}
	
	public void saveCBORFile(HashMap<String, Object> inMap, String fileName)
	{ 
		try
		{
			File outFile = new File(fileName);
			CBORFactory cfac = new CBORFactory();
			ObjectMapper mapper = new ObjectMapper(cfac);
			mapper.writeValue(outFile, inMap);
			System.out.println("Value written.");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public byte[] convertToCBOR(HashMap<String, Object> map)
	{
		byte[] cborData = null;
		
		try
		{
			CBORFactory cfac = new CBORFactory();
			ObjectMapper mapper = new ObjectMapper(cfac);
			cborData = mapper.writeValueAsBytes(map);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return cborData;
	}
	
	public HashMap<String, Object> convertFromCBOR(byte[] inData)
	{
		HashMap<String, Object> retMap = null;
		try
		{
			CBORFactory cfac = new CBORFactory();
			ObjectMapper mapper = new ObjectMapper(cfac);
			retMap = mapper.readValue(inData, new TypeReference<HashMap<String, Object>>(){ });	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return retMap;
	}
}