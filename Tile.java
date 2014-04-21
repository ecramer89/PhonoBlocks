import processing.core.*;
import ddf.minim.*;

import java.awt.Point;
import java.util.*;

public abstract class Tile extends Observable implements Clickable, Drawable {

	public static final Color DEFAULT_COLOR = new Color(255);
	public static final Color SELECT_BORDER_COLOR = new Color(150, 0, 0);
	private static final Color DEFAULT_BORDER_COLOR = new Color(0);
	private static final float DEFAULT_BORDER_WEIGHT = 2;
	public static final boolean SCALE_TILE_GRAPHIC = true;

	protected int tileWidth;
	protected int tileHeight;
	protected Point position;
	protected PApplet canvas;
	protected Color tileBGColor;
	protected PImage graphic;

	protected boolean selected;
	private boolean shouldScaleImage;

	public Tile(int tileWidth_, int tileHeight_, int x, int y, PApplet canvas_) {
		tileWidth = tileWidth_;
		tileHeight = tileHeight_;
		position = new Point(x, y);
		canvas = canvas_;
		tileBGColor = DEFAULT_COLOR;
		shouldScaleImage=false;
	}

	public Tile(int tileWidth_, int tileHeight_, int x, int y) {
		tileWidth = tileWidth_;
		tileHeight = tileHeight_;
		position = new Point(x, y);
		tileBGColor = DEFAULT_COLOR;
	}

	protected void drawTileBackground() {
		drawTileBorder();
		canvas.fill(tileBGColor.r, tileBGColor.g, tileBGColor.b);
		canvas.rectMode(PApplet.CENTER);
		canvas.rect(position.x, position.y, tileWidth, tileHeight);

	}

	private void drawTileBorder() {
		canvas.strokeWeight(DEFAULT_BORDER_WEIGHT);
		if (selected)
			canvas.stroke(SELECT_BORDER_COLOR.r, SELECT_BORDER_COLOR.g,
					SELECT_BORDER_COLOR.b);

		else
			canvas.stroke(DEFAULT_BORDER_COLOR.intensity);
	}

	public void setTileBGColor(Color c) {
		tileBGColor = c;
	}

	public boolean mouseOn(float mouseX, float mouseY) {
		return (mouseOnTileX(mouseX) && mouseOnTileY(mouseY));
	}

	private boolean mouseOnTileX(float mouseX) {
		return (mouseX < position.x + tileWidth / 2 && mouseX > position.x
				- tileWidth / 2);
	}

	private boolean mouseOnTileY(float mouseY) {
		return (mouseY < position.y + tileHeight / 2 && mouseY > position.y
				- tileHeight / 2);
	}

	public void setSelected(boolean selected_) {
		selected = selected_;

	}

	public void setGraphic(PImage graphic_) {
		graphic = graphic_;
	}
	
	public void setGraphic(PImage graphic_, boolean shouldScale) {
		graphic = graphic_;
		shouldScaleImage=shouldScale;
	}

	public void drawGraphic() {
		if (graphic != null) {
			canvas.imageMode(PConstants.CENTER);
			if(shouldScaleImage)
				 canvas.image(graphic, position.x, position.y,tileWidth,tileHeight);
			else canvas.image(graphic, position.x, position.y);
		}
	}

}
