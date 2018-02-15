package SecureResServer.SecureResServer;

/**
 * This class is used to generate a pair of two values, the request code and resource ID.
 * 
 * @author lakshya.tandon
 *
 */
public class Pair extends Object
{
	//Code can be GET, POST, PUT, DELETE, in accordance with the resources defined by the administrator
	String code;
	Object resID;
	String strValue;
	
	/**
	 * Class constructor
	 * 
	 * @param inCode
	 * @param inResID
	 */
	public Pair(String inCode, Object inResID)
	{
		code = inCode.toUpperCase();
		resID = inResID;
		strValue = code + resID;		
	}
	
	/**
	 * This method returns code.
	 * 
	 * @return
	 */
	public String getCode()
	{
		return code;
	}
	
	/**
	 * This method resource ID.
	 * 
	 * @return
	 */
	public Object getResID()
	{
		return resID;
	}

	@Override
	public int hashCode()
	{
		return strValue.hashCode();
	}
	
	@Override
    public boolean equals(Object obj) 
	{
        if (obj == this)
            return true;
        if (!(obj instanceof Pair))
            return false;
        
        Pair p = (Pair) obj;
		String compareValue = p.getCode() + p.getResID();
		if(strValue.equals(compareValue))
		{
			return true;
		}
		return false;
    }
}
