package com.stompuri.lenturi;
import java.io.*;

class TxtFileFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File workfile) {
        return workfile.isDirectory() || workfile.getName().toLowerCase().endsWith(".txt");
    }
    
    public String getDescription() {
        return ".txt files";
    }
}
