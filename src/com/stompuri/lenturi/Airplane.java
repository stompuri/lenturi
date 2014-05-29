package com.stompuri.lenturi;
// *********************************************
// * Lenturi v0.1 - Seppo Tompuri - 04.08.2010 *
// * Class "Airplane"                           *
// * Used by the Lenturi program.              *
// * Stores information of single airplane.     *
// *********************************************

public class Airplane {
  private String type;      // type of the airplane (name of the type, as text)
  private int fuelType;     // type of fuel (1 = JET, 2 = AVGAS)
  private int fuelCapacity; // how much fuel the plane can carry (liter)
  private int fuelConsume;  // how much fuel the plane consumes (liter per hour)
  private int speed;        // speed of the airplane (knots)
  private int range;        // how long the plane can fly with single tank of fuel
  private int takeOffDist;  // how long runway needed for take off (meters)
  private int landingDist;  // how long runway needed for landing (meters)

  // CONSTRUCTOR
  public Airplane(String type, int fuelType, int fuelCapacity, int fuelConsume, int speed, int range, int takeOffDist, int landingDist) {
    this.type = type;
    this.fuelType = fuelType;
    this.fuelCapacity = fuelCapacity;
    this.fuelConsume = fuelConsume;
    this.speed = speed;
    this.range = range;
    this.takeOffDist = takeOffDist;
    this.landingDist = landingDist;
  }
  
  public String toString() {
    return this.type; 
  }
  
  public String getType() {
    return this.type;
  }
  public int getFuelType() {
    return this.fuelType;
  }
  public int getFuelCapacity() {
    return this.fuelCapacity;
  }
  public int getFuelConsume() {
    return this.fuelConsume;
  }
  public int getSpeed() {
    return this.speed;
  }
  public int getRange() {
    return this.range;
  }
  public int getTakeOffDist() {
    return this.takeOffDist;
  }
  public int getLandingDist() {
    return this.landingDist;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  public void setFuelType(int type) {
    this.fuelType = type;
  }
  public void setFuelCapacity(int capacity) {
    this.fuelCapacity = capacity;
  }
  public void setFuelConsume(int consume) {
    this.fuelConsume = consume;
  }
  public void setSpeed(int speed) {
    this.speed = speed;
  }
  public void setRange(int range) {
    this.range = range;
  }
  public void setTakeOffDist(int takeoff) {
    this.takeOffDist = takeoff;
  }
  public void setLandingDist(int landing) {
    this.landingDist = landing;
  }
} 