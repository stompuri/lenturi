package com.stompuri.lenturi;
// *********************************************
// * Lenturi v0.1 - Seppo Tompuri - 05.06.2010 *
// * Class "Airport"                           *
// * Used by the Lenturi program.              *
// * Stores information of single airport.     *
// *********************************************
public class Airport {
  public int latitude, longitude;
  public double d_latitude, d_longitude;
  public double coordX, coordY;
  public String waypoint;
  //private int runwaylength;
  //private int runwaywidth;
  //private int fueltype;
  //private int seasonticket;
  //private int groundtime;
  //private boolean controlled;
  
  private double distance;
  private Airport parent;
  //private LinkedList nextList;
  private Airport next;
  private boolean visited;
  //private boolean tempVisited; // temporary visited info, that can be cleared & used by methods
    
  public int waytype;
  
  public boolean selected;
  public boolean ender; // does the route start/end here
  
  // CONSTRUCTOR
  public Airport(int latitude, int longitude, String waypoint, int runwaylength, int runwaywidth, int fueltype, int seasonticket, int groundtime, boolean controlled) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.waypoint = waypoint;
    //this.runwaylength = runwaylength;
    //this.runwaywidth = runwaywidth;
    //this.fueltype = fueltype;
    //this.seasonticket = seasonticket;
    //this.groundtime = groundtime;
    //this.controlled = controlled;
    
    this.distance = Double.MAX_VALUE;
    this.parent = null;
    this.selected = false;
    this.ender = false;
    //this.nextList = new LinkedList();
    this.next = null;
    this.visited = false;
    //this.tempVisited = false;
    
    this.waytype = 0;
    this.coordX = 0;
    this.coordY = 0;

    // Calculate the degree values for latitude and longitude
    int deg = Integer.valueOf((Double.toString(latitude)).substring(0,2));
    int min = Integer.valueOf((Double.toString(latitude)).substring(2,4));
    int sec = Integer.valueOf((Double.toString(latitude)).substring(4,6));
    // Then these separate values can be calculated again into one value
    this.d_latitude = deg + (min + (double)sec/60)/60;

    deg = Integer.valueOf((Double.toString(longitude)).substring(0,2));
    min = Integer.valueOf((Double.toString(longitude)).substring(2,4));
    sec = Integer.valueOf((Double.toString(longitude)).substring(4,6));
    this.d_longitude = deg + (min + (double)sec/60)/60;
  }

  public String toString() {
/*    if(this.next != null && this.parent != null)
      return this.waypoint + " " + " NEXT: " + (this.next).waypoint + " PREV: " + (this.parent).waypoint;

    if(this.next != null)
      return this.waypoint + " " + " NEXT: " + (this.next).waypoint;

    return this.waypoint + " " + " NEXT: -";
*/

    return this.waypoint;// + "\t" + this.d_latitude + "/" + this.d_longitude; 
  }
  
  public void setDistance(double distance) {
    this.distance = distance;
  }
  public double getDistance() {
    return this.distance;
  }
  
  public void setParent(Airport parent) {
    this.parent = parent;
  }
  public Airport getParent() {
    return this.parent;
  }

/*
  // Method to add one more next value for the airport
  // Needed when the route visits the airport multiple times
  public void addNext(Airport next) {
    if(nextList.getFirst() == null)
      setNext(next);
    else
      this.nextList.addFirst(next);
  }
  // Method to clear nexts
  public void clearNexts() {
    this.nextList = new LinkedList();
  }
*/
  // Method to change the next value.
  public void setNext(Airport next) {
    this.next = next;
  }

  // Returns the next value. First out the first in.
  public Airport getNext() {
    return this.next;
  }
  
  public void setVisited(boolean value) {
    this.visited = value;
  }
  
  public boolean visited() {
    return this.visited;
  }
  
  public void setTempVisited(boolean value) {
    this.visited = value;
  }
  
  public boolean tempVisited() {
    return this.visited;
  }
}
