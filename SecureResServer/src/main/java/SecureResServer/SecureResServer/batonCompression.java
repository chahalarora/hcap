package SecureResServer.SecureResServer;

import java.util.HashMap;

import ExceptionList.ExceptionList;

import java.util.ArrayList;
import java.util.List;

public class batonCompression 
{
	private ExceptionList ex;
	private int batonReduction;
	
	private ExceptionList tempExList;
	
	private ArrayList <String> names = new ArrayList<String>();
	private HashMap<String , String> permNameMatching = new HashMap<String, String>(); 
	private long [] times;
	
	/**
	 * Baton compression constructor, expects exception list to be compressed.
	 * 
	 * @param inEx
	 */
	public batonCompression(ExceptionList inEx)
	{
		ex = inEx;
		//calculate the names here make an arraylist of names as string and calculate it here
		//the exception list has the defs and recreate it from there.
	}
	
	/**
	 * Baton compression constructor, expects exception list AND defs to be passed.
	 * 
	 * @param inEx
	 */
	public batonCompression(ExceptionList inEx, HashMap<String , String> permNameMatching)
	{
		ex = inEx;
		this.permNameMatching = permNameMatching;
		//calculate the names here make an arraylist of names as string and calculate it here
		//the exception list has the defs and recreate it from there.
	}
	
	/**
	 * Method used to compress baton (exception list).
	 * 
	 * @return ExceptionList (compressed)
	 */
	
	public ExceptionList compress()
	{
		//ArrayList<ExceptionList> exStore = new ArrayList<ExceptionList>();
		
		while(ex.getEx() != null)
		{
			String name = ex.getName(); //sh - this will not come from ex.getName() - this will come from something calculated here in the constructor
			
			if(lisStore.containsKey(name))
			{
				//trim exceptionList
				ExceptionList returnEx = lisStore.get(name);
				returnEx.setEx(ex.getEx());
				return returnEx;
			}
			else
			{
				lisStore.put(name, ex);
				ex = ex.getEx();
				batonReduction++;
			}
		}
		return null;
	}
	
	/**
	 * Method used to compress baton (exception list).
	 * 
	 * @return ExceptionList (compressed)
	 */
	
	public ExceptionList compressWithDefs()
	{
		System.out.println("Compression Starts..");
		HashMap<String, ExceptionList> lisStore = new HashMap<String, ExceptionList>();
		String initState=ex.getInitialState();
		ArrayList<Long> permSequence = getPermissionSequence(ex); //3,6,8,10,16,6, --> 3,6 ...
		ArrayList<Long> timeSequence = getTimesSequence(ex); 
		System.out.println(permSequence.toString()); 
		ArrayList<String> statesTraversed = getStatesTraversed(permSequence,initState);//[n1,n2,n3,n4,n1,n2*] --> [n1,n2]
		System.out.println("States traversed = "+statesTraversed.toString());
		int positionofduplicate=-1,positionof2ndduplicate=-1;
		
		for(int i=0 ; i< statesTraversed.size(); i++) {
			List<String> sublist = statesTraversed.subList(i+1, statesTraversed.size()-1);
			if(sublist.contains(statesTraversed.get(i))) {
				positionofduplicate=i;
				//positionof2ndduplicate=(2+i+sublist.indexOf(statesTraversed.get(i)));
				positionof2ndduplicate=(2+i+getLastOccurance(sublist ,statesTraversed.get(i)));
				System.out.println("Duplicate = "+positionofduplicate+" second duplicate= "+positionof2ndduplicate);
				break;
			}
		}
		
		System.out.println("New exceptionList: -----");
		ExceptionList newExList = null; /*getNewExceptionList(ex,positionofduplicate,positionof2ndduplicate,null,statesTraversed.size()-1);*/
		try {
		for(int i=0;i<=positionofduplicate;i++) {
			newExList = new ExceptionList(permSequence.get(i), timeSequence.get(i) , newExList , "n0");
		}
		System.out.println("Here!! ");
		for(int i=positionof2ndduplicate ; i<=permSequence.size()-1 ; i++) {
			newExList = new ExceptionList(permSequence.get(i), timeSequence.get(i) , newExList , "n0");
		}
		//System.out.println("Here 2 !! ");
		//getPermissionSequence(newExList);
		//System.out.println("Here 3 !! "+ newExList.getExTime());
		}catch(Exception e) {
			System.out.println(e);
		}
		return newExList;
	}
	
