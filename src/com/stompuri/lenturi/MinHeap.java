package com.stompuri.lenturi;
// *********************************************
// * Lenturi v0.1 - Seppo Tompuri - 05.06.2010 *
// * Class "MinHeap"                           *
// * Used by the Lenturi program.              *
// * Used to store aiports in minimum order.   *
// *********************************************

//import java.io.*;
//import java.lang.*;

public class MinHeap
{
  // private cell class for heap table
  //------------------------------------------
  private class Cell {
    Airport o;
    double key;
    // Constructors:
    private Cell(Airport o, double key) {
      this.o = o;
      this.key = key;
    }
    // Methods:
    public String toString() {
      return "[" + this.key + "]";
    }
  }
  //------------------------------------------

  int arrayIncrement = 50; // grows the heap array this amount when the array gets full
  private Cell[] heap;
  public int length;
  public int heapsize;
  public Cell root;
  
  // CONSTRUCTOR
  public MinHeap () {
    this.heap = new Cell[arrayIncrement];
    this.length = arrayIncrement;
    this.heapsize = 0;
    this.root = null;  
  } 

  // Adds Airport o with the key value of k into the minHeap
  public void heapInsert(Airport o, double k) {
    // If heap grows over the current heap size, grow the size of the heap
    if(heapsize > heap.length)
      growHeap();

    // Find the place where to insert the new value
    int i = this.heapsize;
    while(i > 0 && this.heap[i/2].key > k) {
      this.heap[i] = this.heap[i/2];
      i = i/2;
    }

    this.heap[i] = new Cell(o, k);
    this.heapsize++;
  }

  // Check if heap contains Airport, returns true/false
  public boolean contains(Airport o) {
    for(int i=0; i<heapsize; i++) {
      if(this.heap[i].o == o)
        return true;
    }
    return false;
  }
  
  // Deletes and returns the minimum object from the heap (=topmost item, as it's a minHeap)
  public Airport heapDelMin() {
    if(this.heapsize > 0) {
      Airport min = (Airport)this.heap[0].o;
      this.heap[0] = this.heap[this.heapsize-1];
      this.heapsize--;
      this.heapify(0);
  
      // If the heap size has decreased enough (25% under), decrease the size of the heap.
      if(heapsize < heap.length * .25)
        decreaseHeap();
  
      return min;
    }
    return null;
  }

  // Returns the minimum object from the heap (=topmost item, as it's a minHeap)
  public Airport getMin() {
    return (Airport)this.heap[0].o;
  }
  
  // Debugging purposes: print the heap
  public void printHeap() {
    for(int i=0; i<this.heapsize; i++) {
      System.out.println("**HEAP: " + this.heap[i].o + " key = " + this.heap[i].key);
    }   
  }

  
  // Sets a new key value for Airport o
  public void heapDecKey(Airport o, double newkey) {
    for(int i=0; i<heapsize; i++) {
      if(this.heap[i].o == o) {
        if(newkey < this.heap[i].key) {
          this.heap[i].key = newkey;
          while(i>0 && this.heap[i].key < this.heap[i/2].key) {
            Cell varasto = this.heap[i/2];
            this.heap[i/2] = this.heap[i];
            this.heap[i] = varasto;
            i = i/2;            
          }
        }
        return;
      }
    }
  }
  
  private void heapify(int i) {
    int smallest;
    int l_child = 2 * (i+1) - 1;
    int r_child = 2 * (i+1);
    if(r_child <= this.heapsize - 1) {
      if(this.heap[l_child].key < this.heap[r_child].key)
        smallest = l_child;
      else
        smallest = r_child;

      if(this.heap[i].key > this.heap[smallest].key) {
        Cell vaihdokas = new Cell(this.heap[i].o, this.heap[i].key);
        this.heap[i] = this.heap[smallest];
        this.heap[smallest] = vaihdokas;
        heapify(smallest);
      }
    } else if(l_child == this.heapsize - 1 && this.heap[i].key > this.heap[l_child].key) {
        Cell vaihdokas = new Cell(this.heap[i].o, this.heap[i].key);
        this.heap[i] = this.heap[l_child];
        this.heap[l_child] = vaihdokas;  
    }
  }
  
  private void growHeap() {
    Cell[] backup = new Cell[heap.length];
    for(int i=0; i<heap.length; i++)
      backup[i] = heap[i];
    heap = new Cell[heap.length + arrayIncrement];
    for(int i=0; i<backup.length; i++)
      heap[i] = backup[i];
  }

  private void decreaseHeap() {
    if(heap.length == arrayIncrement)
      return;
    Cell[] backup = new Cell[heap.length];
    for(int i=0; i<heap.length; i++)
      backup[i] = heap[i];
    heap = new Cell[heap.length - arrayIncrement];
    for(int i=0; i<heap.length; i++)
      heap[i] = backup[i];
  }
}
