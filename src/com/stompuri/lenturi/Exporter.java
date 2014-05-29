package com.stompuri.lenturi;
import java.io.*;
//import java.util.*;

// Class for exporting Airport network into a Google Earth kml file
public class Exporter {
  private String fileName;
  
  // CONSTRUCTOR
  public Exporter(String fileName) {
    this.fileName = fileName;
  }
  
  // PUBLIC METHODS 
  
  // "savefile" method prints the given airports into a kml file
  public void savefile(Airport[] airports, int start) throws FileNotFoundException {
    //int rownumber = 0;
    int fileNmb = 0;
    
    // Check if the given file already exists
    File workFile = new File(this.fileName + "_" + fileNmb + ".kml");
    while(workFile.exists()) {
      fileNmb++;
      workFile = new File(this.fileName + "_" + fileNmb + ".kml");
    }
        
    // Print to file
    PrintWriter output = new PrintWriter(this.fileName + "_" + fileNmb + ".kml"); 
    
    output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    output.println("<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
    output.println("<Folder>");
    output.println("<name>Airports</name>");
    
    for(Airport aa : airports) {
      output.println("<Placemark><name>" + aa.waypoint + "</name><Point><coordinates>" + 
                    aa.d_longitude + "," + aa.d_latitude + "</coordinates></Point></Placemark>");
    }

    // Print the route into the file
    //-------------------------------------
    output.println("<Placemark>");
    output.println("<name>Route</name>");
    output.println("<LineString><tessellate>1</tessellate><extrude>1</extrude>");
    output.println("<altitudeMode>clampedToGround</altitudeMode>");
    output.println("<coordinates>");
    Airport starter = airports[start];
    Airport ender = starter;
    do {
      output.println(starter.d_longitude + "," + starter.d_latitude + ",0.0");
      starter = starter.getNext();
    } while(starter != ender && starter != null);
    //output.println(starter.d_longitude + "," + starter.d_latitude + ",0.0");
    output.println("</coordinates>");
    output.println("</LineString>");
    output.println("<Style><LineStyle><color>FFE600E6</color><width>4</width></LineStyle></Style>");
    output.println("</Placemark>");
    //-------------------------------------

    output.println("</Folder>");
    output.println("</kml>");
    output.close();
  }
  
}
