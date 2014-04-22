import processing.serial.*;

import java.util.*;

import ddf.minim.*;
import ddf.minim.spi.*;
import ddf.minim.signals.*;

import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;
/*
   * "Phonoblocks"
 * 
 * mock-up of the 'letter game' for dyslexic children. children can combine
 * letters into strings; the system determines the pronounciation of the
 * string given the letter's combinations.
 * 
 * Author: Emily Cramer Dec. 18 2013
 */

public static final int DISPLAY_BUFFER = 200;
public static final int DISPLAY_WIDTH = (NGram.NUM_VOWELS + 1)
* (PhonoBlocksUI.TILE_WIDTH + PhonoBlocksUI.TILE_MARGIN)
+ DISPLAY_BUFFER;
public static final int DISPLAY_HEIGHT = NGram.NUM_CONSONANTS
* (PhonoBlocksUI.TILE_WIDTH + PhonoBlocksUI.TILE_MARGIN)
+ DISPLAY_BUFFER;

public static final String DATA_FOLDER_PATH = "C:\\Users\\Emily\\workspace\\PhonoBlocks\\data\\";
public static Minim MINIM;

private PhonoBlocksUI ui;

Serial myPort; // Create object from Serial class
int val; // Data received from the serial port

//!!!! for testing!!!!
boolean testing = true;
int time;
PrintWriter outputWriter;



public void setup() {
  size(DISPLAY_WIDTH, DISPLAY_HEIGHT);
  MINIM = new Minim(this);

  //String portName = "/dev/tty.usbserial-A4001sCD";

  //myPort = new Serial(this, "COM3", 9600);

  ui = new PhonoBlocksUI(this);
  ui.build();


  time = minute();
  outputWriter=createWriter("test_output.txt");
}

public void draw() {

  ui.drawUI();
  if (time < minute()) {
    monitorArduinoPort();
    time = minute();
  }
}

public void printColors() {
  float[][] colors = ui.getColorsAtEachPosition();
  for (int i=0;i<colors.length;i++) {
    float[] aColor=colors[i];
    System.out.println(aColor[0]+" "+aColor[1]+" "+aColor[2]);
  }
}

public void monitorArduinoPort() {
  if (testing) { //|| myPort.available() > 0) {
    //we need to get it to read input from the arduino
    Object[] data = getLetterAndPosition();
    String letter = getLetter();//(String) data[0];
    int relativePosition = getRelativePosition();//(int) data[1];


    //
    ui.handleLetterPlacement(letter, relativePosition);


    //need for each position that is occupied
    //this willl be updated after handle letter placement is called.

    float[][] output = ui.getColorsAtEachPosition();
    printOutput(output);
  }
}

void printOutput(float[][] output) {
  for (int i=0;i<output.length;i++) {
    float[] col=output[i];
    float r=col[0];
    float g=col[1];
    float b=col[2];
    outputWriter.println("Position "+i+" r "+r+" g "+g+" b "+b);
  }
}

void keyPressed() {
  if (key=='k')
    printOutput(ui.getColorsAtEachPosition());
  if (key=='q') {
    outputWriter.flush();
    outputWriter.close();
    exit();
  }
}

public void mouseClicked() {
  ui.handleMouseClick(mouseX, mouseY);
}

// dummy method that simulates action of arduino
public Object[] getLetterAndPosition() {
  return new Object[] { 
    getLetter(), getRelativePosition()
    };
  }

  public String getLetter() {
    ArrayList<NGram> letters = NGram.allLetters();
    int randNGram = (int) random(letters.size());
    return letters.get(randNGram).asString();
  }

public int getRelativePosition() {
  return (int) random(8);
}

