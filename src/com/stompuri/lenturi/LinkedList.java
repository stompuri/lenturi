package com.stompuri.lenturi;
// *********************************************
// * Lenturi - Seppo Tompuri - 19.06.2010      *
// * Class "LinkedList"                        *
// * Used by the Lenturi program.              *
// * Used to store convex hull.                *
// *********************************************

//import java.io.*;
//import java.lang.*;
//import java.util.*;

public class LinkedList
{
  // private cell class for heap table
  //------------------------------------------
  private class Cell {
    Object o;
    // Constructors:
    private Cell(Object o) {
      this.o = o;
    }
  }
  //------------------------------------------

  int arrayIncrement = 50; // grows the heap array this amount when the array gets full
  private Cell[] list;
  public int listsize;
  public Cell first;
  public Cell last;
  
  // CONSTRUCTOR
  public LinkedList() {
    this.list = new Cell[arrayIncrement];
    this.listsize = 0;
  } 

  //-----------------------------------------------------
  // METHODS - Public
  //-----------------------------------------------------  
  // Printing, for debugging purposes
  public void printList() {
    for(int i=0; i<this.listsize; i++)
      System.out.println(this.list[i].o);
  }
  
  public void addFirst(Object o) {
    // If the list grows over the current list size, grow the size of the list
    if(this.listsize >= list.length)
      growList();
    
    // move the other values forward
    for(int i=this.listsize-1; i>=0; i--)
      this.list[i+1] = this.list[i];
    
    // Insert the new value
    this.list[0] = new Cell(o);
    
    // Grow the size of the list
    this.listsize++;
  }
  
  public Object removeFirst() {
    // If the list size has decreased enough (75% under), decrease the size of the list.
    if(this.listsize < list.length * .75)
      decreaseList();
    
    // Take out & remove the first value
    Object palaute = this.list[0].o;
    for(int i=0; i<this.listsize; i++)
      this.list[i] = this.list[i+1];
    
    // Decrease the size of the list
    this.listsize--;
    
    return palaute;
  }

  public Object getFirst() {
    if(this.list[0] != null)
      return this.list[0].o;
    else
      return null;
  }

  public Object get(int index) {
    if(this.list[index] != null)
      return this.list[index].o;
    else
      return null;
  }
    
  public void addLast(Object o) {
    // If the list grows over the current list size, grow the size of the list
    if(this.listsize >= list.length)
      growList();
    
    // Insert the new value
    this.list[this.listsize] = new Cell(o);
    
    // Grow the size of the list
    this.listsize++;
  }
  
  public Object removeLast() {
    // If the list size has decreased enough (75% under), decrease the size of the list.
    if(this.listsize < list.length * .75)
      decreaseList();
//    System.out.println("lsize="+this.listsize+" port= "+this.list[this.listsize-1].o);
    // Take out & remove the last value
    Object palaute = this.list[this.listsize-1].o;
    this.list[listsize-1] = null;
    
    // Decrease the size of the list
    this.listsize--;
    
    return palaute;
  }
  
  public Object getLast() {
    if(this.listsize > 0)
      return this.list[this.listsize-1].o;
    else
      return null;
  }
  
  public LinkedList clone() {
    LinkedList backup = new LinkedList();
    for(int i=0; i<this.listsize; i++)
      backup.addLast(this.list[i].o);

    return backup;
  }
  
  public int size() {
    return listsize;
  }

  public boolean contains(Object o) {
    for(int i=0; i<this.listsize; i++) {
      if(((o.getClass()).getName()).equals("Airport")) {
        if(((Airport)list[i].o).equals((Airport)o)) {
//System.out.println("\t*TRUE* " + o);
          return true;
        }
      } else {
        String eka = o.toString();
        String toka = list[i].o.toString();
//System.out.println("COMPARE: "+this.list[i].o+" == "+o+" Comparator = "+eka.equals(toka));
        if(eka.equals(toka)) {
//System.out.println("\t*TRUE* " + o);
          return true;
        }
      }
      
    }
//System.out.println("FALSE " + o);
    return false;
  }

  //-----------------------------------------------------
  // METHODS - Private
  //-----------------------------------------------------  
  private void growList() {
    Cell[] backup = new Cell[list.length];
    for(int i=0; i<list.length; i++)
      backup[i] = list[i];
    list = new Cell[list.length + arrayIncrement];
    for(int i=0; i<backup.length; i++)
      list[i] = backup[i];
  }

  private void decreaseList() {
    if(list.length == arrayIncrement)
      return;
    Cell[] backup = new Cell[list.length];
    for(int i=0; i<list.length; i++)
      backup[i] = list[i];
    list = new Cell[list.length - arrayIncrement];
    for(int i=0; i<list.length; i++)
      list[i] = backup[i];
  }
}
