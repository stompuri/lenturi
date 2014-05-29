package com.stompuri.lenturi;
// *******************************************
// * Lenturi - Seppo Tompuri - 19.06.2010    *
// *                                         *
// *******************************************

//import java.util.*;

public class Approximator {
  double refAngle = 0.0d;
  boolean debugmode = false;
  boolean debugmode2 = false;

  //Private class "Kaari" for storing routes from one Airport to other
  //------------------------------------------
  private class Kaari
  {
    Airport start, end;
    // CONSTRUCTORS
    private Kaari() {
      this.start = null;
      this.end = null;
    }
    
    private Kaari(Airport start, Airport end) {
      this.start = start;
      this.end = end;
    }
  }
  //------------------------------------------

  private Airport[] airports;
  
  // CONSTRUCTORS
  public Approximator(Airport[] airports) {
    this.airports = airports;
  }
  public Approximator(Airport[] airports, double refAngle) {
    this.airports = airports;
    this.refAngle = refAngle;
  }
    
  //-----------------------------------------------------
  // METHODS
  //-----------------------------------------------------
  // TWOPORTFINDER
  // Find the shortest route from one airport to other. Take the airplane range into account
  public Airport[] twoPortFinder(Airport start, Airport end, Airplane plane) {
    formatAirports();
    
    // If the plane can not fly directly from the start airport to the end airport, then calculate the route
    if(getDistance(start, end) > plane.getRange()) {
      // For start situation set the route directly from start to end
      start.setParent(null);
      start.setNext(end);
      end.setParent(start);
      end.setNext(null);
      
    } else {
      // Fly directly from start to end
      // set next and parent values
      start.setParent(null);
      start.setNext(end);
      end.setParent(start);
      end.setNext(null);
    }
    
    return this.airports;
  }

  public Airport[] middleCalc() {
    // 1) Dig out the start and end airports
    // + Do a double check, which way is shorter: switch start and end ports
    int indexStart = 0, indexEnd = 0;
    boolean change = false;
    for(Airport v : this.airports) {
      if(v.ender && change)
        break;
      if(v.ender)
        change = true;;
      if(!change) {
        indexStart++;
        indexEnd = indexStart;
      }
      else
        indexEnd++;
    }
if(debugmode) System.out.println(this.airports[indexStart] + " & " + this.airports[indexEnd]);
    
    // 2) Calculate the two possible routes & select the shorter
    oneWayHelper(indexStart, indexEnd);
    int length1 = checkRouteLength(this.airports[indexStart]);
    oneWayHelper(indexEnd, indexStart);
    int length2 = checkRouteLength(this.airports[indexEnd]);
if(debugmode) System.out.println("LENGTH1 = " + length1 + " L2 = "+length2);
    if(length1 < length2)
      oneWayHelper(indexStart, indexEnd);

    return this.airports;
  }

  // ONEWAYFINDER
  // Find the shortest route from one airport to other AND
  // visit all the airports selected.
  // Take the airplane range into account
  public Airport[] oneWayFinder() {
    // 1) Dig out the start and end airports
    // + Do a double check, which way is shorter: switch start and end ports
    int indexStart = 0, indexEnd = 0;
    boolean change = false;
    for(Airport v : this.airports){
      if(v.ender && change)
        break;
      if(v.ender)
        change = true;;
      if(!change) {
        indexStart++;
        indexEnd = indexStart;
      }
      else
        indexEnd++;
    }

    Airport start = this.airports[indexStart];
    Airport end = this.airports[indexEnd];
    this.refAngle = countAngle(start, end);
    if(debugmode) System.out.println("START: " + start + " END: " + end);
    
    formatAirports();
    this.airports = approximator();
    if(debugmode) printRoute();
    
    // 2) Check if the start & end airports are next to each other in the convex hull route
    // IF they are, just remove the route between them and we have the route!
    if(start.getNext().equals(end)) {
      start.setNext(null);
      end.setParent(null);
      // Ensure that the direction of next values is correct
      // NOT NEEDED! checkRouteDirection(start, end);
      return this.airports;
    } else if(start.getParent().equals(end)) {
      start.setParent(null);
      end.setNext(null);
      return this.airports;
    }
    
    Airport bottomStart = null;//, bottomEnd = null, middleStart = null, middleEnd = null;
    Airport topStart = null;//, topEnd = null;

    if(debugmode) System.out.println("start.parent = " + start.getParent() + " end.next = " + end.getNext());
    
    // 1) Check the BOTTOM part of the route
    //  1.1) Find out the airport where to start going "up"
    bottomStart = getStart(start, end, "bottom");
    
	if(debugmode) {
		System.out.println("BOTTOMSTART = " + bottomStart + " " + bottomStart.visited());
		System.out.println("Next = " + bottomStart.getNext() + " Parent = " + bottomStart.getParent());
		System.out.println(start + " Next = " + start.getNext() + " Parent = " + start.getParent());
	}
	
    // 2) check the TOP part of the route
    //  2.1) Find out the airport from where to end the primmiddle
	if(debugmode) System.out.println("start == "+start+" end == " + end);

    topStart = getStart(start, end, "top");

    if(debugmode) {
		System.out.println("TOPSTART = " + topStart + " " + topStart.visited());
		System.out.println("Next = " + topStart.getNext() + " Parent = " + topStart.getParent());
		System.out.println(end + " Next = " + end.getNext() + " Parent = " + end.getParent());
		System.out.println("NEXT.Next: " + topStart.getNext().getNext());
		System.out.println("UUDESTAAN1 Bottomstart.next = " + bottomStart.getNext() + " Parent = " + bottomStart.getParent());
		System.out.println("UUDESTAAN1 topStart.next = " + topStart.getNext() + " Parent = " + topStart.getParent());
	}
    // 3) Check the MIDDLE part of the route (if start != end)
    int i = 0;
    for(Airport v : this.airports)
      if(!v.visited())
        i++;

    Airport[] airportsMiddle = new Airport[i];
    i = 0;
    
    Airport middleInsert = null;
    for(Airport v : this.airports)
      if(!v.visited()) {
    	if(debugmode) System.out.println("Middle insert: " + v);
        airportsMiddle[i] = v;
        middleInsert = v;
        i++;
      }
      
    // Save the distance of the special case 1, if it is shorter to go first left side of middle ports, then right
    //double correctSpecial1 = correctSpecial1(bottomStart, topStart, start, end);
//System.out.println("CORRECTSPECIAL1 = " + correctSpecial1);

	if(debugmode) {    
		System.out.println("UUDESTAAN2 Bottomstart.next = " + bottomStart.getNext() + " Parent = " + bottomStart.getParent());     
		System.out.println("UUDESTAAN2 topStart.next = " + topStart.getNext() + " Parent = " + topStart.getParent());
		System.out.println("NEXT.Next: " + topStart.getNext().getNext());
	} 
	
    if(i > 1) {
      Approximator routesMiddle = new Approximator(airportsMiddle, this.refAngle);
      routesMiddle.primMiddle(bottomStart, topStart, start, end);
    } else {
      if(i == 1) {
        bottomStart.setNext(middleInsert);
        middleInsert.setNext(topStart);
        if(!topStart.getNext().equals(end)) {
          topStart.setNext(end);
        }
      } else {
        bottomStart.setNext(topStart);
        if(!topStart.getNext().equals(end)) {
          topStart.setNext(end);
        }
      }
    } 

	if(debugmode) {
		System.out.println("UUDESTAAN3 topStart.next = " + topStart.getNext() + " Parent = " + topStart.getParent());
		System.out.println("NEXT.Next: " + topStart.getNext().getNext());
	}
    start.setParent(null); end.setNext(null);
    
    // Ensure that the direction of next values is correct
    
    checkRouteDirection(start, end, bottomStart, topStart);
//printRoute();
    // Go through the route, and optimize the route by checking any narrow turns
    while(correctNarrowTurns2(start)) {}

    while(correctSpecial3(start)) {}
    
    //double checkDistance = countDistance();
    
    return this.airports;
  }
  
