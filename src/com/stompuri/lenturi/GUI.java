package com.stompuri.lenturi;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.SwingUtilities;
//import javax.swing.filechooser.*;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
//import static javax.swing.GroupLayout.Alignment.*;

public class GUI extends JFrame implements MouseInputListener {
  boolean debugmode = false;
  
  private static final long serialVersionUID = 1L;
  private Airport[] airports;
  private Airplane[] airplanes;
  private Airplane airplane;
  private static JFrame frame;
  private static JPanel rigthPanel;
  private static JFileChooser fc;
  private static JTextField filename;
  private static JTextField distance;
  private static Lenturi lenturi;
  private static Piirturi piirturi;
  
  private static JTextField planeSpeed;
  private static JTextField planeRange;
  private static JTextField planeFuelCapacity;
  private static JTextField planeFuelConsume;
  private static JTextField planeTakeOff;
  private static JTextField planeLanding;
  
  public GUI() {
    GUI.frame = null;
    GUI.rigthPanel = null;
    GUI.fc = null;
    GUI.filename = null;
    GUI.lenturi = null;
    GUI.piirturi = null;
    this.airports = null;
    this.airplane = null;
  }
  
  public static void main (String[] args) {
    GUI gui = new GUI();
    gui.go();
  }
  
  public void go() {
    // Fetch the airplane information
    getAirplanes();
    
    // Create the main frame for the window
    GUI.frame = new JFrame("Lenturi v1.0");
    GUI.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    // Create the interior for the draw panel
    GUI.piirturi = new Piirturi();
    
    // Create the button panel
    GUI.rigthPanel = new JPanel();
    
    //this.rigthPanel.setLayout(new BoxLayout(rigthPanel, BoxLayout.Y_AXIS));
    //this.rigthPanel.setLayout(new FlowLayout());
    
    // Initialize the airport file chooser
    fc = new JFileChooser(".");
    TxtFileFilter filter = new TxtFileFilter();
    fc.setFileFilter(filter);
    
    // Show the selected airport file
    GUI.filename = new JTextField(15);
    GUI.filename.setEditable(false);
    GUI.filename.setText("<Open airport file>");
    
    // Text area for travel distance
    GUI.distance = new JTextField(5);
    GUI.distance.setEditable(false);
    GUI.distance.setText("-");

    // Text area for airplane speed & range & fuel capacity & ...
    GUI.planeSpeed = new JTextField(3);
    GUI.planeSpeed.setEditable(false);
    GUI.planeSpeed.setFont(new Font("sansserif", Font.PLAIN, 10));
    GUI.planeRange = new JTextField(3);
    GUI.planeRange.setEditable(false);
    GUI.planeRange.setFont(new Font("sansserif", Font.PLAIN, 10));
    GUI.planeFuelCapacity = new JTextField(3);
    GUI.planeFuelCapacity.setEditable(false);
    GUI.planeFuelCapacity.setFont(new Font("sansserif", Font.PLAIN, 10));
    GUI.planeFuelConsume = new JTextField(3);
    GUI.planeFuelConsume.setEditable(false);
    GUI.planeFuelConsume.setFont(new Font("sansserif", Font.PLAIN, 10));
    GUI.planeTakeOff = new JTextField(3);
    GUI.planeTakeOff.setEditable(false);
    GUI.planeTakeOff.setFont(new Font("sansserif", Font.PLAIN, 10));
    GUI.planeLanding = new JTextField(3);
    GUI.planeLanding.setEditable(false);
    GUI.planeLanding.setFont(new Font("sansserif", Font.PLAIN, 10));
    
    // Add menu
    GUI.frame.setJMenuBar(createMenuBar());
    
    // Buttons
    JButton buttonClear = new JButton("Clear");
    buttonClear.addActionListener(new ClearListener());
    
    JButton buttonCalculate = new JButton("Round route");
    buttonCalculate.addActionListener(new CalculateListener());
    
    JButton buttonRoute = new JButton("One way route");
    buttonRoute.addActionListener(new RouteListener());
    
    JButton buttonOpen = new JButton("Open");
    buttonOpen.addActionListener(new OpenListener());
    
    // Drop down menu for airplane selection
    JComboBox airplaneList = new JComboBox(this.airplanes);
    airplaneList.setSelectedIndex(0);
    airplaneList.addActionListener(new PlaneChooserListener());
    airplaneList.setFont(new Font("sansserif", Font.PLAIN, 12));
    
    // Set the default selection for the airplane
    this.airplane = (Airplane)airplaneList.getSelectedItem();
    GUI.planeSpeed.setText(Integer.toString(this.airplane.getSpeed()));
    GUI.planeRange.setText(Integer.toString(this.airplane.getRange()));
    GUI.planeFuelCapacity.setText(Integer.toString(this.airplane.getFuelCapacity()));
    GUI.planeFuelConsume.setText(Integer.toString(this.airplane.getFuelConsume()));
    GUI.planeTakeOff.setText(Integer.toString(this.airplane.getTakeOffDist()));
    GUI.planeLanding.setText(Integer.toString(this.airplane.getLandingDist()));

    // Add mouse listeners into Piirturi draw panel
    piirturi.addMouseListener(GUI.this);
    piirturi.addMouseMotionListener(GUI.this);
    
    // Labels
    JLabel airportLabel = new JLabel("Airport File:");
    JLabel travelLabel = new JLabel("Travel distance:");
    JLabel airplaneLabel = new JLabel("Airplane:");
    
    // Instructions
    JLabel instRoundLabel = new JLabel("= Show round route.");
    instRoundLabel.setFont(new Font("sansserif", Font.PLAIN, 12));
    JLabel instOnewayLabel = new JLabel("= Show one way route.");
    instOnewayLabel.setFont(new Font("sansserif", Font.PLAIN, 12));
    JLabel instClearLabel = new JLabel("= Clear route & selections.");
    instClearLabel.setFont(new Font("sansserif", Font.PLAIN, 12));
    
    String labelText =
      "<html>" +
      //"<OL>" +
      "<B>1)</B> Select the airports file" +
      "<UL>" +
      "<LI><FONT SIZE=2>Click on a single airports.</FONT></LI>" +
      "<LI><FONT SIZE=2>Press down the mouse button and" +
      "<P>" +
      "drag the area you want to include in the selection.</FONT></LI>" +
      "</UL></LI>" +
      "<B>2)</B> Select the airports you want to visit from the map." +
      "<P>" +
      "<B>3)</B> In case you want to set a start and an end airport for the route double" +
      "<P>" +
      "double click on an airport to set it as start/end point." +
      "<P>" +
      "<B>4)</B> Click on <I>\"Round route\"</I> or on <I>\"One way route\"</I> button to see the route." +
      //"</OL>" +
      "</html>";
    JLabel instructionLabel = new JLabel(labelText);
    instructionLabel.setBorder(BorderFactory.createTitledBorder("Quickstart"));
    instructionLabel.setFont(new Font("sansserif", Font.PLAIN, 12));
    
    JLabel rangeLabel = new JLabel("Range:");
    rangeLabel.setFont(new Font("sansserif", Font.PLAIN, 10));
    JLabel speedLabel = new JLabel("Speed:");
    speedLabel.setFont(new Font("sansserif", Font.PLAIN, 10));
    JLabel fuelCapacityLabel = new JLabel("Fuel:");
    fuelCapacityLabel.setFont(new Font("sansserif", Font.PLAIN, 10));
    JLabel fuelConsumeLabel = new JLabel("Fuel consume:");
    fuelConsumeLabel.setFont(new Font("sansserif", Font.PLAIN, 10));
    JLabel takeOffLabel = new JLabel("Take Off Dist:");
    takeOffLabel.setFont(new Font("sansserif", Font.PLAIN, 10));
    JLabel landingLabel = new JLabel("Landing Dist:");
    landingLabel.setFont(new Font("sansserif", Font.PLAIN, 10));
    
    GroupLayout glayout = new GroupLayout(GUI.rigthPanel);
    GUI.rigthPanel.setLayout(glayout);
    glayout.setAutoCreateGaps(true);
    glayout.setAutoCreateContainerGaps(true);

    glayout.setHorizontalGroup(
      glayout.createParallelGroup()
        .addGroup(glayout.createSequentialGroup()
          .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(airportLabel)
            .addComponent(airplaneLabel)
            .addComponent(travelLabel)
            .addComponent(buttonCalculate)
            .addComponent(buttonRoute)
            .addComponent(buttonClear))
          .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(buttonOpen)
            .addComponent(filename)
            .addComponent(airplaneList)
            .addGroup(glayout.createSequentialGroup()
              .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(rangeLabel)
                .addComponent(speedLabel)
                .addComponent(fuelConsumeLabel))
              .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(planeRange)
                .addComponent(planeSpeed)
                .addComponent(planeFuelConsume))
              .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(fuelCapacityLabel)
                .addComponent(takeOffLabel)
                .addComponent(landingLabel))
              .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(planeFuelCapacity)
                .addComponent(planeTakeOff)
                .addComponent(planeLanding)))
            .addComponent(distance)
            .addComponent(instRoundLabel)
            .addComponent(instOnewayLabel)
            .addComponent(instClearLabel))
          )
          .addComponent(instructionLabel)
    );

    glayout.setVerticalGroup(
      glayout.createSequentialGroup()
        .addComponent(buttonOpen)
        .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(airportLabel)
          .addComponent(filename))
        .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(airplaneLabel)
          .addComponent(airplaneList))
        .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addGroup(glayout.createSequentialGroup()
            .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
              .addComponent(rangeLabel)
              .addComponent(planeRange)
              .addComponent(fuelCapacityLabel)
              .addComponent(planeFuelCapacity))
            .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
              .addComponent(speedLabel)
              .addComponent(planeSpeed)
              .addComponent(takeOffLabel)
              .addComponent(planeTakeOff))
            .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
              .addComponent(fuelConsumeLabel)
              .addComponent(planeFuelConsume)
              .addComponent(landingLabel)
              .addComponent(planeLanding))))
        .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(travelLabel)
          .addComponent(distance))
        .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(buttonCalculate)
          .addComponent(instRoundLabel))
        .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(buttonRoute)
          .addComponent(instOnewayLabel))
        .addGroup(glayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(buttonClear)
          .addComponent(instClearLabel))
        .addComponent(instructionLabel)
    );
    glayout.linkSize(SwingConstants.HORIZONTAL, buttonCalculate, buttonClear);

    GUI.frame.getContentPane().add(BorderLayout.EAST, rigthPanel);
    GUI.frame.getContentPane().add(BorderLayout.CENTER, piirturi);
    
    GUI.frame.pack();
    
    GUI.frame.setVisible(true);
  }

