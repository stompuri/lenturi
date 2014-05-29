package com.stompuri.lenturi.tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.*;

import com.stompuri.lenturi.Airport;
import com.stompuri.lenturi.AirportFile;

public class AirportFileTests {

    @BeforeClass
    public static void testSetup()
    {
    	// Initialize
    }

    @AfterClass
    public static void testCleanup()
    {
        // Teardown for data used by the unit tests
    }
    
	@Test
	public void testSavedata() {
		Airport[] airports = null;
		AirportFile airportsFile = new AirportFile("airports.txt");
		
		try {
		  airports = airportsFile.savedata();
		} catch (FileNotFoundException e) {
		  fail("No such airports file found!");
		}
		
		// Check the data in the first airport
		assertEquals("Airport data mismatch", "EFET", airports[0].waypoint);
		
	}

	@Test
	public void testSavedataExpection() {
		Airport[] airports = null;
		AirportFile airportsFile = new AirportFile("asdferq.txt");
		try {
			  airports = airportsFile.savedata();
			  fail("Must throw expection");
		} catch (FileNotFoundException e) {
			  // Works as expected, we got an expection
		}
	}
	
	@Test
	@Ignore
	public void testToString() {
		fail("Not yet implemented");
	}

}
