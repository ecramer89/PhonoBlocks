import processing.core.*;
import ddf.minim.*;

import java.util.*;
import java.awt.Point;

public class PhonoBlocksUI implements Observer {

  public static final int TILE_WIDTH = 50;
  public static final int TILE_HEIGHT = 50;
  public static final int TILE_MARGIN = 10;
  public static final int MAX_GRAMS = 8;
  private static final int FIRST_TURN = 0;
  private static final Color BG_COLOR = new Color(150, 180, 200);
  public static final String SOUND_TILE_GRAPHIC_FILENAME = "soundTileGraphic";
  public static final String ERASE_TILE_GRAPHIC_FILENAME = "eraseTileGraphic";
  private static final String SOUND_TILE = "soundTile";
  private static final String ERASE_TILE = "eraseTile";

  private GraphemeTile[] graphemeTiles;
  private int numVowels;
  private int numConsonants;
  private ArrayList<GramTile> gramTiles;
  private GramTile activeGramTile;
  private ActionTile soundTile;
  private ActionTile eraseTile;
  private PImage soundTileGraphic;
  private PImage eraseTileGraphic;
  ;
  private ArrayList<Phonogram> phonograms;
  private LinkedList<Tile> tiles;
  private PhonoBlocksUIBuilder uIBuilder;
  private PhonoBlocks canvas;

  // user response variables
  private NGram selectedGrapheme;
  private int turn;

  public static final int NUM_POSITIONS = 7;
  float[][] colorsAtEachPosition;

  public PhonoBlocksUI(PhonoBlocks canvas_) {
    numVowels = NGram.NUM_VOWELS;
    numConsonants = NGram.NUM_CONSONANTS;
    graphemeTiles = new GraphemeTile[numVowels + numConsonants];
    gramTiles = new ArrayList<GramTile>();
    tiles = new LinkedList<Tile>();
    uIBuilder = new PhonoBlocksUIBuilder();
    canvas = canvas_;
    turn = FIRST_TURN;
    initializeColorsAtEachPosition();
  }

  private void initializeColorsAtEachPosition() {
    colorsAtEachPosition = new float[NUM_POSITIONS][];
    setAllColorsToWhite(colorsAtEachPosition);
  }

  private void setAllColorsToWhite(float[][] colorsAtPositions) {
    for (int i = 0; i < NUM_POSITIONS; i++)
    colorsAtPositions[i] = new float[] { 
      0, 0, 0
    };
  }

  public void build() {
    canvas.background(BG_COLOR.r, BG_COLOR.b, BG_COLOR.g);
    uIBuilder.build();
    putTilesInOneListAndAddThisAsObserver();
  }

  public void putTilesInOneListAndAddThisAsObserver() {
    for (GraphemeTile gt : graphemeTiles) {
      tiles.add(gt);
      gt.addObserver(this);
    }
    for (GramTile grt : gramTiles) {
      tiles.add(grt);
      grt.addObserver(this);
    }

    tiles.add(soundTile);
    tiles.add(eraseTile);
    soundTile.addObserver(this);
    eraseTile.addObserver(this);
  }

  public void drawUI() {
    for (Tile t : tiles)
      t.draw();
  }

  public void handleMouseClick(float mouseX, float mouseY) {
    for (Tile t : tiles) {
      if (t.mouseOn(mouseX, mouseY)) {
        t.onClick();
      } 
      else
        t.setSelected(false);
    }
  }

  /*
	 * 
   	 */
  public void handleLetterPlacement(String letter, int relativePosition) {
    rememberCurrentLetterAsNGram(letter);
    renderLetterToTileIfPossible(relativePosition);
    rememberNewColorsAtTilePositions();
  }

  /*output for arduino; the colors that should appear at each place in the board. 0 if no color */
  private void rememberNewColorsAtTilePositions() {
    int index=0;
    for (GramTile gt : gramTiles) {
      Color col = SpeechSoundColorMap.getColorForSpeechSound(gt.getPhonogram().sound);
      col=(col==null?new Color(0, 0, 0):col);
      putRGBValuesAtPosition(index, col);
      index++;
    }
  }

