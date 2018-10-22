package SecureResServer.SecureResServer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ExceptionList.ExceptionList;
import java.util.HashMap;
import java.util.ArrayList;

public class batonCompressionTest 
{

	ExceptionList lis;
	int len;
	private HashMap<String , String> permNameMatching = new HashMap<String, String>(); 

	//initializing all the exceptionlists
	private ExceptionList lis1 = null;
	private ExceptionList lis2 = null;
	private ExceptionList lis3 = null;
	private ExceptionList lis4 = null;
	private ExceptionList lis5 = null;
	private ExceptionList lis6 = null;
	private ExceptionList lis7 = null;
	private ExceptionList lis8 = null;
	private ExceptionList lis9 = null;
	private ExceptionList lis10 = null;
	private HashMap<String , String> permNameMatching1 = new HashMap<String, String>(); 
	private HashMap<String , String> permNameMatching2 = new HashMap<String, String>(); 
	private HashMap<String , String> permNameMatching3 = new HashMap<String, String>(); 
	private HashMap<String , String> permNameMatching4 = new HashMap<String, String>(); 
	private HashMap<String , String> permNameMatching5 = new HashMap<String, String>(); 
	private HashMap<String , String> permNameMatching6 = new HashMap<String, String>(); 
	private HashMap<String , String> permNameMatching7 = new HashMap<String, String>(); 
	private HashMap<String , String> permNameMatching8 = new HashMap<String, String>(); 
	private HashMap<String , String> permNameMatching9 = new HashMap<String, String>(); 
	private HashMap<String , String> permNameMatching10 = new HashMap<String, String>();	
	