  private Airport getStart(Airport start, Airport end, String type) {
    Airport lowest = null;
    if(type.equals("bottom")) {
      if(debugmode) System.out.println("bottom");
      lowest = portsBehind(start);
      Airport workPort = lowest;
      if(lowest == null)
        lowest = start;
      else {
        boolean useParent = true;
        while(!workPort.equals(start) && !workPort.equals(end))
          workPort = workPort.getParent();
        if(workPort.equals(end))
          rotateAirports(this.airports);
        workPort = lowest;
        while(!workPort.equals(start)) {
          if(debugmode) System.out.println(workPort + " = VISITED " + useParent);
          workPort.setVisited(true);
          if(useParent) {
            if(countRefAngle(end, workPort) < 90 || countRefAngle(end, workPort) > 270) {
              workPort.getParent().setNext(workPort.getNext());
              workPort.getNext().setParent(workPort.getParent());
              if(debugmode) System.out.println(workPort + " = NOT VISITED1");
              workPort.setVisited(false);
            }
            workPort = workPort.getParent();
          } else {
            if(countRefAngle(end, workPort) < 90 || countRefAngle(end, workPort) > 270) {
              workPort.getNext().setParent(workPort.getParent());
              workPort.getParent().setNext(workPort.getNext());
              if(debugmode) System.out.println(workPort + " = NOT VISITED2");
              workPort.setVisited(false);
            }
            workPort = workPort.getNext();
          }
          
        }
      }
      start.setVisited(true);
      //start.setParent(null);
    } else if(type.equals("top")) {
      if(debugmode) System.out.println("top");
      lowest = portsBehindTop(end);
      Airport workPort = lowest;
      boolean useParent = true;
      if(debugmode) System.out.println("GETSTART TOP: lowest = " + lowest + " start = " + start + " end = " + end);
      
      if(lowest == null) {
        lowest = end.getNext();
        if(debugmode) System.out.println("IFFI: lowest = " + lowest);
        lowest = portsAbove(lowest, end);
        if(lowest == null) {
          lowest = end;
          lowest.setVisited(true);
        } else {
          lowest.setNext(end);
          end.setParent(lowest);
          lowest.setVisited(true);
        }    
      } else {
        while(!workPort.equals(start) && !workPort.equals(end))
          workPort = workPort.getParent();
        if(workPort.equals(start))
          useParent = false;
        workPort = lowest;
        while(!workPort.equals(end)) {
          if(debugmode) System.out.println(workPort + " = VISITED1 " + useParent);
          workPort.setVisited(true);
          if(useParent) {
            if(countRefAngle(end, workPort) > 90 && countRefAngle(end, workPort) < 270) {
              workPort.getParent().setNext(workPort.getNext());
              workPort.getNext().setParent(workPort.getParent());
              if(debugmode) System.out.println(workPort + " = NOT VISITED11");
              workPort.setVisited(false);
            }
            workPort = workPort.getParent();
          } else {
            if(countRefAngle(end, workPort) > 90 && countRefAngle(end, workPort) < 270) {
              workPort.getNext().setParent(workPort.getParent());
              workPort.getParent().setNext(workPort.getNext());
              if(debugmode) System.out.println(workPort + " = NOT VISITED12");
              workPort.setVisited(false);
            }
            workPort = workPort.getNext();
          }
          
        }

      }

    }
    end.setVisited(true);
    
    return lowest;
  }   
  
  // ONEWAYFINDER
  // Find the shortest route from one airport to other AND visit all the airports selected. Take the airplane range into account
  
  private void rotateAirports(Airport[] airports) {
    for(Airport v : airports) {
      Airport backup = v.getNext();
      v.setNext(v.getParent());
      v.setParent(backup);
    }
  }

  // PRIMCALC
  // Find the shortest route from one airport to other. ---not yet: Take the airplane range into account
  public Airport[] primCalc() {
    formatAirports();
    
    int indexStart = 0;
    for(Airport v : this.airports){
      if(v.ender)
        break;
      indexStart++;
    }
    
    Kaari[] kaaret = prim(indexStart);
    
    for(Kaari k : kaaret) {
      if(debugmode) System.out.println(k.start + " --> " + k.end);
      (k.start).setNext(k.end);
      (k.end).setParent(k.start);
    }
    
    return this.airports;
  }
  
