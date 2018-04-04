package SecureResServer.SecureResServer;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * This class provides access control to access special resources in HCAP (e.g.: shutdown resource)
 * 
 * @author lakshya.tandon
 *
 */
public class acHcapRes {
	
	private File xmlFile;
	
	/**
	 * Class constructor
	 * 
	 * 
	 * @param xmlFileLocation location of XML file which contains access control list.
	 */
	public acHcapRes(String xmlFileLocation) {
		xmlFile = new File(xmlFileLocation);
	}

	/**
	 * Method to use XML file and check if the requester is an administrator.
	 * 
	 * @param inName the name which is to be validated against.
	 * @return true if authorization check passes, false otherwise.
	 */
	public boolean checkAuthorization(String inName) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			
			doc.getDocumentElement().normalize();
			NodeList lis = doc.getElementsByTagName("administrator");
			
			for(int i = 0; i < lis.getLength(); i++)
			{
				Node n = lis.item(i);
				Element e = (Element) n;
				
				if(e.getElementsByTagName("name").item(0).getTextContent().equals(inName))
				{
					return true;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
}