// ---------------------------------------------------------------
// INTERNAL METHODS
// ---------------------------------------------------------------
  private JMenuBar createMenuBar() {
    JMenuBar menuBar;
    JMenu menuFile, menuAction, menuHelp;
    JMenuItem menuItem;
    
    // Create the menu bar
    menuBar = new JMenuBar();
    // Build "File" menu
    menuFile = new JMenu("File");
    menuFile.setMnemonic(KeyEvent.VK_F);
    menuBar.add(menuFile);

    // Build "Action" menu
    menuAction = new JMenu("Action");
    menuAction.setMnemonic(KeyEvent.VK_A);
    menuBar.add(menuAction);
        
    // Build "Help" menu
    menuHelp = new JMenu("Help");
    menuBar.add(menuHelp);

    // FILE MENU
    // Add "Open" item in the "File" menu
    menuItem = new JMenuItem("Open Airports file",
                         KeyEvent.VK_O);
    menuItem.setMnemonic(KeyEvent.VK_O);
    menuItem.addActionListener(new OpenListener());
    menuFile.add(menuItem);
    // Add "Export to .kml" item in the "File" menu
    menuItem = new JMenuItem("Export to .kml",
                         KeyEvent.VK_X);
    menuItem.setMnemonic(KeyEvent.VK_X);
    menuItem.addActionListener(new ExportListener());
    menuFile.add(menuItem);

    // Add separator
    menuFile.addSeparator();

    // Add "Exit" item in the "File" menu
    menuItem = new JMenuItem("Exit",
                         KeyEvent.VK_E);
    menuItem.setMnemonic(KeyEvent.VK_E);
    menuItem.addActionListener(new ExitListener());
    menuFile.add(menuItem);
    
    // ACTION MENU
    // Add "Calculate" item in the "Action" menu
    menuItem = new JMenuItem("Calculate route",
                         KeyEvent.VK_R);
    menuItem.setMnemonic(KeyEvent.VK_R);
    menuItem.addActionListener(new CalculateListener());
    menuAction.add(menuItem);

    // Add separator
    menuAction.addSeparator();
        
    // Add "Clear" item in the "Action" menu
    menuItem = new JMenuItem("Clear",
                         KeyEvent.VK_C);
    menuItem.setMnemonic(KeyEvent.VK_C);
    menuItem.addActionListener(new ClearListener());
    menuAction.add(menuItem);

    // HELP MENU
    // Add "Instructions" item in the "Help" menu
    menuItem = new JMenuItem("Instructions");
    menuItem.addActionListener(new InstructionsListener());
    menuHelp.add(menuItem);
    // Add separator
    menuHelp.addSeparator();
    // Add "About" item in the "Help" menu
    menuItem = new JMenuItem("About");
    menuItem.addActionListener(new AboutListener());
    menuHelp.add(menuItem);
        
    return menuBar;
  }

  private void calculateRoute() {
    if(lenturi != null && lenturi.isAirports()) {
      String[] args = {"-e"};
      // Set airportsIn, the airports in the selection
      lenturi.setAirportsIn();
      lenturi.go(args);
      distance.setText(Integer.toString((int)lenturi.totalDistance));
      piirturi.updatePoints(airports);
      frame.repaint();
    } else {
      System.out.println("No airports defined!");
    }
  }
  private void calculateOneWayRoute() {
    if(lenturi != null && lenturi.isAirports() && piirturi.getEnderCount() == 2) {
if(debugmode)
System.out.println("*CLICK*");
      String[] args = {"-oneway"};
      // Set airportsIn, the airports in the selection
      lenturi.setAirportsIn();
      lenturi.go(args);
      distance.setText(Integer.toString((int)lenturi.totalDistance));
      piirturi.updatePoints(airports);
      frame.repaint();
    } else {
      System.out.println("No Start/End airports defined!");
    }
  }

  private void clearRoute() {
    if(airports != null) {
      lenturi = new Lenturi(airports);
      lenturi.clearSelections();
      piirturi.updatePoints(airports);
      piirturi.flag_pressed = false;
      piirturi.clearArea();
      distance.setText("-");
      frame.repaint();
    } else {
      System.out.println("No airports defined!");
    }
  }

  private void openFile() {
    int retu = fc.showDialog(GUI.this, "Select the airports file");
    if(retu == JFileChooser.APPROVE_OPTION) {
      File retuFile = fc.getSelectedFile();
      filename.setText(retuFile.getName());
      
      // Read the airport data from the text file
      // using the data from the first argument
      AirportFile airportsFile = new AirportFile(retuFile.getName());
      try {
        this.airports = airportsFile.savedata();
      } catch (FileNotFoundException e) {
        System.out.println("No such file!");
        return;
      }

      lenturi = new Lenturi(airports);
      piirturi.updatePoints(airports);
      frame.repaint();
    }
  }
  
  // Read the airplane data from the text file
  private void getAirplanes() {
    AirplaneFile airplaneFile = new AirplaneFile("airplanes.txt");
    try {
      this.airplanes = airplaneFile.savedata();
    } catch (FileNotFoundException e) {
      System.out.println("No such file!");
      return;
    }
  }

