import processing.core.*;
import ddf.minim.*;

import java.util.*;
import java.awt.*;

public class GraphemeTile extends Tile {

	private NGram grapheme;


	public GraphemeTile(NGram grapheme_, int tileWidth_, int tileHeight_,
			int x, int y, PApplet canvas) {
		super(tileWidth_, tileHeight_, x, y, canvas);
		grapheme = grapheme_;
		
	}


	public void onClick() {
		notifyObservers(grapheme);
	}

	@Override
	public void draw() {
		drawTileBackground();
		drawGrapheme(calculateGraphemeColor());
	}
	
	@Override
	public void draw(Color c) {
		drawTileBackground();
		drawGrapheme(c);
	}

	private Color calculateGraphemeColor() {
		return (DEFAULT_COLOR.intensity>100? new Color(0,0,0): new Color(255,255,255));
	}
	
	private void drawGrapheme(Color c) {
		canvas.fill(c.r, c.g, c.b);
		float textSize=(float) ((float)tileHeight/1.5);
		canvas.textSize(textSize);
		canvas.text(grapheme.asString(), position.x-textSize/2, position.y+textSize/2);

	}



}
