

import processing.core.*;


import java.util.*;
import java.awt.*;

public class GramTile extends Tile {
  // fields

  private GramTile leftNeighbor;
  private GramTile rightNeighbor;

  private Phonogram phonogram;
  private NGram grapheme;
  private ArrayList<GramTile> chain;
  private Color color;



  public GramTile(int tileWidth_, int tileHeight_, int x, int y, 
  PApplet canvas) {
    super(tileWidth_, tileHeight_, x, y, canvas);
    chain = new ArrayList<GramTile>();
    color=new Color(0);
  }

  public GramTile(int tileWidth_, int tileHeight_, int x, int y) {
    super(tileWidth_, tileHeight_, x, y);
    chain = new ArrayList<GramTile>();
  }


  public void onClick() {
    notifyObservers(null);
  }

  //
  public void chain() {
    chain.clear();
    ArrayList<GramTile> leftHalf = new ArrayList<GramTile>();
    ArrayList<GramTile> rightHalf = new ArrayList<GramTile>();
    chainLeft(leftHalf);
    chainRight(rightHalf);
    chainTogether(leftHalf, rightHalf);
  }


  private void chainTogether(ArrayList<GramTile> leftHalf, 
  ArrayList<GramTile> rightHalf) {
    chain.addAll(leftHalf);
    if (grapheme!=null) chain.add(this);
    chain.addAll(rightHalf);
  }

  private void chainLeft(ArrayList<GramTile> chain) {
    if (hasLeftNeighborWithGrapheme()) {
      chain.add(0, leftNeighbor);
      leftNeighbor.chainLeft(chain);
    }
  }

  private void chainRight(ArrayList<GramTile> chain) {
    if (hasRightNeighborWithGrapheme()) {
      chain.add(rightNeighbor);
      rightNeighbor.chainRight(chain);
    }
  }

  public ArrayList<NGram> getNGramChain() {
    ArrayList<NGram> result = new ArrayList<NGram>();
    for (GramTile g : chain)
      result.add(g.grapheme);
    return result;
  }



  public GramTile getHead() {
    if (chain.size() > 0)
      return chain.get(0);
    return this;
  }

  @Override
    public void draw(Color c) {
    draw();
  }

  @Override
    public void draw() {
    drawTileBackground();
    if (grapheme != null)
      drawGrapheme();
  }

  private void drawGrapheme() {
    canvas.fill(color.r, color.g, color.b);
    float textSize = (float) ((float) tileHeight / 1.5);
    canvas.textSize(textSize);
    canvas.text(grapheme.asString(), position.x - textSize / 2, position.y
      + textSize / 2);
  }

  public void updateGrapheme(NGram selectedGrapheme) {
    grapheme = selectedGrapheme;
  }

  public boolean isNeighborOfATileWithAGrapheme() {
    return (hasLeftNeighborWithGrapheme()|| hasRightNeighborWithGrapheme());
  }

  private boolean hasLeftNeighborWithGrapheme() {
    return (leftNeighbor != null && leftNeighbor.grapheme != null);
  }

  private boolean hasRightNeighborWithGrapheme() {
    return (rightNeighbor != null && rightNeighbor.grapheme != null);
  }

  public void setLeftNeighbor(GramTile gramTile) {
    leftNeighbor = gramTile;
  }

  public void setRightNeighbor(GramTile gramTile) {
    rightNeighbor = gramTile;
  }

  /* temp methods for testing */
  public GramTile getRightNeighbor() {
    return rightNeighbor;
  }

  public GramTile getLeftNeighbor() {
    return leftNeighbor;
  }

  public void updatePhonogram(Phonogram p) {
    phonogram=p;
    setColorGivenPhonogram();
  }

  private void setColorGivenPhonogram() {
    if (phonogram.isSilent) color=SpeechSoundColorMap.SILENT;
    else color=SpeechSoundColorMap.getColorForSpeechSound(phonogram.sound);
  }


  //for testing
  public Phonogram getPhonogram() {
    // TODO Auto-generated method stub
    return phonogram;
  }

  public NGram getGrapheme() {
    return grapheme;
  }

  public void playSound() {
    if (hasPhonogram()) phonogram.playSound();
  }

  public boolean hasPhonogram() {
    return phonogram!=null;
  }

  public boolean needToReloadPhonogramSound() {
    return (hasPhonogram()&&phonogram.needToReloadSound());
  }

  public void reloadPhonogramSound() {
    if (hasPhonogram()) phonogram.loadSound();
  }

  public void setToBlank() {
    grapheme=null;
    phonogram=null;
  }

  public boolean isEndGramTile() {
    return (!hasLeftNeighborWithGrapheme() || !hasRightNeighborWithGrapheme());
  }
}