  void putRGBValuesAtPosition(int indexOfColor, Color color) {
    float r=color.r;
    float g=color.g;
    float b=color.b;
    colorsAtEachPosition[indexOfColor]=new float[] {
      r, g, b
    };
  }


/* getter method for the array of colors that appear at each position*/
  public float[][] getColorsAtEachPosition() {
    return colorsAtEachPosition;
  }

  private void rememberCurrentLetterAsNGram(String letter) {
    updateSelectedGrapheme(NGram.asNGram(letter));
  }

  private void renderLetterToTileIfPossible(int relativePositionOfTile) {
    GramTile positionOfLetter = gramTiles.get(relativePositionOfTile);
    handleClickedGramTile(positionOfLetter);
  }

  private void updateTurn() {
    turn++;
  }

  @Override
    public void update(Observable arg0, Object arg1) {
    if (arg0 instanceof GraphemeTile) {
      handleClickedGraphemeTile((GraphemeTile) arg0, (NGram) arg1);
    } 
    else if (arg0 instanceof GramTile) {
      handleClickedGramTile((GramTile) arg0);
    } 
    else if (arg0 instanceof ActionTile) {
      handleClickedActionTile((ActionTile) arg0);
    }
  }

  private void handleClickedGraphemeTile(GraphemeTile graphemeTile, 
  NGram grapheme) {
    graphemeTile.setSelected(true);
    updateSelectedGrapheme(grapheme);
  }

  private void updateSelectedGrapheme(NGram grapheme) {
    selectedGrapheme = grapheme;
  }

  private void handleClickedGramTile(GramTile gramTile) {
    if (clickingGramTileHasEffect()) {
      reloadLastGramTilePhonogramSoundIfNecessary();
      activeGramTile = gramTile;
      if (actionTileClicked())
        takeActionOnGramTile();
      else
        updateGramTilePhonogramsIfGramTileHasNeighborsWithPhonogramsOrIfNoGramTilesHavePhonograms();
    }
  }

  private boolean clickingGramTileHasEffect() {
    return selectedGrapheme != null || actionTileClicked();
  }

  private boolean actionTileClicked() {
    return (eraseTile.selected || soundTile.selected);
  }

  private void takeActionOnGramTile() {
    if (soundTile.selected && activeGramTile.hasPhonogram())
      activeGramTile.playSound();
    else if (eraseTile.selected && canErase()) {
      activeGramTile.setToBlank();
      updateGramTilePhonograms();
    }
  }

  private boolean canErase() {
    return (activeGramTile.hasPhonogram() && activeGramTile.isEndGramTile());
  }

  private void reloadLastGramTilePhonogramSoundIfNecessary() {
    if (activeGramTile != null
      && activeGramTile.needToReloadPhonogramSound())
      activeGramTile.reloadPhonogramSound();
  }

  private void updateGramTilePhonogramsIfGramTileHasNeighborsWithPhonogramsOrIfNoGramTilesHavePhonograms() {
    if (noGramTilesHavePhonograms()) {
      updateGramTile();
    } 
    else
      updateIfGramTileClickedHasPhonoGramOrNeighbors();
  }

  private boolean noGramTilesHavePhonograms() {
    for (GramTile gt : gramTiles)
      if (gt.hasPhonogram())
        return false;
    return true;
  }

  private void updateGramTile() {
    activeGramTile.updateGrapheme(selectedGrapheme);
    updateGramTilePhonograms();
    activeGramTile.setSelected(true);
  }

  private void updateIfGramTileClickedHasPhonoGramOrNeighbors() {
    if (activeGramTile.isNeighborOfATileWithAGrapheme())
      updateGramTile();
  }

  private void updateGramTilePhonograms() {
    updatePhonograms();
    matchPhonogramsToGramTiles();
  }

  private void updatePhonograms() {
    activeGramTile.chain();
    phonograms = GraphemeToPhonogramDecoder.decode(activeGramTile
      .getNGramChain());
  }

  private void matchPhonogramsToGramTiles() {
    GramTile head = activeGramTile.getHead();
    int cursor = gramTiles.indexOf(head);
    int limit;
    for (Phonogram p : phonograms) {
      limit = p.grapheme.cardinality() + cursor;
      for (; cursor < limit; cursor++) {
        gramTiles.get(cursor).updatePhonogram(p);
      }
    }
  }

