package SecureResServer.SecureResServer;

import java.util.HashMap;

import ExceptionList.ExceptionList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

/**
 * This class is used for encoding some data as CBOR and vice versa. 
 * 
 * @author lakshya.tandon
 *
 */
public class cborConverter 
{
	/**
	 * This method encodes a map as CBOR byte array.
	 * 
	 * @param map
	 * @return byte array
	 */
	{
		byte[] cborData = null;
		try
		{
			CBORFactory cfac = new CBORFactory();
			ObjectMapper mapper = new ObjectMapper(cfac);
			//System.out.println("cbor converter4: " + System.currentTimeMillis());
			cborData = mapper.writeValueAsBytes(map);
			//System.out.println("cbor converter5: " + System.currentTimeMillis());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return cborData;
	}
	
	/**
	 * This methods decodes CBOR byte to a map.
	 * 
	 * @param inData
	 * @return map
	 */
	{
		try
		{
			CBORFactory cfac = new CBORFactory();
			//System.out.println("cbor converter1: " + System.currentTimeMillis());
			ObjectMapper mapper = new ObjectMapper(cfac);
			//System.out.println("cbor converter2: " + System.currentTimeMillis());
			retMap = mapper.readValue(inData, new TypeReference<HashMap<String, Object>>(){ });
			//retMap = mapper.readValue(inData, tf);
			//System.out.println("cbor converter3: " + System.currentTimeMillis());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return retMap;
	}
	
	
}