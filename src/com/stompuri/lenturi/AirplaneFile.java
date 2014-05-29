package com.stompuri.lenturi;
import java.io.*;
import java.util.*;

// AirplaneFile -class for handling Airplanes
// Includes methods for handling input from the file
public class AirplaneFile {
  private File workFile;
  
  // CONSTRUCTOR
  public AirplaneFile(String fileName) {
    this.workFile = new File(fileName);
  }
  
  // PUBLIC METHODS 
  
  // savedata method reads the data from all of the airports from the file
  // returns Airport table with all the airports in it
  public Airplane[] savedata() throws FileNotFoundException {
    String type = "_";      // type of the airplane (name of the type, as text)
    int fuelType = 0;     // type of fuel (1 = JET, 2 = AVGAS)
    int fuelCapacity = 0; // how much fuel the plane can carry (liter)
    int fuelConsume = 0;  // how much fuel the plane consumes (liter per hour)
    int speed = 0;        // speed of the airplane (knots)
    int range = 0;        // how long the plane can fly with single tank of fuel
    int takeOffDist = 0;  // how long runway needed for take off (meters)
    int landingDist = 0;  // how long runway needed for landing (meters)
    
    Airplane[] airplanes = new Airplane[countColumns()];
    for(int i=0; i<airplanes.length; i++) {
      airplanes[i] = new Airplane(type, fuelType, fuelCapacity, fuelConsume, speed, range, takeOffDist, landingDist);
    }
    int rowNumber = 0;
    Scanner fileScanner = new Scanner(this.workFile);
    while (fileScanner.hasNextLine()) {
      String workRow = fileScanner.nextLine();
//System.out.println("Workrow = "+workRow);
      // Read the data from the line
      int i = 0, start = 0, end = 0;
      while(end < workRow.length()) {
        start = workRow.indexOf("\t", end) + 1;
        end = workRow.indexOf("\t" , start);
        //if(start == end) end++;

        if(end <= 0)
          end = workRow.length();
        String item = (workRow.substring(start, end)).replace("\t", "");
//System.out.println("row="+rowNumber+" i="+i+" start="+start+" end=" + end + " item = "+item+":");
        if(rowNumber == 0) {
          airplanes[i].setType(item);
        } else if(rowNumber == 1) {
//System.out.println(airplanes[i] + " Fuel type = " + Integer.valueOf(item));
          airplanes[i].setFuelType(Integer.valueOf(item));
        } else if(rowNumber == 2) {
//System.out.println(airplanes[i] + " Fuel capacity = " + Integer.valueOf(item));
          airplanes[i].setFuelCapacity(Integer.valueOf(item));
        } else if(rowNumber == 3) {
//System.out.println(airplanes[i] + " fuel consume = " + Integer.valueOf(item));
          airplanes[i].setFuelConsume(Integer.valueOf(item));
//System.out.println("=="+airplanes[i].getFuelConsume());
        } else if(rowNumber == 4) {
//System.out.println(airplanes[i] + " speed = " + Integer.valueOf(item));
          airplanes[i].setSpeed(Integer.valueOf(item));
        } else if(rowNumber == 5) {
//System.out.println(airplanes[i] + " speed = " + Integer.valueOf(item));
          airplanes[i].setRange(Integer.valueOf(item));
        } else if(rowNumber == 6) {
//System.out.println(airplanes[i] + " speed = " + Integer.valueOf(item));
          airplanes[i].setTakeOffDist(Integer.valueOf(item));
        } else if(rowNumber == 7) {
//System.out.println(airplanes[i] + " speed = " + Integer.valueOf(item));
          airplanes[i].setLandingDist(Integer.valueOf(item));
        }
        
        i++;
      }
      rowNumber++;
    }
    
    fileScanner.close();
    return airplanes;
  }

  public String toString() {
    return this.workFile.getName();
  }

  private int countColumns() throws FileNotFoundException{
    int rowcount = 0, start = 0, end = 0;
    Scanner fileScanner = new Scanner(this.workFile);
    String workRow = fileScanner.nextLine();
    while(end < workRow.length()) {
      start = workRow.indexOf("\t", end) + 1;
      end = workRow.indexOf("\t" , start) - 1;
      if(end <= 0)
        end = workRow.length();
      rowcount++;
    }
    fileScanner.close();
    
    return rowcount;
  }
}
