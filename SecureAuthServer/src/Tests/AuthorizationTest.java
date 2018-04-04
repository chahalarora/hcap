package Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import SecureAuthServer.SecureAuthServer.acHcapRes;

public class AuthorizationTest {
	
	acHcapRes obj = new acHcapRes("C:\\Users\\lakshya.tandon\\Desktop\\HCAP\\AuthFile.xml"); 

	/**
	 * Unit test for checkAuthrization method of acHcapRes.java class.
	 * 
	 */
	@Test
	public void test() {
		assertTrue(obj.checkAuthorization("Admin2"));
	}

}
