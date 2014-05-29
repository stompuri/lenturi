package com.stompuri.lenturi;
// ***********************************************
// * Lenturi v0.8 - Seppo Tompuri - 09.07.2010   *
// *                                             *
// * Program to solve flight routes between      *
// * airports.                                   *
// ***********************************************

import java.io.*;

public class Lenturi {
  boolean debugmode = false;
  
  // Global table for all the airports
  public Airport[] airports;
  public Airport[] airportsIn;
  private int startPort;
  private Approximator routes;
  public double totalDistance;

  // CONSTURCTOR
  public Lenturi(Airport[] airports) {
    this.airports = airports;
    this.airportsIn = null;
    this.startPort = 0;
    this.totalDistance = 0.0;
    // Clear the next values from all the airports
    clearNexts();
    
    // Initialize the approximators class for the airport network
    this.routes = null;
  }
  
//----------------------------------------------------------------------------
// PUBLIC METHODS
//----------------------------------------------------------------------------
  public void go(String[] args) {
    this.routes = new Approximator(this.airportsIn);
    // Rough test, is there a correct amount of arguments
    if(args.length != 1) {
      System.out.println("Bad argument number!");
      return;
    }

    this.startPort = 0;
    
    int argument = checkArguments(args, airports[startPort]);
    switch(argument) {
      case -1:
        System.out.println("Bad argument error!");
        break;
      case 1: // ALL THE AIRPORTS IN THE AIRPORT FILE

        // Clear the next value for all the aiports
        clearNexts();
        
        this.airports = this.routes.approximator();
        if(debugmode) System.out.println("----APPROXIMATOR----");
        
        if(this.airports.length > 1)
          this.totalDistance = this.routes.countDistance();
        else
          this.totalDistance = 0;
        this.routes.printRoute();
        break;
      case 2: // EXPORT TO GOOGLE EARTH KML FILE
        // Create a .kml file for google earth, displaying the airports & route
        Exporter fileExport = new Exporter("KML_export");
        try {
          fileExport.savefile(this.airportsIn, this.startPort);
        } catch (FileNotFoundException e) {
          System.out.println("No such file!");
          return;
        }
        break;
        
      case 4: // CALCULATE ONE WAY ROUTE
        // Clear the next value for all the aiports
        clearNexts();
        //this.airports = this.routes.oneWayFinder();
        this.airports = this.routes.oneWayFinder();
        if(debugmode) System.out.println("----ONE WAY ROUTE----");
        
        if(this.airports.length > 1)
          this.totalDistance = this.routes.countDistance();
        else
          this.totalDistance = 0;
        //this.routes.printRoute();
        break;
      case 5: // CALCULATE PRIM
        // Clear the next value for all the aiports
        clearNexts();
        this.airports = this.routes.primCalc();
        if(debugmode) System.out.println("----PRIM----");
        
        if(this.airports.length > 1)
          this.totalDistance = this.routes.countDistance();
        else
          this.totalDistance = 0;
          
        break;
/*
      case 3: // CONVEX HULL FOR ALL THE AIRPORTS
        // Clear the next value for all the aiports
        clearNexts();
        for(Airport v : airports) {
          System.out.println(v);
        }
        
        // Test printout, the convex hull got with Graham algorithm
        System.out.println("GRAHAM TEST");     
        System.out.println("Convex hull for all the airports:");
        testi = routes.graham();
        while(testi.size()>0) {
          System.out.println((Airport)testi.removeLast());
        }
        
        break;
*/
      default:
if(debugmode) System.out.println("Argument default");
        break;
    }
    
  }

  public boolean isAirports() {
    if(this.airportsIn == null || this.airportsIn.length == 0)
      return false;
    return true;
  }
  
  public Airport[] getAirports() {
    return this.airports;
  }
  
  public void setAirportsIn() {
    int counter = 0;
    for(Airport v : this.airports) {
      if(v.selected)
        counter++;
    }
    Airport[] airportsIn = new Airport[counter];
    
    counter = 0;
    for(Airport v : this.airports) {
      if(v.selected) {
        airportsIn[counter] = v;
        counter++;
      }
    }
    this.airportsIn = airportsIn;
  } 
//----------------------------------------------------------------------------
// Method to check arguments & tell which variation of the approximators is used
//----------------------------------------------------------------------------
  private int checkArguments(String[] args, Airport startPort) {
    int retu = 0;

    if((args[0]).equals("-e")) {
      retu = 1;
      if(args.length != 1) {
        return -1;
      }
/*    } else if((args[0]).equals("-graham")) {
      retu = 3;
      if(args.length != 1) {
        return -1;
      }*/
    } else if((args[0]).equals("-oneway")) {
      retu = 4;
      if(args.length != 1) {
        return -1;
      }
    } else if((args[0]).equals("-kml")) {
      retu = 2;
      if(args.length != 1) {
        return -1;
      }
    } else if((args[0]).equals("-prim")) {
      retu = 5;
      if(args.length != 1) {
        return -1;
      }
    }
    return retu;
  }
  
  public void clearSelections() {
    for(Airport v : this.airports) {
      v.selected = false;        
      v.ender = false;
    }
  }  
  public void clearNexts() {
    for(Airport v : this.airports)
      v.setNext(null);
  }
}