  // PRIMMIDDLE APPROXIMATOR
  public Airport[] primMiddle(Airport bottomStart, Airport topEnd, Airport realStart, Airport realEnd) {
    Airport now = null, next = null;
    Airport parent = bottomStart;
    Airport start = null, end = null;
    boolean toLeft = false;
    //Airport backupNext = null;
    
// 1) Search the closest points from the middle area
    if(bottomStart.equals(realStart))
      if(getDistance(bottomStart, bottomStart.getNext()) < getDistance(bottomStart, bottomStart.getParent()) && !bottomStart.getNext().visited())
        now = bottomStart.getNext();
      else
        now = bottomStart.getParent();
    else {
      now = selectClosest(bottomStart);
    }
    if(debugmode) System.out.println("NOW = " + now);
    
    start = now;
    start.setVisited(true);
    
    if(topEnd.equals(realEnd))
      if(getDistance(topEnd, topEnd.getNext()) < getDistance(topEnd, topEnd.getParent()))
        end = topEnd.getNext();
      else
        end = topEnd.getParent();
    else {
      end = selectClosest(topEnd);
    }
    if(debugmode) System.out.println("End top: " + end + " == " + portsAbove(end, realEnd));
    //if(portsAbove(end, realEnd) != null)
    //  end = portsAbove(end, realEnd);
	if(debugmode) {
	  System.out.println("START.closest = " + start + " END.closest = " + end + " parent.next = " + parent.getNext());
	  System.out.println("topEnd = " + topEnd);
	}
	
    boolean right = true;
    Airport check = end;
    //int i = 0;
    do {
      check = check.getParent();
      if(check.equals(topEnd))
        right = false;
      
      if(debugmode) System.out.println(check + " == " + topEnd);
    } while(!check.equals(topEnd) && !check.equals(bottomStart) && !check.equals(null));
    if(!right) {
      end.setNext(end.getParent());
      if(debugmode) System.out.println("SET5: " + end + " --> " + end.getParent());
    }
    
    parent.setParent(null);
    //start.setVisited(true);
    end.setVisited(true);
    
	if(debugmode) {
	  System.out.println(end + " = visited = " + end.visited());
	  System.out.println("END.Next = " + end.getNext() + " .Parent = " + end.getParent());
	}
    
    if(start.equals(end)) {
      bottomStart.setNext(start); start.setParent(bottomStart);
      start.setNext(topEnd); topEnd.setParent(start);
      return this.airports;
    }

// 2) create approximator route for middle airports
    Approximator middleRoute = new Approximator(this.airports);
    this.airports = middleRoute.approximator();

    end.setVisited(true);
    
	if(debugmode) System.out.println(end + " = visited = " + end.visited());

// 3) make sure that the route goes from left to right 
    if(countRefAngle(start, start.getNext()) < countRefAngle(start, start.getParent()))
      rotateAirports(this.airports);

// 4) follow up the route like the convex hull goes
//    4.1) if the next one is above the lowest not visited one, go to the not visited one
    while(!now.visited()) {
      now.setVisited(true);
      if(toLeft)
        next = now.getParent();
      else
        next = now.getNext();
        
      if(next.visited()) {
        next = selectClosest(now);
        if(next == null)
          next = end;
      }
      
      Airport v = portsBehind(next);
      
      if(v != null) {
        // Do not change the side by default, check first
        // if the same side port is on the way (the corner degree is below 30)
        if( (Math.abs(countRefAngle(v, now) - countRefAngle(next, now)) >= 30) ||
            (getDistance(now, v) < getDistance(now, next)) ) {
          // by default the way of going is from left to right
          // if going up on the left side, then take the parent, not next
          if(!toLeft) {
        	if(debugmode) System.out.println("INES1.1");
            if(!checkSameSide(v, next, start, end))
              toLeft = true;
          } else {
        	if(debugmode) System.out.println("INES1.2");
            if(!checkSameSide(v, next, start, end))
              toLeft = false;
          }
          next = v;
        }
      }

      if(debugmode) System.out.println("SET: " + parent + " -- " + now + " --> " + next + " next.parent = " + next.getParent() + " next.next = " + next.getNext());      
      parent.setNext(now);
      
      if(!next.visited() || next.equals(end)) {
        now.setNext(next);
          now.setParent(parent);
        parent = now;
        now = next;
      } else if(!now.getParent().visited()) {
        now.setNext(now.getParent());
        parent = now;
        now = now.getParent();
        if(debugmode) System.out.println("SETFIX: " + now.getParent());
      }
    }

    if(debugmode) System.out.println("SET3: " + now + " --> " + topEnd);
    now.setNext(topEnd);

	if(debugmode) {
	  System.out.println("realEnd = " + realEnd + " realEnd.parent = " + realEnd.getParent() + "realEnd.next = " + realEnd.getNext());
	  System.out.println("realEnd.next.next = " + realEnd.getNext().getNext() + " n.p = " + realEnd.getNext().getParent());
	}
	
    if(onTheRoute(realEnd, topEnd, realStart, realEnd)) {
      realEnd.getNext().setNext(realEnd);
      realEnd.setParent(realEnd.getNext());
    }
    
    // Set tempVis for realEnd->topEnd to false
    Airport nexti = realEnd;
    int test = 0;
    while(!nexti.equals(topEnd) && nexti != null && test < 30) {
      nexti.setTempVisited(false);
      nexti = nexti.getNext();
      test++;
    }
    nexti.setTempVisited(false);  

    // Go the route, and fix the directions
    checkRouteDirection(realStart, realEnd, bottomStart, topEnd);

	if(debugmode) {    
	System.out.println("now = " + now + " now.next = " + now.getNext() + " parent = " + parent + " parent.next " + parent.getNext() + " end = " + end + " end.next " + end.getNext());
	System.out.println("realStart = " + realStart + " realEnd = " + realEnd);
	System.out.println("realEnd.next = " + realEnd.getNext() + "realEnd.parent = " + realEnd.getParent());
	}
	
    bottomStart.setNext(start);
    start.setParent(bottomStart);
    
    if(debugmode) System.out.println(bottomStart + " 1--> " + start);
    
//XXX TO BE DONE XXX - Should be checked, now commented out as doesn't work :(
    if(!topEnd.equals(realEnd) && now.equals(end)) {
      //Airport backup = topEnd.getParent();
    	if(debugmode) System.out.println("topEnd.next = " + topEnd.getNext() + " topEnd.parent = " + topEnd.getParent());
//      now.setNext(topEnd);
//      topEnd.setParent(now);
//      topEnd.setNext(backup);
    	if(debugmode) System.out.println(now + " 2--> " + topEnd);
    } else {
//      end.setNext(topEnd);
    	if(debugmode) System.out.println(end + " 3--> " + topEnd);
      // a check, so we don't lose parent data
      if(topEnd.getNext().equals(end)) {
//        topEnd.setNext(topEnd.getParent());
    	  if(debugmode) System.out.println(end + " 3.2--> " + topEnd);
      }
//      topEnd.setParent(end);
//      parent.setNext(end);
      // a check, so we don't lose parent data
      if(end.getNext().equals(parent)) {
//        end.setNext(end.getParent());
    	  if(debugmode) System.out.println(end + " 3.3--> " + topEnd);
      }
//      end.setParent(parent);

      if(debugmode) System.out.println(parent + " 4--> " + end);
      parent.setNext(end);
      end.setParent(parent);
    }
    
    return this.airports;
  }
  
  private boolean checkSameSide(Airport one, Airport two, Airport start, Airport end) {
    // Check the situation when the airports are next to each other
	if(debugmode2) System.out.println("CHECKSAMESIDE: " + one + " one.parent: " + one.getParent() + " one.next: " + one.getNext() + " two: " + two + " two.parent " + two.getParent());
    if(one.getNext().equals(two.getParent()))
      return false;
//XXX CHECK THIS XXX - Jää luuppiin joskus:
    Airport backup = null, backupCheck = null;
    //Airport nextCheck = true;
    int testi = 0;
    while(one != null && !one.equals(start) && !one.equals(end) && !one.equals(two)) {
      if(testi > 50)
        System.exit(0);
      backup = one;
      if(one.equals(backupCheck)) {
        one = one.getParent();
        //nextCheck = false;
      } else {
        one = one.getNext();
        //nextCheck = true;
      }
      if(!one.getParent().equals(backup))
        backupCheck = one;
      testi++;
      if(debugmode2) System.out.println("ONE CHECK: " + one + " one.parent = " + one.getParent() + " one.next = " + one.getNext());// + " start = " + start + " end = " + end + " two = " + two);
    }

    if(one.equals(two))
      return true;
    one = one.getParent();
    testi = 0;
    while(one != null && !one.equals(start) && !one.equals(end) && !one.equals(two)) {
      if(testi > 50)
        System.exit(0);
      one = one.getParent();
      testi++;
//System.out.println("******SS2: one = " + one);
    }
    if(one != null && one.equals(two))
      return true;
    return false;
  }
  
  // PRIMMIDDLE APPROXIMATOR
  public Airport[] primMiddle_old_old(Airport start, Airport realStart) {
    formatAirports();

// 1) create approximator route for middle airports
    Approximator middleRoute = new Approximator(this.airports);
    this.airports = middleRoute.approximator();
    
// 2) make sure that the route goes from left to right 
    if(countRefAngle(start, start.getNext()) < countRefAngle(start, start.getParent()))
      rotateAirports(this.airports);

// 3) follow up the route like the convex hull goes
//    3.1) if the next one is above the lowest not visited one, go to the not visited one
    Airport now = start, next = null;
    Airport parent = realStart;
//    realStart.setNext(start);
    
//System.out.println("NOW = "+now);
    boolean toLeft = false;
    while(!now.visited()) {
      now.setVisited(true);
      if(toLeft)
        next = now.getParent();
      else
        next = now.getNext();
//System.out.println("PORTSBEHIND KUTSU: " + next + " " + toLeft + " " + next.visited());
      Airport v = portsBehind(next);
//System.out.println("v = " + v);
      if(v != null) {
        next = v;
        // by default the way of going is from left to right
        // if going up on the left side, then take the parent, not next
        if(!toLeft)
          toLeft = true;
        else
          toLeft = false;
      }
      if(next.visited())
        break;
      
      if(debugmode) System.out.println("SET: " + now + " --> " + next);
      now.setNext(next);
      now.setParent(parent);
      parent = now;
      now = next;
    }
    
    now.setNext(null);
    start.setParent(now);
    return this.airports;
  }
    
