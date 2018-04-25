import java.io.File;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

public class Test {
  
  public static void main(String[] args) {
    HashMap <String , Object> defs = new HashMap<String , Object>();
    HashMap <Long , String> oneDef = new HashMap <Long , String>();
    ArrayList <Object> defObject = new ArrayList<Object>();
    
    try {
      Scanner input = new Scanner(System.in);
      File file = new File("test02.txt");
      input = new Scanner(file);
      String svertices = input.nextLine();
      int vertices = Integer.parseInt(svertices);
      System.out.println(vertices);
      int stateNo=0, permission=0;
      String s="";
      while (input.hasNextLine()) {
        oneDef = new HashMap <Long , String>();
        defObject = new ArrayList<Object>();
        //each n
        s=input.nextLine();
        System.out.println(s);
        String arr [] = s.split(" ");
        permission=0;
        for(int i=0 ; i<arr.length; i+=2){
          permission = Integer.parseInt(arr[i]);
          if(permission>0){
            String state= "n"+arr[i+1];
            System.out.println(permission+"->"+state);
            oneDef.put(Long.valueOf(permission), state);
            defObject.add(null);
            defObject.add(oneDef);
          }
        }
        if(permission==-2){
          System.out.println("breaking");
          break;
        }else{
          defs.put("n"+stateNo, defObject);
        }
      }
      int testcases = Integer.parseInt(input.nextLine());
      while(input.hasNextLine()){
        String [] exercisedPermissions=input.nextLine().split(" ");
        
        System.out.println(exercisedPermissions.toString());
      }
      input.close();
      
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
}