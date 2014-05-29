package com.stompuri.lenturi;
import java.io.*;
import java.util.*;

// AirportFile -class for handling AirportFiles
// Includes methods for handling input from the file
// This AirportFile -class is modified to fit the needs of "Lenturi" program
public class AirportFile {
  private File workFile;
  
  // CONSTRUCTOR
  public AirportFile(String fileName) {
    this.workFile = new File(fileName);
  }
  
  // PUBLIC METHODS 
  
  // savedata method reads the data from all of the airports from the file
  // returns Airport table with all the airports in it
  public Airport[] savedata() throws FileNotFoundException {
    int latitude = 0, longitude = 0;
    String waypoint = "";
    int runwaylength = 0, runwaywidth = 0;
    int fueltype = 0, seasonticket = 0, groundtime = 0;
    boolean controlled;
    
    Airport[] airports = new Airport[countrows()-2]; // -2, as there's two extrarows in the txt-file
    int rownumber = 0;
    Scanner fileScanner = new Scanner(this.workFile);

    while (fileScanner.hasNextLine()) {
      String workRow = fileScanner.nextLine();

      if(workRow.indexOf("*") < 0) {      
        
        // Read CONTROLLED
        if(workRow.charAt(0) == '1')
          controlled = true;
        else controlled = false;
        
        // Read WAYPOINT
        int place_start = workRow.indexOf("\t") + 1;
        int place_end = workRow.indexOf("\t", place_start);
        waypoint = workRow.substring(place_start, place_end);
        
        // Read LATITUDE
        place_start = workRow.indexOf("\t", place_start) + 1;
        place_end = workRow.indexOf("\t", place_start);
        if(place_start < place_end) {
          // If there's a comma character in Latitude, remove it:
          if((workRow.substring(place_start, place_end)).indexOf(",") >= 0)
            place_end = workRow.indexOf(",", place_start);
          latitude = Integer.valueOf(workRow.substring(place_start, place_end));
        } else latitude = 0;
        
        // Read LONGITUDE
        place_start = workRow.indexOf("\t", place_start) + 1;
        place_end = workRow.indexOf("\t", place_start);
        if(place_start < place_end) {
          // If there's a comma character in Longitude, remove it:
          if((workRow.substring(place_start, place_end)).indexOf(",") >= 0)
            place_end = workRow.indexOf(",", place_start);
          longitude = Integer.valueOf(workRow.substring(place_start, place_end));
        } else longitude = 0;
        
        // Read RUNWAY_LENGTH
        place_start = workRow.indexOf("\t", place_start) + 1;
        place_end = workRow.indexOf("x", place_start) - 1;
        if(place_start < place_end) {
          runwaylength = Integer.valueOf(workRow.substring(place_start, place_end));
          
          // Read RUNWAY_WIDTH
          place_start = workRow.indexOf("x", place_start) + 2;
          place_end = workRow.indexOf("m", place_start) - 1;
          if(place_start < place_end)
            runwaywidth = Integer.valueOf(workRow.substring(place_start, place_end));
        } else { runwaylength = 0; runwaywidth = 0; }
        
        // Read FUELTYPE
        place_start = workRow.indexOf("\t", place_start) + 1;
        place_end = workRow.indexOf("\t", place_start);
        if(place_start < place_end)
          fueltype = Integer.valueOf(workRow.substring(place_start, place_end));
        else fueltype = 0;
        
        // Read SEASONTICKET
        place_start = workRow.indexOf("\t", place_start) + 1;
        place_end = workRow.indexOf("\t", place_start);
        if(place_start < place_end)
          seasonticket = Integer.valueOf(workRow.substring(place_start, place_end));
        else seasonticket = 0;
        
        // Read GROUNDTIME
        place_start = workRow.indexOf("\t", place_start) + 1;
        place_end = workRow.length();
        if(place_start < place_end)
          groundtime = Integer.valueOf(workRow.substring(place_start, place_end));
        else groundtime = 0;
        
        //System.out.println("C:" + controlled + " W=" + waypoint + " Lat: " + latitude + " Lon: " + longitude);
        //System.out.println("RL:" + runwayLength + " RW=" + runwayWidth + " F:" + fueltype + " S:" + seasonticket + " G:" + groundtime);
        
        airports[rownumber] = new Airport(latitude, longitude, waypoint, runwaylength, runwaywidth, fueltype, seasonticket, groundtime, controlled);
        rownumber++;
      }
    }
    
    fileScanner.close();
    return airports;
  }

  public String toString() {
    return this.workFile.getName();
  }

  private int countrows() throws FileNotFoundException{
    int rowcount = 0;
    
    Scanner fileScanner = new Scanner(this.workFile);
    while (fileScanner.hasNextLine()) {
      fileScanner.nextLine();
      rowcount++; 
    }
    fileScanner.close();
    
    return rowcount;
  }
}