  private Airport portsBehind(Airport find) {
    //int portCount = 0;
    Airport lowest = find;
    for(Airport v : this.airports) {
//System.out.println("PORTSBEHIND: " + v + " refAngle = " + this.refAngle + " count: " + countRefAngle(find, v) + " visited: " + v.visited());
      if(!v.visited() && (countRefAngle(find, v) > 90 && countRefAngle(find, v) < 270)) {
        //portCount++;
//System.out.println("PORTSBEHIND2: " + v + " lowest = " + lowest + " count: " + countRefAngle(lowest, v));
        if(countRefAngle(lowest, v) > 90 && countRefAngle(lowest, v) < 270)
        //if(countRefAngle(find, v) < countRefAngle(lowest, v))
          lowest = v;
//System.out.println("PORTSBEHIND: " + v + " refAngle = " + this.refAngle + " count: " + countRefAngle(find, v));
      }
    }
//System.out.println("BEHIND INES: " + find + " && " + lowest);
    if(lowest.equals(find))
      return null;
    return lowest;
  }

  private Airport portsAbove(Airport find, Airport end) {
    Airport lowest = find;
    for(Airport v : this.airports) {
//System.out.println("PORTSABOVE: " + v + " refAngle = " + this.refAngle + " count: " + countRefAngle(lowest, v) + " end = " + end + " lowest = " + lowest);
//System.out.println(!v.equals(end) + " " + !v.equals(lowest) + " " + (countRefAngle(lowest, v) < 90 || countRefAngle(lowest, v) > 270));
      if(!v.equals(end) && !v.equals(lowest) && 
        (countRefAngle(lowest, v) < 90 || countRefAngle(lowest, v) > 270)) {
if(debugmode)
System.out.println("SET lowest = " + v);
        lowest = v;
      }
    }
if(debugmode)
System.out.println("BEHINDABOVE INES: " + find + " && " + lowest);
    if(lowest.equals(find))
      return null;
    return lowest;
  }
  
  private Airport portsBehindTop(Airport find) {
    //int portCount = 0;
    Airport lowest = find;
    for(Airport v : this.airports) {
      if(!v.equals(find) && !v.visited() && ((countRefAngle(find, v) < 90 || countRefAngle(find, v) > 270))) {
        //portCount++;
        if(!v.equals(lowest) && !v.visited() && ((countRefAngle(lowest, v) < 90 || countRefAngle(lowest, v) > 270)))
          lowest = v;
if(debugmode)
System.out.println("PORTSBEHINDTOP: " + v + " refAngle = " + this.refAngle + " count: " + countRefAngle(find, v));
      }
    }
if(debugmode)
System.out.println("BEHINDTOP INES: " + find + " && " + lowest);
    if(lowest.equals(find))
      return null;
    return lowest;
  }
    
  /*private int indexPlace(Airport find) {
    int i = 0;
    for(Airport v : this.airports) {
      if(v.equals(find))
        return i;
      else
        i++;
    }
    return -1;
  }*/
  
  // APPROXIMATOR
  // Based on Stewarts and Bodins convex hull algorithm idea (http://www2.isye.gatech.edu/logisticstutorial/vehicle/tsp/tsp017__.htm)
  public Airport[] approximator() {
    formatAirports();
    // 1. Get the convex hull for the network of airports
    LinkedList convex = graham();

    // 2. Go through every airport NOT in convex hull, and set the prev&next airports for them from the convex hull(so that the prev->airport->next is as short as possible)  
    // a) Luo minimikeko konveksin verkon ulkopuolisista lentokentista.
    // Avaimena arvo, joka kertoo kuinka paljon kentan lisaaminen konveksiin verhoon
    // kasvattaisi konveksin verhon pituutta.
    // b) Valitaan lisattavaksi kentta, joka pidentaa vahiten.
    // c) Lisataan tama kentta konveksiin verhoon. 
    // d) Toistetaan, kunnes kaikki kentat konveksissa verhossa.
    while(convex.size() != this.airports.length) {
      Airport added = convexNext(convex);
      convex = addConvexAP(convex, added);
    }
    
    return this.airports;
  }

  // Method to print out the route starting from the "starter" airport
  // Aikavaativuus O(n) Tilavaativuus O(1)
  public void printRoute() {
    Airport starter = null;
    for(Airport v : this.airports) {
      if(v.getParent() == null) {
        starter = v;
        break;
      }
    }
    if(starter == null)
      starter = this.airports[0];
    Airport ender = starter;
    do {
if(debugmode)
System.out.print("(" + starter.getParent() + ")" + starter + " -> ");
      starter = starter.getNext();
    } while(starter != ender && starter != null);
if(debugmode)
System.out.println();
  }
  
  public double countDistance() {
    double totalDistance = 0;
    for(Airport v : this.airports) {
//System.out.println("DIST: " + v + " --> " + v.getNext());
      if(v != null && v.getNext() != null)
        totalDistance += getDistance(v, v.getNext());
    }
    return totalDistance;
  }
  
//----------------------------------------------------------------------------
// Below are the helper methods/algorithms for the approximator algorithms
//----------------------------------------------------------------------------

  // Method to add airport into the convex hull. Convex hull is hold in LinkedList queue.
  // INPUT: Convex hull queue, and the airport to be added
  // OUTPUT: Updated queue including the added airport.
  // Aikavaativuus O(n) Tilavaativuus O(n)
  private LinkedList addConvexAP(LinkedList convex, Airport added) {
    // Add the new airport into the convex hull
    // update the Next & Parent values of the airports in the convex hull
if(debugmode)
System.out.println("!!! "+added+" --> "+added.getNext());
    (added.getParent()).setNext(added);
    (added.getNext()).setParent(added);
    
    // Update the distance value of the airport that refers to the new airport
    (added.getParent()).setDistance(getDistance(added.getParent(), added));    
    
    // Update the convex queue
    LinkedList storage = new LinkedList();
    while(!storage.contains(added.getParent()))
      storage.addFirst((Airport)convex.removeLast());
    storage.addFirst(added);
    while(storage.size() > 0)
      convex.addLast((Airport)storage.removeFirst());
    
    return convex;
  }

  // Method to searches the one airport that is not in the convex hull
  // that would increase the length of the convex hull the minimum amount.
  // INPUT: Convex hull as queue.
  // OUTPUT: A new airport that increases the length of the convex hull the minimum amount.
  // Aikavaativuus O(n^2) Tilavaativuus O(n)
  private Airport convexNext(LinkedList convex) {
    MinHeap H = new MinHeap();
    Airport closest = null;
    
    // Go through every airport that is not included in the convex hull
    for(Airport v : this.airports) {
      if(!convex.contains(v) && !v.visited()) {
        // Check how to place the new aiport in a way, where it minimizes the length increase
        // --> What are the two airports in the convex hull, that are the parent&next of the new airport.
        // Try to put the new airport between each airport in convex hull & pickup the one with minimized effect.
        LinkedList backup = (LinkedList)convex.clone();
        double refDist = Double.MAX_VALUE;
        double minDist = Double.MAX_VALUE;
        Airport port1 = (Airport)backup.removeLast();
        Airport port2 = null;
        Airport first = port1;
        
        //1. Go through all the routes in the convex hull
        while(backup.size() > 0) {
          port2 = (Airport)backup.removeLast();
          refDist = (getDistance(port1, v) + getDistance(v, port2)) - getDistance(port1, port2);
//System.out.println("1."+port1+" 2."+port2+" refDist="+refDist);
          if(refDist < minDist) {
            minDist = refDist;
            closest = port1;
          }
          port1 = port2;
        }
        //2. check the route from the first to last
        port1 = port2; port2 = first;
        refDist = (getDistance(port1, v) + getDistance(v, port2)) - getDistance(port1, port2);
//System.out.println("1."+port1+" 2."+port2+" refDist="+refDist);
        if(refDist < minDist) {
          minDist = refDist;
          closest = port1;
        }
        
        // Update the Parent and Next values of the checked airport
//System.out.println("closest = "+closest);
        v.setParent(closest);
//System.out.println("SET NEXT-OUT: "+v+" --> "+closest+" "+closest.getNext());
        v.setNext(closest.getNext());
        // Update also the distance value for the checked airport
        v.setDistance(getDistance(v, v.getNext()));
        
        // Put the checked airport into minimum heap
//System.out.println("v = "+v+" key = "+minDist);
//H.printHeap();
        H.heapInsert(v, minDist);
      }
    }
    
    // Take the airport from the Minimum heap that increases the length of the route the minimum amount
    // and return it
    return H.heapDelMin();
  }
  
