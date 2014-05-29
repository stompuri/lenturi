package com.stompuri.lenturi;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
//import javax.swing.event.MouseInputListener;
//import java.awt.event.MouseEvent;

class Piirturi extends JPanel {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private Point[] points;
  public int sizeX;
  public int sizeY;
  private double maxX;
  private double maxY;
  private double minX;
  private double minY;
  private double multiplierX;
  private double multiplierY;
  private BufferedImage bg;
  private int enderCount;
  
  public boolean flag_pressed;
  public int startX, startY;
  public int endX, endY;
  
  //private LinkedList selected;
  
  //Private class "Point" for storing routes from one Airport to other
  //------------------------------------------
  private static class Point
  {
    String name;
    int x, y;
    Airport next;
    boolean selected;
    boolean ender;
    
    // CONSTRUCTORS
    private Point() {
      this.name = "No name";
      this.x = 0;
      this.y = 0;
      this.next = null;
      this.selected = false;
      this.ender = false;
    }
    private Point(String name, int x, int y, Airport next, boolean selected, boolean ender) {
      this.name = name;
      this.x = x;
      this.y = y;
      this.next = next;
      this.selected = selected;
      this.ender = ender;
    }
    
    public String toString() {
      return "[" + this.x + ", " + this.y + "]";
    }
  }
  //------------------------------------------
  
  public Piirturi() {
    this.enderCount = 0;
    this.bg = null;
    this.flag_pressed = false;
    try {
        this.bg = ImageIO.read(new File("resources/suomi_small.jpg"));
        this.sizeX = bg.getWidth(null);
        this.sizeY = bg.getHeight(null);
    } catch (IOException ex) {
        System.exit(0);
    }
    
    setPreferredSize(new Dimension(this.sizeX, this.sizeY));
    
    // Coordinates for the background image
    // min = top left corner, max = bottom rigth corner
    this.minX = 19.193115234375;
    this.minY = 70.24262304379806;
    this.maxX = 31.981201;
    this.maxY = 59.675877;

    this.multiplierX = this.sizeX / (this.maxX - this.minX);
    this.multiplierY = this.sizeY / (this.minY - this.maxY);

    points = null;
    this.startX = 0; this.startY = 0;
    this.endX = 0; this.endX = 0;
  }
  
  public void paint(Graphics g) {
    g.drawImage(this.bg, 0, 0, this.sizeX, this.sizeY, 0, 0, this.sizeX, this.sizeY, null);
    
    if(points != null) {
      for(Point p : points) {
        // Draw the name and mark the airport
        g.setColor(Color.red);
        g.setFont(new Font("sansserif", Font.BOLD, 13));
        g.drawString(p.name, p.x + 8, p.y);

        // Select airport oval color (selected/not selected)
        // and draw the outline for the airport marker
        if(p.ender) {
          g.setColor(Color.red);
          g.fillOval(p.x-2, p.y-2, 12, 12);
        }
        else {
          g.setColor(Color.black);
          g.fillOval(p.x-1, p.y-1, 10, 10);
        }
        
        // and draw the inner area of the airport marker
        if( (p.x > startX && p.y > startY && p.x < endX && p.y < endY)    // 1. left to bottomrigth
            || (p.x > startX && p.y < startY && p.x < endX && p.y > endY) // 2. left to toprigth
            || (p.x < startX && p.y > startY && p.x > endX && p.y < endY) // 3. rigth to bottomleft
            || (p.x < startX && p.y < startY && p.x > endX && p.y > endY) // 4. rigth to topleft
            || p.selected)
          g.setColor(Color.yellow);
        else
          g.setColor(Color.blue);
        g.fillOval(p.x, p.y, 8, 8);
        
        // Draw the flight route from the point to next
        g.setColor(Color.blue);     
        if(p.next != null) {
          Point lineEnd = getPoint(p.next);
          g.drawLine(p.x+4, p.y+4, lineEnd.x+4, lineEnd.y+4);
        }
      }
    }
    
    // if mouse is dragged, draw a rectangle showing the area being selected
    if(this.flag_pressed) {
      // 1. dragging from left to bottomrigth
      if(this.endX > this.startX && this.endY > this.startY)
        g.drawRect(this.startX, this.startY, this.endX-this.startX, this.endY-this.startY);
      // 2. dragging from rigth to topleft
      else if(this.endX < this.startX && this.endY < this.startY)
        g.drawRect(this.endX, this.endY, this.startX-this.endX, this.startY-this.endY);
      // 3. dragging from rigth to bottomleft
      else if(this.endX < this.startX && this.endY > this.startY)
        g.drawRect(this.endX, this.startY, this.startX-this.endX, this.endY-this.startY);
      // 4. dragging from left to bottomrigth
      else if(this.endX > this.startX && this.endY < this.startY)
        g.drawRect(this.startX, this.endY, this.endX-this.startX, this.startY-this.endY);
    }
  }
  
  private Point getPoint(Airport v) {
    int x = (int)((v.d_longitude - this.minX) * this.multiplierX);
    int y = (int)((this.minY - v.d_latitude) * this.multiplierY);
    v.coordX = x;
    v.coordY = y;
    return new Point(v.waypoint, x, y, v.getNext(), v.selected, v.ender);
  }
  
  public Airport[] checkPoint(Airport[] airports, int clickX, int clickY, int clickCount) {
    int selectArea = 8;
    for(Point p : points) {
      if(clickX >= (p.x) && clickX <= (p.x+selectArea) && clickY >= (p.y) && clickY <= (p.y+selectArea)) {
        Airport ap = getPointAirport(airports, p);
//System.out.println("CLICK COUNT = " + clickCount);
        if(p.selected && clickCount != 2) {
            p.selected = false;
            ap.selected = false;
        } else {
          p.selected = true;
          ap.selected = true;
        }
        
        if(clickCount == 2 && this.enderCount < 2) {
          if(!p.ender)
            this.enderCount++;
          p.ender = true;
          ap.ender = true;
        } else {
          if(p.ender)
            this.enderCount--;
          p.ender = false;
          ap.ender = false;
        }
      }
    }
    return airports;
  }
  
  // Find the match for the point from the airports.
  // Return the airport matching the point.
  private Airport getPointAirport(Airport[] airports, Point p) {
    for(Airport v : airports) {
      int x = (int)((v.d_longitude - this.minX) * this.multiplierX);
      int y = (int)((this.minY - v.d_latitude) * this.multiplierY);
      if(x == p.x && x == p.x && y == p.y && y == p.y) {
        return v;
      }
    }
    return null;
  }
  
  // Update points values
  public void updatePoints(Airport[] airports) {
    this.points = new Point[airports.length];
      
    int counter = 0;
    this.enderCount = 0;
    for(Airport v : airports) {
      if(v.ender)
        this.enderCount++;
      this.points[counter] = getPoint(v);
      counter++;
    }
  }

  public void clearPoints() {
    for(Point p : points) {
      p.selected = false;
      if(p.ender) {
        this.enderCount--;
        p.ender = false;
      }
    }
  }
    
  public void clearArea() {
    this.startX = 0; this.startY = 0;
    this.endX = 0; this.endX = 0;
  }
  
  public int getEnderCount() {
    return this.enderCount;
  }
}