	@Before
	public void initExList()
	{
		
		//Start test 01 - Lakshs tests
		
		//Defining the defs - each def for each state
		
		HashMap <Long , String> oneDef1 = new HashMap <Long , String>(); //3-n1 12-n2
		HashMap <Long , String> oneDef2 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef3 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef4 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef5 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef6 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef7 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef8 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef9 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef10 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef11 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef12 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef13 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef14 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef15 = new HashMap <Long , String>();
		HashMap <Long , String> oneDef16 = new HashMap <Long , String>();

		//what each def will contain
		oneDef1.put(Long.valueOf(3), "n1"); //for n0
		oneDef1.put(Long.valueOf(12), "n5"); //for n0
		oneDef2.put(Long.valueOf(6), "n2"); //for n1
		oneDef3.put(Long.valueOf(8), "n3"); //for n2
		oneDef4.put(Long.valueOf(10), "n4"); //for n3
		oneDef5.put(Long.valueOf(16), "n1"); //for n4
		oneDef6.put(Long.valueOf(14), "n6"); //for n5
		oneDef7.put(null, null); //for n6
		
		//TOTAL DEF WILL BE IN THIS defs
		HashMap <String , Object> defs = new HashMap<String , Object>();
		
		//setting each def in the defs
		ArrayList <Object> defObject = new ArrayList<Object>();
		defObject.add(null); //no stationary permissions yet. 12th April- we are not testing the stationary permissions
		defObject.add(oneDef1);
		defs.put("n0", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef2);
		defs.put("n1", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef3);
		defs.put("n2", defObject);
		
		defObject = new ArrayList<Object>();		
		defObject.add(null);
		defObject.add(oneDef4);
		defs.put("n3", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef5);
		defs.put("n4", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef6);
		defs.put("n5", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef7);
		defs.put("n6", defObject);
		
		//setting the permission to name mapping here
		permNameMatching1 = getPermissionMapping(defs);
		
		//creating the initial list
		ExceptionList tempLis = null;
		
		lis1=new ExceptionList(Long.valueOf(3) , Long.valueOf(10) , tempLis, "n0");
		lis1=new ExceptionList(Long.valueOf(6) , Long.valueOf(20) , lis1, "n0");
		lis1=new ExceptionList(Long.valueOf(8) , Long.valueOf(30) , lis1, "n0");
		lis1=new ExceptionList(Long.valueOf(10) , Long.valueOf(40) , lis1, "n0");
		lis1=new ExceptionList(Long.valueOf(16) , Long.valueOf(50) , lis1, "n0");
		lis1=new ExceptionList(Long.valueOf(6) , Long.valueOf(60) , lis1, "n0");
		
		len = calculateLength(lis1);
		System.out.println("length of exception is :"+len);
		
		
		
		//TEST 02 STARTS HERE------------------ perm name matching 2
		
		//Defining the defs - each def for each state
				
		oneDef1 = new HashMap <Long , String>(); //3-n1 12-n2
		oneDef2 = new HashMap <Long , String>();
		oneDef3 = new HashMap <Long , String>();
		oneDef4 = new HashMap <Long , String>();
		oneDef5 = new HashMap <Long , String>();
		oneDef6 = new HashMap <Long , String>();
		oneDef7 = new HashMap <Long , String>();
		oneDef8 = new HashMap <Long , String>();
		oneDef9 = new HashMap <Long , String>();
		oneDef10 = new HashMap <Long , String>();
		oneDef11 = new HashMap <Long , String>();
		oneDef12 = new HashMap <Long , String>();
		oneDef13 = new HashMap <Long , String>();
		oneDef14 = new HashMap <Long , String>();
		oneDef15 = new HashMap <Long , String>();
		oneDef16 = new HashMap <Long , String>();

		//what each def will contain
		oneDef1.put(Long.valueOf(1), "n1"); //for n0
		oneDef1.put(Long.valueOf(2), "n2"); //for n0
		
		oneDef2.put(Long.valueOf(3), "n5"); //for n1
		oneDef2.put(Long.valueOf(5), "n3"); //for n1
		
		oneDef3.put(Long.valueOf(4), "n4"); //for n2
		
		oneDef4.put(Long.valueOf(8), "n6"); //for n3
		
		oneDef5.put(Long.valueOf(6), "n6"); //for n4
		oneDef5.put(Long.valueOf(7), "n3"); //for n4
		
		oneDef6.put(Long.valueOf(9), "n6"); //for n5
		
		oneDef7.put(Long.valueOf(10), "n7"); //for n6
		
		oneDef8.put(Long.valueOf(11), "n4"); //for n7
		oneDef8.put(Long.valueOf(12), "n5"); //for n7
		
		//TOTAL DEF WILL BE IN THIS defs
		defs = new HashMap<String , Object>();
		
		//setting each def in the defs
		defObject = new ArrayList<Object>();
		defObject.add(null); //no stationary permissions yet. 12th April- we are not testing the stationary permissions
		defObject.add(oneDef1);
		defs.put("n0", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef2);
		defs.put("n1", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef3);
		defs.put("n2", defObject);
		
		defObject = new ArrayList<Object>();		
		defObject.add(null);
		defObject.add(oneDef4);
		defs.put("n3", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef5);
		defs.put("n4", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef6);
		defs.put("n5", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef7);
		defs.put("n6", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef8);
		defs.put("n7", defObject);
		
		//setting the permission to name mapping here
		permNameMatching2 = getPermissionMapping(defs);
		
		//creating the initial list
		tempLis = null;
		
		lis2=new ExceptionList(Long.valueOf(1) , Long.valueOf(10) , tempLis, "n0");
		lis2=new ExceptionList(Long.valueOf(5) , Long.valueOf(20) , lis2, "n0");
		lis2=new ExceptionList(Long.valueOf(8) , Long.valueOf(30) , lis2, "n0");
		lis2=new ExceptionList(Long.valueOf(10) , Long.valueOf(40) , lis2, "n0");
		lis2=new ExceptionList(Long.valueOf(11) , Long.valueOf(50) , lis2, "n0");
		lis2=new ExceptionList(Long.valueOf(4) , Long.valueOf(60) , lis2, "n0");
		lis2=new ExceptionList(Long.valueOf(7) , Long.valueOf(70) , lis2, "n0");
		lis2=new ExceptionList(Long.valueOf(8) , Long.valueOf(80) , lis2, "n0");
		
		len = calculateLength(lis2);
		System.out.println("length of exception is :"+len);
		
		//Test -02 ends here
		
		//TEST -03 STARTS HERE WITH THE SAME  permNameMatching -02
		tempLis = null;
		
		lis3=new ExceptionList(Long.valueOf(1) , Long.valueOf(10) , tempLis, "n0");
		lis3=new ExceptionList(Long.valueOf(3) , Long.valueOf(20) , lis3, "n0");
		lis3=new ExceptionList(Long.valueOf(9) , Long.valueOf(30) , lis3, "n0");
		lis3=new ExceptionList(Long.valueOf(10) , Long.valueOf(40) , lis3, "n0");
		lis3=new ExceptionList(Long.valueOf(12) , Long.valueOf(50) , lis3, "n0");
		lis3=new ExceptionList(Long.valueOf(9) , Long.valueOf(60) , lis3, "n0");
		lis3=new ExceptionList(Long.valueOf(10) , Long.valueOf(70) , lis3, "n0");
		lis3=new ExceptionList(Long.valueOf(11) , Long.valueOf(80) , lis3, "n0");
		
		len = calculateLength(lis3);
		System.out.println("length of exception is :"+len);
		
		//TEST -03 ENDS HERE
		
		
		//TEST-04 STARTS
		tempLis = null;
		
		lis4=new ExceptionList(Long.valueOf(2) , Long.valueOf(10) , tempLis, "n0");
		lis4=new ExceptionList(Long.valueOf(4) , Long.valueOf(20) , lis4, "n0");
		lis4=new ExceptionList(Long.valueOf(7) , Long.valueOf(30) , lis4, "n0");
		lis4=new ExceptionList(Long.valueOf(8) , Long.valueOf(40) , lis4, "n0");
		lis4=new ExceptionList(Long.valueOf(10) , Long.valueOf(50) , lis4, "n0");
		lis4=new ExceptionList(Long.valueOf(11) , Long.valueOf(60) , lis4, "n0");
		lis4=new ExceptionList(Long.valueOf(6) , Long.valueOf(70) , lis4, "n0");
		lis4=new ExceptionList(Long.valueOf(10) , Long.valueOf(80) , lis4, "n0");
		lis4=new ExceptionList(Long.valueOf(12) , Long.valueOf(90) , lis4, "n0");
		
		len = calculateLength(lis4);
		System.out.println("length of exception lis4 is :"+len);
		
		//TEST -04 ENDS
		
		//PERMISSION NAME MATCHING - 03 HERE

		//Defining the defs - each def for each state
				
		oneDef1 = new HashMap <Long , String>();
		oneDef2 = new HashMap <Long , String>();
		oneDef3 = new HashMap <Long , String>();
		oneDef4 = new HashMap <Long , String>();
		oneDef5 = new HashMap <Long , String>();
		oneDef6 = new HashMap <Long , String>();
		oneDef7 = new HashMap <Long , String>();
		oneDef8 = new HashMap <Long , String>();
		oneDef9 = new HashMap <Long , String>();

		//what each def will contain
		oneDef1.put(Long.valueOf(1), "n1"); //for n0
		oneDef1.put(Long.valueOf(2), "n2"); //for n0
		oneDef1.put(Long.valueOf(3), "n3"); //for n0
		oneDef1.put(Long.valueOf(4), "n4"); //for n0
		oneDef1.put(Long.valueOf(5), "n5"); //for n0
		oneDef1.put(Long.valueOf(6), "n6"); //for n0
		oneDef1.put(Long.valueOf(7), "n7"); //for n0
		oneDef1.put(Long.valueOf(8), "n8"); //for n0
		
		oneDef2.put(Long.valueOf(12), "n2"); //for n1
		oneDef2.put(Long.valueOf(18), "n8"); //for n1
		
		oneDef3.put(Long.valueOf(23), "n3"); //for n2
		oneDef3.put(Long.valueOf(21), "n1"); //for n2
		
		oneDef4.put(Long.valueOf(34), "n4"); //for n3
		oneDef4.put(Long.valueOf(32), "n2"); //for n3
		
		oneDef5.put(Long.valueOf(45), "n5"); //for n4
		oneDef5.put(Long.valueOf(43), "n3"); //for n4
		
		oneDef6.put(Long.valueOf(56), "n6"); //for n5
		oneDef6.put(Long.valueOf(54), "n4"); //for n5
		
		oneDef7.put(Long.valueOf(67), "n7"); //for n6
		oneDef7.put(Long.valueOf(65), "n5"); //for n6
		
		oneDef8.put(Long.valueOf(78), "n8"); //for n7
		oneDef8.put(Long.valueOf(76), "n6"); //for n7
		
		oneDef9.put(Long.valueOf(81), "n1"); //for n8
		oneDef9.put(Long.valueOf(87), "n7"); //for n8
		
		//TOTAL DEF WILL BE IN THIS defs
		defs = new HashMap<String , Object>();
		
		//setting each def in the defs
		defObject = new ArrayList<Object>();
		defObject.add(null); //no stationary permissions yet. 12th April- we are not testing the stationary permissions
		defObject.add(oneDef1);
		defs.put("n0", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef2);
		defs.put("n1", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef3);
		defs.put("n2", defObject);
		
		defObject = new ArrayList<Object>();		
		defObject.add(null);
		defObject.add(oneDef4);
		defs.put("n3", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef5);
		defs.put("n4", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef6);
		defs.put("n5", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef7);
		defs.put("n6", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef8);
		defs.put("n7", defObject);
		
		defObject = new ArrayList<Object>();
		defObject.add(null);
		defObject.add(oneDef9);
		defs.put("n8", defObject);
		
		//setting the permission to name mapping here
		permNameMatching3 = getPermissionMapping(defs);
		//PERMISSION NAME MATCHING DONE FOR 03
		
		
		//TEST-05 STARTS
		tempLis = null;
		
		lis5=new ExceptionList(Long.valueOf(1) , Long.valueOf(10) , tempLis, "n0");
		lis5=new ExceptionList(Long.valueOf(12) , Long.valueOf(20) , lis5, "n0");
		lis5=new ExceptionList(Long.valueOf(23) , Long.valueOf(30) , lis5, "n0");
		lis5=new ExceptionList(Long.valueOf(34) , Long.valueOf(40) , lis5, "n0");
		lis5=new ExceptionList(Long.valueOf(45) , Long.valueOf(50) , lis5, "n0");
		lis5=new ExceptionList(Long.valueOf(56) , Long.valueOf(60) , lis5, "n0");
		lis5=new ExceptionList(Long.valueOf(67) , Long.valueOf(70) , lis5, "n0");
		lis5=new ExceptionList(Long.valueOf(78) , Long.valueOf(80) , lis5, "n0");
		lis5=new ExceptionList(Long.valueOf(81) , Long.valueOf(90) , lis5, "n0");
		lis5=new ExceptionList(Long.valueOf(18) , Long.valueOf(100) , lis5, "n0");
		
		len = calculateLength(lis5);
		System.out.println("length of exception lis5 is :"+len);
		
		//TEST -05 ENDS
		
		//TEST-06 STARTS
		tempLis = null;
		
		lis6=new ExceptionList(Long.valueOf(1) , Long.valueOf(10) , tempLis, "n0");
		lis6=new ExceptionList(Long.valueOf(12) , Long.valueOf(20) , lis6, "n0");
		lis6=new ExceptionList(Long.valueOf(21) , Long.valueOf(30) , lis6, "n0");
		lis6=new ExceptionList(Long.valueOf(12) , Long.valueOf(40) , lis6, "n0");
		lis6=new ExceptionList(Long.valueOf(21) , Long.valueOf(50) , lis6, "n0");
		lis6=new ExceptionList(Long.valueOf(12) , Long.valueOf(60) , lis6, "n0");
		
		len = calculateLength(lis6);
		System.out.println("length of exception lis6 is :"+len);
		
		//TEST -06 ENDS
		
		//TEST-07 STARTS
		tempLis = null;
		
		lis7=new ExceptionList(Long.valueOf(5) , Long.valueOf(10) , tempLis, "n0");
		lis7=new ExceptionList(Long.valueOf(54) , Long.valueOf(20) , lis7, "n0");
		lis7=new ExceptionList(Long.valueOf(45) , Long.valueOf(30) , lis7, "n0");
		lis7=new ExceptionList(Long.valueOf(56) , Long.valueOf(40) , lis7, "n0");
		lis7=new ExceptionList(Long.valueOf(67) , Long.valueOf(50) , lis7, "n0");
		lis7=new ExceptionList(Long.valueOf(76) , Long.valueOf(60) , lis7, "n0");
		lis7=new ExceptionList(Long.valueOf(67) , Long.valueOf(60) , lis7, "n0");
		lis7=new ExceptionList(Long.valueOf(78) , Long.valueOf(60) , lis7, "n0");
		
		len = calculateLength(lis7);
		System.out.println("length of exception lis7 is :"+len);
		
		//TEST -07 ENDS
	}
	