  // Prim - Algorithm to calculate & return the minimum spanning tree of airports
  // http://en.wikipedia.org/wiki/Prim%27s_algorithm
  // Aikavaativuus O(n^2) Tilavaativuus O(n)
  private Kaari[] prim(int r) {
    Kaari[] T = new Kaari[this.airports.length * 2];
    int kaariplace = 0;
    MinHeap H = new MinHeap();

    for(Airport v : this.airports)
      v.setDistance(Double.MAX_VALUE);
    
    this.airports[r].setDistance(0);
    
    for(Airport v : this.airports)
      H.heapInsert(v, v.getDistance());
    
    while(H.heapsize > 0) {
      Airport u = H.heapDelMin();
      if(u.getParent() != null) {
        T[kaariplace] = new Kaari(u.getParent(), u);
        kaariplace++;
      }
        
      for(Airport v : this.airports) // Do for every
      {                              // adjanced cell
        if(u != v) {                 // (not itself)
          double distance = getDistance(u, v);
          if(H.contains(v) && distance < v.getDistance()) {
            v.setParent(u);
            v.setDistance(distance);
            H.heapDecKey(v, distance);
          }
        }
      }
    }

    // reduce the size of returned table
    Kaari[] palaute = new Kaari[kaariplace];
    for(int i=0; i < kaariplace; i++) {
      palaute[i] = T[i];
    }
    return palaute;
  }
  
