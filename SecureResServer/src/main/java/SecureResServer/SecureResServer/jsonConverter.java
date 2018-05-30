package SecureResServer.SecureResServer;

import java.util.HashMap;
import java.util.Map;

import ExceptionList.ExceptionList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is used for encoding some data as CBOR and vice versa. 
 * 
 * @author lakshya.tandon
 *
 */

public class jsonConverter 
{
	/**
	 * This method encodes a map as JSON byte array.
	 * 
	 * @param map
	 * @return byte array
	 */
	public byte[] convertToJSON(Map<String, Object> map)
	{
		byte[] jsonData = null;
		try
		{
			JsonFactory jfac = new JsonFactory();
			ObjectMapper objectMapper = new ObjectMapper(jfac);
			jsonData = objectMapper.writeValueAsBytes(map);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return jsonData;
	}
	
	/**
	 * This methods decodes JSON byte to a map.
	 * 
	 * @param inData
	 * @return map
	 */
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
	
	/*
	public HashMap<String, Object> convertFromJSONExList(byte[] inData)
	{
		HashMap<String, Object> retMap = null;
		try
		{
			JsonFactory jfac = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(jfac);
			retMap = mapper.readValue(inData, new TypeReference<HashMap<String, ExceptionList>>(){ });	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return retMap;
	}
	*/
}