// ---------------------------------------------------------------
// ACTION LISTENERS
// ---------------------------------------------------------------
  class ClearListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      clearRoute();
    }
  }
  class CalculateListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      calculateRoute();
    }
  }
  class RouteListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      calculateOneWayRoute();
    }
  }
  
  class ExitListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      System.exit(0);
    }
  }
  
  class OpenListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      openFile();
    }
  }
  
  class ExportListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      if(lenturi != null && lenturi.isAirports()) {
        String[] args = {"-kml"};
        lenturi.go(args);
      } else {
        System.out.println("No airports defined!");
      }
    }
  }
  
  class AboutListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      String title = "About";
      String text = "Lenturi v0.8\nBy Seppo Tompuri 2010\n";
      TextBox aboutBox = new TextBox(title, text);
    }
  }

  class PlaneChooserListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      JComboBox cb = (JComboBox)event.getSource();
      airplane = (Airplane)cb.getSelectedItem();
      
      planeSpeed.setText(Integer.toString(airplane.getSpeed()));
      planeRange.setText(Integer.toString(airplane.getRange()));
      planeFuelCapacity.setText(Integer.toString(airplane.getFuelCapacity()));
      planeFuelConsume.setText(Integer.toString(airplane.getFuelConsume()));
      planeTakeOff.setText(Integer.toString(airplane.getTakeOffDist()));
      planeLanding.setText(Integer.toString(airplane.getLandingDist()));
    }
  }
  
  class InstructionsListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      String title = "Instructions";
      
      String text = "";
      
      // Read the text from the Instructions.txt file
      File workFile = new File("Instructions.txt");      
      try {
        Scanner fileScanner = new Scanner(workFile);
        while (fileScanner.hasNextLine()) {
          String workRow = fileScanner.nextLine();
          text = text.concat("\n");
          text = text.concat(workRow);
        }
        fileScanner.close();
      } catch (FileNotFoundException e) {
        System.out.println("No such file!");
        return;
      }
      text = text.concat("\n");

      TextBox instructionsBox = new TextBox(title, text);
    }
  }

