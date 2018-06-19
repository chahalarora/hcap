package SecureAdmin.SecureAdmin;

import java.io.File;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class jsonConverter 
{
	//HashMap<String, Object> map = null;
	/*public jsonConverter(HashMap<String, Object> inMap)
	{
		map = inMap;
	}*/
	
	public HashMap<String, Object> readJSONFile(File file)
	{
		HashMap<String, Object> retMap = null;
		try
		{
			JsonFactory jfac = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(jfac);
			retMap = mapper.readValue(file, new TypeReference<HashMap<String, Object>>(){ });
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return retMap;
	}
	
	public void saveJSONFile(HashMap<String, Object> inMap, String fileName)
	{ 
		try
		{
			File outFile = new File(fileName);
			JsonFactory cfac = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(cfac);
			mapper.writeValue(outFile, inMap);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public byte[] convertToJSON(HashMap<String, Object> map)
	{
		byte[] jsonData = null;
		try
		{
			JsonFactory jfac = new JsonFactory();
			ObjectMapper objectMapper = new ObjectMapper(jfac);
			//String jsonStr = objectMapper.writeValueAsString(map);
			jsonData = objectMapper.writeValueAsBytes(map);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return jsonData;
	}
	
	public HashMap<String, Object> convertFromJSON(byte[] inData)
	{
		HashMap<String, Object> retMap = null;
		try
		{
			JsonFactory jfac = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(jfac);
			retMap = mapper.readValue(inData, new TypeReference<HashMap<String, Object>>(){ });	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return retMap;
	}
}
