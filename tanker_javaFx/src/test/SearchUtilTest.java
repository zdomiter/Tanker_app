package test;

import org.junit.Assert;
import org.junit.Test;

import application.controller.MainFrameController;
import application.util.SearchUtil;

public class SearchUtilTest {
	
	@Test
	public void testGetMachineIdByLicensePlate() {
		SearchUtil srcObj = new SearchUtil();
		int actualId = srcObj.getMachineIdByLicensePlate(MainFrameController.getMachines(), "PPP-533");
		int expectedId = 12;
		Assert.assertEquals(expectedId, actualId);
		
	}

}
