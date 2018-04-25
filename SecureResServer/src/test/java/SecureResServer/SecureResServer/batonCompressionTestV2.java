package SecureResServer.SecureResServer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ExceptionList.ExceptionList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class batonCompressionTestV2
{

	ExceptionList lis;
	int len;
	private HashMap<String , String> permNameMatching = new HashMap<String, String>(); 

	@Before
	public void initExList()
	{
		HashMap <String , Object> defs = new HashMap<String , Object>();
	    HashMap <Long , String> oneDef = new HashMap <Long , String>();
	    ArrayList <Object> defObject = new ArrayList<Object>();
	    
	    try {
	      Scanner input = new Scanner(System.in);
	      File file = new File("D:\\uCalgary\\HCAPv1\\HCAPCode\\SecureResServer\\src\\test\\java\\SecureResServer\\SecureResServer\\test02.txt");
	      input = new Scanner(file);
	      String svertices = input.nextLine();
	      int vertices = Integer.parseInt(svertices);
	      //System.out.println(vertices);
	      int stateNo=0, permission=0;
	      String s="";
	      while (input.hasNextLine()) {
	        oneDef = new HashMap <Long , String>();
	        defObject = new ArrayList<Object>();
	        //each n
	        s=input.nextLine();
	        //System.out.println(s);
	        String arr [] = s.split(" ");
	        permission=0;
	        for(int i=0 ; i<arr.length; i+=2){
	          permission = Integer.parseInt(arr[i]);
	          if(permission>0){
	        	 // System.out.println("Enters for: "+permission);
	            String state= "n"+arr[i+1];
	            //System.out.println(permission+"->"+state);
	            oneDef.put(Long.valueOf(permission), state);
	            defObject.add(null);
	            defObject.add(oneDef);
	          }
	        }
	        if(permission==-2){
	          //System.out.println("breaking");
	          break;
	        }else if(permission!=-1){
	        	//System.out.println("Enters for: n"+stateNo);
	          defs.put("n"+stateNo, defObject);
	          
	        }
	        stateNo++;
	      }
	      input.close();
	      
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }
		
		getPermissionMapping(defs);
		
		ExceptionList tempLis = null;
		
		lis=new ExceptionList(Long.valueOf(1) , Long.valueOf(10) , tempLis, "n0");
		lis=new ExceptionList(Long.valueOf(3) , Long.valueOf(20) , lis, "n0");
		lis=new ExceptionList(Long.valueOf(11) , Long.valueOf(30) , lis, "n0");
		lis=new ExceptionList(Long.valueOf(12) , Long.valueOf(40) , lis, "n0");
		lis=new ExceptionList(Long.valueOf(5) , Long.valueOf(50) , lis, "n0");
		
		len = calculateLength(lis);
		System.out.println("length of exception is :"+len);
	}
	
	@Test
	public void compressList() 
	{
		batonCompression comp = new batonCompression(lis , permNameMatching);
		ExceptionList compressedList = comp.compressWithDefs();
		assertEquals("Test 1 fails.",comp.getBatonSize(compressedList), 3);
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
	
	public void getPermissionMapping(HashMap <String, Object> defs) {
		System.out.println("Defs: ");
		for (Object key : defs.keySet()) { //key of defs = n0,n1,n2 etc
			//System.out.print("Defs for state: "+defs.get(key) + "-->");
			ArrayList<Object> firstValue=(ArrayList<Object>) defs.get(key);
			HashMap <Long, String> oneDef = (HashMap <Long, String>)firstValue.get(1);
			System.out.println(key + " " + defs.get(key)+"  -----  "+firstValue.get(1));
			for(Long keyDef : oneDef.keySet()) {
				System.out.println(keyDef + " " + oneDef.get(keyDef));
				permNameMatching.put(keyDef+"", oneDef.get(keyDef)+"");
			}
			//System.out.println();
		    //System.out.println(key + " " + defs.get(key));
		}
		
		for(String nameMatchKey: permNameMatching.keySet()) {
			System.out.print("permission: " + nameMatchKey + ", state: " + permNameMatching.get(nameMatchKey)+ "--");
		}
	}
	
	@After
	public void afterDemo() {
		
	}

}