	/*@Test
	public void compressList_test01() 
	{
		
		batonCompression comp = new batonCompression(lis1 , permNameMatching1);
		//System.out.println(comp.getPermissionSequence(lis1).toString());
		//System.out.println(comp.getTimesSequence(lis1).toString());

		ExceptionList compressedList = comp.compressWithDefs();
		System.out.println("Previously: ");
		comp.printExceptionList(lis1);
		System.out.println("After Compression: ");
		comp.printExceptionList(compressedList);
		assertEquals("Test 1 fails.",comp.getBatonSize(compressedList), 2);
	}
	
	@Test
	public void compressList_test02() 
	{
		batonCompression comp = new batonCompression(lis2 , permNameMatching2);
		//ExceptionList compressedList = comp.compress();
		ExceptionList compressedList = comp.compressWithDefs();
		System.out.println("Previously: ");
		comp.printExceptionList(lis2);
		System.out.println("After Compression: ");
		comp.printExceptionList(compressedList);
		assertEquals("Test 2 fails.",comp.getBatonSize(compressedList), 3); 
	}
	
	@Test
	public void compressList_test03() 
	{
		batonCompression comp = new batonCompression(lis3 , permNameMatching2);
		//ExceptionList compressedList = comp.compress();
		ExceptionList compressedList = comp.compressWithDefs();
		System.out.println("Previously: ");
		comp.printExceptionList(lis3);
		System.out.println();
		System.out.println("After Compression: ");
		comp.printExceptionList(compressedList);
		assertEquals("Test 3 fails.",comp.getBatonSize(compressedList), 5); 
	}
	
	@Test
	public void compressList_test04() 
	{
		batonCompression comp = new batonCompression(lis4 , permNameMatching2);
		//ExceptionList compressedList = comp.compress();
		ExceptionList compressedList = comp.compressWithDefs();
		System.out.println("Previously: ");
		comp.printExceptionList(lis4);
		System.out.println();
		System.out.println("After Compression: ");
		comp.printExceptionList(compressedList);
		assertEquals("Test 4 fails.",comp.getBatonSize(compressedList), 5); 
	}
	
	@Test
	public void compressList_test05() 
	{
		batonCompression comp = new batonCompression(lis5 , permNameMatching3);
		//ExceptionList compressedList = comp.compress();
		ExceptionList compressedList = comp.compressWithDefs();
		System.out.println("Previously: ");
		comp.printExceptionList(lis5);
		System.out.println();
		System.out.println("After Compression: ");
		comp.printExceptionList(compressedList);
		assertEquals("Test 5 fails.", comp.getBatonSize(compressedList) , 2); 
	}
	
	@Test
	public void compressList_test06() 
	{
		batonCompression comp = new batonCompression(lis6 , permNameMatching3);
		//ExceptionList compressedList = comp.compress();
		ExceptionList compressedList = comp.compressWithDefs();
		System.out.println("Previously: ");
		comp.printExceptionList(lis6);
		System.out.println();
		System.out.println("After Compression: ");
		comp.printExceptionList(compressedList);
		assertEquals("Test 6 fails.", comp.getBatonSize(compressedList) , 2); 
	}*/
	