// ---------------------------------------------------------------
// MOUSE METHODS
// ---------------------------------------------------------------
  public void mouseMoved(MouseEvent e) {
     
  }
  public void mouseDragged(MouseEvent e) {
    if(piirturi.flag_pressed) {
      piirturi.endX = e.getX();
      piirturi.endY = e.getY();
      frame.repaint();
//System.out.println("X = "+piirturi.endX+" Y = "+piirturi.endY);
    }
  }
  public void mousePressed(MouseEvent e) {
    piirturi.startX = e.getX();
    piirturi.startY = e.getY();
    piirturi.flag_pressed = true;
  }
  public void mouseReleased(MouseEvent e) {
    if(piirturi.flag_pressed) {
      piirturi.endX = e.getX();
      piirturi.endY = e.getY();
//System.out.println("StartX = " + piirturi.startX +" StartY = " + piirturi.startY);
//System.out.println("EndX = " + piirturi.endX +" EndY = " + piirturi.endY);

      for(Airport v : this.airports) {
//System.out.println(v);       
        if( v.coordX > piirturi.startX && v.coordX < piirturi.endX && v.coordY > piirturi.startY && v.coordY < piirturi.endY    // 1. left to bottomrigth
            || v.coordX > piirturi.startX && v.coordX < piirturi.endX && v.coordY < piirturi.startY && v.coordY > piirturi.endY // 2. left to toprigth
            || v.coordX < piirturi.startX && v.coordX > piirturi.endX && v.coordY > piirturi.startY && v.coordY < piirturi.endY // 3. rigth to bottomleft
            || v.coordX < piirturi.startX && v.coordX > piirturi.endX && v.coordY < piirturi.startY && v.coordY > piirturi.endY) // 4. rigth to topleft
          v.selected = true;
      }
      
      lenturi.airports = this.airports;
      // Clear the next values for the points, so the route selection is cleared.
      piirturi.updatePoints(this.airports);
      lenturi.setAirportsIn();
    }
    
    piirturi.flag_pressed = false;
    frame.repaint();
  }
  // CLICKED: If the point is not selected, select it.
  // If the point is selected, deselect it.
  public void mouseClicked(MouseEvent e) {
    this.airports = piirturi.checkPoint(airports, e.getX(), e.getY(), e.getClickCount());
    frame.repaint();
  }
  public void mouseEntered(MouseEvent e) {
     //piirturi.flag_pressed = false;
  }
  public void mouseExited(MouseEvent e) {

  }
}