  private void handleClickedActionTile(ActionTile actionTile) {
    selectedGrapheme = null;
    if (actionTile.label().equals(SOUND_TILE))
      soundTile.setSelected(true);
    if (actionTile.label().equals(ERASE_TILE))
      eraseTile.setSelected(true);
  }

  /*
	 * temporary methods for testing
   	 */
  public LinkedList<Tile> getTiles() {
    return tiles;
  }

  public ArrayList<GramTile> getGramTiles() {
    return gramTiles;
  }

  private class PhonoBlocksUIBuilder {

    public void build() {
      buildGraphemeTileBars();
      buildGramTileBar();
      buildSoundTile();
      buildEraseTile();
    }

    public void buildGraphemeTileBars() {
      buildVowelBar();
      buildConsonantBar();
    }

    public void buildConsonantBar() {
      int graphemeTileArrayPointer = 0;
      int tileCenterX = TILE_MARGIN + TILE_WIDTH / 2;
      int tileCenterY = TILE_HEIGHT;
      for (NGram c : NGram.allConsonants()) {
        graphemeTiles[graphemeTileArrayPointer] = new GraphemeTile(c, 
        TILE_WIDTH, TILE_HEIGHT, tileCenterX, tileCenterY, 
        canvas);
        graphemeTileArrayPointer++;
        tileCenterX += TILE_MARGIN + TILE_WIDTH;
      }
    }

    public void buildVowelBar() {
      int graphemeTileArrayPointer = numConsonants;
      int tileCenterX = PhonoBlocks.DISPLAY_WIDTH - TILE_WIDTH;
      int tileCenterY = TILE_HEIGHT;
      for (NGram v : NGram.allVowels()) {
        graphemeTiles[graphemeTileArrayPointer] = new GraphemeTile(v, 
        TILE_WIDTH, TILE_HEIGHT, tileCenterX, tileCenterY, 
        canvas);
        graphemeTileArrayPointer++;
        tileCenterY += TILE_MARGIN + TILE_HEIGHT;
      }
    }

    public void buildGramTileBar() {
      int tileCenterX = TILE_WIDTH;
      int tileCenterY = PhonoBlocks.DISPLAY_HEIGHT - TILE_HEIGHT * 2;
      int centerXIncrement = TILE_MARGIN + TILE_WIDTH;
      for (int i = 0; i < MAX_GRAMS; i++) {
        GramTile gt = new GramTile(TILE_WIDTH, TILE_HEIGHT, 
        tileCenterX, tileCenterY, canvas);
        gramTiles.add(gt);
        tileCenterX += centerXIncrement;
      }
      setGramTileNeighbors();
    }

    private void setGramTileNeighbors() {
      for (int i = 0; i < gramTiles.size(); i++) {
        GramTile gt = gramTiles.get(i);
        if (i > 0)
          gt.setLeftNeighbor(gramTiles.get(i - 1));

        if (i < gramTiles.size() - 1)
          gt.setRightNeighbor(gramTiles.get(i + 1));
      }
    }

    private void buildSoundTile() {
      int tileCenterX = TILE_MARGIN + TILE_WIDTH / 2;
      int tileCenterY = TILE_HEIGHT * 2 + TILE_MARGIN;
      soundTileGraphic = canvas.loadImage(PhonoBlocks.DATA_FOLDER_PATH
        + SOUND_TILE_GRAPHIC_FILENAME + ".png");
      soundTile = new ActionTile(TILE_WIDTH, TILE_HEIGHT, tileCenterX, 
      tileCenterY, canvas, SOUND_TILE);
      soundTile.setGraphic(soundTileGraphic, Tile.SCALE_TILE_GRAPHIC);
    }

    private void buildEraseTile() {
      int tileCenterX = TILE_MARGIN + TILE_WIDTH / 2;
      int tileCenterY = TILE_HEIGHT * 3 + TILE_MARGIN;
      eraseTileGraphic = canvas.loadImage(PhonoBlocks.DATA_FOLDER_PATH
        + ERASE_TILE_GRAPHIC_FILENAME + ".png");
      eraseTile = new ActionTile(TILE_WIDTH, TILE_HEIGHT, tileCenterX, 
      tileCenterY, canvas, ERASE_TILE);
      eraseTile.setGraphic(eraseTileGraphic, Tile.SCALE_TILE_GRAPHIC);
    }
  }
}

