import processing.core.PApplet;


public class ActionTile extends Tile implements Clickable, Drawable {
	
	private String label;
	
	public ActionTile(int tileWidth_, int tileHeight_, int x, int y, PApplet canvas,String label_) {
		super(tileWidth_, tileHeight_, x, y,canvas);
		label=label_;
	}

	@Override
	public void onClick() {
        notifyObservers(this);
	}

	@Override
	public void draw() {
		drawTileBackground();
		drawGraphic();

	}

	@Override
	public void draw(Color c) {
		draw();
	}

	public String label() {
		return label;
	}

}
