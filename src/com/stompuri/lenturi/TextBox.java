package com.stompuri.lenturi;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.SwingUtilities;

public class TextBox extends JFrame implements ActionListener {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private static JFrame frame;
  private static JTextArea textArea;
  //private static JPanel textPanel;
  private static JPanel buttonPanel;
  
  public TextBox(String title, String text) {
    createTextBox(title, text);
  }
  
  public void createTextBox(String title, String text) {
    TextBox.frame = new JFrame(title);
    TextBox.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    TextBox.textArea = new JTextArea(text);
    TextBox.textArea.setEditable(false);
    TextBox.textArea.setBorder(BorderFactory.createLineBorder(Color.black));
    //this.textPanel.add(this.textArea);

    // Create the button panel
    TextBox.buttonPanel = new JPanel();

    // Add the OK Button
    JButton button = new JButton("OK");
    button.addActionListener(this);
    TextBox.buttonPanel.add(button);
    
    TextBox.frame.getContentPane().add(BorderLayout.NORTH, TextBox.textArea);
    TextBox.frame.getContentPane().add(BorderLayout.SOUTH, TextBox.buttonPanel);
    
    TextBox.frame.pack();
    TextBox.frame.setVisible(true);
  }
  
  public void actionPerformed(ActionEvent event) {
    TextBox.frame.setVisible(false);
  }
}