	public ExceptionList getNewExceptionList(ExceptionList recurExceptionList, int firstPosition, int secondPosition, ExceptionList saved, int currentPosition) {
		//System.out.println(currentPosition + " - "+ secondPosition);
		if(currentPosition==secondPosition) {
			saved = new ExceptionList(recurExceptionList.getPerm(),recurExceptionList.getExTime(),saved,recurExceptionList.getInitialState());
			return getNewExceptionList(recurExceptionList.getEx(), firstPosition, secondPosition, saved, --currentPosition);
		}
		if(currentPosition == firstPosition) {
			saved.setEx(recurExceptionList);
			return saved;
		}else {
			saved=recurExceptionList;
			return getNewExceptionList(recurExceptionList.getEx(), firstPosition, secondPosition, saved, --currentPosition);
		}
	}
	
	public static int getLastOccurance(List<String> al , String searchTerm){
	    for(int i=al.size()-1 ; i>=0 ; i--){
	      if(al.get(i).equals(searchTerm)){
	        return i;
	      }
	    }
	    return -1;
	  }
	
	/**
	 * Method to calculate baton size.
	 * 
	 * 
	 * @param originalBatonSize
	 * @return
	 */
	public int getBatonSize(ExceptionList recurExceptionList)
	{
		if(recurExceptionList == null) {
			return 0;
		}else {
			return 1+getBatonSize(recurExceptionList.getEx());
		}
	}
	
	/*public String getPermissionSequence(ExceptionList recurExceptionList) {
		String pSeq = "";
		if(recurExceptionList == null) {
			return pSeq;
		}else {
			pSeq = pSeq+getPermissionSequence(recurExceptionList.getEx()).toString();
			//System.out.println(recurExceptionList.getPerm()+" time = "+ recurExceptionList.getExTime());
			pSeq+=recurExceptionList.getPerm()+",";
			return pSeq;
		}
	}*/
	
	public ArrayList<Long> getPermissionSequence(ExceptionList recurExceptionList) {
		ArrayList<Long> lis = new ArrayList<Long>();
		if(recurExceptionList.getEx() == null) {
			lis.add(recurExceptionList.getPerm());
			return lis;
		}else {
				
			lis=getPermissionSequence(recurExceptionList.getEx());
			lis.add(recurExceptionList.getPerm());
			return lis;
			//pSeq = pSeq+getPermissionSequence(recurExceptionList.getEx()).toString();
			//System.out.println(recurExceptionList.getPerm()+" time = "+ recurExceptionList.getExTime());
			//pSeq+=recurExceptionList.getPerm()+",";
			//return pSeq;
		}
	}
	
	public ArrayList<Long> getTimesSequence(ExceptionList recurExceptionList) {
		ArrayList<Long> lis = new ArrayList<Long>();
		if(recurExceptionList.getEx() == null) {
			lis.add(recurExceptionList.getExTime());
			return lis;
		}else {
				
			lis=getTimesSequence(recurExceptionList.getEx());
			lis.add(recurExceptionList.getExTime());
			return lis;
			//pSeq = pSeq+getPermissionSequence(recurExceptionList.getEx()).toString();
			//System.out.println(recurExceptionList.getPerm()+" time = "+ recurExceptionList.getExTime());
			//pSeq+=recurExceptionList.getPerm()+",";
			//return pSeq;
		}
	}
	

	
	public ArrayList<String> getStatesTraversed(ArrayList <Long> pSeq, String initState){
		//ExceptionList newExceptionList = null;
		ArrayList <String> states = new ArrayList<String>();
		//String [] pSeqArr = pSeq;
		//String currentState="";
		for(int i=0;i<pSeq.size();i++) {
			if(permNameMatching.containsKey(pSeq.get(i)+"")) {
				//System.out.println(pSeq[i]+" goes to: "+permNameMatching.get(pSeq[i])+",");
				states.add(permNameMatching.get(pSeq.get(i)+""));
			}
		}
		return states;
	}
	
	
	public void printExceptionList(ExceptionList e) {
		
		if(e.getEx() == null) {
			System.out.print(e.getPerm()+"("+e.getExTime()+")"+"->");
			return;
		}
		else {
			printExceptionList(e.getEx());
			System.out.print(e.getPerm()+"("+e.getExTime()+")"+"->");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
