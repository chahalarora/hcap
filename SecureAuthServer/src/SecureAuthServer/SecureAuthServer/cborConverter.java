package SecureAuthServer.SecureAuthServer;

import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
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
	
	/**
	 * This methods decodes CBOR byte to a map.
	 * 
	 * @param inData
	 * @return map
	 */
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