  // Harversine - Algorithm to calculate the distance of two points on sphere
  // Aikavaativuus O(1) Tilavaativuus O(1)
  private double getDistance(Airport a, Airport b) { 
    int r = 6371; // radius of earth, km

    // Airport A
    double lati1 = a.d_latitude;
    double longi1 = a.d_longitude;

    // Airport B
    double lati2 = b.d_latitude;
    double longi2 = b.d_longitude;
    
    // The degress have to be transfromed into Radians for calculation:
    double dLat = Math.toRadians(Math.abs((lati2-lati1)));
    double dLon = Math.toRadians(Math.abs((longi2-longi1))); 
    
    // Actual calculation (the format is got from internet, I don't understand the Math under it
    double eka =  Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lati1)) *
                Math.cos(Math.toRadians(lati2)) * Math.sin(dLon/2) * Math.sin(dLon/2); 
    double toka = 2 * Math.atan2(Math.sqrt(eka), Math.sqrt(1-eka)); 
    double d = r * toka;
    
    return d;
  }
  // Harversine - Algorithm to calculate the distance of two points on sphere
  // Aikavaativuus O(1) Tilavaativuus O(1)
  private double getDistance(double ax, double ay, double bx, double by) { 
    int r = 6371; // radius of earth, km

    // Airport A
    double lati1 = ay;
    double longi1 = ax;

    // Airport B
    double lati2 = by;
    double longi2 = bx;
    
    // The degress have to be transfromed into Radians for calculation:
    double dLat = Math.toRadians(Math.abs((lati2-lati1)));
    double dLon = Math.toRadians(Math.abs((longi2-longi1))); 
    
    // Actual calculation (the format is got from internet, I don't understand the Math under it
    double eka =  Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lati1)) *
                Math.cos(Math.toRadians(lati2)) * Math.sin(dLon/2) * Math.sin(dLon/2); 
    double toka = 2 * Math.atan2(Math.sqrt(eka), Math.sqrt(1-eka)); 
    double d = r * toka;
    
    return d;
  }

  // Graham scan algorithm to find out the convex hull for a network
  // http://en.wikipedia.org/wiki/Graham_scan
  // public for debugging purposes, otherwise should be private
  // Aikavaativuus O(n^2) Tilavaativuus O(n)
  public LinkedList graham() {
    // LinkedList jono is used to save the airports in the convex hull & is the value returned
    LinkedList jono = new LinkedList();
    if(airports.length > 1) {
      // 1. Find out the reference airport
      // Reference airport is one with minimum latitude, and if two airports have the same, then with the minimum longitude.
      // Arrange airports into minHeap, so that we get out our reference airport
      MinHeap H = new MinHeap();
      for(Airport v : this.airports) {
        H.heapInsert(v, v.d_longitude+(10000000*v.d_latitude));
      }
      
      Airport reference = H.heapDelMin();
  
      // 2. Put other airports into stack S. The reference aiport is the first one put into the S
      // Order is got by comparing each airport to the reference airport
      // The Rightmost airport, from the reference, is the first, and leftmost is the last.
      // referenssin ja v:n kulmakertoimen laskeminen ja minHeapiin laittaminen kulmakertoimen kanssa
      H = new MinHeap();
      for(Airport v : this.airports) {
        if(v != reference) {
          H.heapInsert(v, ((countAngle(reference, v)*100000+(getDistance(reference, v)))));
        }
      }
  
      // 3. Go through the airports from the minheap and collect the candidate airports for the convex hull
      boolean flag = true; // used to check if aiports were removed from the linkedlist  
      Airport last = null;
      Airport now = reference;
      Airport next = null;
      while(H.heapsize > 0) {
        
        next = H.heapDelMin();
        LinkedList removed = new LinkedList();
        while(turningRight(last, now, next)) {       
          now = (Airport)jono.removeFirst();
          removed.addFirst(now);
          flag = true;
          
          //Airport helper = last;
          while(now != last) {
            now = (Airport)jono.removeFirst();
            removed.addFirst(now);        
          }
        
          last = (Airport)jono.getFirst();
          flag = true;
        }
        
        if(flag) {
          last = (Airport)jono.getFirst();
          jono.addFirst(now);
          flag = false;
        }
  
        last = (Airport)jono.getFirst();
        jono.addFirst(next);
        now = next;
  
        if(removed.size() > 0 && next != null) {
          while(removed.size() > 0) {
            Airport remo = (Airport)removed.removeFirst();
            if(getDistance(reference, remo) < getDistance(reference, now) && countAngle(reference, remo) == countAngle(reference, next)) {
              last = (Airport)jono.getFirst();
              now = remo;
              jono.addFirst(remo);
            }
          }
        }
        next = null;
      }
      
      // Go through the queue and update the parent&next values of all the airports
      LinkedList backup = (LinkedList)jono.clone();
      now = (Airport)backup.removeLast();
      now.setParent((Airport)backup.getFirst());
      Airport first = now;
      next = (Airport)backup.removeLast();
      while(backup.size() > 0) {
        now.setNext(next);
        now.setDistance(getDistance(now, next));
        next.setParent(now);
        now = next;
        next = (Airport)backup.removeLast();
      }
      // And the last one...
      now.setNext(next);
      now.setDistance(getDistance(now, next));
      next.setParent(now);
      
      // And set the last Aiport --> back to first
      next.setNext(first);
      next.setDistance(getDistance(next, first));
    } else {
    // airports size is 0 or 1
      if(airports.length == 1)
        jono.addFirst(airports[0]);
      else
        return null;
    }
    
    // return queue that holds the info of the aiports in the convex hull (in order)
    return jono;
  }

  // Format next and parent info for all airports
  // Aikavaativuus O(n), Tilavaativuus O(1)
  public void formatAirports() {
    for(Airport a : this.airports) {
      a.setNext(null);
      a.setParent(null);
      a.setVisited(false);
    }
  }
  
  // Counting the angle of two airports
  // Aikavaativuus O(1) Tilavaativuus O(1)
  private double countAngle(Airport one, Airport two) {
    double one_x = one.d_longitude;
    double one_y = one.d_latitude;
    double two_x = two.d_longitude;
    double two_y = two.d_latitude;

// KORJAUS ***************'
// LASKE a JA b NIIN, ETTÄ OTTAA HUOMIOON PALLON MUODON, NYT KÄYTTÄÄ SUORAA (MAAN SISÄLTÄ) MENEVÄÄ
    double aD, bD;
    if((two_x-one_x>0 && two_y-one_y>0) || (two_x-one_x<0 && two_y-one_y<0)) {
      bD = getDistance(one_x, one_y, two_x, one_y);
      aD = getDistance(two_x, one_y, two_x, two_y);
    } else {
      aD = getDistance(one_x, one_y, two_x, one_y);
      bD = getDistance(two_x, one_y, two_x, two_y);
    }
    
    double a = two_y - one_y;
    double b = two_x - one_x;

    double angle = 0.0;
    if(a == 0)
      if(b < 0)
        angle = 180.0;
      else
        angle = 0.0;
    else if(b == 0)
      if(a > 0)
        angle = 90.0;
      else
        angle = 270.0;
    else
      angle = Math.abs(Math.toDegrees(Math.atan(aD/bD)));

    if(a < 0 && b > 0) angle += 270;
    if(a < 0 && b < 0) angle += 180;
    if(a > 0 && b < 0) angle += 90;
//if(two.waypoint.equals("EFHJ")) 
//  System.out.println(one + " --> " + two + " aD = "+aD+" bD = "+bD+" ANGLE = "+angle);    
    return angle;
  }
  
  // Method to calculate if the turning is to left/right (used when calculating the contextual area)
  // Turning: If we go from a to b, and then to c, do we turn left/right at the point b?
  // Aikavaativuus O(1) Tilavaativuus O(1)
  private boolean turningRight(Airport a, Airport b, Airport c) {
    // Calculate the x,y coordinates for the airports
    if(a==null || b==null || c==null)
      return false;

    // First the latitude and longitude values have to be separated into
    // degress, minutes and seconds:
    // Airport A
    double a_y = a.d_latitude;
    double a_x = a.d_longitude;

    // Airport B
    double b_y = b.d_latitude;
    double b_x = b.d_longitude;

    // Airport C
    double c_y = c.d_latitude;
    double c_x = c.d_longitude;
//System.out.println("TURNING RIGHT: "+((b_x-a_x)*(c_y-b_y)-(c_x-b_x)*(b_y-a_y))+" "+(int)a_y+" "+(int)b_y+" "+(int)c_y);
    if(b_y < a_y && b_y < c_y)
      return true;
      
    if((b_x-a_x)*(c_y-b_y)-(c_x-b_x)*(b_y-a_y)>=0)
      return false;
    
    return true;
  }

  // Method to check if the airport c is on the Right side of the airport b
  // when coming from the airport a.
  // Used by the approximator algorithm.
  // Aikavaativuus O(1) Tilavaativuus O(1)
  /*private boolean rightSide(Airport a, Airport b, Airport c) {
    // Calculate the x,y coordinates for the airports

    // First the latitude and longitude values have to be separated into
    // degress, minutes and seconds:
    
    // Airport A
    double a_x = a.d_latitude;
    double a_y = a.d_longitude;

    // Airport B
    double b_x = b.d_latitude;
    double b_y = b.d_longitude;

    // Airport C
    double c_x = c.d_latitude;
    double c_y = c.d_longitude;

    // Check if the line a-->c is on the right side of the line a-->b
    if((b_x-a_x)*(c_y-a_y)-(c_x-a_x)*(b_y-a_y)>0)
      return false;
    return true;
  }*/
  
  /*private boolean rightSide2(Airport a, Airport b, Airport c) {    
    double angle1 = countRefAngle(a, b);
    double angle2 = countRefAngle(a, c);
    
    // use angle1 as a refAngle
    angle2 = (angle2 - angle1);
    if(angle2 < 0)
      angle2 = 360 + angle2;
//System.out.println("ANGLE kokeilu: " + angle2);
    return (angle2 > 180);
  }*/

  private int checkRouteLength(Airport start) {
    int length = 0;
    Airport v = start;
    do {
      length += getDistance(v, v.getNext());
    } while (v != null && v != start);
    return length;
  }
  
  private void oneWayHelper(int indexStart, int indexEnd) {
    // Initialize the start situation --> optimal round route
    approximator();
    
    // 3) Edit the optimal route in two ways
    // 3.1 Change the route so, that the airports AFTER the end airport
    //     are visited FIRST. After them continue the normal optimal route.
    // 3.2 Clear the next value of the last airport.
    
    // 3.1
    Airport startNext = (this.airports[indexStart]).getNext();
    Airport endNext = (this.airports[indexEnd]).getNext();
    Airport v = endNext;
    if(v != this.airports[indexStart]) {
      (this.airports[indexStart]).setNext(v);
      v.setParent(this.airports[indexStart]);
    }
    v = (this.airports[indexStart]).getParent();
    if(v != this.airports[indexEnd])
      v.setNext(startNext);
      (startNext).setParent(v);
      
    // 3.2
    (this.airports[indexStart]).setParent(null);
    (this.airports[indexEnd]).setNext(null);
  }
  
  // count the angle to airport three based on the airports one and two.
  private double countRefAngle(Airport one, Airport two) {
    double angle = countAngle(one, two);
//System.out.println("ANGLE " + one + " -> " + two + " = " + angle);
    if(angle == 0 || angle == this.refAngle)
      return 360.0;
      
    // Adjust the angle to the reference angle
    angle = (angle - this.refAngle);
    if(angle < 0)
      angle = 360 + angle;
    
    return angle;
  }
  
  // calculate how much the route distance increases if taking airport three between the airports one & two
  /*private double routeGrowth(Airport one, Airport two, Airport three) {
    double growth = (getDistance(one, three) + getDistance(three, two)) - getDistance(one, two);
//System.out.println("MATKA = " + growth);
    return (growth / getDistance(one, two));
  }*/
  
  /*private void removeAirport(Airport removed) {
    int size = this.airports.length;
    Airport[] backup = new Airport[size];
    for(int i = 0; i<size; i++) {
      backup[i] = this.airports[i];
    }

    this.airports = new Airport[size-1];
    int remu = 0;
    for(int i = 0; i<size; i++) {
      if(!backup[i].equals(removed))
        this.airports[i-remu] = backup[i];
      else
        remu = 1;
    }
  }*/

  private void checkRouteDirection(Airport start, Airport end, Airport bottomStart, Airport topStart) {
    // check if the route has went pass bottomStart/topStart
    boolean checkBottom = false, checkTop = false;
    if(start.equals(bottomStart))
      checkBottom = true;
    else if(start.equals(topStart))
      checkTop = true;
 
    // Format the tempVisited info for the airports
    for(Airport v : this.airports) {
      v.setTempVisited(false);
if(debugmode)
System.out.println(v + " = FALSE");
    }
    
if(debugmode) {
System.out.println("ROUTE DIR START: Start = " + start + " next " + start.getNext() + " End = " + end);
  if(start.getParent() != null) System.out.println("parent " + start.getParent());
System.out.println("bottomStart = " + bottomStart + " topStart = " + topStart);
}

    Airport backup = null;
    //Airport backupParent = null;
    int testi = 0;
    while(!start.equals(end)) { // && !start.getNext().equals(start.getParent())) {
      if(testi > 50) {
        System.out.println("ERROR: System error - Break");
        System.exit(0);
      }
      if(start != null)
      {
if(debugmode) {
  if(start.getParent()!=null)
    System.out.println(start + " next= " + start.getNext() + " " + start.getNext().tempVisited() + " parent = " + start.getParent() + " " + start.getParent().tempVisited()+ " backup = " + backup + " " + checkBottom + " " + checkTop);
  else
    System.out.println(start + " next= " + start.getNext() + " " + start.getNext().tempVisited() + " parent = " + start.getParent() + " backup = " + backup + " " + checkBottom + " " + checkTop);
}

        if( start.getNext() != null && start.getParent() != null && 
            start.getNext().tempVisited() && checkBottom && !checkTop) {
if(debugmode) {
  System.out.println(start + " 1--> " + start.getParent());
  System.out.println(backup + " 1<-- " + start);
}
          start.setNext(start.getParent());
          start.setParent(backup);
        } else if(checkBottom && checkTop && !start.getParent().tempVisited()) {
if(debugmode) {
  System.out.println(start + " 2--> " + start.getParent());
  System.out.println(backup + " 2<-- " + start);
}
          start.setNext(start.getParent());
          start.setParent(backup);
        } else {
if(debugmode) {
  System.out.println(start + " 3--> " + start.getNext());
  System.out.println(backup + " 3<-- " + start);
}
          start.setParent(backup);
        }

        if(start.getParent() == null)
          start.setParent(backup);
        start.setTempVisited(true);
if(debugmode)
System.out.println(start + " = TRUE");
      }

//System.out.println("Set TEMPVis: " + start);
      backup = start;
      //backupParent = start.getParent();
      start = start.getNext();        
      if(start.equals(bottomStart))
        checkBottom = true;
      else if(start.equals(topStart))
        checkTop = true;
      testi++;
    }
    if(start != null) {
if(debugmode)
System.out.println("ROUTE DIR END1: Start = " + start + " next " + start.getNext() + " start.parent = " + start.getParent());
      start.setParent(backup);
    } //else
//System.out.println("ROUTE DIR END2: Start = " + backup + " next " + backup.getNext() + " start.parent = " + backup.getParent());
    
  for(Airport v : this.airports) {
    v.setTempVisited(false);
  }
  
  }
  
  private Airport selectClosest(Airport port) {
  // KORJAA: VALITSE JOS EI VISITED
    Airport chosen = null;
    for(Airport v : this.airports) {
      if(!v.visited()) {
        chosen = v;
        break;
      }
    }
    if(chosen == null)
      return null;
    for(Airport v : this.airports) {
//System.out.println("DISTANCE " + v + " <-> " + port + " = " + getDistance(v, port));
      if(getDistance(v, port) < getDistance(chosen, port) && !v.visited()) {
        chosen = v;
//System.out.println(v + " JE!");
      }
//System.out.println(v + " " + v.visited());
    }
if(debugmode)
System.out.println("DISTANCE CHOSEN " + chosen + " <-> " + port + " = " + getDistance(chosen, port));    
    return chosen;
  }
  
  // Go through the route from start to end, and correct the turns under 45 degrees
  // A, B, C = Airports. If the turn from A -> B -> C, when coming from A, is under 45 degrees
  // go A -> C -> B.
  /*private void correctNarrowTurns(Airport start) {
    Airport A = null, B = null, C = null, D = null;
    Airport backup = null;
//System.out.println("Narrow start = " + start);
    while(true) {
//    for(int i=0; i<5; i++) {
      A = start; B = start.getNext(); C = B.getNext(); D = C.getNext();
//System.out.println("Angle A & B = " + countRefAngle(A, B));
//System.out.println("Angle C & B = " + countRefAngle(C, B));
      if( (Math.abs(countRefAngle(A, B) - countRefAngle(C, B))) < 45.0 &&
          (Math.abs(countRefAngle(B, C) - countRefAngle(D, C))) < 45.0 &&
          (getDistance(A, B) > getDistance(C, B))
          ) {
//System.out.println("Angle A & B = " + countRefAngle(A, B));
//System.out.println("Angle C & B = " + countRefAngle(C, B));
//System.out.println("CORRECTNARROWTURNS: A = " + A + " B = " + B + " C = " + C);
//System.out.println("CORRECTNARROWTURNS: " + A + " -> " + C + " -> " + B + " start.next.next = " + start.getNext().getNext().getNext());
        backup = C.getNext();
        A.setNext(C); C.setParent(A);
        C.setNext(B); B.setParent(C);
        B.setNext(backup); backup.setParent(B);
      }
      start = start.getNext();
      if(D.getNext() == null)
        break;
    }
  }*/

  // check if there's some shorter way to go the narrow turns situations
  // Airports: A, B, C, D, E
  // Narrow turn between airports B -> C -> D
  // Check the routes:
  //  1) A -> B -> D -> C -> E
  //  2) A -> D -> B -> C -> E
  //  3) A -> C -> B -> D -> E
  // A - B || A - B - 180
  // KESKEN
  private boolean correctNarrowTurns2(Airport start) {
    boolean returnVal = false;
    Airport A = null, B = null, C = null, D = null, E = null;
    //Airport backup = null;
if(debugmode)
System.out.println("Narrow2 start = " + start);
    while(true) {
      // 1) Go through the route, and check the situations when there's a narrow turn
      A = start;
      if(A.getNext() == null)
        break;
      B = start.getNext();
      if(B.getNext() == null)
        break;
      C = B.getNext();
      if(C.getNext() == null)
        break;
      D = C.getNext();
      if(D.getNext() == null)
        break;
      E = D.getNext();

//System.out.println("Angle B & C = " + countRefAngle(B, C) + " Angle D & C = " + countRefAngle(D, C) + " = " + (countRefAngle(B, C) - countRefAngle(D, C)));
//System.out.println("B = " + B + " C = " + C + " D = " + D);
      if( ((Math.abs(countRefAngle(B, C) - countRefAngle(D, C))) < 45.0 ||
          (Math.abs(countRefAngle(B, C) - countRefAngle(D, C) - 180)) < 45.0) ||
          ((Math.abs(countRefAngle(A, B) - countRefAngle(C, B))) < 45.0 ||
          (Math.abs(countRefAngle(A, B) - countRefAngle(C, B) - 180)) < 45.0)
        ){
        double dist1 = getDistance(A, B) + getDistance(B, D) + getDistance(D, C) + getDistance(C, E);
        double dist2 = getDistance(A, D) + getDistance(D, B) + getDistance(B, C) + getDistance(C, E);
        double dist3 = getDistance(A, C) + getDistance(C, B) + getDistance(B, D) + getDistance(D, E);
        double distO = getDistance(A, B) + getDistance(B, C) + getDistance(C, D) + getDistance(D, E);
        
//System.out.println("CORRECTNARROWTURNS2: Dist1 = " + (int)dist1 + " Dist2 = " + (int)dist2 + " Dist3 = " + (int)dist3 + " DistO = " + (int)distO);

        if(dist1 < dist2 && dist1 < dist3 && dist1 < distO) {
if(debugmode)
System.out.println("CORRECTNARROWTURNS2: Dist1");
          B.setNext(D); D.setParent(B);
          D.setNext(C); C.setParent(D);
          C.setNext(E); E.setParent(C);
          returnVal = true;
        } else if(dist2 < dist1 && dist2 < dist3 && dist2 < distO) {
if(debugmode)
System.out.println("CORRECTNARROWTURNS2: Dist2");
          A.setNext(D); D.setParent(A);
          D.setNext(B); B.setParent(D);
          C.setNext(E); E.setParent(C);
          returnVal = true;
        } else if(dist3 < dist1 && dist3 < dist1 && dist3 < distO) {
if(debugmode)
System.out.println("CORRECTNARROWTURNS2: Dist3");
          A.setNext(C); C.setParent(A);
          C.setNext(B); B.setParent(C);
          B.setNext(D); D.setParent(B);
          returnVal = true;
        }

        /*backup = D.getNext();
        A.setNext(D); D.setParent(A);
        D.setNext(B); B.setParent(D);
        C.setNext(backup); backup.setParent(C);*/
      }
      start = start.getNext();
      if(E.getNext() == null)
        break;
    }
    
    return returnVal;
  }
    
  // To be decided should these be used to get better results...
  // Maybe: correct the case, when the middle route should be went through
  // first the other side, then the other side (left side from down to up, then rigth side from down to up)
  // In some cases the route is shorter this way.
  /*private double correctSpecial1(Airport bottomStart, Airport topEnd, Airport realStart, Airport realEnd) {
    //this.airports = approximator();
    Airport now = null, backup = null, start = null, end = null;

    if(bottomStart.equals(realStart))
      if(getDistance(bottomStart, bottomStart.getNext()) < getDistance(bottomStart, bottomStart.getParent()))
        now = bottomStart.getNext();
      else
        now = bottomStart.getParent();
    else {
      now = selectClosest(bottomStart);
    }

    start = now;
    
    if(topEnd.equals(realEnd))
      if(getDistance(topEnd, topEnd.getNext()) < getDistance(topEnd, topEnd.getParent()))
        end = topEnd.getNext();
      else
        end = topEnd.getParent();
    else {
      end = selectClosest(topEnd);
    }

if(debugmode)
System.out.println("start = " + start + " end = " + end);
    while(!now.equals(end)) {
if(debugmode)
System.out.println("now = " + now);
      now = now.getNext();
    }
    now.setNext(start.getParent());
    backup = now;
    now = start.getParent();
    while(!now.equals(end)) {
      now.setNext(now.getParent());
      now.setParent(backup);
      backup = now;
      now = now.getNext();
    }
    now.setParent(backup);
    now.setNext(realEnd);
    realEnd.setParent(now);
    realStart.setNext(start);
    start.setParent(realStart);
    
    double dist = 0.0;
if(debugmode)
System.out.println("DIST: ");
    //while(start != null) {
    for(int i=0; i<5; i++) {
if(debugmode)
System.out.print(start + " -> ");
      dist += getDistance(start, start.getNext());
      start = start.getNext();
    }

    return dist;
  }
  
  // XXX
  private void correctSpecial2(Airport start) {
    Airport startcount = start;
    Airport startcount2 = null;
    Airport A=null, B=null, A2=null, A2Next=null, A2Parent=null;
    
    for(int i=0; i<5; i++) {
    //while(true) {
      A = startcount; B = startcount.getNext();
      startcount2 = start.getNext();

//      for(int j=0; j<5; j++) {
      while(true) {
        A2 = startcount2; A2Next = startcount2.getNext(); A2Parent = startcount2.getParent();
if(debugmode)
System.out.println("CORRECTSPECIAL2: A = " + A + " B = " + B + " A2 = " + A2 + " A2Parent = " + A2Parent + " A2Next = " + A2Next);
        
        if( A != null && B != null && A2 != null && A2Next != null && A2Parent != null &&
            getDistance(A, A2) + getDistance(A2, B) + getDistance(A2Parent, A2Next) < getDistance(A, B) + getDistance(A2Parent, A2) + getDistance(A2, A2Next) &&
            !A2.equals(A) && !A2.equals(B) && !A2Next.equals(A) && !A2Next.equals(B)
        ) {
if(debugmode)
System.out.println("CORRECTSPECIAL2: " + A + " -> " + A2 + " -> " + B + " JA " + A2Parent + " -> " + A2Next);
          A.setNext(A2); A2.setParent(A);
          A2.setNext(B); B.setParent(A2);
          A2Parent.setNext(A2Next); A2Next.setParent(A2Parent);
        }
        
        startcount2 = startcount2.getNext();
        if(A2Next.getNext() == null)
          break;
      }
            
      startcount = startcount.getNext();
      if(B.getNext() == null)
        break;
    }
  }*/
  
  // Go through the route and check the situations where there's an airport
  // really close to some route: check if that airport should be visited in that route,
  // instead of in the original order.
  private boolean correctSpecial3(Airport start) {
    boolean returnVal = false;
    Airport A = null, B = null;//, C = null, D = null, E = null;
    //Airport backup = null;
if(debugmode) System.out.println("Special3 start = " + start);
    while(true) {
      // 1) Go through the route, and check the situations when there's a port between
      // 1.1) Select the airports for the route to be checked
      A = start;
      if(A.getNext() == null)
        break;
      B = start.getNext();
      if(B.getNext() == null)
        break;
      // 1.2) Check if there's an airport close to the route between A->B
      returnVal = portBetween(A, B);
      if(returnVal)
        return true;
      // 2) Select next airport
      start = start.getNext();
      if(B.getNext() == null)
        break;
    }
    
    return returnVal;
  }
  
  // Check if there's any airport close to the route A->B
  private boolean portBetween(Airport A, Airport B) {
    double middleX = Math.min(A.coordX, B.coordX) + Math.abs(A.coordX - B.coordX) / 2;
    double middleY = Math.min(A.coordY, B.coordY) + Math.abs(A.coordY - B.coordY) / 2;
if(debugmode) {
System.out.println("PORTBETWEEN: A = " + A + " B =" + B + " MiddleX = " + middleX + " MiddleY = " + middleY);
System.out.println("PORTBETWEEN: A.coordX = " + A.coordX + " B.coordX = " + B.coordX + " A.coordY = " + A.coordY + " B.coordY = " + B.coordY);
}
    Airport smallest = null;
    double diff = 0.0, smallestDiff = Double.MAX_VALUE;
    double xDif = 0.0, yDif = 0.0;
    for(Airport v : this.airports) {
      if(!v.equals(A) && !v.equals(B) && v.getNext() != null && v.getParent() != null) {
        if(A.coordX - B.coordX != 0)
          xDif = Math.abs(v.coordX - middleX) / Math.abs(A.coordX - B.coordX);
        else
          xDif = Math.abs(v.coordX - middleX);
        if(A.coordY - B.coordY != 0)
          yDif = Math.abs(v.coordY - middleY) / Math.abs(A.coordY - B.coordY);
        else
          yDif = Math.abs(v.coordY - middleY);
        diff =  (xDif) + (yDif);
        if(diff < 0.4 && diff < smallestDiff) {
          smallest = v;
          smallestDiff = diff;
        }
if(debugmode)
System.out.println("F1: " + xDif + " + " + yDif + " = " + diff + " v.X = " + v.coordX + " v.Y = " + v.coordY + " " + v);
      }
    }
if(debugmode)
System.out.println("Smallest = " + smallest + " diff = " + smallestDiff);
    if(smallest != null) {
      Airport C = smallest.getParent();
      Airport D = smallest.getNext();
      if( getDistance(A, smallest) + getDistance(smallest, B) + getDistance(C, D) <
          getDistance(C, smallest) + getDistance(smallest, D) + getDistance(A, B)) {
if(debugmode)
System.out.println("*FOUND2 = " + smallest);
        A.setNext(smallest);
        smallest.setParent(A);
        smallest.setNext(B);
        B.setParent(smallest);
        C.setNext(D);
        D.setParent(C);
        return true;
      }
    }
    return false;
  }
  
  // Check if the searched route is on the way when going next()
  private boolean onTheRoute(Airport start, Airport searched, Airport realStart, Airport realEnd) {
    int check = 0;
    while(true) {
      if(start.equals(searched))
        return true;
      if(start.getNext() == null || start.getParent() == null || start.equals(realEnd) || start.equals(realStart) || check > 1000)
        break;
      start = start.getNext();
      check++;
    }
    return false;
  }
}