	@Test
	public void compressList_test07() 
	{
		batonCompression comp = new batonCompression(lis7 , permNameMatching3);
		//ExceptionList compressedList = comp.compress();
		ExceptionList compressedList = comp.compressWithDefs();
		System.out.println("Previously: ");
		comp.printExceptionList(lis7);
		System.out.println();
		System.out.println("After Compression: ");
		comp.printExceptionList(compressedList);
		assertEquals("Test 7 fails.", comp.getBatonSize(compressedList) , 6); 
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
	
	public HashMap<String , String> getPermissionMapping(HashMap <String, Object> defs) {
		HashMap<String, String> permNameMatching = new HashMap<String , String>();
		System.out.println("Defs: ");
		for (Object key : defs.keySet()) { //key of defs = n0,n1,n2 etc
			//System.out.print("Defs for state: "+defs.get(key) + "-->");
			ArrayList<Object> firstValue=(ArrayList<Object>) defs.get(key);
			HashMap <Long, String> oneDef = (HashMap <Long, String>)firstValue.get(1);
			//System.out.println(key + " " + defs.get(key)+"  -----  "+firstValue.get(1));
			for(Long keyDef : oneDef.keySet()) {
				//System.out.println(keyDef + " " + oneDef.get(keyDef));
				permNameMatching.put(keyDef+"", oneDef.get(keyDef)+"");
			}
			//System.out.println();
		    //System.out.println(key + " " + defs.get(key));
		}
		
		for(String nameMatchKey: permNameMatching.keySet()) {
			System.out.println("permission: " + nameMatchKey + ", state: " + permNameMatching.get(nameMatchKey)+ "--");
		}
		return permNameMatching;
	}
	
	@After
	public void afterDemo() {
		
	}

}
